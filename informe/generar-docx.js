// Genera el informe en Word con el formato de Estructuras Discretas I (UPCH).
// Uso (desde informe/): npm i && node generar-docx.js   → Informe-ChipGuard.docx y .pdf
// Dos pasadas: la 1.ª convierte a PDF con LibreOffice para saber en qué página cae cada título,
// figura y tabla; la 2.ª escribe los índices con esos números.
const fs = require("fs");
const path = require("path");
const { execSync } = require("child_process");
const os = require("os");
// Windows: java imprime en UTF-8 aunque la salida vaya a un pipe; soffice y pdftotext pueden no estar en el PATH.
const ENV = { ...process.env, JAVA_TOOL_OPTIONS: "-Dfile.encoding=UTF-8" };
const SOFFICE = process.env.SOFFICE || (process.platform === "win32" ? "\"C:/Program Files/LibreOffice/program/soffice.exe\"" : "soffice");
const PDFTOTEXT = process.env.PDFTOTEXT || "pdftotext";
const d = require("docx");

const RAIZ = path.resolve(__dirname, "..");
const CAP = (f) => path.join(RAIZ, "capturas", f);
const SALIDA = path.join(__dirname, "Informe-ChipGuard.docx");
const GRUPO = process.env.GRUPO || "1";
const FECHA = process.env.FECHA || "20 de noviembre de 2026";   // fecha de la carátula
let CPU = "", JAVA = "";                        // equipo de la medición (sección 7.4)         // número de grupo del equipo

// ---------- medidas (twips) ----------
const cm = (x) => Math.round(x * 566.93);
const ANCHO_TEXTO = cm(21 - 3 - 2.5);         // 15,5 cm
const PIE = "Estructuras Discretas I · Ingeniería Informática · 2026-II";
const GRANATE = "A90A2E";

// ---------- referencias IEEE (se numeran por orden de primera cita) ----------
const ACC = "Accedido: 8 nov. 2026. [En línea]. Disponible en:";
const REFS = {
  osiptel116: `OSIPTEL, “Resolución de Consejo Directivo N.° 000116-2025-CD/OSIPTEL,” Lima, Perú, 2025. ${ACC} https://www.osiptel.gob.pe/media/xpxl0map/resol116-2025-cd.pdf`,
  congreso: `Congreso de la República del Perú, “OSIPTEL bloquea más de 650 mil celulares y alerta crecimiento del uso delictivo de líneas móviles,” 2025. ${ACC} https://comunicaciones.congreso.gob.pe/damos-cuenta/osiptel-bloquea-mas-de-650-mil-celulares-y-alerta-crecimiento-del-uso-delictivo-de-lineas-moviles/`,
  dplbaja: `DPL News, “Perú: Osiptel dispuso la baja de más de 102 mil líneas con registros inconsistentes durante el estado de emergencia,” 2025. ${ACC} https://dplnews.com/peru-osiptel-dispuso-baja-102-mil-lineas-registros-inconsistentes-estado-de-emergencia/`,
  canaln: `Canal N, “Osiptel detecta 200 mil usuarios con más de 10 líneas móviles,” 2025. ${ACC} https://canaln.pe/actualidad/osiptel-detecta-200-mil-usuarios-mas-10-lineas-moviles-n488632`,
  dplambulante: `DPL News, “Perú: Osiptel refuerza acciones contra la venta ambulatoria de chips con operativo preventivo en Lima Norte,” s. f. ${ACC} https://dplnews.com/peru-osiptel-refuerza-acciones-contra-venta-ambulatoria-chips-operativo-preventivo-lima-norte/`,
  americatv: `América TV, “Así operaba banda criminal que vendía chips activados a nombre de terceras personas,” s. f. ${ACC} https://www.americatv.com.pe/noticias/amp/actualidad/asi-operaba-banda-criminal-que-vendia-chips-activados-nombres-terceras-personas-n492832`,
  bloomberg: `Bloomberg Línea, “¿Pueden clonar su huella digital y robar su dinero? Así operaban «Los Huelleros del Norte» en Perú,” s. f. ${ACC} https://www.bloomberglinea.com/latinoamerica/peru/pueden-clonar-su-huella-digital-y-robar-su-dinero-asi-operaban-los-huelleros-del-norte-en-peru/`,
  peruanocheca: `El Peruano, “Osiptel refuerza tu seguridad: detecta y reporta líneas móviles no contratadas en Checa tus líneas,” s. f. ${ACC} https://elperuano.pe/noticia/288143-osiptel-refuerza-tu-seguridad-detecta-y-reporta-lineas-moviles-no-contratadas-en-checa-tus-lineas`,
  peruanobloqueo: `El Peruano, “¿Puedes bloquear nuevas líneas a tu nombre? Osiptel habilita restricción contra contrataciones no autorizadas,” 2025. ${ACC} https://elperuano.pe/noticia/288845-puedes-bloquear-nuevas-lineas-a-tu-nombre-osiptel-habilita-restriccion-contra-contrataciones-no-autorizadas`,
  rosen: "K. H. Rosen, *Discrete Mathematics and Its Applications*, 8.ª ed. Nueva York, NY, EE. UU.: McGraw-Hill Education, 2019.",
  epp: "S. S. Epp, *Discrete Mathematics with Applications*, 5.ª ed. Boston, MA, EE. UU.: Cengage Learning, 2020.",
  dirichlet1837: "P. G. L. Dirichlet, “Über die Darstellung ganz willkürlicher Functionen durch Sinus- und Cosinusreihen,” *Repertorium der Physik*, vol. 1, pp. 152–174, 1837.",
  dirichlet1842: "P. G. L. Dirichlet, “Verallgemeinerung eines Satzes aus der Lehre von den Kettenbrüchen nebst einigen Anwendungen auf die Theorie der Zahlen,” *Bericht über die Verhandlungen der Königlich Preussischen Akademie der Wissenschaften*, pp. 93–95, 1842.",
  rittaud: "B. Rittaud y A. Heeffer, “The pigeonhole principle, two centuries before Dirichlet,” *The Mathematical Intelligencer*, vol. 36, n.° 2, pp. 27–29, 2014.",
  cantor: "G. Cantor, “Ein Beitrag zur Mannigfaltigkeitslehre,” *Journal für die reine und angewandte Mathematik*, vol. 84, pp. 242–258, 1878.",
  dedekind: "R. Dedekind, *Was sind und was sollen die Zahlen?* Braunschweig, Alemania: Vieweg, 1888.",
  bourbaki: "N. Bourbaki, *Éléments de mathématique. Théorie des ensembles*, cap. I–II. París, Francia: Hermann, 1954.",
  knuth: "D. E. Knuth, *The Art of Computer Programming, Vol. 3: Sorting and Searching*, 2.ª ed. Reading, MA, EE. UU.: Addison-Wesley, 1998.",
  cormen: "T. H. Cormen, C. E. Leiserson, R. L. Rivest y C. Stein, *Introduction to Algorithms*, 4.ª ed. Cambridge, MA, EE. UU.: MIT Press, 2022.",
  repo: `J. J. Martel Vasquez, A. J. Sanga Limache, R. J. H. Tangoa Alva y E. J. Vidalon Ampudia, “ChipGuard,” repositorio GitHub, 2026. ${ACC} https://github.com/jordymartel-hash/ChipGuard`,
  geist: `Vercel, “Geist font,” licencia SIL Open Font License 1.1, 2024. ${ACC} https://github.com/vercel/geist-font`,
  temurin: `Eclipse Adoptium, “Eclipse Temurin 21 (JRE),” s. f. ${ACC} https://adoptium.net`,
};
const orden = [];
const num = (k) => {
  if (!REFS[k]) throw new Error("Referencia desconocida: " + k);
  if (!orden.includes(k)) orden.push(k);
  return orden.indexOf(k) + 1;
};

