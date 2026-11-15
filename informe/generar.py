"""Genera el informe final de ChipGuard en PDF (A4) con Playwright, con el formato UPCH de los trabajos grupales.

Uso:  python3 informe/generar.py
Sale: ~/Descargas/Informe-ChipGuard.pdf  (y una copia en docs/ y en ~/claude/entregas/expo-funciones/)

La portada se genera aparte (sin encabezado) y se une al cuerpo con pdfunite (poppler).
Antes conviene regenerar las capturas:
  ./probar.sh && java -Djava.awt.headless=true -cp out:recursos simuchip.Capturas capturas
"""
import base64
import html
import shutil
import subprocess
import tempfile
from pathlib import Path

from playwright.sync_api import sync_playwright

AQUI = Path(__file__).resolve().parent
RAIZ = AQUI.parent
CAPTURAS = RAIZ / "capturas"
SALIDA = Path.home() / "Descargas/Informe-ChipGuard.pdf"
COPIAS = [RAIZ / "docs/Informe-ChipGuard.pdf", Path.home() / "claude/entregas/expo-funciones/Informe-ChipGuard.pdf"]

LOGO = base64.b64encode((RAIZ / "recursos/imagenes/logo-upch.png").read_bytes()).decode()
TITULO = "ChipGuard: ¿a quién le pertenece tu chip?"
SUBTITULO_CORTO = "Proyecto grupal de Estructuras Discretas I"
REPO = "https://github.com/jordymartel-hash/ChipGuard"

DOCENTE = ("Dr. Ing. CIP. Illesca Cangalaya, Omar Felix", "0000-0002-0144-6878")
INTEGRANTES = [
    ("Martel Vasquez, Jordy Joel", "0009-0008-6410-8615"),
    ("Sanga Limache, Angeles Jemina", "0009-0003-7669-4168"),
    ("Tangoa Alva, Richard José Hernan", "0009-0006-4441-0185"),
    ("Vidalon Ampudia, Eduardo Josias", "0009-0008-2704-013X"),
]

GRANATE = "#A90A2E"
AMARILLO = "#F5B400"

_figuras = 0
_tablas = 0
_codigos = 0
FIGURAS = []  # nombre de la captura de cada figura, en orden
PAGINAR = (AQUI / "paginar.js").read_text(encoding="utf-8")
ALTO_PAGINA_PX = (29.7 - 3.1 - 2.2) / 2.54 * 96 - 12  # alto útil de la hoja en px CSS, con margen de seguridad


def imagen(nombre, pie, ancho="78%"):
    global _figuras
    _figuras += 1
    FIGURAS.append(nombre)
    datos = base64.b64encode((CAPTURAS / nombre).read_bytes()).decode()
    return (f'<figure data-n="{nombre}"><img src="data:image/png;base64,{datos}" style="width:{ancho}">'
            f'<figcaption>Figura {_figuras}. {pie}</figcaption></figure>')


def galeria(items, columnas=3):
    """Anexo de capturas en filas de tres; cada una con su número de figura. El paginador quita las filas que no
    caben en la última página, así que se puede pasar de más."""
    global _figuras
    filas = []
    for i in range(0, len(items), columnas):
        celdas = []
        for nombre, pie in items[i:i + columnas]:
            _figuras += 1
            datos = base64.b64encode((CAPTURAS / nombre).read_bytes()).decode()
            celdas.append(f'<figure><img src="data:image/png;base64,{datos}" style="width:100%">'
                          f'<figcaption>Figura {_figuras}. {pie}</figcaption></figure>')
        filas.append(f'<div class="galeria">{"".join(celdas)}</div>')
    return "".join(filas)


def tabla(titulo, filas):
    """filas[0] es la cabecera."""
    global _tablas
    _tablas += 1
    cab = "".join(f"<th>{c}</th>" for c in filas[0])
    cuerpo = "".join("<tr>" + "".join(f"<td>{c}</td>" for c in f) + "</tr>" for f in filas[1:])
    return (f'<p class="cap tcap">Tabla {_tablas}. {titulo}</p>'
            f'<table><thead><tr>{cab}</tr></thead><tbody>{cuerpo}</tbody></table>')


def fragmento(ruta, desde, titulo):
    """Copia literal del código real: desde la línea que contiene "desde" hasta el cierre "    }" del método."""
    global _codigos
    _codigos += 1
    lineas = (RAIZ / ruta).read_text(encoding="utf-8").splitlines()
    i = next(k for k, l in enumerate(lineas) if desde in l)
    j = next(k for k in range(i + 1, len(lineas)) if lineas[k].rstrip() == "    }")
    texto = "\n".join(l[4:] for l in lineas[i:j + 1])  # sin la sangría de la clase
    # Escapar: los genéricos de Java (<A, B>) el navegador los leería como etiquetas HTML.
    return (f'<div class="bloque"><p class="cap">Código {_codigos}. {titulo}</p>'
            f'<pre>{html.escape(texto)}</pre></div>')


