// Pagina el informe dentro del navegador antes de imprimir, para que no queden huecos al pie de página.
// Recorre los bloques en orden y, donde uno no cabe en la página actual:
//   - párrafo / lista / tabla  → lo parte en la línea, ítem o fila que no cabe;
//   - figura                   → la achica para que entre (sin bajar de MIN_FIG del ancho);
//                                si no puede, sube el texto siguiente y la figura pasa a la otra página;
//   - título                   → nunca queda solo al pie: salta con lo que sigue.
// Cada salto se marca con un separador; al final los separadores se vuelven saltos de página reales.
(({ altoPagina, minFig }) => {
  const cuerpo = document.body;
  const top = (el) => el.getBoundingClientRect().top + window.scrollY;
  const bottom = (el) => el.getBoundingClientRect().bottom + window.scrollY;
  const anchoTexto = cuerpo.clientWidth;
  const TEXTO = ['P', 'UL', 'OL'];
  const esTitulo = (el) => el && (el.tagName === 'H2' || el.tagName === 'H3' || el.classList.contains('tcap'));
  const LINEA = 22; // ~ una línea de texto, en px

  const saltos = [];
  const registro = [];
  const nombre = (e) => (e.tagName + ':' + (e.innerText || '').slice(0, 30)).replace(/\n/g, ' ');
  function saltoAntes(el, motivo) {
    registro.push(`p${saltos.length + 3} ${motivo || '?'} → salto antes de ${nombre(el)} (sobraban ${Math.round(inicio + altoPagina - top(el))}px)`);
    // los títulos (y la leyenda de una tabla) viajan con lo que encabezan
    while (esTitulo(el.previousElementSibling)) el = el.previousElementSibling;
    // si queda espacio, se agranda la última figura de la página para llenarlo
    const libre = inicio + altoPagina - top(el) - 4;
    if (libre > 24) {
      const figs = [...document.querySelectorAll('body > figure')]
        .filter((f) => top(f) >= inicio && bottom(f) <= top(el) && f.querySelector('img').getBoundingClientRect().width < 0.96 * anchoTexto);
      const f = figs[figs.length - 1];
      if (f) {
        const im = f.querySelector('img');
        const r = im.getBoundingClientRect();
        const ancho = Math.min(0.96 * anchoTexto, r.width * (r.height + libre) / r.height);
        im.style.width = ancho + 'px';
        if (inicio + altoPagina - top(el) < 0) im.style.width = r.width + 'px'; // por si acaso: nunca desbordar
      }
    }
    const sep = document.createElement('div');
    sep.className = 'salto-pag';
    el.before(sep);
    el.style.marginTop = '0';
    saltos.push(sep);
    sep.style.height = '0px';
    sep.style.height = Math.max(0, inicio + altoPagina - top(el)) + 'px';
    inicio = top(el);
  }

  // parte un bloque de texto en el punto (x, y): todo lo que sigue va a un clon
  function partirTexto(el, linea) {
    const r = document.caretRangeFromPoint(linea.left + 1, linea.top + linea.height / 2);
    if (!r || !el.contains(r.startContainer)) return null;
    const resto = document.createRange();
    resto.setStart(r.startContainer, r.startOffset);
    resto.setEnd(el, el.childNodes.length);
    const clon = el.cloneNode(false);
    clon.appendChild(resto.extractContents());
    el.after(clon);
    el.style.textAlignLast = 'justify'; // la línea partida sigue justificada
    el.style.marginBottom = '0';
    return clon;
  }

  function lineas(el) {
    const r = document.createRange();
    r.selectNodeContents(el);
    const vistas = [];
    for (const c of r.getClientRects()) {
      const t = c.top + window.scrollY;
      if (!vistas.some((v) => Math.abs(v.top - t) < 4)) vistas.push({ top: t, left: c.left, height: c.height });
    }
    return vistas.sort((a, b) => a.top - b.top);
  }

  // al imprimir, el primer bloque tras un salto forzado conserva su margen superior: se anula para que cuadre
  document.querySelector('.reservada').nextElementSibling.style.marginTop = '0';
  let inicio = top(document.querySelector('.reservada').nextElementSibling);
  const bajadas = new Map();
  let el = document.querySelector('.reservada').nextElementSibling;
  let guardia = 0;
  while (el && guardia++ < 5000) {
    const limite = inicio + altoPagina;
    if (bottom(el) <= limite) {
      // un título al pie, sin espacio para dos líneas de lo que sigue, salta
      if (esTitulo(el) && bottom(el) + 2 * LINEA > limite) { saltoAntes(el, 'título al pie'); }
      el = el.nextElementSibling;
      continue;
    }
    const sobra = limite - top(el);
    if (TEXTO.includes(el.tagName) && el.className !== 'formula' && !el.classList.contains('cap')) {
      if (el.tagName === 'P') {
        const ls = lineas(el);
        let i = ls.findIndex((l) => l.top + l.height > limite);
        if (i === -1) i = ls.length - 1; // se pasa solo por el interlineado de la última línea
        if (i > 0) {
          const clon = partirTexto(el, ls[i]);
          if (clon) { saltoAntes(clon, 'párrafo partido'); el = clon; continue; }
        }
      } else {
        const items = [...el.children];
        const i = items.findIndex((li) => bottom(li) > limite);
        if (i > 0) {
          const clon = el.cloneNode(false);
          items.slice(i).forEach((li) => clon.appendChild(li));
          if (el.tagName === 'OL') clon.start = (el.start || 1) + i;
          el.style.marginBottom = '0';
          el.after(clon);
          saltoAntes(clon);
          el = clon;
          continue;
        }
      }
      saltoAntes(el);
      el = el.nextElementSibling;
      continue;
    }
    if (el.tagName === 'TABLE') {
      const filas = [...el.tBodies[0].rows];
      const i = filas.findIndex((f) => bottom(f) > limite);
      if (i > 0) {
        const clon = el.cloneNode(false);
        clon.appendChild(el.tHead.cloneNode(true));
        const tb = document.createElement('tbody');
        filas.slice(i).forEach((f) => tb.appendChild(f));
        clon.appendChild(tb);
        el.style.marginBottom = '0';
        el.after(clon);
        saltoAntes(clon);
        el = clon;
        continue;
      }
      saltoAntes(el);
      el = el.nextElementSibling;
      continue;
    }
    if (el.classList.contains('bloque') && sobra > 5 * LINEA) {
      // código: se parte por líneas y la segunda parte lleva «(continuación)»
      const pre = el.querySelector('pre');
      const alto = parseFloat(getComputedStyle(pre).lineHeight);
      const caben = Math.floor((limite - top(pre) - 2) / alto);
      const lineasCodigo = pre.textContent.split('\n');
      if (caben >= 3 && caben < lineasCodigo.length) {
        const clon = el.cloneNode(true);
        pre.textContent = lineasCodigo.slice(0, caben).join('\n');
        clon.querySelector('pre').textContent = lineasCodigo.slice(caben).join('\n');
        const cap = clon.querySelector('.cap');
        cap.textContent = cap.textContent.replace(/^(Código \d+)\..*/, '$1 (continuación).');
        el.style.marginBottom = '0';
        el.after(clon);
        saltoAntes(clon, 'código partido');
        el = clon;
        continue;
      }
    }
    if (el.classList.contains('galeria')) {
      // la galería va al final: las filas que ya no caben en la última página se quitan
      let f = el;
      while (f) { const n = f.nextElementSibling; f.remove(); f = n; }
      break;
    }
    if (el.tagName === 'FIGURE') {
      const img = el.querySelector('img');
      const exceso = bottom(el) - limite + 2;
      const nuevoAlto = img.getBoundingClientRect().height - exceso;
      const nuevoAncho = img.getBoundingClientRect().width * nuevoAlto / img.getBoundingClientRect().height;
      if (nuevoAncho >= minFig * anchoTexto - 2) {
        img.style.width = nuevoAncho + 'px';
        continue; // volver a medir la misma figura
      }
      // el texto que sigue (aunque haya un subtítulo en medio, nunca otra sección) sube a llenar el espacio
      let sig = el.nextElementSibling;
      while (sig && (sig.tagName === 'H3' || sig.classList.contains('tcap'))) sig = sig.nextElementSibling;
      const veces = bajadas.get(el) || 0;
      const relleno = sig && (TEXTO.includes(sig.tagName) || sig.tagName === 'TABLE') && !sig.classList.contains('formula');
      if (sobra > 2 * LINEA && relleno && veces < 4) {
        const primero = el.nextElementSibling;
        sig.after(el);
        bajadas.set(el, veces + 1);
        el = primero;
        continue;
      }
      // no hay texto que suba: se achican un poco las figuras anteriores de esta página para que esta quepa
      if (sobra > 3 * LINEA && !el.dataset.hecho) {
        el.dataset.hecho = '1';
        const minAlto = img.getBoundingClientRect().height * (minFig * anchoTexto) / img.getBoundingClientRect().width;
        let falta = bottom(el) - limite + 8 - (img.getBoundingClientRect().height - minAlto); // 8px de holgura
        const previas = [...document.querySelectorAll('body > figure')].filter((f) => f !== el && top(f) >= inicio && bottom(f) <= limite).reverse();
        const cambios = [];
        for (const f of previas) {
          if (falta <= 0) break;
          const im = f.querySelector('img');
          const r = im.getBoundingClientRect();
          const puede = r.height - r.height * (0.6 * anchoTexto) / r.width;
          if (puede <= 0) continue;
          const quita = Math.min(puede, falta);
          cambios.push([im, r.width * (r.height - quita) / r.height]);
          falta -= quita;
        }
        if (falta <= 0) {
          cambios.forEach(([im, w]) => { im.style.width = w + 'px'; });
          continue; // ahora la figura entra achicándose ella misma
        }
      }
    }
    saltoAntes(el);
    el = el.nextElementSibling;
  }
  // en el papel, cada separador es un salto de página
  for (const s of saltos) { s.style.height = '0px'; s.style.breakAfter = 'page'; }
  return registro;
})