// ---------- texto con marcas: **negrita**, *cursiva*, `código`, {ref} o {ref,ref} ----------
function runs(texto, base = {}) {
  const out = [];
  for (const t of String(texto).split(/(\*\*[^*]+\*\*|\*[^*]+\*|`[^`]+`|\{[a-z0-9,]+\})/)) {
    if (!t) continue;
    if (t.startsWith("**")) out.push(new d.TextRun({ ...base, text: t.slice(2, -2), bold: true }));
    else if (t.startsWith("*") && t.length > 2) out.push(new d.TextRun({ ...base, text: t.slice(1, -1), italics: true }));
    else if (t.startsWith("`")) out.push(new d.TextRun({ ...base, text: t.slice(1, -1), font: "Courier New", size: (base.size || 24) - 2 }));
    else if (/^\{[a-z0-9,]+\}$/.test(t)) out.push(new d.TextRun({ ...base, text: t.slice(1, -1).split(",").map((k) => `[${num(k)}]`).join(", ") }));
    else for (const u of t.split(/([⇒⇔])/)) if (u) out.push(new d.TextRun({ ...base, text: u, ...(/[⇒⇔]/.test(u) ? { font: "Cambria Math" } : {}) }));   // Arial de Windows no trae ⇒ ni ⇔
  }
  return out;
}

// ---------- registro de entradas para los índices ----------
let C, entradas, nFig, nTab, nCod;
function reiniciar() { C = []; entradas = []; nFig = 0; nTab = 0; nCod = 0; orden.length = 0; }

const P = (t, o = {}) => C.push(new d.Paragraph({ alignment: d.AlignmentType.JUSTIFIED, spacing: { after: 160 }, ...o, children: runs(t, o.run) }));
const sangria = (t) => P(t, { indent: { left: cm(0.75) } });
function H1(t, { salto = false } = {}) {
  entradas.push({ tipo: "h1", texto: t });
  C.push(new d.Paragraph({ heading: d.HeadingLevel.HEADING_1, pageBreakBefore: salto, children: [new d.TextRun(t)] }));
}
function H2(t) {
  entradas.push({ tipo: "h2", texto: t });
  C.push(new d.Paragraph({ heading: d.HeadingLevel.HEADING_2, children: [new d.TextRun(t)] }));
}
function H3(t) { C.push(new d.Paragraph({ heading: d.HeadingLevel.HEADING_3, children: runs(t) })); }
function lista(items, numerada = false) {
  const inst = Math.floor(Math.random() * 1e6);
  for (const it of items)
    C.push(new d.Paragraph({ alignment: d.AlignmentType.JUSTIFIED, spacing: { after: 80 },
      numbering: { reference: numerada ? "numeros" : "vinetas", level: 0, instance: numerada ? inst : 0 }, children: runs(it) }));
}
const formula = (t) => C.push(new d.Paragraph({ alignment: d.AlignmentType.CENTER, spacing: { before: 60, after: 200 },
  children: [new d.TextRun({ text: t, font: "Cambria Math", size: 24 })] }));
const fuente = (t = "Elaboración propia.") => C.push(new d.Paragraph({ alignment: d.AlignmentType.LEFT, spacing: { after: 220, line: 240 },
  children: runs("*Fuente:* " + t, { size: 18 }) }));

function imagenRun(archivo, anchoCm) {
  const buf = fs.readFileSync(archivo);
  const w = buf.readUInt32BE(16), h = buf.readUInt32BE(20);      // cabecera PNG
  const px = anchoCm / 2.54 * 96;
  return new d.ImageRun({ type: "png", data: buf, transformation: { width: Math.round(px), height: Math.round(px * h / w) } });
}
function figura(archivo, titulo, { ancho = 11.5, fte = "Captura de la aplicación ChipGuard (elaboración propia)." } = {}) {
  nFig++;
  entradas.push({ tipo: "fig", texto: `Figura ${nFig}. ${titulo}`, clave: `Figura ${nFig}. ${titulo.slice(0, 18)}` });
  C.push(new d.Paragraph({ alignment: d.AlignmentType.CENTER, keepNext: true, spacing: { before: 120, after: 60, line: 240 },
    children: [imagenRun(CAP(archivo), ancho)] }));
  C.push(new d.Paragraph({ alignment: d.AlignmentType.CENTER, keepNext: true, spacing: { after: 40, line: 240 },
    children: [new d.TextRun({ text: `Figura ${nFig}. `, bold: true, size: 20 }), ...runs(titulo, { size: 20, italics: true })] }));
  fuente(fte);
  return nFig;
}
const borde = { style: d.BorderStyle.SINGLE, size: 4, color: "808080" };
function celda(t, ancho, cab = false) {
  return new d.TableCell({ width: { size: ancho, type: d.WidthType.DXA },
    shading: cab ? { fill: "E8E3E4", type: d.ShadingType.CLEAR, color: "auto" } : undefined,
    margins: { top: 50, bottom: 50, left: 90, right: 90 },
    children: String(t).split("\n").map((l) => new d.Paragraph({ spacing: { line: 240, after: 0 }, children: runs(l, { size: 20, bold: cab }) })) });
}
function tabla(titulo, filas, pesos, { fte } = {}) {
  nTab++;
  entradas.push({ tipo: "tab", texto: `Tabla ${nTab}. ${titulo}`, clave: `Tabla ${nTab}. ${titulo.slice(0, 18)}` });
  C.push(new d.Paragraph({ keepNext: true, spacing: { before: 160, after: 80, line: 240 },
    children: [new d.TextRun({ text: `Tabla ${nTab}. `, bold: true, size: 20 }), ...runs(titulo, { size: 20, italics: true })] }));
  const suma = pesos.reduce((a, b) => a + b, 0);
  const anchos = pesos.map((p) => Math.floor(ANCHO_TEXTO * p / suma));
  anchos[anchos.length - 1] += ANCHO_TEXTO - anchos.reduce((a, b) => a + b, 0);
  C.push(new d.Table({ width: { size: ANCHO_TEXTO, type: d.WidthType.DXA }, columnWidths: anchos,
    borders: { top: borde, bottom: borde, left: borde, right: borde, insideHorizontal: borde, insideVertical: borde },
    rows: filas.map((f, i) => new d.TableRow({ tableHeader: i === 0, cantSplit: true, children: f.map((t, j) => celda(t, anchos[j], i === 0)) })) }));
  fuente(fte || "Elaboración propia.");
  return nTab;
}
function codigo(titulo, texto) {
  nCod++;
  C.push(new d.Paragraph({ keepNext: true, spacing: { before: 160, after: 80, line: 240 },
    children: [new d.TextRun({ text: `Código ${nCod}. `, bold: true, size: 20 }), ...runs(titulo, { size: 20, italics: true })] }));
  const lineas = texto.replace(/\n$/, "").split("\n");
  const caja = { style: d.BorderStyle.SINGLE, size: 6, color: "BFB5B8" };
  C.push(new d.Table({ width: { size: ANCHO_TEXTO, type: d.WidthType.DXA }, columnWidths: [ANCHO_TEXTO],
    borders: { top: caja, bottom: caja, left: caja, right: caja, insideHorizontal: caja, insideVertical: caja },
    rows: [new d.TableRow({ cantSplit: lineas.length < 28, children: [new d.TableCell({ width: { size: ANCHO_TEXTO, type: d.WidthType.DXA },
      shading: { fill: "F7F5F5", type: d.ShadingType.CLEAR, color: "auto" }, margins: { top: 140, bottom: 140, left: 200, right: 200 },
      children: lineas.map((l) => new d.Paragraph({ spacing: { line: 300, after: 0 },
        children: [new d.TextRun({ text: l || " ", font: "Courier New", size: 19, color: l.trim().startsWith("//") ? "7A6E71" : "1F1F1F" })] })) })] })] }));
  C.push(new d.Paragraph({ spacing: { after: 160, line: 240 }, children: [] }));
}
function teorema(etq, enunciado, prueba) {
  P(`**${etq}.** ${enunciado}`);
  if (prueba) P(`*Demostración.* ${prueba} ∎`, { indent: { left: cm(0.75) } });
}

// ============================ CONTENIDO ============================
// Versión compacta (≤ 20 páginas en total): una figura o tabla solo si prueba algo que el texto no dice.
let TOTAL = 0;                                       // pruebas que pasan; se calcula de ./probar.sh en construir()

function resumen() {
  H1("RESUMEN", { salto: false });
  P(`En el Perú, cada línea móvil se registra a nombre de una persona mediante su DNI; sin embargo, OSIPTEL detectó 309 454 líneas con datos inconsistentes entre mayo y octubre de 2025, y muchas se usan para extorsionar desde números cuyo titular es una persona inocente. El objetivo de este trabajo es modelar el registro de líneas como una función entre conjuntos finitos, identificar con precisión dónde está la falla y proponer y validar una solución. La metodología combina el modelado matemático —dos funciones de Chips en Personas, *f* (registro) y *u* (uso real), el conteo por fibras y el principio del palomar— con la implementación en Java de un verificador de propiedades de funciones y de una simulación de una tienda de chips. Se demuestra que la falla no está en el tipo de *f*, sino en que *f*(c) ≠ *u*(c) para los chips fraudulentos, y que la trazabilidad exige una biyección entre los chips activos y las autorizaciones de su titular. Con ese fundamento se propone ChipGuard, un servicio que solo activa un chip cuando el dueño del DNI lo aprueba. En la simulación, aun con decisiones perfectas en el mostrador, dos de seis atenciones terminan con un chip en manos criminales; con ChipGuard, ninguna. Las ${TOTAL} pruebas automáticas pasan y la medición del verificador confirma un costo cuadrático que orienta su diseño a escala real.`);
  P("**Palabras clave:** funciones biyectivas; principio del palomar; trazabilidad; telefonía móvil; simulación.", { alignment: d.AlignmentType.LEFT });
}

function cuerpo(tiempos, suites, traza, escenarios) {
  // ======================= I =======================
  H1("I. INTRODUCCIÓN", { salto: false });
  H2("1.1 Contextualización y justificación");
  P(`En el Perú, cada línea móvil se registra a nombre de una persona mediante su DNI y la verificación de su huella en RENIEC, de modo que ante un delito cometido desde un número las autoridades deberían llegar al responsable. En la práctica no ocurre así: entre mayo y octubre de 2025 se detectaron 309 454 líneas con datos inconsistentes {congreso}, se dieron de baja más de 102 mil {dplbaja} y unas 200 mil personas tenían más de diez líneas a su nombre {canaln}. Una forma frecuente, advertida por OSIPTEL en sus operativos, es la **doble huella**: el vendedor dice que la huella «no salió», la pide otra vez y con esa segunda lectura válida activa un chip extra a nombre del cliente, que luego vende aparte. También se han documentado bandas que activaban chips con datos de terceros y huellas clonadas en silicona.`);
  P(`Desde la informática es un problema de **integridad de datos y trazabilidad**: la tabla que asocia cada línea con su titular contiene pares falsos. Las medidas vigentes —el portal «Checa tus líneas» {peruanocheca}, la restricción de nuevas contrataciones {peruanobloqueo} y la validación de quienes tienen más de diez líneas {osiptel116}— exigen que el ciudadano revise por iniciativa propia, cuando el chip fraudulento ya está activo. Un modelo con funciones permite separar lo que es legítimo (tener varias líneas) de la falla real y deducir qué propiedad debe garantizar una solución.`);
  H2("1.2 Delimitación y pregunta de investigación");
  P("El ámbito es el registro de líneas móviles en el Perú con cifras públicas de 2025, modelado con conjuntos finitos, funciones, fibras, biyecciones y el principio del palomar. Se desarrolla una aplicación en Java que verifica propiedades de funciones y simula una tienda con una fila fija de seis clientes; ChipGuard se diseña a nivel de arquitectura, sin implementar su servidor real. Quedan fuera las tasas reales de fraude, la integración con operadoras y los aspectos legales.");
  P("**Pregunta:** ¿qué propiedad, expresada con funciones entre conjuntos finitos, debe cumplir el registro de líneas para que el rastreo de un número lleve a una persona responsable, y en qué medida un mecanismo de autorización del titular (ChipGuard) la garantiza en una simulación?");

  // ======================= II =======================
  H1("II. ANTECEDENTES HISTÓRICOS Y APORTES DE AUTORES");
  P(`Dirichlet formuló en 1837 la función como **asignación arbitraria**, no necesariamente dada por una fórmula {dirichlet1837}: por eso el registro, que es una tabla, es una función. El **principio del palomar** lleva su nombre por un trabajo de 1842 {dirichlet1842}, aunque el argumento aparece dos siglos antes en textos franceses {rittaud}. Cantor comparó conjuntos mediante biyecciones {cantor}, Dedekind estudió las aplicaciones inyectivas como objeto propio {dedekind} y Bourbaki fijó los términos inyectiva, sobreyectiva y biyectiva {bourbaki}. En informática, el palomar explica las colisiones inevitables de las tablas de dispersión {knuth}; en el registro la relación es inversa: varias líneas por persona es legal, y lo ilegal es que la tabla apunte a quien no corresponde.`);

  // ======================= III =======================
  H1("III. MARCO TEÓRICO");
  P(`Se sigue la notación de Rosen {rosen} y Epp {epp}; |*A*| es el número de elementos de un conjunto finito *A*.`);
  H2("3.1 Función, fibra y tipos de función");
  teorema("Definición 1", "Una función *f* : *A* → *B* asigna a cada *a* ∈ *A* exactamente un *f*(*a*) ∈ *B*. La **fibra** de *b* ∈ *B* es *f*⁻¹(*b*) = {*a* ∈ *A* : *f*(*a*) = *b*}: en un diagrama sagital, |*f*⁻¹(*b*)| es el número de flechas que llegan a *b*.");
  teorema("Definición 2", "*f* es **inyectiva** si *f*(*x*₁) = *f*(*x*₂) ⇒ *x*₁ = *x*₂ (ejemplo: pasajero → asiento, que deja de serlo con la sobreventa); **sobreyectiva** si todo *y* ∈ *B* tiene un *x* con *f*(*x*) = *y* (alumno → grupo, sin grupos vacíos); y **biyectiva** si es ambas (persona ↔ DNI). *g* : *B* → *A* es **inversa** de *f* si *g*(*f*(*a*)) = *a* y *f*(*g*(*b*)) = *b* para todo *a*, *b*.");
  teorema("Teorema 1", "*f* tiene inversa si y solo si es biyectiva.",
    "(⇒) Si *f*(*a*₁) = *f*(*a*₂), aplicando *g* resulta *a*₁ = *a*₂: inyectiva; y para *b* ∈ *B*, *a* = *g*(*b*) cumple *f*(*a*) = *b*: sobreyectiva. (⇐) Para cada *b* existe un *a* con *f*(*a*) = *b* y es único; *g*(*b*) = *a* cumple ambas igualdades.");
  H2("3.2 Conteo por fibras y principio del palomar");
  teorema("Teorema 2", "Si *A* y *B* son finitos, |*A*| = Σ |*f*⁻¹(*b*)|, sumando sobre todos los *b* ∈ *B*.",
    "Cada *a* está en exactamente una fibra, la de *f*(*a*); las fibras son disjuntas y su unión es *A*, y se aplica la regla de la suma.");
  teorema("Corolario 1", "*f* es inyectiva ⇔ |*f*⁻¹(*b*)| ≤ 1 para todo *b*, y sobreyectiva ⇔ |*f*⁻¹(*b*)| ≥ 1 para todo *b*. Por tanto, inyectiva ⇒ |*A*| ≤ |*B*| y biyectiva ⇒ |*A*| = |*B*|.",
    "Es la definición leída sobre las fibras; las desigualdades salen de acotar cada sumando del Teorema 2.");
  teorema("Teorema 3 (palomar)", "Si |*A*| > |*B*|, ninguna *f* : *A* → *B* es inyectiva {rosen}. En general, alguna fibra tiene al menos ⌈*n*/*k*⌉ elementos si |*A*| = *n* y |*B*| = *k*.",
    "Si *f* fuera inyectiva, el Corolario 1 daría |*A*| ≤ |*B*|. Para la versión general, si toda fibra tuviera menos de *n*/*k* elementos, el Teorema 2 daría *n* < *n*.");
  teorema("Corolario 2 (tope de líneas)", "Si el registro garantiza |*f*⁻¹(*p*)| ≤ *k* para toda persona *p*, el chip *k* + 1 no puede registrarse a nombre de quien ya tiene *k*.");
  P("El Corolario 1 es la base del verificador (sección 5.3): para decidir si una relación es inyectiva o sobreyectiva basta **contar las flechas que llegan** a cada elemento de *B*.");
  H2("3.3 Composición e inducción");
  teorema("Teorema 4", "Si *g* : *A* → *B* y *h* : *B* → *C* son inyectivas, *h* ∘ *g* es inyectiva.",
    "Si *h*(*g*(*a*₁)) = *h*(*g*(*a*₂)), la inyectividad de *h* da *g*(*a*₁) = *g*(*a*₂), y la de *g*, *a*₁ = *a*₂.");
  teorema("Teorema 5 (inducción)", "Si *P*(1) es verdadera y, para todo *n* ≥ 1, *P*(1), …, *P*(*n*) implican *P*(*n* + 1), entonces *P*(*n*) vale para todo *n* ≥ 1 {rosen}.");

  // ======================= IV =======================
  H1("IV. OBJETIVOS");
  H2("4.1 Objetivo general");
  P("Modelar el registro de líneas móviles del Perú como una función entre conjuntos finitos para explicar por qué permite la extorsión con chips a nombre de terceros, y proponer, implementar y validar mediante simulación una solución basada en las propiedades de las funciones.");
  H2("4.2 Objetivos específicos");
  lista([
    "**OE1.** Representar el registro y el uso real de las líneas como dos funciones, clasificarlas y demostrar las propiedades (inversa, conteo por fibras y palomar) que debe cumplir una solución.",
    "**OE2.** Diseñar ChipGuard a partir de esa condición matemática, describiendo su arquitectura, su flujo de autorización y sus casos especiales.",
    "**OE3.** Implementar en Java un verificador de propiedades de funciones y una simulación de una tienda de chips, y validarlos con pruebas automáticas contrastadas con la teoría.",
  ]);

  // ======================= V =======================
  H1("V. METODOLOGÍA Y DISEÑO");
  H2("5.1 Enfoque y modelo matemático");
  P("El trabajo es aplicado: (1) caracterizar el problema con fuentes oficiales y periodísticas; (2) modelarlo con funciones y demostrar las propiedades necesarias; (3) diseñar la solución desde esa condición; y (4) implementarla y validarla con pruebas y una simulación, contrastando cada resultado con un teorema (sección 7.3). Sean *C* los chips activos y *P* las personas con DNI:");
  tabla("Las dos funciones del modelo", [
    ["Función", "Qué asigna", "Quién la conoce"],
    ["*f* : *C* → *P* (registro)", "A cada chip, la persona a cuyo nombre está", "La operadora y la policía"],
    ["*u* : *C* → *P* (uso real)", "A cada chip, la persona que realmente lo usa", "Nadie, salvo quien lo usa"],
  ], [1.2, 1.8, 1.2]);
  P("Ninguna debe ser inyectiva (tener varias líneas es legal) ni sobreyectiva (no todos tienen chip). El problema está en el conjunto donde no coinciden:");
  formula("D = { c ∈ C : f(c) ≠ u(c) }  ≠  ∅");
  P(`La policía aplica *f* y llega al titular en papel, no al responsable: en el caso de Ana, *f*(…935) = Ana pero *u*(…935) = extorsionador (Figura ${nFig + 1}). Como no todo chip de *D* es delito (un padre puede registrar la línea de su hijo), la condición correcta no es *f* = *u*, sino que todo chip activo haya sido **autorizado por su titular**. Sea *K* el conjunto de autorizaciones usadas, cada una emitida por una persona *t*(κ), para una sola solicitud y de un solo uso. ChipGuard impone: **c se activa solo si existe κ emitida por f(c) para c**.`);
  figura("2i-matematica.png", "El problema: las dos flechas de un mismo chip no llegan a la misma persona.", { ancho: 10 });
  teorema("Proposición 1", "Bajo esa regla, *g* : *C* → *K*, que asigna a cada chip la autorización que lo activó, es biyectiva y *t*(*g*(c)) = *f*(c) para todo c.",
    "*g* es función porque cada chip se activó con una sola autorización; es inyectiva porque cada autorización es de un solo uso y ligada a una solicitud (Teorema 4 sobre chip → autorización → solicitud); es sobreyectiva porque *K* solo contiene autorizaciones usadas. La igualdad es la regla. Por el Teorema 1 existe *g*⁻¹: de cada chip se llega a una persona real que dijo «sí, fui yo».");
  teorema("Proposición 2 (primer chip)", "Si (i) el primer chip c₁ de *p* solo se activa cuando *p* contesta en el mostrador una llamada al número nuevo, y un DNI sin líneas puede activar un solo chip en sus primeras 72 horas, y (ii) cada cₙ₊₁ se activa solo con aprobación de *p* desde algún cⱼ con *j* ≤ *n*, entonces todo chip de *p* está ligado a una validación presencial de *p*.",
    "Por inducción (Teorema 5): c₁ se validó en persona; si c₁, …, cₙ están ligados, cₙ₊₁ se aprobó desde uno de ellos. Además, la doble huella sobre un DNI nuevo pediría dos chips en 72 horas; con tope *k* = 1, por el palomar el segundo no tiene «cajón» y no se activa.");
  H2("5.2 Datos y entradas");
  P("Las entradas son las cifras públicas citadas, conjuntos finitos pequeños para el verificador (por ejemplo *A* = {1, 2, 3}, *B* = {a, b, c, d} y casos borde como el dominio vacío) y una fila fija de seis clientes que cubre los casos posibles:");
  const tFila = tabla("La fila de clientes de la simulación", [
    ["Cliente", "DNI que entrega", "¿Es el dueño?", "¿El vendedor saca un chip extra?"],
    ["Luis", "Luis", "Sí", "No"], ["Encapuchado", "Pedro", "No", "No"], ["Rosa", "Rosa", "Sí", "Sí (doble huella)"],
    ["Carmen", "Carmen", "Sí", "No"], ["Impostora", "Juana", "No", "No"], ["Jorge", "Jorge", "Sí", "Sí (doble huella)"],
  ], [1, 1, 1, 1.6], { fte: "Elaboración propia (`Tienda.filaDelDia()` en el código)." });
  H2("5.3 Algoritmos");
  P("El verificador representa una relación como una lista de flechas (*a*, *b*) y aplica el Corolario 1 contando flechas:");
  codigo("Pseudocódigo del verificador y de ChipGuard", `¿ES FUNCIÓN?     de cada a de A sale exactamente 1 flecha
¿ES INYECTIVA?   función y a ningún b de B le llegan 2 o más
¿ES SOBREYECTIVA? función y a todo b de B le llega al menos 1
¿ES BIYECTIVA?   inyectiva Y sobreyectiva

CHIPGUARD, al llegar una solicitud de chip:
1. ¿Huella válida en RENIEC?   no → RECHAZAR
2. ¿El DNI ya tiene k chips?   sí → RECHAZAR por tope (Cor. 2)
3. ¿Primer chip? llamar al número nuevo en el mostrador
      contesta → ACTIVAR (1 chip en 72 h);  no → NO SE ACTIVA
4. Si no: alerta al chip ya validado (paso inductivo)
      «Sí, fui yo» + huella → ACTIVAR;  «No fui yo» → BLOQUEAR y
      reportar a OSIPTEL;  sin respuesta en 10 min → NO SE ACTIVA`);
  P("**Corrección y costo.** Si la relación es función, llegadas(*b*) = |*f*⁻¹(*b*)| y las pruebas de inyectividad y sobreyectividad son el Corolario 1 literal. Cada conteo recorre las |*F*| = *n* flechas, así que con |*A*| = |*B*| = *n* el costo es O(*n*²); con un mapa de dispersión sería O(*n*) {cormen}, pero se eligió la versión cuadrática por legibilidad y su costo se mide en la sección 7.4.");
  H2("5.4 Diseño de ChipGuard");
  P("ChipGuard es un servicio con una sola regla: **ningún chip se activa con tu DNI sin tu permiso**. No reemplaza la biometría de RENIEC; añade un paso posterior que materializa la Proposición 1. Tiene tres partes: una **app móvil** que avisa al instante, aprueba con la huella del celular, bloquea y muestra todos los chips a tu nombre; una **web** con DNI y código por SMS para quien no tiene smartphone; y un **servidor central** que recibe cada solicitud de la operadora, espera la respuesta del dueño y ordena activar o bloquear. A las cuatro piezas que ya existen (tienda, operadora, RENIEC y OSIPTEL) agrega solo dos: el servidor y la app (Figura " + (nFig + 1) + ").");
  figura("3f-arquitectura.png", "Componentes de ChipGuard; en verde, lo que se agrega.", { ancho: 11 });
  P("Una solicitud sigue siete pasos: la tienda envía DNI, huella, tienda y hora a la operadora; la operadora valida la huella con RENIEC y consulta a ChipGuard, con la línea en espera; ChipGuard notifica al dueño, que responde «Sí, fui yo» con su huella o «No fui yo»; ChipGuard ordena activar o bloquear, y si hubo bloqueo OSIPTEL recibe un reporte con la tienda y la hora. **Casos especiales:** sin respuesta en 10 minutos la línea no se activa; sin smartphone se usa SMS o llamada automática; sin celular se aprueba en una agencia con la huella; el primer chip se valida con la llamada en el mostrador (Proposición 2). **Privacidad:** se guarda el DNI cifrado, el identificador del celular y el historial de solicitudes; nunca la huella, llamadas, mensajes ni ubicación. Se propone una API REST en Java (Spring Boot), PostgreSQL, notificaciones por Firebase y APNs con SMS de respaldo, e integración por HTTPS con firma digital.");
  H2("5.5 Herramientas y decisiones");
  P("Se eligió **Java 17** (genéricos para *A* y *B* de cualquier tipo), **Swing y Java2D** sin dependencias para funcionar sin internet en el aula, un **ejecutor de pruebas propio** que imprime cada prueba en lenguaje claro, la **lista de flechas** porque refleja el diagrama sagital y admite relaciones que no son función, un **paquete Windows con Java incluido** y **Git y GitHub** para un historial verificable {repo}.");

  // ======================= VI =======================
  H1("VI. IMPLEMENTACIÓN Y SIMULACIÓN");
  H2("6.1 Arquitectura mínima y código esencial");
  P("El código se separa en tres paquetes: `simuchip.funciones` (flecha, verificador, traza y diagnóstico), `simuchip.caso` (persona, línea, solicitud, tienda y simulador) y `simuchip.ui` (los capítulos de la exposición); las siete suites de pruebas solo dependen de los dos primeros. El fragmento central es la traducción del Corolario 1:");
  codigo("`Funciones.java`: ¿es inyectiva?", `public static <A, B> boolean esInyectiva(Set<A> dominio,
        Set<B> codominio, List<Flecha<A, B>> flechas) {
    if (!esFuncion(dominio, flechas)) return false;
    for (B b : codominio)
        if (llegadas(b, dominio, flechas) > 1) return false;
    return true;
}`);
  H2("6.2 Reproducción, entradas, salidas y casos");
  P(`Con Java 17 y Git: \`git clone https://github.com/jordymartel-hash/ChipGuard.git\`, luego \`./probar.sh\` (compila y corre las ${TOTAL} pruebas), \`./empaquetar.sh\` y \`java -jar ChipGuard.jar\`. En Windows basta descargar \`ChipGuard-Windows.zip\` del repositorio y abrir \`Abrir ChipGuard.bat\`. El verificador recibe *A*, *B* y las flechas y devuelve verdadero o falso por propiedad; la tienda recibe la fila, con o sin ChipGuard, y la decisión por cliente, y devuelve los contadores de chips vendidos, en manos criminales, bloqueados y clientes perdidos.`, { alignment: d.AlignmentType.LEFT });
  tabla("Casos normales y casos límite verificados", [
    ["Tipo", "Caso (texto literal de la prueba)", "Resultado esperado"],
    ["Normal", "1→a, 2→b, 3→c con 'd' sola", "Inyectiva, no sobreyectiva"],
    ["Normal", "1→a, 2→b, 3→c con |A| = |B| = 3", "Biyectiva; su inversa también"],
    ["Límite", "dominio vacío → función vacía", "Es función"],
    ["Límite", "el 1 tiene dos flechas y el 3 ninguna", "No es función"],
    ["Límite", "flecha desde fuera de A (7→c)", "No cuenta; no es sobreyectiva"],
    ["Simulación", "vender a los 6 clientes con ChipGuard", "0 en manos criminales, 4 bloqueados"],
  ], [0.8, 2.2, 1.6], { fte: "Elaboración propia a partir de `PruebasFunciones.java` y `PruebasTienda.java`." });
  H2("6.3 La aplicación de exposición");
  P("La aplicación se usa para exponer: avanza con las flechas del teclado, tiene cronómetro y está organizada en **ocho capítulos**: La historia (el caso de Ana y las cifras de OSIPTEL), Objetivos (el objetivo general, los tres específicos y el marco teórico con diagramas), La matemática, Qué es ChipGuard, En acción, Por dentro (arquitectura y flujo), La tienda (la simulación jugable en dos rondas) y Cierre.");
  P(`**Atribución.** El código es propio y solo usa la biblioteca estándar de Java {repo}. Se incluyen la tipografía Geist bajo SIL Open Font License 1.1 {geist} y, en el paquete Windows, el entorno Eclipse Temurin. El logo institucional pertenece a la UPCH y se usa con fines académicos.`);

  // ======================= VII =======================
  H1("VII. RESULTADOS Y ANÁLISIS");
  H2("7.1 Pruebas automáticas y traza");
  const DESC = { PruebasFunciones: "Función, inyectiva, sobreyectiva, biyectiva e inversa", PruebasSimulador: "Registro por días con biometría, tope y auditoría", PruebasDiagnostico: "Por qué falla una propiedad", PruebasTraza: "Pasos del código que muestra la app", PruebasDetective: "Minijuego de líneas fraudulentas", PruebasTienda: "Tienda con y sin ChipGuard", PruebasDisenio: "Diseño: símbolos, textos y barra a cada resolución" };
  tabla("Resultado de las suites de pruebas", [["Suite", "Qué verifica", "Pruebas", "Fallos"],
    ...suites.map((s) => [s.nombre, DESC[s.nombre] || "", String(s.n), "0"]),
    ["**Total**", "", `**${TOTAL}**`, "**0**"]], [1.3, 2.6, 0.6, 0.5], { fte: "Salida de `./probar.sh` (elaboración propia)." });
  P(`La Tabla ${nTab + 1} es la traza real de \`Traza.esInyectiva\` para la sobreventa de un bus (Luis→A1, Rosa→A2, Jorge→A2): se detiene en el primer asiento cuya fibra tiene dos elementos, como predice el Corolario 1.`);
  const tTraza = tabla("Traza de esInyectiva para la sobreventa del bus", [["Paso", "Línea", "Variables", "Mensaje"],
    ...traza.map((t, i) => [String(i + 1), t.linea, t.vars, t.msg])], [0.5, 0.5, 1.8, 2.4], { fte: "Salida del programa (elaboración propia)." });
  H2("7.2 Simulación de la tienda");
  P(`El público atiende la tienda: llegan los seis clientes de la Tabla ${tFila} y hay que decidir si venderle a cada uno comparando su cara con la foto del DNI. La misma fila se juega sin y con ChipGuard bajo tres estrategias (Tabla ${nTab + 1}).`);
  const tSim = tabla("Resultado de la simulación según la estrategia de quien atiende", [["Estrategia", "ChipGuard", "En manos criminales", "Bloqueados por el dueño", "Ventas legítimas", "Clientes perdidos"],
    ...escenarios.map((e) => [e.estr, e.cg, String(e.crim), String(e.bloq), String(e.vend), String(e.perd)])], [1.6, 0.8, 0.9, 0.9, 0.8, 0.8], { fte: "Ejecución de `Tienda` (elaboración propia)." });
  P(`La fila clave es la primera: **aun decidiendo todo bien, dos chips terminan en manos criminales**, porque el chip extra de la doble huella no se ve desde el mostrador. El control en la tienda reduce los impostores, pero no elimina los chips de *D* creados por el propio vendedor. Con ChipGuard la decisión pasa a la única persona que sabe si el chip es suyo, que lo bloquea desde su celular (Figura ${nFig + 1}), y los chips en manos criminales son 0 en las tres estrategias.`);
  figura("4g-tienda-bloqueado.png", "Ronda 2: el dueño del DNI bloquea el chip desde su celular.", { ancho: 10 });
  H2("7.3 Contraste con los resultados matemáticos");
  tabla("Resultado teórico frente a la evidencia de ejecución", [["Resultado teórico", "Evidencia en la ejecución"],
    ["Corolario 1: inyectiva ⇔ toda fibra tiene ≤ 1 elemento", `La traza se detiene con llegadas(A2) = 2 (Tabla ${tTraza})`],
    ["Teorema 3: |A| > |B| ⇒ no inyectiva", "Con |A| = 4 y |B| = 3, «la 'a' recibe dos, NO es inyectiva» pasa"],
    ["Teorema 1: biyectiva ⇒ existe inversa biyectiva", "«la inversa de una biyectiva también es biyectiva» pasa"],
    ["Proposición 1: ningún chip de D se activa sin permiso del titular", `Con ChipGuard, 0 en manos criminales (Tabla ${tSim})`],
  ], [2, 2.4]);
  H2("7.4 Complejidad y escalabilidad");
  P(`Se midió \`esBiyectiva\` sobre la identidad con |*A*| = |*B*| = *n* (mejor de cinco ejecuciones tras un calentamiento, en ${CPU} con Java ${JAVA}).`);
  tabla("Tiempo de esBiyectiva según el tamaño de los conjuntos", [["n", "Tiempo (ms)", "Razón frente a n/2"],
    ...tiempos.map((t, i) => [String(t.n), t.ms.toFixed(1).replace(".", ","), i ? (t.ms / tiempos[i - 1].ms).toFixed(1).replace(".", ",") : "—"])], [1, 1, 1.2], { fte: "Medición propia." });
  const razones = tiempos.slice(1).map((t, i) => t.ms / tiempos[i].ms);
  const media = Math.exp(razones.reduce((a, r) => a + Math.log(r), 0) / razones.length);
  const coma = (x, dec = 1) => x.toFixed(dec).replace(".", ",");
  const minutos = Math.round((309454 / tiempos[0].n) ** 2 * tiempos[0].ms / 60000);
  P(`Al duplicar *n*, el tiempo se multiplica en promedio (media geométrica) por ${coma(media)}, cercano al factor 4 de un algoritmo cuadrático. Extrapolando desde *n* = ${tiempos[0].n}, verificar las 309 454 líneas inconsistentes tomaría unos ${minutos} minutos. El servidor de ChipGuard no recorrería el registro entero: le basta consultar |*f*⁻¹(*p*)| de una persona por solicitud, casi constante con un índice por DNI. El verificador cuadrático sirve para enseñar, no para el registro nacional.`);

  // ======================= VIII =======================
  H1("VIII. DISCUSIÓN");
  P("**Interpretación.** El modelo desplaza la pregunta: el registro no falla por ser o no inyectivo o sobreyectivo, sino porque *f* y *u* se separan en *D*. Las medidas actuales atacan los síntomas y llegan tarde; la Proposición 1 muestra que la trazabilidad se restituye con una biyección entre chips y autorizaciones del titular, y la simulación, que esa propiedad elimina el fraude que el mostrador no ve.");
  P("**Limitaciones y posibles errores.**");
  lista([
    "La simulación usa una fila fija de seis clientes: ilustra el mecanismo, no estima tasas reales de fraude.",
    "Si roban el celular del titular, el atacante podría aprobar; exigir la huella en el celular reduce ese riesgo, no lo elimina. Un vendedor podría engañar al cliente para aprobar dos veces; el tope del Corolario 2 y el historial visible lo acotan.",
    "Primer chip con vendedor cómplice: si activa el chip extra antes que el del cliente, la llamada suena en otro celular; el cliente lo nota, pero solo si lo comprueba antes de irse.",
    "ChipGuard requiere una norma de OSIPTEL que obligue a las operadoras a consultarlo; varias cifras vienen de prensa sin fecha y deben contrastarse con informes oficiales; los tiempos dependen del equipo.",
  ]);
  P(`**Comparación con la bibliografía y mejoras.** Rosen {rosen} y Epp {epp} usan el palomar para contar; aquí es una regla de diseño que vuelve imposible el chip *k* + 1. Frente a las medidas de OSIPTEL {osiptel116}, ChipGuard no las reemplaza: pasa de la detección posterior a la autorización previa. Como mejoras se proponen el conteo en una pasada, O(*n*), una simulación con miles de solicitudes aleatorias y un prototipo del servidor con notificaciones reales.`);

  // ======================= IX =======================
  H1("IX. CONCLUSIONES");
  lista([
    "**OE1.** El registro es una función *f* : *C* → *P* cuyo defecto no es de tipo, sino que no coincide con el uso real *u* en el conjunto *D* de chips fraudulentos. Se demostró que solo una biyección admite inversa (Teorema 1) y que el tope de *k* líneas por DNI impide el chip *k* + 1 (Corolario 2).",
    "**OE2.** ChipGuard implementa la Proposición 1: un chip se activa solo con una autorización de un solo uso de su titular, lo que forma una biyección entre chips y autorizaciones. Agrega dos piezas a las cuatro existentes y cubre los casos sin respuesta, sin smartphone, sin celular y el del primer chip, resuelto por inducción (Proposición 2).",
    `**OE3.** El verificador y la simulación en Java pasan ${TOTAL} pruebas automáticas que coinciden con los teoremas. Aun con decisiones perfectas en el mostrador, 2 chips terminan en manos criminales; con ChipGuard, 0. La medición confirma el costo cuadrático del verificador.`,
    "**Pregunta de investigación.** La propiedad necesaria es que cada chip activo esté en correspondencia biyectiva con una autorización de su titular; en la simulación, ChipGuard la garantiza, con las limitaciones señaladas.",
  ]);

  // ======================= X =======================
  H1("X. REFERENCIAS BIBLIOGRÁFICAS");
  for (const [i, k] of orden.entries())
    C.push(new d.Paragraph({ alignment: d.AlignmentType.LEFT, spacing: { after: 80, line: 260 }, indent: { left: cm(1), hanging: cm(1) },
      children: [new d.TextRun({ text: `[${i + 1}]\t`, size: 20 }), ...runs(REFS[k], { size: 20 })], tabStops: [{ type: d.TabStopType.LEFT, position: cm(1) }] }));
}

function anexos() {
  H1("XI. ANEXOS", { salto: false });
  H2("Anexo A. Repositorio, manual mínimo y pruebas");
  P("**Repositorio:** https://github.com/jordymartel-hash/ChipGuard (código completo, pruebas, informe y descargas). **Windows:** bajar `ChipGuard-Windows.zip` de la sección de descargas, descomprimir y abrir `Abrir ChipGuard.bat` (Java incluido; si aparece «Windows protegió su PC», elegir «Más información» y «Ejecutar de todas formas»). **Otro sistema con Java 17+:** `java -jar ChipGuard.jar`. **Controles:** flechas izquierda y derecha para avanzar o retroceder; clic en la barra superior para saltar de capítulo; clic en el cronómetro para iniciarlo o pausarlo y clic derecho para reiniciarlo. **Pruebas:** `./probar.sh` imprime cada una de las " + TOTAL + " pruebas con su nombre en lenguaje claro.");
  H2("Anexo B. Distribución de tareas y bitácora");
  tabla("Reparto de la exposición (16 minutos, 4 integrantes)", [
    ["Integrante", "Capítulos", "Tiempo", "Idea que debe quedar"],
    ["Tangoa Alva, Richard José Hernan", "La historia · Objetivos", "4:00", "A Ana le sacaron un chip con su segunda huella, y la policía llegó a ella; nuestra misión es modelarlo y arreglarlo."],
    ["Sanga Limache, Angeles Jemina", "La matemática", "4:00", "El registro es una función; el problema es que registro ≠ uso real."],
    ["Martel Vasquez, Jordy Joel", "Qué es ChipGuard · En acción · Por dentro", "4:30", "Ningún chip se activa sin el permiso del dueño del DNI."],
    ["Vidalon Ampudia, Eduardo Josias", "La tienda (con el público) · Cierre", "3:30", "Aun decidiendo bien, sin ChipGuard 2 chips llegan a criminales; con ChipGuard, 0."],
  ], [1.4, 1.3, 0.5, 2]);
  tabla("Bitácora de versiones del repositorio", [
    ["Fecha", "Versión", "Cambio principal"],
    ["2026-11-07", "v1 a v3", "Verificador de funciones, simulador, primeras pruebas; nombre ChipGuard"],
    ["2026-11-08", "v6 a v1.1", "Exposición por capítulos; juego «La tienda»; publicación en GitHub y paquete Windows"],
    ["2026-11-12", "v1.2", "Caso del primer chip; informe con el formato del curso y referencias IEEE"],
    ["2026-11-15", "v1.3", "Capítulo Objetivos con marco teórico; letra para proyector; nuevo reparto"],
    ["2026-11-20", "v1.3.1", "Objetivos de la app alineados a OE1–OE3; informe compacto"],
  ], [0.8, 0.8, 3], { fte: "Historial de commits del repositorio {repo}." });
  H2("Anexo C. Registro de evaluación adjunto");
  C.push(new d.Paragraph({ alignment: d.AlignmentType.CENTER, spacing: { after: 120 }, children: [new d.TextRun({ text: "REGISTRO DE EVALUACIÓN ADJUNTA AL INFORME DE INVESTIGACIÓN", bold: true })] }));
  P(`**Equipo:** ChipGuard${GRUPO ? ` (Grupo ${GRUPO})` : ""}`, { alignment: d.AlignmentType.LEFT, spacing: { after: 40 } });
  P("**Tema:** ChipGuard: el registro de líneas móviles modelado con funciones", { alignment: d.AlignmentType.LEFT, spacing: { after: 40 } });
  P("**Integrantes evaluados:** 1) Martel Vasquez, Jordy Joel; 2) Sanga Limache, Angeles Jemina; 3) Tangoa Alva, Richard José Hernan; 4) Vidalon Ampudia, Eduardo Josias.", { alignment: d.AlignmentType.LEFT });
  const crit = [["Rigor de la investigación y marco teórico", 4], ["Diseño, programación y simulación", 4], ["Estructura y calidad del informe", 2], ["Citación, referencias e integridad académica", 1], ["Exposición oral y dominio conceptual", 3], ["Vestimenta formal y presentación profesional", 2], ["Trabajo en equipo y contribución verificable", 1], ["Defensa, preguntas y pensamiento crítico", 3]];
  nTab++;
  entradas.push({ tipo: "tab", texto: `Tabla ${nTab}. Registro de evaluación (para uso del docente)`, clave: `Tabla ${nTab}. Registro de eval` });
  C.push(new d.Paragraph({ keepNext: true, spacing: { before: 120, after: 80, line: 240 }, children: [new d.TextRun({ text: `Tabla ${nTab}. `, bold: true, size: 20 }), new d.TextRun({ text: "Registro de evaluación (para uso del docente)", italics: true, size: 20 })] }));
  const an = [Math.floor(ANCHO_TEXTO * 0.42), Math.floor(ANCHO_TEXTO * 0.1), Math.floor(ANCHO_TEXTO * 0.28)];
  an.push(ANCHO_TEXTO - an.reduce((a, b) => a + b, 0));
  C.push(new d.Table({ width: { size: ANCHO_TEXTO, type: d.WidthType.DXA }, columnWidths: an,
    borders: { top: borde, bottom: borde, left: borde, right: borde, insideHorizontal: borde, insideVertical: borde },
    rows: [["Criterio", "Peso", "Observaciones", "Puntaje obtenido"], ...crit.map(([c, p]) => [c, String(p), "", ""]), ["**TOTAL**", "**20**", "", ""]]
      .map((f, i) => new d.TableRow({ tableHeader: i === 0, height: { value: i ? 520 : 300, rule: d.HeightRule.ATLEAST }, children: f.map((t, j) => celda(t, an[j], i === 0)) })) }));
  fuente("Rúbrica de evaluación del curso Estructuras Discretas I, 2026-II.");
}

// ============================ DOCUMENTO ============================
const ROMANOS = ["", "i", "ii", "iii", "iv", "v", "vi", "vii", "viii", "ix", "x", "xi", "xii", "xiii", "xiv", "xv"];

function caratula() {
  const linea = (t, size, bold = true, after = 0, extra = {}) => new d.Paragraph({ alignment: d.AlignmentType.CENTER, spacing: { after, line: 276 }, ...extra,
    children: [new d.TextRun({ text: t, size, bold })] });
  return [
    linea("UNIVERSIDAD PERUANA CAYETANO HEREDIA", 40, true, 60),
    linea("FACULTAD DE CIENCIAS E INGENIERÍA", 32, true, 160),
    new d.Paragraph({ alignment: d.AlignmentType.CENTER, spacing: { after: 160 }, children: [new d.ImageRun({ type: "jpg", data: fs.readFileSync(path.join(__dirname, "logo-formato.jpg")), transformation: { width: 175, height: 175 } })] }),
    linea("ESCUELA PROFESIONAL DE INGENIERÍA INFORMÁTICA", 32, true, 60),
    linea("ESTRUCTURAS DISCRETAS I", 32, true, 360),
    linea("“ChipGuard: modelado con funciones del registro de líneas móviles a nombre de terceros en el Perú”", 28, true, 360, { indent: { left: cm(0.8), right: cm(0.8) } }),
    linea("AUTORES:", 24, true, 60),
    linea("Martel Vasquez, Jordy Joel (0009-0008-6410-8615)", 24, false),
    linea("Sanga Limache, Angeles Jemina (0009-0003-7669-4168)", 24, false),
    linea("Tangoa Alva, Richard José Hernan (0009-0006-4441-0185)", 24, false),
    linea("Vidalon Ampudia, Eduardo Josias (0009-0008-2704-013X)", 24, false, 240),
    linea(`EQUIPO: ChipGuard    ·    GRUPO: ${GRUPO || "____"}`, 24, true, 240),
    linea("ASESOR:", 24, true, 60),
    linea("Dr. Ing. CIP. Illesca Cangalaya, Omar Felix", 24, false),
    linea("ORCID: 0000-0002-0144-6878", 24, false, 360),
    linea("SAN MARTÍN DE PORRES – PERÚ", 24, true),
    linea("2026", 32, true, 120),
    linea(`Fecha: ${FECHA}`, 24, false),
  ];
}

function indices(paginas) {
  const out = [];
  const titulo = (t, salto) => out.push(new d.Paragraph({ heading: d.HeadingLevel.HEADING_1, pageBreakBefore: salto, children: [new d.TextRun(t)] }));
  const fila = (texto, pag, sangria, bold) => out.push(new d.Paragraph({ alignment: d.AlignmentType.LEFT, spacing: { line: 276, after: 60 },
    indent: { left: cm(sangria + 0.9), hanging: cm(0.9), right: cm(0.9) },
    tabStops: [{ type: d.TabStopType.RIGHT, position: ANCHO_TEXTO, leader: "dot" }],
    children: [new d.TextRun({ text: texto, bold, size: 22 }), new d.TextRun({ size: 22, children: [new d.Tab(), pag] })] }));
  const pg = (e) => (paginas && paginas.get(e.texto)) || "00";
  titulo("ÍNDICE", false);
  for (const e of entradas.filter((x) => x.tipo === "h1" || x.tipo === "h2")) fila(e.texto, pg(e), e.tipo === "h2" ? 0.8 : 0, e.tipo === "h1");
  titulo("ÍNDICE DE TABLAS", true);
  for (const e of entradas.filter((x) => x.tipo === "tab")) fila(e.texto, pg(e), 0, false);
  titulo("ÍNDICE DE FIGURAS", false);   // comparte página con el de tablas
  for (const e of entradas.filter((x) => x.tipo === "fig")) fila(e.texto, pg(e), 0, false);
  return out;
}

function pie(conNumero) {
  return new d.Footer({ children: [new d.Paragraph({ spacing: { line: 240 }, children: [
    new d.TextRun({ size: 18, color: "595959", children: [new d.Tab(), PIE] }),
    ...(conNumero ? [new d.TextRun({ size: 22, children: [new d.Tab(), d.PageNumber.CURRENT] })] : []),
  ], tabStops: [{ type: d.TabStopType.CENTER, position: Math.round(ANCHO_TEXTO / 2) }, { type: d.TabStopType.RIGHT, position: ANCHO_TEXTO }] })] });
}

function construir(paginas, datos) {
  reiniciar();
  TOTAL = datos.cuerpo[1].reduce((a, s) => a + s.n, 0);
  resumen();
  const bloqueResumen = C; C = [];
  cuerpo(...datos.cuerpo);
  const bloqueCuerpo = C; C = [];
  anexos(datos.pruebas);
  const bloqueAnexos = C;
  const idx = indices(paginas);
  const pagina = { size: { width: 11906, height: 16838 }, margin: { top: cm(2.5), bottom: cm(3), left: cm(3), right: cm(2.5), footer: cm(1.2) } };
  return new d.Document({
    creator: "Equipo ChipGuard", title: "ChipGuard: modelado con funciones del registro de líneas móviles",
    styles: {
      default: { document: { run: { font: "Arial", size: 24 }, paragraph: { spacing: { line: 360 } } } },
      paragraphStyles: [
        { id: "Heading1", name: "Heading 1", basedOn: "Normal", next: "Normal", quickFormat: true, run: { font: "Arial", size: 24, bold: true, color: "000000" }, paragraph: { alignment: d.AlignmentType.CENTER, spacing: { before: 480, after: 240, line: 360 }, keepNext: true, outlineLevel: 0 } },
        { id: "Heading2", name: "Heading 2", basedOn: "Normal", next: "Normal", quickFormat: true, run: { font: "Arial", size: 24, bold: true, color: "000000" }, paragraph: { spacing: { before: 240, after: 120, line: 360 }, keepNext: true, outlineLevel: 1 } },
        { id: "Heading3", name: "Heading 3", basedOn: "Normal", next: "Normal", quickFormat: true, run: { font: "Arial", size: 24, bold: true, italics: true }, paragraph: { spacing: { before: 120, after: 80 }, keepNext: true, outlineLevel: 2 } },
      ],
    },
    numbering: { config: [
      { reference: "vinetas", levels: [{ level: 0, format: d.LevelFormat.BULLET, text: "•", alignment: d.AlignmentType.LEFT, style: { paragraph: { indent: { left: cm(1), hanging: cm(0.5) } } } }] },
      { reference: "numeros", levels: [{ level: 0, format: d.LevelFormat.DECIMAL, text: "%1.", alignment: d.AlignmentType.LEFT, style: { paragraph: { indent: { left: cm(1), hanging: cm(0.6) } } } }] },
    ] },
    sections: [
      { properties: { page: pagina }, footers: { default: new d.Footer({ children: [new d.Paragraph("")] }) }, children: caratula() },
      { properties: { page: { ...pagina, pageNumbers: { start: 2, formatType: d.NumberFormat.LOWER_ROMAN } } }, footers: { default: pie(true) },
        children: [...idx, new d.Paragraph({ pageBreakBefore: true, children: [] }), ...bloqueResumen] },
      { properties: { page: { ...pagina, pageNumbers: { start: 1, formatType: d.NumberFormat.DECIMAL } } }, footers: { default: pie(true) }, children: bloqueCuerpo },
      { properties: { page: pagina }, footers: { default: pie(false) }, children: bloqueAnexos },
    ],
  });
}

// ---------- datos reales del proyecto ----------
function datosReales() {
  const salida = execSync("bash probar.sh", { cwd: RAIZ, encoding: "utf8", env: ENV });
  const suites = [];
  const cmds = fs.readFileSync(path.join(RAIZ, "probar.sh"), "utf8").match(/simuchip\.(?:ui\.)?Pruebas\w+/g).map((s) => s.split(".").pop());
  function cmdsIdx(n) { const i = cmds.indexOf(n); if (i < 0) throw new Error("Falta " + n); return i; }
  [...salida.matchAll(/Resultado: (\d+) pruebas OK, (\d+) fallos/g)].forEach((m, i) => {
    if (m[2] !== "0") throw new Error("Hay pruebas fallando");
    suites.push({ nombre: cmds[i], n: +m[1] });
  });
  const bloques = salida.split(/(?=Resultado: )/);   // shortcut: depende del orden de probar.sh (Funciones 1.ª, Tienda 6.ª)
  const lineasDe = (txt) => txt.split("\n").filter((l) => /^\s*(▶|✔)/.test(l));
  const porSuite = []; let acum = [];
  for (const l of salida.split("\n")) { if (/^\s*(▶|✔)/.test(l)) acum.push(l); if (l.startsWith("Resultado:")) { porSuite.push(acum); acum = []; } }
  const pruebas = [...porSuite[cmdsIdx("PruebasFunciones")], ...porSuite[cmdsIdx("PruebasTienda")]].join("\n");
  const exp = execSync(`javac -cp out -d out/experimento informe/Experimento.java && java -cp out${path.delimiter}out/experimento Experimento`, { cwd: RAIZ, encoding: "utf8", env: ENV });
  const traza = exp.split("\n").filter((l) => l.startsWith("TRAZA|")).map((l) => { const [, linea, vars, msg] = l.split("|"); return { linea, vars: vars.replace(/\s{2,}·\s{2,}/g, " · "), msg }; });
  const tiempos = exp.split("\n").filter((l) => l.startsWith("TIEMPO|")).map((l) => { const [, n, ms] = l.split("|"); return { n: +n, ms: +ms.replace(",", ".") }; });
  const escenarios = exp.split("\n").filter((l) => l.startsWith("ESC|")).map((l) => { const [, estr, cg, crim, bloq, vend, perd] = l.split("|"); return { estr, cg, crim, bloq, vend, perd }; });
  if (!traza.length || tiempos.length < 4 || escenarios.length !== 6) throw new Error("Experimento incompleto");
  CPU = os.cpus()[0].model.trim();
  JAVA = execSync("java -version 2>&1", { encoding: "utf8", env: ENV }).match(/version "([\d.]+)/)[1];
  return { pruebas, cuerpo: [tiempos, suites, traza, escenarios] };
}

// ---------- paginación: qué página real ocupa cada entrada ----------
function mapearPaginas(pdf) {
  const texto = execSync(`${PDFTOTEXT} "${pdf}" -`, { encoding: "utf8", maxBuffer: 64 << 20 });
  const pags = texto.split("\f").map((t) => t.replace(/\s+/g, ""));
  const norm = (s) => s.replace(/\s+/g, "");
  const iResumen = pags.findIndex((t) => t.includes("Palabrasclave:"));
  if (iResumen < 0) throw new Error("No encuentro el resumen en el PDF");
  const iCuerpo = pags.findIndex((t, i) => i > iResumen && t.includes(norm("I. INTRODUCCIÓN")));
  const etiqueta = (i) => (i < iCuerpo ? ROMANOS[i + 1] : String(i - iCuerpo + 1));
  const mapa = new Map();
  let k = iResumen;
  for (const e of entradas) {
    const clave = norm(e.clave || e.texto);
    let j = k;
    while (j < pags.length && !pags[j].includes(clave)) j++;
    if (j === pags.length) throw new Error("No encuentro en el PDF: " + e.texto);
    mapa.set(e.texto, etiqueta(j));
    if (e.tipo === "h1" || e.tipo === "h2") k = j;
  }
  return { mapa, total: pags.length - 1, iCuerpo };
}

async function main() {
  const datos = datosReales();
  const pdf = SALIDA.replace(/\.docx$/, ".pdf");
  const convertir = async (doc) => {
    fs.writeFileSync(SALIDA, await d.Packer.toBuffer(doc));
    execSync(`${SOFFICE} --headless --convert-to pdf --outdir "${__dirname}" "${SALIDA}"`, { stdio: "ignore" });
  };
  await convertir(construir(null, datos));
  let { mapa } = mapearPaginas(pdf);
  await convertir(construir(mapa, datos));
  const r = mapearPaginas(pdf);                      // segunda lectura: los índices no deben haber movido nada
  const cambios = entradas.filter((e) => r.mapa.get(e.texto) !== mapa.get(e.texto));
  if (cambios.length) { mapa = r.mapa; await convertir(construir(mapa, datos)); console.log(`Reajuste: ${cambios.length} entradas cambiaron de página.`); }
  console.log(`Listo: ${SALIDA} y PDF (${r.total} páginas, cuerpo desde la ${r.iCuerpo + 1}; figuras ${nFig}, tablas ${nTab}, referencias ${orden.length}).`);
}
main().catch((e) => { console.error(e); process.exit(1); });