CSS = f"""
@page {{ size: A4; margin: 3.1cm 2.2cm 2.2cm 2.2cm; }}
body {{ font-family: 'Liberation Sans', Arial, Helvetica, sans-serif; font-size: 11pt; line-height: 1.5; color: #222; margin: 0; }}
h2 {{ font-size: 17pt; color: {GRANATE}; margin: 18pt 0 10pt; line-height: 1.25; break-after: avoid; }}
h3 {{ font-size: 12pt; color: {GRANATE}; margin: 14pt 0 6pt; break-after: avoid; }}
h2:first-child {{ margin-top: 0; }}
p {{ margin: 0 0 9pt; text-align: justify; }}
ul, ol {{ margin: 0 0 9pt 18pt; padding: 0; }}
li {{ margin-bottom: 3pt; text-align: justify; }}
.bloque {{ break-inside: avoid; margin: 6pt 0 12pt; }}
.cap {{ font-style: italic; color: #666; font-size: 9pt; margin: 0 0 4pt; text-align: left; }}
table {{ width: 100%; border-collapse: collapse; font-size: 10pt; margin: 0 0 12pt; }}
.tcap {{ margin-top: 6pt; break-after: avoid; }}
tr {{ break-inside: avoid; }}
th {{ background: {GRANATE}; color: #fff; text-align: left; padding: 6pt 8pt; font-weight: bold; }}
td {{ border-bottom: 1px solid #D6D0D2; padding: 5pt 8pt; vertical-align: top; }}
tr:nth-child(odd) td {{ background: #F6F3F4; }}
figure {{ margin: 8pt 0 12pt; break-inside: avoid; text-align: center; }}
figure img {{ border: 1px solid #E2DDDF; }}
figcaption {{ font-style: italic; color: #666; font-size: 9pt; margin-top: 4pt; }}
pre {{ font-family: 'Liberation Mono', 'Courier New', Courier, monospace; font-size: 8.4pt; line-height: 1.4;
       margin: 0 0 0 0.6cm; white-space: pre-wrap; color: #222; }}
.formula {{ text-align: center; font-size: 11.5pt; margin: 4pt 0 10pt; }}
.ref {{ text-align: left; padding-left: 1.27cm; text-indent: -1.27cm; }}
a {{ color: #8C0826; word-break: break-all; }}
.galeria {{ display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 0 10pt; }}
.galeria figcaption {{ font-size: 8pt; }}
.galeria figure {{ margin: 4pt 0 6pt; }}
@media screen {{ body {{ width: 16.6cm; }} }}
p, li {{ orphans: 1; widows: 1; }}
.reservada {{ break-after: page; height: 1px; }}
"""

PORTADA_CSS = f"""
@page {{ size: A4; margin: 0; }}
html, body {{ margin: 0; height: 297mm; font-family: 'Liberation Sans', Arial, Helvetica, sans-serif; color: #222; }}
.barra {{ position: fixed; left: 0; top: 0; bottom: 0; width: 1.15cm; background: {GRANATE}; }}
.barra2 {{ position: fixed; left: 1.15cm; top: 0; bottom: 0; width: 0.22cm; background: {AMARILLO}; }}
.hoja {{ position: absolute; left: 1.37cm; right: 0; top: 0; bottom: 0; text-align: center; }}
.logo {{ width: 10.5cm; margin-top: 2.6cm; }}
h1 {{ color: {GRANATE}; font-size: 24pt; line-height: 1.25; margin: 3.1cm 1.8cm 0.5cm; text-wrap: balance; }}
.sub {{ text-wrap: balance; }}
.sub {{ color: #777; font-size: 12.5pt; margin: 0 2.2cm; line-height: 1.4; }}
.rot {{ color: {GRANATE}; font-weight: bold; font-size: 11pt; margin: 1.6cm 0 0.35cm; letter-spacing: .3px; }}
.nom {{ font-size: 11.5pt; margin: 0 0 3pt; }}
.orcid {{ color: #777; font-size: 9pt; margin: 0 0 7pt; }}
.pie {{ position: absolute; left: 0; right: 0; bottom: 2.2cm; font-size: 11.5pt; line-height: 1.5; }}
"""


def portada():
    gente = "".join(f'<p class="nom">{n}</p><p class="orcid">ORCID: {o}</p>' for n, o in INTEGRANTES)
    return f"""<!doctype html><html lang='es'><head><meta charset='utf-8'><style>{PORTADA_CSS}</style></head><body>
<div class="barra"></div><div class="barra2"></div>
<div class="hoja">
  <img class="logo" src="data:image/png;base64,{LOGO}">
  <h1>ChipGuard: ¿a quién le pertenece tu chip?</h1>
  <p class="sub">Funciones inyectivas, sobreyectivas y biyectivas aplicadas al registro de líneas móviles en el Perú</p>
  <p class="rot">DOCENTE</p>
  <p class="nom">{DOCENTE[0]}</p><p class="orcid">ORCID: {DOCENTE[1]}</p>
  <p class="rot" style="margin-top:0.7cm">INTEGRANTES</p>
  {gente}
  <div class="pie">Estructuras Discretas I<br>Lima, Perú<br>2026</div>
</div></body></html>"""


ENCABEZADO = f"""
<div style="width:100%; margin:0 2.2cm; font-family:'Liberation Sans',Arial,sans-serif; -webkit-print-color-adjust:exact;">
  <div style="display:flex; justify-content:space-between; align-items:flex-end; padding-bottom:6px;">
    <img src="data:image/png;base64,{LOGO}" style="height:30px;">
    <div style="text-align:right; font-size:8.5px; color:#777; line-height:1.35;">{TITULO}<br>{SUBTITULO_CORTO}</div>
  </div>
  <div style="border-top:1.6px solid {GRANATE}; margin-bottom:1.5px;"></div>
  <div style="border-top:1.2px solid {AMARILLO};"></div>
</div>"""

PIE = """<div style="width:100%; margin:0 2.2cm; text-align:right; font-size:9px; color:#777;
font-family:'Liberation Sans',Arial,sans-serif;"><span class="pageNumber"></span></div>"""


