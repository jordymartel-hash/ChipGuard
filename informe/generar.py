"""Genera el informe final de ChipGuard en PDF (A4) con Playwright.

Uso:  python3 informe/generar.py
Sale: ~/claude/entregas/expo-funciones/Informe-ChipGuard.pdf

Antes de generarlo conviene regenerar las capturas:
  ./probar.sh && java -Djava.awt.headless=true -cp out:recursos simuchip.Capturas capturas
"""
import base64
import html
from pathlib import Path

from playwright.sync_api import sync_playwright

AQUI = Path(__file__).resolve().parent
RAIZ = AQUI.parent
CAPTURAS = RAIZ / "capturas"
SALIDA = Path.home() / "claude/entregas/expo-funciones/Informe-ChipGuard.pdf"


LOGO = base64.b64encode((Path(__file__).resolve().parent.parent / "recursos/imagenes/logo-upch.png").read_bytes()).decode()


def imagen(nombre, pie, ancho="100%"):
    datos = base64.b64encode((CAPTURAS / nombre).read_bytes()).decode()
    return (f'<figure><img src="data:image/png;base64,{datos}" style="width:{ancho}">'
            f'<figcaption>{pie}</figcaption></figure>')


def codigo(texto):
    # Escapar: los genéricos de Java (<A, B>) el navegador los leería como etiquetas HTML.
    return f'<pre>{html.escape(texto.strip())}</pre>'


CSS = """
@page { size: A4; margin: 2.3cm 2.2cm 2.3cm 2.2cm; }
body { font-family: 'Noto Serif', 'DejaVu Serif', serif; font-size: 11pt; line-height: 1.55; color: #1D1D1F; }
h1, h2, h3 { font-family: 'Noto Sans', 'DejaVu Sans', sans-serif; color: #1D1D1F; line-height: 1.25; }
h1 { font-size: 20pt; margin: 0 0 6pt; }
h2 { font-size: 15pt; margin: 22pt 0 8pt; padding-bottom: 4pt; border-bottom: 2px solid #D8A200; break-after: avoid; }
h3 { font-size: 12pt; margin: 14pt 0 6pt; color: #A90A2E; break-after: avoid; }
p { margin: 0 0 8pt; text-align: justify; }
ul, ol { margin: 0 0 8pt 18pt; padding: 0; }
li { margin-bottom: 3pt; }
table { width: 100%; border-collapse: collapse; margin: 8pt 0 12pt; font-size: 10pt; break-inside: avoid; }
th { background: #A90A2E; color: #FFFFFF; font-family: 'Noto Sans', sans-serif; text-align: left; padding: 6pt 8pt; }
td { border-bottom: 1px solid #D5DCE6; padding: 6pt 8pt; vertical-align: top; }
tr:nth-child(even) td { background: #F6F8FB; }
figure { margin: 10pt 0 14pt; break-inside: avoid; text-align: center; }
figure img { border: 1px solid #D5DCE6; border-radius: 6px; }
figcaption { font-family: 'Noto Sans', sans-serif; font-size: 9pt; color: #4A5568; margin-top: 4pt; }
pre { font-family: 'DejaVu Sans Mono', monospace; font-size: 8.8pt; line-height: 1.45; background: #1D1D1F; color: #E6ECF5;
      padding: 10pt 12pt; border-radius: 6px; white-space: pre-wrap; break-inside: avoid; }
.formula { font-family: 'Noto Sans', sans-serif; text-align: center; font-size: 11.5pt; margin: 6pt 0 10pt; }
.portada { height: 24cm; display: flex; flex-direction: column; justify-content: space-between; break-after: page; }
.portada .sup { font-family: 'Noto Sans', sans-serif; letter-spacing: 3px; font-size: 10pt; color: #A90A2E; font-weight: 700; }
.portada .logo { width: 7.5cm; margin-bottom: 14pt; }
.portada h1 { font-size: 30pt; margin-top: 10pt; }
.portada .sub { font-family: 'Noto Sans', sans-serif; font-size: 13pt; color: #4A5568; margin-top: 8pt; }
.portada .datos p { text-align: left; margin: 0 0 4pt; font-family: 'Noto Sans', sans-serif; }
.indice li { margin-bottom: 2pt; }
.salto { break-before: page; }
.ref { text-align: left; padding-left: 1.27cm; text-indent: -1.27cm; }
a { color: #8C0826; word-break: break-all; }
"""