def ref(autor, anio, titulo, medio, url=""):
    medio_txt = f" {medio}." if medio else ""
    enlace = f' <a href="{url}">{html.escape(url)}</a>' if url else ""
    return f'<p class="ref">{autor} ({anio}). <i>{titulo}</i>.{medio_txt}{enlace}</p>'


REFERENCIAS = "\n".join([
    ref("América TV.", "s. f.", "Así operaba banda criminal que vendía chips activados a nombre de terceras personas",
        "", "https://www.americatv.com.pe/noticias/amp/actualidad/asi-operaba-banda-criminal-que-vendia-chips-activados-nombres-terceras-personas-n492832"),
    ref("Bloomberg Línea.", "s. f.", "¿Pueden clonar su huella digital y robar su dinero? Así operaban «Los Huelleros del Norte» en Perú",
        "", "https://www.bloomberglinea.com/latinoamerica/peru/pueden-clonar-su-huella-digital-y-robar-su-dinero-asi-operaban-los-huelleros-del-norte-en-peru/"),
    ref("Canal N.", "2025", "Osiptel detecta 200 mil usuarios con más de 10 líneas móviles", "",
        "https://canaln.pe/actualidad/osiptel-detecta-200-mil-usuarios-mas-10-lineas-moviles-n488632"),
    ref("Congreso de la República del Perú.", "2025", "OSIPTEL bloquea más de 650 mil celulares y alerta crecimiento del uso delictivo de líneas móviles",
        "", "https://comunicaciones.congreso.gob.pe/damos-cuenta/osiptel-bloquea-mas-de-650-mil-celulares-y-alerta-crecimiento-del-uso-delictivo-de-lineas-moviles/"),
    ref("DPL News.", "2025", "Perú: Osiptel dispuso la baja de más de 102 mil líneas con registros inconsistentes durante el estado de emergencia",
        "", "https://dplnews.com/peru-osiptel-dispuso-baja-102-mil-lineas-registros-inconsistentes-estado-de-emergencia/"),
    ref("DPL News.", "s. f.", "Perú: Osiptel refuerza acciones contra la venta ambulatoria de chips con operativo preventivo en Lima Norte",
        "", "https://dplnews.com/peru-osiptel-refuerza-acciones-contra-venta-ambulatoria-chips-operativo-preventivo-lima-norte/"),
    ref("El Peruano.", "2025", "¿Puedes bloquear nuevas líneas a tu nombre? Osiptel habilita restricción contra contrataciones no autorizadas",
        "", "https://elperuano.pe/noticia/288845-puedes-bloquear-nuevas-lineas-a-tu-nombre-osiptel-habilita-restriccion-contra-contrataciones-no-autorizadas"),
    ref("El Peruano.", "s. f.", "Osiptel refuerza tu seguridad: detecta y reporta líneas móviles no contratadas en Checa tus líneas",
        "", "https://elperuano.pe/noticia/288143-osiptel-refuerza-tu-seguridad-detecta-y-reporta-lineas-moviles-no-contratadas-en-checa-tus-lineas"),
    ref("Organismo Supervisor de Inversión Privada en Telecomunicaciones.", "2025",
        "Resolución de Consejo Directivo N.° 000116-2025-CD/OSIPTEL", "",
        "https://www.osiptel.gob.pe/media/xpxl0map/resol116-2025-cd.pdf"),
    ref("Rosen, K. H.", "2019", "Discrete mathematics and its applications", "(8.ª ed.). McGraw-Hill Education"),
])


def cuerpo():
    return f"""
<div class="reservada"></div>

<h2>1. Introducción</h2>
<p>En el Perú, cada línea móvil se registra a nombre de una persona mediante su DNI. Ese registro debería permitir
que, ante un delito cometido desde un número de teléfono, las autoridades lleguen a la persona responsable. En la
práctica no siempre ocurre así: miles de líneas están registradas a nombre de personas que nunca las contrataron, y
esas líneas se usan para extorsionar y estafar. Cuando la policía rastrea el número, llega a una persona inocente.</p>
<p>Este trabajo analiza el problema con las herramientas del curso. El registro de líneas es, en términos
matemáticos, una <b>función</b> que asigna a cada chip un dueño. Estudiar si esa función es inyectiva, sobreyectiva o
biyectiva, y compararla con la función que asigna a cada chip quien realmente lo usa, permite explicar con precisión
dónde está la falla y qué propiedad debería cumplir un sistema que la corrija.</p>
<p>A partir de ese modelo proponemos <b>ChipGuard</b>, un servicio que impide que un chip se active con un DNI sin la
aprobación de su dueño, y desarrollamos una aplicación en Java que presenta el caso, explica la teoría con gráficos y
simula una tienda de chips con y sin la propuesta. La aplicación se usa para la exposición, está publicada en GitHub y
se puede descargar para Windows sin instalar nada.</p>

<h2>2. Problemática</h2>
<h3>2.1. Las cifras</h3>
{tabla("Cifras oficiales sobre líneas móviles con datos inconsistentes en el Perú", [
    ["Dato", "Fuente"],
    ["309 454 líneas con datos inconsistentes detectadas entre mayo y octubre de 2025", "Congreso de la República del Perú (2025)"],
    ["Más de 102 mil líneas dadas de baja por registros inconsistentes", "DPL News (2025)"],
    ["Más de 650 mil celulares bloqueados durante el estado de emergencia", "Congreso de la República del Perú (2025)"],
    ["Alrededor de 200 mil personas con más de 10 líneas a su nombre", "Canal N (2025)"],
])}
<h3>2.2. Cómo ocurre</h3>
<p>Para contratar una línea se exige el DNI y la verificación de la huella dactilar contra la base de datos de RENIEC.
Aun así, existen varias formas de registrar líneas a nombre de otra persona. La más sencilla de explicar, y sobre la
que OSIPTEL ha advertido, es la <b>doble huella</b>: el vendedor le dice al cliente que la huella «no salió» y le pide
ponerla otra vez; con esa segunda lectura, que es válida, activa un chip adicional a nombre del cliente y lo vende
aparte (DPL News, s. f.). También se han documentado bandas que activaban chips con datos de terceros (América TV,
s. f.) y casos de huellas clonadas en moldes de silicona (Bloomberg Línea, s. f.).</p>
{imagen("1c-historia.png", "La doble huella: con la segunda lectura se registra un chip extra a nombre de Ana.")}
{imagen("1e-historia.png", "La extorsión se hace desde un chip registrado a nombre de Ana.")}
<h3>2.3. Lo que ya existe</h3>
<p>El Estado ha tomado medidas. OSIPTEL ofrece el portal «Checa tus líneas», donde cualquier persona puede consultar las
líneas registradas con su DNI y reportar las que no reconoce (El Peruano, s. f.); permite además restringir la
contratación de nuevas líneas a nombre propio (El Peruano, 2025), y la Resolución 000116-2025-CD/OSIPTEL dispuso
medidas temporales de seguridad en la contratación, como validar la identidad de quienes tienen más de diez líneas
(OSIPTEL, 2025). Todas estas medidas tienen algo en común: el ciudadano debe tomar la iniciativa de revisar, y el
chip fraudulento ya está activo cuando se detecta.</p>

<h2>3. Objetivos</h2>
<h3>3.1. Objetivo general</h3>
<p>Modelar el registro de líneas móviles del Perú como una función entre conjuntos finitos para explicar por qué
permite la extorsión con chips a nombre de terceros, y proponer y simular una solución basada en las propiedades de las
funciones.</p>
<h3>3.2. Objetivos específicos</h3>
<ul>
  <li>Representar el registro y el uso real de las líneas como dos funciones y clasificarlas.</li>
  <li>Explicar la inyectividad, la sobreyectividad, la biyectividad y el principio del palomar con casos cotidianos.</li>
  <li>Diseñar ChipGuard, describiendo qué es, qué funciones ofrece, su interfaz y su arquitectura.</li>
  <li>Desarrollar una aplicación en Java que presente el caso y simule el efecto de ChipGuard con pruebas automáticas.</li>
</ul>

<h2>4. Marco teórico</h2>
<h3>4.1. Función</h3>
<p>Sean A y B conjuntos. Una función f de A en B es una regla que asigna a <b>cada</b> elemento de A <b>exactamente
un</b> elemento de B (Rosen, 2019). A se llama dominio, B codominio, y el conjunto de valores que efectivamente toma f
se llama imagen. En un diagrama sagital, de cada elemento de A sale una y solo una flecha.</p>
<p class="formula">f : A → B &nbsp;&nbsp; tal que &nbsp;&nbsp; ∀ a ∈ A, ∃! b ∈ B : f(a) = b</p>
{imagen("2a-matematica.png", "El registro de chips como función: de cada chip sale exactamente una flecha.")}
<h3>4.2. Función inyectiva</h3>
<p>f es inyectiva si elementos distintos del dominio tienen imágenes distintas: a cada elemento de B le llega como
máximo una flecha.</p>
<p class="formula">x₁ ≠ x₂ &nbsp;⇒&nbsp; f(x₁) ≠ f(x₂)</p>
<p><b>Caso cotidiano: los asientos de un bus.</b> La asignación pasajero → asiento es inyectiva: cada pasajero tiene su
propio asiento y pueden sobrar asientos vacíos. Si la empresa vende el mismo asiento dos veces (sobreventa), a ese
asiento le llegan dos flechas y la función deja de ser inyectiva.</p>
{imagen("2d-matematica.png", "Caso real de función inyectiva y su ruptura por sobreventa.")}
<h3>4.3. Función sobreyectiva</h3>
<p>f es sobreyectiva si su imagen es todo el codominio: cada elemento de B recibe al menos una flecha.</p>
<p class="formula">∀ y ∈ B, &nbsp;∃ x ∈ A : f(x) = y</p>
<p><b>Caso cotidiano: los grupos de la exposición.</b> La asignación alumno → grupo es sobreyectiva porque ningún grupo
queda vacío. Como hay más alumnos que grupos, no puede ser inyectiva.</p>
{imagen("2f-matematica.png", "Caso real de función sobreyectiva.")}
<h3>4.4. Función biyectiva e inversa</h3>
<p>f es biyectiva si es inyectiva y sobreyectiva a la vez. Solo las funciones biyectivas tienen inversa f⁻¹ : B → A,
que permite volver de cada imagen a su único origen.</p>
<p><b>Caso cotidiano: persona y DNI.</b> Cada persona tiene un DNI y cada DNI pertenece a una sola persona. Por eso la
relación funciona en ambas direcciones: con el DNI se encuentra a la persona y viceversa.</p>
{imagen("2h-matematica.png", "Caso real de función biyectiva.")}
<h3>4.5. Principio del palomar</h3>
<p>Si se reparten n objetos en k cajones y n &gt; k, al menos un cajón recibe más de un objeto (Rosen, 2019).
Equivalentemente, si |A| &gt; |B| ninguna función de A en B puede ser inyectiva. En el contexto de las líneas, si cada
DNI solo admite k chips y llegan más de k, los sobrantes no pueden registrarse: es la base matemática de un tope de
líneas por persona.</p>
{imagen("2j-matematica.png", "El palomar explicado con cajones: 5 chips y 3 cajones.")}

<h2>5. Modelamiento matemático del caso</h2>
<p>Sean C el conjunto de chips (líneas) activos y P el conjunto de personas con DNI. Definimos dos funciones:</p>
{tabla("Las dos funciones del modelo", [
    ["Función", "Qué asigna", "Quién la conoce"],
    ["f : C → P (registro)", "A cada chip, la persona a cuyo nombre está", "La operadora y la policía"],
    ["u : C → P (uso real)", "A cada chip, la persona que realmente lo usa", "Nadie, salvo quien lo usa"],
])}
<p>Ambas son funciones: cada chip tiene un único titular registrado y un único usuario real. Ninguna de las dos debe
ser inyectiva, porque tener varias líneas es legal, ni sobreyectiva, porque no toda persona tiene un chip. El problema
no está en el tipo de f, sino en que <b>f y u no coinciden</b> en los chips fraudulentos:</p>
<p class="formula">existe c ∈ C tal que f(c) ≠ u(c)</p>
<p>Cuando la policía rastrea un número, aplica f: llega a f(c), el titular en papel, en lugar de u(c), el
responsable. En el caso de Ana, f(…935) = Ana, pero u(…935) = extorsionador.</p>
{imagen("2i-matematica.png", "El problema: las dos flechas de un mismo chip no llegan a la misma persona.")}
<h3>5.1. Condición que debe cumplir la solución</h3>
<p>Una solución debe garantizar que, para todo chip activo, el titular registrado sea la persona que lo usa o que lo
autorizó expresamente. Si cada chip se activa solo cuando el dueño del DNI lo confirma, se forma una correspondencia
uno a uno entre los chips activos y las confirmaciones de sus dueños: la relación chip ↔ confirmación es una biyección
y su inversa lleva de cada chip a una persona real que dijo «sí, soy yo». A esto se suma un tope de k chips por DNI,
que por el principio del palomar impide acumular líneas sin límite:</p>
<p class="formula">|f⁻¹(p)| ≤ k &nbsp;&nbsp; para toda persona p ∈ P</p>
{imagen("2k-matematica.png", "La solución: las flechas del registro y del uso real llegan a la misma persona.")}

<h2>6. La propuesta: ChipGuard</h2>
<h3>6.1. Qué es</h3>
<p>ChipGuard es un servicio gratuito de protección de identidad móvil. Su regla es una sola: <b>ningún chip se activa
con tu DNI sin tu permiso</b>. No reemplaza la verificación biométrica de RENIEC, que sigue validando la huella, sino
que añade un paso posterior: preguntarle al dueño del DNI. Tiene tres partes:</p>
{tabla("Las tres partes de ChipGuard", [
    ["Parte", "Para quién", "Qué hace"],
    ["App móvil (Android e iPhone)", "El ciudadano", "Recibe alertas al instante, permite aprobar con la huella del celular o bloquear, y muestra todos los chips a su nombre"],
    ["Página web", "Quien no tiene smartphone", "Consulta y bloqueo con DNI y un código enviado por SMS"],
    ["Servidor central", "Operadoras y OSIPTEL", "Recibe cada solicitud de activación, espera la respuesta del dueño y ordena activar o bloquear"],
])}
{imagen("Qa-que-es.png", "Las tres partes de ChipGuard.")}
<h3>6.2. Para qué sirve</h3>
<ul>
  <li><b>Te avisa:</b> cada vez que alguien intenta activar un chip con tu DNI, en cualquier tienda u operadora.</li>
  <li><b>Te deja decidir:</b> apruebas con tu huella en el celular o tocas «No fui yo» y la línea no se activa.</li>
  <li><b>Te muestra tus chips:</b> todas las líneas a tu nombre, con operadora, tienda y fecha.</li>
  <li><b>Denuncia por ti:</b> cada intento bloqueado llega a OSIPTEL con la tienda y la hora.</li>
</ul>
<h3>6.3. Quién lo usa</h3>
<p>El <b>ciudadano</b> usa la app o la web. La <b>tienda y la operadora</b> no ven una pantalla nueva: su sistema de
activación consulta a ChipGuard antes de activar y no puede saltarse ese paso. <b>OSIPTEL y la policía</b> reciben los
reportes y pueden identificar las tiendas con más intentos bloqueados.</p>
<h3>6.4. La interfaz</h3>
<p>La app tiene cuatro pantallas principales. En el <b>registro</b>, el usuario ingresa su DNI y su celular y verifica
su identidad con la huella una sola vez. En <b>Mis chips</b> ve sus líneas, el tope permitido y un interruptor para
cerrar su DNI a nuevas líneas. La <b>alerta</b> aparece cuando alguien intenta activar un chip: indica la tienda, la
hora y ofrece dos botones, «No fui yo · Bloquear» y «Sí, fui yo». El <b>historial</b> registra aprobaciones, bloqueos y
reportes.</p>
{imagen("Qd-que-es.png", "Las cuatro pantallas de la app ChipGuard.", "92%")}
{imagen("Qe-que-es.png", "La versión web, pensada para personas sin smartphone.")}
<h3>6.5. Qué cambia frente a lo que existe hoy</h3>
{tabla("Comparación entre las medidas actuales y ChipGuard", [
    ["Aspecto", "Hoy («Checa tus líneas» y restricción)", "Con ChipGuard"],
    ["Cómo ves tus chips", "Entrando a la web cuando te acuerdas", "Siempre en tu celular"],
    ["Cuándo te enteras", "Cuando revisas, o por un SMS si llega", "Al instante, antes de la activación"],
    ["El chip fraudulento", "Se activa y luego se reclama", "Espera tu permiso; si no apruebas, no se activa"],
    ["Bloquear nuevas líneas", "Restricción general", "Restricción general y además decisión chip por chip"],
    ["La tienda", "Reclamo aparte", "Reporte automático a OSIPTEL"],
])}
{imagen("3b-chipguard.png", "ChipGuard en acción: la alerta llega en el momento de la segunda huella.")}

<h2>7. Arquitectura de ChipGuard</h2>
<h3>7.1. Componentes</h3>
<p>El sistema tiene seis piezas. Cuatro ya existen en el Perú (la tienda con lector de huella, la operadora, RENIEC y
OSIPTEL) y ChipGuard agrega dos: el servidor y la app del dueño.</p>
{imagen("3f-arquitectura.png", "Componentes de ChipGuard; en verde, lo que se agrega.")}
<h3>7.2. Flujo de una solicitud</h3>
<ol>
  <li>La tienda envía la solicitud a la operadora: DNI, huella, tienda y hora.</li>
  <li>La operadora valida la huella con RENIEC, como ocurre hoy.</li>
  <li>La operadora consulta al servidor ChipGuard si el dueño autoriza; la línea queda en espera.</li>
  <li>ChipGuard envía una notificación push al celular del dueño del DNI.</li>
  <li>El dueño responde «Sí, fui yo» (con su huella en el celular) o «No fui yo».</li>
  <li>ChipGuard responde a la operadora: activar o bloquear.</li>
  <li>Queda el registro; si fue un bloqueo, OSIPTEL recibe un reporte con la tienda y la hora.</li>
</ol>
{imagen("3g-arquitectura.png", "Los siete pasos de una solicitud aprobada.")}
<h3>7.3. Casos especiales</h3>
<ul>
  <li><b>Sin respuesta en 10 minutos:</b> la línea no se activa; puede intentarse de nuevo cuando el dueño apruebe.</li>
  <li><b>Sin smartphone:</b> se envía un SMS con un código o una llamada automática para responder.</li>
  <li><b>Sin celular:</b> el dueño aprueba en persona, con su huella, en una agencia de la operadora.</li>
</ul>
<h3>7.4. Datos y privacidad</h3>
<p>ChipGuard guarda el DNI cifrado, el identificador del celular para enviar avisos y el historial de solicitudes.
Nunca guarda la huella, que sigue validándose en RENIEC, ni llamadas, mensajes o ubicación.</p>
<h3>7.5. Tecnologías propuestas</h3>
{tabla("Tecnologías propuestas para cada capa", [
    ["Capa", "Tecnología"],
    ["App del dueño", "Android e iOS; notificaciones push; aprobación con la huella del propio celular"],
    ["Notificaciones", "Firebase Cloud Messaging (Android), APNs (iPhone) y SMS de respaldo"],
    ["Servidor", "API REST en Java (Spring Boot) que decide esperar, activar o bloquear"],
    ["Base de datos", "PostgreSQL: DNI cifrado, historial de solicitudes y tope de chips por DNI"],
    ["Integración", "API segura (HTTPS y firma digital) con operadoras y OSIPTEL"],
])}

<h2>8. Simulación: el juego «La tienda»</h2>
<p>Para comprobar el efecto de la propuesta, la aplicación incluye una simulación jugable. El público atiende una
tienda de chips: llegan seis clientes, cada uno entrega un DNI y hay que decidir si venderle o no comparando su cara con
la foto del documento. La misma fila se juega dos veces, primero sin ChipGuard y luego con ChipGuard.</p>
{tabla("La fila de clientes de la simulación", [
    ["Cliente", "DNI que entrega", "¿Es el dueño?", "¿El vendedor saca un chip extra?"],
    ["Luis", "Luis", "Sí", "No"],
    ["Encapuchado", "Pedro", "No", "No"],
    ["Rosa", "Rosa", "Sí", "Sí (doble huella)"],
    ["Carmen", "Carmen", "Sí", "No"],
    ["Impostora", "Juana", "No", "No"],
    ["Jorge", "Jorge", "Sí", "Sí (doble huella)"],
])}
<p>Sin ChipGuard, vender a un impostor o el chip extra que saca el vendedor termina en manos criminales, aunque quien
atiende decida bien: el chip extra no se ve desde el mostrador. Con ChipGuard, el dueño del DNI recibe la alerta en su
celular y bloquea el chip antes de que se active.</p>
{imagen("4e-tienda-extra.png", "Ronda 1: el vendedor saca un chip extra que el público no puede ver.")}
{imagen("4g-tienda-bloqueado.png", "Ronda 2: el dueño del DNI bloquea el chip desde su celular.")}

<h2>9. La aplicación desarrollada</h2>
<p>La aplicación ChipGuard está escrita en Java 17 con Swing y gráficos dibujados a mano (Java2D), sin librerías
externas. Se usa para exponer: avanza con las flechas del teclado como una presentación y tiene un cronómetro para el
expositor. Está organizada en ocho capítulos:</p>
{tabla("Capítulos de la aplicación", [
    ["Capítulo", "Contenido"],
    ["La historia", "Portada con el equipo y el caso de Ana en seis escenas animadas, de la doble huella a la policía, con las cifras de OSIPTEL"],
    ["Objetivos", "El objetivo general y los cuatro específicos, cada uno con el capítulo donde se cumple, y el marco teórico: función, inyectiva, sobreyectiva, biyectiva y palomar, cada una con su diagrama"],
    ["La matemática", "Función, inyectiva, sobreyectiva y biyectiva con personas y casos reales; el problema f(c) ≠ u(c); el palomar; la solución"],
    ["Qué es ChipGuard", "Las tres partes, sus usos, sus actores, las pantallas de la app, la web y la comparación con lo que existe"],
    ["En acción", "El celular de Ana recibe la alerta y bloquea el chip"],
    ["Por dentro", "La arquitectura, el flujo de siete pasos, los casos especiales, los datos y las tecnologías"],
    ["La tienda", "La simulación jugable en dos rondas"],
    ["Cierre", "Ideas clave, el reto «Checa tus líneas», las referencias y el agradecimiento con el equipo"],
])}
{imagen("1a-historia.png", "Portada de la aplicación con el docente y los integrantes.")}
<h3>9.1. Las tres funciones en el código</h3>
<p>El corazón del programa son las definiciones del curso escritas en Java. Todo se reduce a <b>contar flechas</b>:
<code>salidas(a)</code> cuenta cuántas flechas salen de un elemento de A y <code>llegadas(b)</code> cuántas llegan a
un elemento de B. Con esas dos cuentas se verifica cada propiedad. Antes de clasificar, cada método comprueba que la
relación sea función (de cada elemento de A sale exactamente una flecha).</p>
{fragmento("src/simuchip/funciones/Funciones.java", "/** INYECTIVA", "Función inyectiva: a ningún elemento de B le llegan dos o más flechas.")}
{fragmento("src/simuchip/funciones/Funciones.java", "/** SOBREYECTIVA", "Función sobreyectiva: ningún elemento de B se queda sin flecha.")}
{fragmento("src/simuchip/funciones/Funciones.java", "/** BIYECTIVA", "Función biyectiva: inyectiva y sobreyectiva a la vez.")}
<h3>9.2. Pruebas</h3>
<p>El proyecto incluye 144 pruebas automáticas que se ejecutan con un solo comando. Verifican, entre otros casos, que
las tres funciones clasifiquen bien ejemplos con y sin flechas repetidas, que sin ChipGuard terminen tres chips en
manos criminales aun decidiendo bien, que con ChipGuard no termine ninguno, que la tienda de la historia no tape el
texto en pantallas bajas y que ningún símbolo matemático salga como un cuadradito en Windows.</p>
<h3>9.3. Diseño y distribución</h3>
<p>La interfaz usa la paleta de la Universidad Peruana Cayetano Heredia: granate para las acciones y el conjunto B,
dorado y amarillo para el conjunto A y los resaltados, y gris para el texto secundario; el rojo vivo se reserva para el
fraude y el verde para lo seguro. La portada y el cierre muestran el logo de la universidad, al docente y a los cuatro
integrantes con su ORCID.</p>
<p>El código está publicado en GitHub ({REPO}). En la sección de descargas hay una versión portable para Windows que
incluye su propio Java: se descomprime y se abre con doble clic, sin instalar nada. Se probó con el Java de Windows en
un emulador, comparando las 47 escenas con las de Linux.</p>

<h2>10. Resultados y discusión</h2>
{tabla("Resultado de las dos rondas de la simulación", [
    ["Ronda", "Chips en manos de extorsionadores", "Bloqueados por su dueño"],
    ["Sin ChipGuard (decidiendo bien)", "3", "0"],
    ["Con ChipGuard (vendiendo a todos)", "0", "4"],
])}
{imagen("4h-tienda-final.png", "Resultado de las dos rondas.")}
<p>El resultado muestra el punto central del modelo: el control desde el mostrador no basta, porque el fraude del
chip extra no es visible para quien atiende. Aun con buenas decisiones, f y u dejan de coincidir en tres chips. Con
ChipGuard la decisión pasa a la única persona que sabe si el chip es suyo, el dueño del DNI, y la coincidencia entre
registro y uso real se recupera aunque la tienda se equivoque.</p>
<p>La propuesta tiene limitaciones. Requiere que las operadoras integren la consulta a ChipGuard antes de activar
líneas, lo que implica una norma de OSIPTEL. Las personas sin celular dependen de canales más lentos. Si el celular del
dueño fue robado, el atacante podría aprobar; por eso la aprobación exige la huella en el propio celular. Finalmente, la
simulación es didáctica: usa una fila fija de seis clientes y no estima tasas reales de fraude.</p>

<h2>11. Conclusiones</h2>
<ol>
  <li>El registro de líneas es una función f: Chips → Personas. Su falla no es de tipo (no debe ser inyectiva ni
  sobreyectiva), sino que no coincide con la función de uso real u en los chips fraudulentos.</li>
  <li>La trazabilidad que necesita la policía exige una correspondencia biyectiva entre cada chip activo y una persona
  real que lo autorizó; solo así la inversa lleva al responsable.</li>
  <li>El principio del palomar fundamenta el tope de líneas por DNI: con k cajones, el chip k + 1 no puede registrarse.</li>
  <li>ChipGuard traslada la decisión al dueño del DNI antes de la activación y, en la simulación, reduce de 3 a 0 los
  chips que terminan en manos criminales.</li>
</ol>

<h2>12. Recomendaciones</h2>
<ul>
  <li>Revisar las líneas a nombre propio en «Checa tus líneas» de OSIPTEL y reportar las que no se reconozcan.</li>
  <li>No poner la huella dos veces en una tienda; si el vendedor lo pide, verificar luego las líneas a nombre propio.</li>
  <li>Activar la restricción de nuevas contrataciones si no se planea contratar más líneas.</li>
  <li>Como trabajo futuro, simular con datos reales y miles de solicitudes para estimar el tiempo de respuesta del servidor.</li>
</ul>

<h2>13. Referencias</h2>
{REFERENCIAS}

<h2>Anexo A. Guion de la exposición</h2>
{tabla("Reparto de la exposición (15 minutos, 4 integrantes)", [
    ["Integrante", "Capítulos", "Tiempo", "Idea que debe quedar"],
    ["Martel Vasquez, Jordy Joel", "La historia", "3:00", "A Ana le sacaron un chip con su segunda huella, y la policía llegó a ella."],
    ["Sanga Limache, Angeles Jemina", "La matemática", "4:00", "El registro es una función; el problema es que registro ≠ uso real."],
    ["Tangoa Alva, Richard José Hernan", "Qué es ChipGuard · En acción · Por dentro", "4:30", "Ningún chip se activa sin el permiso del dueño del DNI."],
    ["Vidalon Ampudia, Eduardo Josias", "La tienda (con el público) · Cierre", "3:30", "Sin ChipGuard terminan 3 chips en manos criminales; con ChipGuard, 0."],
])}
<p>Si el tiempo supera los 15 minutos, se recorta primero el capítulo «Por dentro» (mostrar solo el flujo de siete
pasos).</p>

<h2>Anexo B. Cómo abrir la aplicación</h2>
<p><b>En Windows:</b> entrar a la sección de descargas del repositorio ({REPO}/releases), bajar
<b>ChipGuard-Windows.zip</b>, descomprimirlo y abrir <b>Abrir ChipGuard.bat</b>. No hace falta instalar Java porque viene
incluido. Si aparece el aviso «Windows protegió su PC», elegir «Más información» y luego «Ejecutar de todas formas».</p>
<p><b>En cualquier sistema con Java 17 o superior:</b> bajar <b>ChipGuard.jar</b> de la misma página y abrirlo con
doble clic o con <i>java -jar ChipGuard.jar</i>.</p>
<p><b>Controles:</b> las flechas izquierda y derecha avanzan o retroceden escenas y capítulos; un clic en la barra
superior salta a un capítulo; el cronómetro se inicia o pausa con un clic y se reinicia con clic derecho.</p>

<h2>Anexo C. Galería de la aplicación</h2>
<p>Otras escenas de la aplicación que se muestran durante la exposición.</p>
{galeria([
    ("1d-historia.png", "El chip extra se vende por la espalda."),
    ("1f-historia.png", "La policía rastrea el número y llega a Ana."),
    ("1g-historia.png", "Las cifras de OSIPTEL."),
    ("2b-matematica.png", "Lo que no es función."),
    ("2c-matematica.png", "Función inyectiva."),
    ("2e-matematica.png", "Función sobreyectiva."),
    ("2g-matematica.png", "Función biyectiva."),
    ("Qb-que-es.png", "Para qué sirve ChipGuard."),
    ("Qc-que-es.png", "Quién usa ChipGuard."),
    ("Qf-que-es.png", "Qué cambia frente a lo que existe hoy."),
    ("3a-chipguard.png", "ChipGuard protege el DNI de Ana."),
    ("3c-chipguard.png", "El chip bloqueado desde el celular."),
    ("3d-chipguard.png", "El mapa de chips de Ana."),
    ("3h-arquitectura.png", "Si el dueño dice «No fui yo»."),
    ("3i-arquitectura.png", "Sin respuesta o sin smartphone."),
    ("3j-arquitectura.png", "Qué guarda ChipGuard."),
    ("3k-arquitectura.png", "Con qué se construye."),
    ("4a-tienda-portada.png", "Portada del juego «La tienda»."),
    ("4b-tienda-cliente.png", "Llega un cliente con su DNI."),
    ("4f-tienda-resumen.png", "Resumen de la ronda sin ChipGuard."),
    ("5a-cierre.png", "Las cuatro ideas del cierre."),
])}
"""