def fragmento(ruta, desde, hasta):
    """Copia literal de un trozo del código real, desde la línea que contiene "desde" hasta la que contiene "hasta"."""
    lineas = (RAIZ / ruta).read_text(encoding="utf-8").splitlines()
    i = next(k for k, l in enumerate(lineas) if desde in l)
    j = next(k for k in range(i, len(lineas)) if hasta in lineas[k])
    return codigo("\n".join(lineas[i:j + 1]))


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

CUERPO = f"""
<div class="portada">
  <div>
    <img class="logo" src="data:image/png;base64,{LOGO}">
    <div class="sup">MATEMÁTICA DISCRETA · PROYECTO DE MODELAMIENTO Y SIMULACIÓN</div>
    <h1>ChipGuard: ¿a quién le pertenece tu chip?</h1>
    <div class="sub">Funciones inyectivas, sobreyectivas y biyectivas aplicadas al registro de líneas móviles en el Perú,
    y una propuesta para que ningún chip se active sin el permiso del dueño del DNI</div>
  </div>
  {imagen("1c-historia.png", "La aplicación de la exposición: la historia de Ana, contada con personitas")}
  <div class="datos">
    <p><b>Universidad:</b> Universidad Peruana Cayetano Heredia</p>
    <p><b>Curso:</b> Matemática Discreta &nbsp;·&nbsp; <b>Docente:</b> [Nombre del docente]</p>
    <p><b>Grupo:</b> [N] &nbsp;·&nbsp; <b>Integrantes:</b> Martel, Jordy; Sanga, Angeles; Tangoa, Richard; Vilva, Eduardo</p>
    <p><b>Fecha:</b> 13 de noviembre de 2026</p>
  </div>
</div>

<h2>Índice</h2>
<ol class="indice">
  <li>Introducción</li>
  <li>Problemática</li>
  <li>Objetivos</li>
  <li>Marco teórico</li>
  <li>Modelamiento matemático del caso</li>
  <li>La propuesta: ChipGuard</li>
  <li>Arquitectura de ChipGuard</li>
  <li>Simulación: el juego «La tienda»</li>
  <li>La aplicación desarrollada</li>
  <li>Resultados y discusión</li>
  <li>Conclusiones</li>
  <li>Recomendaciones</li>
  <li>Referencias</li>
  <li>Anexo A: guion de la exposición</li>
  <li>Anexo B: cómo ejecutar la aplicación</li>
</ol>

<h2 class="salto">1. Introducción</h2>
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
simula una tienda de chips con y sin la propuesta.</p>

<h2>2. Problemática</h2>
<h3>2.1 Las cifras</h3>
<table>
  <tr><th>Dato</th><th>Fuente</th></tr>
  <tr><td>309 454 líneas con datos inconsistentes detectadas entre mayo y octubre de 2025</td><td>Congreso de la República del Perú (2025)</td></tr>
  <tr><td>Más de 102 mil líneas dadas de baja por registros inconsistentes</td><td>DPL News (2025)</td></tr>
  <tr><td>Más de 650 mil celulares bloqueados durante el estado de emergencia</td><td>Congreso de la República del Perú (2025)</td></tr>
  <tr><td>Alrededor de 200 mil personas con más de 10 líneas a su nombre</td><td>Canal N (2025)</td></tr>
</table>
<h3>2.2 Cómo ocurre</h3>
<p>Para contratar una línea se exige el DNI y la verificación de la huella dactilar contra la base de datos de RENIEC.
Aun así, existen varias formas de registrar líneas a nombre de otra persona. La más sencilla de explicar, y sobre la
que OSIPTEL ha advertido, es la <b>doble huella</b>: el vendedor le dice al cliente que la huella «no salió» y le pide
ponerla otra vez; con esa segunda lectura, que es válida, activa un chip adicional a nombre del cliente y lo vende
aparte (DPL News, s. f.). También se han documentado bandas que activaban chips con datos de terceros (América TV,
s. f.) y casos de huellas clonadas en moldes de silicona (Bloomberg Línea, s. f.).</p>
{imagen("1e-historia.png", "Figura 1. La extorsión se hace desde un chip registrado a nombre de Ana", "88%")}
<h3>2.3 Lo que ya existe</h3>
<p>El Estado ha tomado medidas. OSIPTEL ofrece el portal «Checa tus líneas», donde cualquier persona puede consultar las
líneas registradas con su DNI y reportar las que no reconoce (El Peruano, s. f.); permite además restringir la
contratación de nuevas líneas a nombre propio (El Peruano, 2025), y la Resolución 000116-2025-CD/OSIPTEL dispuso
medidas temporales de seguridad en la contratación, como validar la identidad de quienes tienen más de diez líneas
(OSIPTEL, 2025). Todas estas medidas tienen algo en común: el ciudadano debe tomar la iniciativa de revisar, y el
chip fraudulento ya está activo cuando se detecta.</p>

<h2>3. Objetivos</h2>
<h3>Objetivo general</h3>
<p>Modelar el registro de líneas móviles del Perú como una función entre conjuntos finitos para explicar por qué
permite la extorsión con chips a nombre de terceros, y proponer y simular una solución basada en las propiedades de las
funciones.</p>
<h3>Objetivos específicos</h3>
<ul>
  <li>Representar el registro y el uso real de las líneas como dos funciones y clasificarlas.</li>
  <li>Explicar la inyectividad, la sobreyectividad, la biyectividad y el principio del palomar con casos cotidianos.</li>
  <li>Diseñar ChipGuard, describiendo qué es, qué funciones ofrece, su interfaz y su arquitectura.</li>
  <li>Desarrollar una aplicación en Java que presente el caso y simule el efecto de ChipGuard con pruebas automáticas.</li>
</ul>

<h2 class="salto">4. Marco teórico</h2>
<h3>4.1 Función</h3>
<p>Sean A y B conjuntos. Una función f de A en B es una regla que asigna a <b>cada</b> elemento de A <b>exactamente
un</b> elemento de B (Rosen, 2019). A se llama dominio, B codominio, y el conjunto de valores que efectivamente toma f
se llama imagen. En un diagrama sagital, de cada elemento de A sale una y solo una flecha.</p>
<p class="formula">f : A → B &nbsp;&nbsp; tal que &nbsp;&nbsp; ∀ a ∈ A, ∃! b ∈ B : f(a) = b</p>
{imagen("2a-matematica.png", "Figura 2. El registro de chips como función: de cada chip sale exactamente una flecha", "88%")}
<h3>4.2 Función inyectiva</h3>
<p>f es inyectiva si elementos distintos del dominio tienen imágenes distintas: a cada elemento de B le llega como
máximo una flecha.</p>
<p class="formula">x₁ ≠ x₂ &nbsp;⇒&nbsp; f(x₁) ≠ f(x₂)</p>
<p><b>Caso cotidiano: los asientos de un bus.</b> La asignación pasajero → asiento es inyectiva: cada pasajero tiene su
propio asiento y pueden sobrar asientos vacíos. Si la empresa vende el mismo asiento dos veces (sobreventa), a ese
asiento le llegan dos flechas y la función deja de ser inyectiva.</p>
{imagen("2d-matematica.png", "Figura 3. Caso real de función inyectiva y su ruptura por sobreventa", "88%")}
<h3>4.3 Función sobreyectiva</h3>
<p>f es sobreyectiva si su imagen es todo el codominio: cada elemento de B recibe al menos una flecha.</p>
<p class="formula">∀ y ∈ B, &nbsp;∃ x ∈ A : f(x) = y</p>
<p><b>Caso cotidiano: los grupos de la exposición.</b> La asignación alumno → grupo es sobreyectiva porque ningún grupo
queda vacío. Como hay más alumnos que grupos, no puede ser inyectiva.</p>
{imagen("2f-matematica.png", "Figura 4. Caso real de función sobreyectiva", "88%")}
<h3>4.4 Función biyectiva e inversa</h3>
<p>f es biyectiva si es inyectiva y sobreyectiva a la vez. Solo las funciones biyectivas tienen inversa f⁻¹ : B → A,
que permite volver de cada imagen a su único origen.</p>
<p><b>Caso cotidiano: persona y DNI.</b> Cada persona tiene un DNI y cada DNI pertenece a una sola persona. Por eso la
relación funciona en ambas direcciones: con el DNI se encuentra a la persona y viceversa.</p>
{imagen("2h-matematica.png", "Figura 5. Caso real de función biyectiva", "88%")}
<h3>4.5 Principio del palomar</h3>
<p>Si se reparten n objetos en k cajones y n &gt; k, al menos un cajón recibe más de un objeto (Rosen, 2019).
Equivalentemente, si |A| &gt; |B| ninguna función de A en B puede ser inyectiva. En el contexto de las líneas, si cada
DNI solo admite k chips y llegan más de k, los sobrantes no pueden registrarse: es la base matemática de un tope de
líneas por persona.</p>
{imagen("2j-matematica.png", "Figura 6. El palomar explicado con cajones: 5 chips y 3 cajones", "88%")}

<h2 class="salto">5. Modelamiento matemático del caso</h2>
<p>Sean C el conjunto de chips (líneas) activos y P el conjunto de personas con DNI. Definimos dos funciones:</p>
<table>
  <tr><th>Función</th><th>Qué asigna</th><th>Quién la conoce</th></tr>
  <tr><td>f : C → P (registro)</td><td>A cada chip, la persona a cuyo nombre está</td><td>La operadora y la policía</td></tr>
  <tr><td>u : C → P (uso real)</td><td>A cada chip, la persona que realmente lo usa</td><td>Nadie, salvo quien lo usa</td></tr>
</table>
<p>Ambas son funciones: cada chip tiene un único titular registrado y un único usuario real. Ninguna de las dos debe
ser inyectiva, porque tener varias líneas es legal, ni sobreyectiva, porque no toda persona tiene un chip. El problema
no está en el tipo de f, sino en que <b>f y u no coinciden</b> en los chips fraudulentos:</p>
<p class="formula">existe c ∈ C tal que f(c) ≠ u(c)</p>
<p>Cuando la policía rastrea un número, aplica f: llega a f(c), el titular en papel, en lugar de u(c), el
responsable. En el caso de Ana, f(…935) = Ana, pero u(…935) = extorsionador.</p>
{imagen("2i-matematica.png", "Figura 7. Las dos flechas de un mismo chip no llegan a la misma persona", "88%")}
<h3>5.1 Condición que debe cumplir la solución</h3>
<p>Una solución debe garantizar que, para todo chip activo, el titular registrado sea la persona que lo usa o que lo
autorizó expresamente. Si cada chip se activa solo cuando el dueño del DNI lo confirma, se forma una correspondencia
uno a uno entre los chips activos y las confirmaciones de sus dueños: la relación chip ↔ confirmación es una biyección
y su inversa lleva de cada chip a una persona real que dijo «sí, soy yo». A esto se suma un tope de k chips por DNI,
que por el principio del palomar impide acumular líneas sin límite:</p>
<p class="formula">|f⁻¹(p)| ≤ k &nbsp;&nbsp; para toda persona p ∈ P</p>

<h2 class="salto">6. La propuesta: ChipGuard</h2>
<h3>6.1 Qué es</h3>
<p>ChipGuard es un servicio gratuito de protección de identidad móvil. Su regla es una sola: <b>ningún chip se activa
con tu DNI sin tu permiso</b>. No reemplaza la verificación biométrica de RENIEC, que sigue validando la huella, sino
que añade un paso posterior: preguntarle al dueño del DNI. Tiene tres partes:</p>
<table>
  <tr><th>Parte</th><th>Para quién</th><th>Qué hace</th></tr>
  <tr><td>App móvil (Android e iPhone)</td><td>El ciudadano</td><td>Recibe alertas al instante, permite aprobar con la huella del celular o bloquear, y muestra todos los chips a su nombre</td></tr>
  <tr><td>Página web</td><td>Quien no tiene smartphone</td><td>Consulta y bloqueo con DNI y un código enviado por SMS</td></tr>
  <tr><td>Servidor central</td><td>Operadoras y OSIPTEL</td><td>Recibe cada solicitud de activación, espera la respuesta del dueño y ordena activar o bloquear</td></tr>
</table>
{imagen("Qa-que-es.png", "Figura 8. Las tres partes de ChipGuard", "88%")}
<h3>6.2 Para qué sirve</h3>
<ul>
  <li><b>Te avisa:</b> cada vez que alguien intenta activar un chip con tu DNI, en cualquier tienda u operadora.</li>
  <li><b>Te deja decidir:</b> apruebas con tu huella en el celular o tocas «No fui yo» y la línea no se activa.</li>
  <li><b>Te muestra tus chips:</b> todas las líneas a tu nombre, con operadora, tienda y fecha.</li>
  <li><b>Denuncia por ti:</b> cada intento bloqueado llega a OSIPTEL con la tienda y la hora.</li>
</ul>
<h3>6.3 Quién lo usa</h3>
<p>El <b>ciudadano</b> usa la app o la web. La <b>tienda y la operadora</b> no ven una pantalla nueva: su sistema de
activación consulta a ChipGuard antes de activar y no puede saltarse ese paso. <b>OSIPTEL y la policía</b> reciben los
reportes y pueden identificar las tiendas con más intentos bloqueados.</p>
<h3>6.4 La interfaz</h3>
<p>La app tiene cuatro pantallas principales. En el <b>registro</b>, el usuario ingresa su DNI y su celular y verifica
su identidad con la huella una sola vez. En <b>Mis chips</b> ve sus líneas, el tope permitido y un interruptor para
cerrar su DNI a nuevas líneas. La <b>alerta</b> aparece cuando alguien intenta activar un chip: indica la tienda, la
hora y ofrece dos botones, «No fui yo · Bloquear» y «Sí, fui yo». El <b>historial</b> registra aprobaciones, bloqueos y
reportes.</p>
{imagen("Qd-que-es.png", "Figura 9. Las cuatro pantallas de la app ChipGuard")}
{imagen("Qe-que-es.png", "Figura 10. La versión web, pensada para personas sin smartphone", "88%")}
<h3>6.5 Qué cambia frente a lo que existe hoy</h3>
<table>
  <tr><th>Aspecto</th><th>Hoy («Checa tus líneas» y restricción)</th><th>Con ChipGuard</th></tr>
  <tr><td>Cómo ves tus chips</td><td>Entrando a la web cuando te acuerdas</td><td>Siempre en tu celular</td></tr>
  <tr><td>Cuándo te enteras</td><td>Cuando revisas, o por un SMS si llega</td><td>Al instante, antes de la activación</td></tr>
  <tr><td>El chip fraudulento</td><td>Se activa y luego se reclama</td><td>Espera tu permiso; si no apruebas, no se activa</td></tr>
  <tr><td>Bloquear nuevas líneas</td><td>Restricción general</td><td>Restricción general y además decisión chip por chip</td></tr>
  <tr><td>La tienda</td><td>Reclamo aparte</td><td>Reporte automático a OSIPTEL</td></tr>
</table>
{imagen("3b-chipguard.png", "Figura 11. ChipGuard en acción: la alerta llega en el momento de la segunda huella", "88%")}

<h2 class="salto">7. Arquitectura de ChipGuard</h2>
<h3>7.1 Componentes</h3>
<p>El sistema tiene seis piezas. Cuatro ya existen en el Perú (la tienda con lector de huella, la operadora, RENIEC y
OSIPTEL) y ChipGuard agrega dos: el servidor y la app del dueño.</p>
{imagen("3f-arquitectura.png", "Figura 12. Componentes de ChipGuard; en verde, lo que se agrega", "88%")}
<h3>7.2 Flujo de una solicitud</h3>
<ol>
  <li>La tienda envía la solicitud a la operadora: DNI, huella, tienda y hora.</li>
  <li>La operadora valida la huella con RENIEC, como ocurre hoy.</li>
  <li>La operadora consulta al servidor ChipGuard si el dueño autoriza; la línea queda en espera.</li>
  <li>ChipGuard envía una notificación push al celular del dueño del DNI.</li>
  <li>El dueño responde «Sí, fui yo» (con su huella en el celular) o «No fui yo».</li>
  <li>ChipGuard responde a la operadora: activar o bloquear.</li>
  <li>Queda el registro; si fue un bloqueo, OSIPTEL recibe un reporte con la tienda y la hora.</li>
</ol>
{imagen("3g-arquitectura.png", "Figura 13. Los siete pasos de una solicitud aprobada", "88%")}
<h3>7.3 Casos especiales</h3>
<ul>
  <li><b>Sin respuesta en 10 minutos:</b> la línea no se activa; puede intentarse de nuevo cuando el dueño apruebe.</li>
  <li><b>Sin smartphone:</b> se envía un SMS con un código o una llamada automática para responder.</li>
  <li><b>Sin celular:</b> el dueño aprueba en persona, con su huella, en una agencia de la operadora.</li>
</ul>
<h3>7.4 Datos y privacidad</h3>
<p>ChipGuard guarda el DNI cifrado, el identificador del celular para enviar avisos y el historial de solicitudes.
Nunca guarda la huella, que sigue validándose en RENIEC, ni llamadas, mensajes o ubicación.</p>
<h3>7.5 Tecnologías propuestas</h3>
<table>
  <tr><th>Capa</th><th>Tecnología</th></tr>
  <tr><td>App del dueño</td><td>Android e iOS; notificaciones push; aprobación con la huella del propio celular</td></tr>
  <tr><td>Notificaciones</td><td>Firebase Cloud Messaging (Android), APNs (iPhone) y SMS de respaldo</td></tr>
  <tr><td>Servidor</td><td>API REST en Java (Spring Boot) que decide esperar, activar o bloquear</td></tr>
  <tr><td>Base de datos</td><td>PostgreSQL: DNI cifrado, historial de solicitudes y tope de chips por DNI</td></tr>
  <tr><td>Integración</td><td>API segura (HTTPS y firma digital) con operadoras y OSIPTEL</td></tr>
</table>

<h2 class="salto">8. Simulación: el juego «La tienda»</h2>
<p>Para comprobar el efecto de la propuesta, la aplicación incluye una simulación jugable. El público atiende una
tienda de chips: llegan seis clientes, cada uno entrega un DNI y hay que decidir si venderle o no comparando su cara con
la foto del documento. La misma fila se juega dos veces, primero sin ChipGuard y luego con ChipGuard.</p>
<table>
  <tr><th>Cliente</th><th>DNI que entrega</th><th>¿Es el dueño?</th><th>¿El vendedor saca un chip extra?</th></tr>
  <tr><td>Luis</td><td>Luis</td><td>Sí</td><td>No</td></tr>
  <tr><td>Encapuchado</td><td>Pedro</td><td>No</td><td>No</td></tr>
  <tr><td>Rosa</td><td>Rosa</td><td>Sí</td><td>Sí (doble huella)</td></tr>
  <tr><td>Carmen</td><td>Carmen</td><td>Sí</td><td>No</td></tr>
  <tr><td>Impostora</td><td>Juana</td><td>No</td><td>No</td></tr>
  <tr><td>Jorge</td><td>Jorge</td><td>Sí</td><td>Sí (doble huella)</td></tr>
</table>
<p>La lógica del juego está en la clase <code>Tienda</code>. Sin ChipGuard, vender a un impostor o un chip extra del
vendedor termina en manos criminales; con ChipGuard, el dueño del DNI recibe la alerta y lo bloquea:</p>
{fragmento("src/simuchip/caso/Tienda.java", "public List<Evento> decidir(boolean vender)", "        return siPasa;")}
{imagen("4e-tienda-extra.png", "Figura 14. Ronda 1: el vendedor saca un chip extra que el público no puede ver", "88%")}
{imagen("4g-tienda-bloqueado.png", "Figura 15. Ronda 2: el dueño del DNI bloquea el chip desde su celular", "88%")}

<h2 class="salto">9. La aplicación desarrollada</h2>
<p>La aplicación ChipGuard está escrita en Java 17 con Swing y gráficos dibujados a mano (Java2D). Se usa para
exponer: avanza con las flechas del teclado como una presentación y tiene un cronómetro para el expositor. Está
organizada en siete capítulos:</p>
<table>
  <tr><th>Capítulo</th><th>Contenido</th></tr>
  <tr><td>La historia</td><td>El caso de Ana en seis escenas animadas, de la doble huella a la policía, y las cifras de OSIPTEL</td></tr>
  <tr><td>La matemática</td><td>Función, inyectiva, sobreyectiva y biyectiva con personas y casos reales; el problema f(c) ≠ u(c); el palomar; la solución</td></tr>
  <tr><td>Qué es ChipGuard</td><td>Las tres partes, sus usos, sus actores, las pantallas de la app, la web y la comparación con lo que existe</td></tr>
  <tr><td>En acción</td><td>El celular de Ana recibe la alerta y bloquea el chip</td></tr>
  <tr><td>Por dentro</td><td>La arquitectura, el flujo de siete pasos, los casos especiales, los datos y las tecnologías</td></tr>
  <tr><td>La tienda</td><td>La simulación jugable en dos rondas</td></tr>
  <tr><td>Cierre</td><td>Ideas clave, el reto «Checa tus líneas» y las referencias</td></tr>
</table>
<h3>9.1 Pruebas</h3>
<p>El proyecto incluye 136 pruebas automáticas que se ejecutan con <code>./probar.sh</code>. Las pruebas de la tienda
verifican, entre otros casos, que sin ChipGuard terminan tres chips en manos criminales aun decidiendo bien, que con
ChipGuard no termina ninguno, y que la fila no acepta decisiones cuando ya se atendió a todos.</p>

<h2>10. Resultados y discusión</h2>
<table>
  <tr><th>Ronda</th><th>Chips en manos de extorsionadores</th><th>Bloqueados por su dueño</th></tr>
  <tr><td>Sin ChipGuard (decidiendo bien)</td><td>3</td><td>0</td></tr>
  <tr><td>Con ChipGuard (vendiendo a todos)</td><td>0</td><td>4</td></tr>
</table>
{imagen("4h-tienda-final.png", "Figura 16. Resultado de las dos rondas", "88%")}
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

<h2 class="salto">13. Referencias</h2>
{REFERENCIAS}

<h2 class="salto">Anexo A: guion de la exposición (15 minutos, 4 integrantes)</h2>
<table>
  <tr><th>Integrante</th><th>Capítulos</th><th>Tiempo</th><th>Idea que debe quedar</th></tr>
  <tr><td>Martel, Jordy</td><td>La historia</td><td>3:00</td><td>A Ana le sacaron un chip con su segunda huella, y la policía llegó a ella.</td></tr>
  <tr><td>Sanga, Angeles</td><td>La matemática</td><td>4:00</td><td>El registro es una función; el problema es que registro ≠ uso real.</td></tr>
  <tr><td>Tangoa, Richard</td><td>Qué es ChipGuard · En acción · Por dentro</td><td>4:30</td><td>Ningún chip se activa sin el permiso del dueño del DNI.</td></tr>
  <tr><td>Vilva, Eduardo</td><td>La tienda (con el público) · Cierre</td><td>3:30</td><td>Sin ChipGuard terminan 3 chips en manos criminales; con ChipGuard, 0.</td></tr>
</table>
<p>Si el tiempo supera los 15 minutos, se recorta primero el capítulo «Por dentro» (mostrar solo el flujo de siete
pasos).</p>

<h2>Anexo B: cómo ejecutar la aplicación</h2>
{codigo('''
# Abrir la aplicación (Java 17 o superior)
java -jar ChipGuard.jar          # o ./ejecutar.sh

# Compilar y correr las 136 pruebas
./probar.sh

# Regenerar las capturas del informe sin abrir ventanas
java -Djava.awt.headless=true -cp out:recursos simuchip.Capturas capturas

# Generar este informe en PDF
python3 informe/generar.py
''')}
<p>Controles: flechas ← → para avanzar o retroceder escenas y capítulos; clic en la barra superior para saltar a un
capítulo; el cronómetro de la barra se inicia con un clic.</p>
"""


def main():
    pagina = f"<!doctype html><html lang='es'><head><meta charset='utf-8'><style>{CSS}</style></head><body>{CUERPO}</body></html>"
    (AQUI / "informe.html").write_text(pagina, encoding="utf-8")
    SALIDA.parent.mkdir(parents=True, exist_ok=True)
    with sync_playwright() as p:
        nav = p.chromium.launch()
        pag = nav.new_page()
        pag.set_content(pagina, wait_until="load")
        pag.pdf(path=str(SALIDA), prefer_css_page_size=True, print_background=True, display_header_footer=True,
                header_template="<span></span>",
                footer_template="<div style='width:100%;font-size:8pt;color:#4A5568;text-align:center;"
                                "font-family:sans-serif'>ChipGuard · Matemática Discreta · "
                                "<span class='pageNumber'></span> / <span class='totalPages'></span></div>")
        nav.close()
    print(f"PDF listo: {SALIDA}")


if __name__ == "__main__":
    main()