def main():
    with tempfile.TemporaryDirectory() as tmp:
        tmp = Path(tmp)
        pagina = (f"<!doctype html><html lang='es'><head><meta charset='utf-8'><style>{CSS}</style></head>"
                  f"<body>{cuerpo()}</body></html>")
        (AQUI / "informe.html").write_text(pagina, encoding="utf-8")
        with sync_playwright() as p:
            nav = p.chromium.launch()
            pag = nav.new_page()
            pag.set_content(portada(), wait_until="load")
            pag.pdf(path=str(tmp / "portada.pdf"), prefer_css_page_size=True, print_background=True)
            # ventana tan alta como el documento: caretRangeFromPoint solo ve lo que está dentro de la ventana
            pag.set_viewport_size({"width": 1280, "height": 30000})
            pag.set_content(pagina, wait_until="load")
            pag.evaluate(PAGINAR, {"altoPagina": ALTO_PAGINA_PX, "minFig": 0.5})
            # La página 1 del cuerpo es un hueco reservado para la portada: así la numeración empieza en 2.
            pag.pdf(path=str(tmp / "cuerpo.pdf"), prefer_css_page_size=True, print_background=True,
                    display_header_footer=True, header_template=ENCABEZADO, footer_template=PIE)
            nav.close()
        subprocess.run(["pdfseparate", "-f", "2", str(tmp / "cuerpo.pdf"), str(tmp / "p-%03d.pdf")], check=True)
        paginas = sorted(tmp.glob("p-*.pdf"))
        SALIDA.parent.mkdir(parents=True, exist_ok=True)
        subprocess.run(["pdfunite", str(tmp / "portada.pdf"), *map(str, paginas), str(SALIDA)], check=True)
    for copia in COPIAS:
        copia.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy(SALIDA, copia)
    print(f"PDF listo: {SALIDA}")


if __name__ == "__main__":
    main()
