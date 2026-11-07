"""Genera el informe de SimuChip en PDF (A4) con Playwright.

Uso:  python3 informe/generar.py
Sale: ~/claude/entregas/expo-funciones/Informe-SimuChip.pdf

Antes de generarlo conviene regenerar las capturas:
  ./probar.sh && java -Djava.awt.headless=true -cp out simuchip.Capturas capturas
"""
import base64
import html
from pathlib import Path

from playwright.sync_api import sync_playwright

AQUI = Path(__file__).resolve().parent
RAIZ = AQUI.parent
CAPTURAS = RAIZ / "capturas"
SALIDA = Path.home() / "claude/entregas/expo-funciones/Informe-SimuChip.pdf"


def imagen(nombre, pie, ancho="100%"):
    datos = base64.b64encode((CAPTURAS / nombre).read_bytes()).decode()
    return (f'<figure><img src="data:image/png;base64,{datos}" style="width:{ancho}">'
            f'<figcaption>{pie}</figcaption></figure>')


def codigo(texto):
    # Escapar: los genéricos de Java (<A, B>) el navegador los leería como etiquetas HTML.
    return f'<pre>{html.escape(texto.strip())}</pre>'


CSS = """
@page { size: A4; margin: 2.3cm 2.2cm 2.3cm 2.2cm; }
body { font-family: 'Noto Serif', 'DejaVu Serif', serif; font-size: 11pt; line-height: 1.55; color: #1E2A44; }
h1, h2, h3 { font-family: 'Noto Sans', 'DejaVu Sans', sans-serif; color: #1E2A44; line-height: 1.25; }
h1 { font-size: 20pt; margin: 0 0 6pt; }
h2 { font-size: 15pt; margin: 22pt 0 8pt; padding-bottom: 4pt; border-bottom: 2px solid #E8832E; break-after: avoid; }
h3 { font-size: 12pt; margin: 14pt 0 6pt; color: #2559A8; break-after: avoid; }
p { margin: 0 0 8pt; text-align: justify; }
ul, ol { margin: 0 0 8pt 18pt; padding: 0; }
li { margin-bottom: 3pt; }
table { width: 100%; border-collapse: collapse; margin: 8pt 0 12pt; font-size: 10pt; break-inside: avoid; }
th { background: #1E2A44; color: #FFF8EE; font-family: 'Noto Sans', sans-serif; text-align: left; padding: 6pt 8pt; }
td { border-bottom: 1px solid #D5DCE6; padding: 6pt 8pt; vertical-align: top; }
tr:nth-child(even) td { background: #F6F8FB; }
figure { margin: 10pt 0 14pt; break-inside: avoid; text-align: center; }
figure img { border: 1px solid #D5DCE6; border-radius: 6px; }
figcaption { font-family: 'Noto Sans', sans-serif; font-size: 9pt; color: #4A5568; margin-top: 4pt; }
pre { font-family: 'DejaVu Sans Mono', monospace; font-size: 8.8pt; line-height: 1.45; background: #1E2A44; color: #E6ECF5;
      padding: 10pt 12pt; border-radius: 6px; white-space: pre-wrap; break-inside: avoid; }
.formula { font-family: 'Noto Sans', sans-serif; text-align: center; font-size: 11.5pt; margin: 6pt 0 10pt; }
.portada { height: 24cm; display: flex; flex-direction: column; justify-content: space-between; break-after: page; }
.portada .sup { font-family: 'Noto Sans', sans-serif; letter-spacing: 3px; font-size: 10pt; color: #B85A12; font-weight: 700; }
.portada h1 { font-size: 30pt; margin-top: 10pt; }
.portada .sub { font-family: 'Noto Sans', sans-serif; font-size: 13pt; color: #4A5568; margin-top: 8pt; }
.portada .datos p { text-align: left; margin: 0 0 4pt; font-family: 'Noto Sans', sans-serif; }
.indice li { margin-bottom: 2pt; }
.salto { break-before: page; }
"""

CUERPO = f"""
<div class="portada">
  <div>
    <div class="sup">MATEMÁTICA DISCRETA · PROYECTO DE SIMULACIÓN</div>
    <h1>¿A quién le pertenece tu chip?</h1>
    <div class="sub">Funciones inyectivas, sobreyectivas y biyectivas aplicadas al registro de líneas móviles en el Perú,
    con el simulador SimuChip desarrollado en Java</div>
  </div>
  {imagen("01-inicio.png", "SimuChip, pantalla de inicio")}
  <div class="datos">
    <p><b>Curso:</b> [Curso] &nbsp;·&nbsp; <b>Docente:</b> [Nombre del docente]</p>
    <p><b>Grupo:</b> [N] &nbsp;·&nbsp; <b>Integrantes:</b> [Integrante 1], [Integrante 2], [Integrante 3], [Integrante 4]</p>
    <p><b>Fecha:</b> 13 de noviembre de 2026</p>
  </div>
</div>

<h2>Índice</h2>
<ol class="indice">
  <li>Introducción</li><li>Problemática</li><li>Objetivos</li><li>Marco teórico</li>
  <li>Las funciones en otros sistemas reales</li><li>Modelamiento matemático del caso</li>
  <li>Solución propuesta</li><li>Diseño del simulador SimuChip</li><li>Simulación y resultados</li>
  <li>Limitaciones</li><li>Conclusiones</li><li>Recomendaciones</li><li>Referencias</li>
  <li>Anexo A: guion de la exposición</li><li>Anexo B: cómo ejecutar SimuChip</li>
</ol>

<h2>1. Introducción</h2>
<p>Las funciones son uno de los conceptos centrales de la matemática discreta: relacionan cada elemento de un conjunto
con exactamente un elemento de otro. Según cómo se reparten esas relaciones, una función puede ser inyectiva,
sobreyectiva o biyectiva. Estas propiedades no son solo un ejercicio de pizarra; muchos sistemas que usamos todos
los días funcionan correctamente solo si una de ellas se cumple.</p>
<p>En este trabajo tomamos un problema actual del Perú, el uso de líneas móviles registradas a nombre de terceros
para cometer extorsiones y estafas, y lo analizamos como un problema de funciones. Luego proponemos una solución
basada en una función biyectiva y la comprobamos con un simulador propio, SimuChip, programado en Java.</p>

<h2>2. Problemática</h2>
<p>En el Perú, buena parte de las extorsiones y estafas telefónicas se hace con chips registrados con el DNI de otra
persona. El delincuente usa la línea, pero la empresa operadora tiene registrado a alguien que muchas veces ni sabe
que esa línea existe. Cuando la policía rastrea el número, llega a una persona inocente.</p>
<p>Según notas de prensa del Organismo Supervisor de Inversión Privada en Telecomunicaciones (OSIPTEL) publicadas
en 2025, entre mayo y octubre de ese año se detectaron 309 454 líneas con datos erróneos o inconsistentes en un primer
cruce de información; se ordenó dar de baja más de 102 mil líneas con registros inconsistentes; se bloquearon más de
650 mil equipos celulares durante el estado de emergencia, y se identificaron alrededor de 200 mil usuarios con más de
10 líneas a su nombre. El Decreto Legislativo N.° 1738 limita la cantidad de líneas por persona como respuesta a estos
delitos (Congreso de la República, 2025; DPL News, 2025; Canal N, 2025; El Peruano, 2025).</p>
<p>La pregunta que guía el trabajo es: <b>¿cómo garantizar que cada línea móvil lleve a la persona que realmente la
usa?</b> Veremos que, dicha en lenguaje matemático, la pregunta es si cierta función es biyectiva.</p>

<h2>3. Objetivos</h2>
<h3>Objetivo general</h3>
<p>Modelar el registro de líneas móviles con funciones de matemática discreta y demostrar, mediante una simulación en
Java, que una correspondencia biyectiva entre líneas y contratos validados con huella permite identificar a la
persona que usa cada línea.</p>
<h3>Objetivos específicos</h3>
<ul>
  <li>Explicar con diagramas sagitales qué es una función y cuándo es inyectiva, sobreyectiva o biyectiva.</li>
  <li>Identificar qué propiedad falla en el registro actual de líneas y por qué eso permite el fraude.</li>
  <li>Relacionar el tope de líneas por persona con el principio del palomar.</li>
  <li>Construir un simulador visual, con pruebas automáticas, que clasifique funciones y reproduzca el caso.</li>
  <li>Comparar los indicadores del sistema antes y después de aplicar la solución.</li>
</ul>

<h2>4. Marco teórico</h2>
<h3>4.1 Función</h3>
<p>Dados dos conjuntos A (dominio) y B (codominio), una función f: A → B asigna a cada elemento x de A exactamente un
elemento f(x) de B. En un diagrama sagital esto significa que de cada elemento de A sale exactamente una flecha. Si de
algún elemento salen dos flechas o no sale ninguna, la relación no es función. La imagen o rango es el conjunto de
elementos de B que sí reciben al menos una flecha, y siempre está contenida en el codominio (Rosen, 2019).</p>
<h3>4.2 Función inyectiva</h3>
<p>f es inyectiva (uno a uno) si elementos distintos de A tienen imágenes distintas:</p>
<p class="formula">x₁ ≠ x₂ ⇒ f(x₁) ≠ f(x₂)</p>
<p>En el diagrama, a ningún elemento de B le llegan dos o más flechas. Que algún elemento de B quede sin flecha no
importa para esta propiedad.</p>
<h3>4.3 Función sobreyectiva</h3>
<p>f es sobreyectiva si todo elemento del codominio es imagen de alguno del dominio:</p>
<p class="formula">∀ y ∈ B, ∃ x ∈ A : f(x) = y</p>
<p>En el diagrama, a cada elemento de B le llega al menos una flecha; la imagen es igual al codominio.</p>
<h3>4.4 Función biyectiva e inversa</h3>
<p>f es biyectiva si es inyectiva y sobreyectiva a la vez: cada elemento de A tiene exactamente una pareja en B y
nadie en B queda solo. Solo las funciones biyectivas tienen inversa f⁻¹: B → A, que permite ir y volver sin
ambigüedad. Para conjuntos finitos, si f es biyectiva entonces |A| = |B|.</p>
<h3>4.5 Preimagen y principio del palomar</h3>
<p>La preimagen de un elemento y ∈ B es el conjunto f⁻¹(y) = {{ x ∈ A : f(x) = y }}, es decir, todos los elementos cuya
flecha llega a y. El principio del palomar dice que si se reparten n palomas en k nidos con n &gt; k, algún nido recibe
al menos dos. En términos de funciones: si |A| &gt; |B|, ninguna función de A en B puede ser inyectiva. En su forma
general, algún nido recibe al menos ⌈n/k⌉ palomas.</p>
<table>
  <tr><th>Tipo</th><th>Regla en palabras</th><th>Pregunta clave</th><th>Tamaños (finitos)</th></tr>
  <tr><td>Función</td><td>De cada elemento de A sale una flecha</td><td>¿Alguien de A tiene 0 o 2 flechas?</td><td>Cualquiera</td></tr>
  <tr><td>Inyectiva</td><td>Nadie de B comparte flecha</td><td>¿Alguien de B recibe 2?</td><td>|A| ≤ |B|</td></tr>
  <tr><td>Sobreyectiva</td><td>Nadie de B se queda solo</td><td>¿Alguien de B recibe 0?</td><td>|A| ≥ |B|</td></tr>
  <tr><td>Biyectiva</td><td>Parejas perfectas, con inversa</td><td>¿Las dos anteriores dan no?</td><td>|A| = |B|</td></tr>
</table>

<h2>5. Las funciones en otros sistemas reales</h2>
<p>Antes del caso principal, conviene notar que muchos sistemas cotidianos son funciones que deben cumplir una
propiedad concreta. Cuando la propiedad se rompe, aparece un problema real.</p>
<table>
  <tr><th>Sistema</th><th>Función</th><th>Propiedad necesaria</th><th>Si falla</th></tr>
  <tr><td>Identidad</td><td>Persona → DNI</td><td>Biyectiva</td><td>Suplantación de identidad</td></tr>
  <tr><td>Universidad</td><td>Alumno → código</td><td>Inyectiva</td><td>Se mezclan notas y pagos</td></tr>
  <tr><td>Transporte</td><td>Boleto → asiento</td><td>Inyectiva</td><td>Sobreventa de pasajes</td></tr>
  <tr><td>Seguridad</td><td>Mensaje → mensaje cifrado</td><td>Biyectiva</td><td>El mensaje no se puede descifrar</td></tr>
  <tr><td>Bases de datos</td><td>Fila → clave primaria</td><td>Inyectiva</td><td>Registros duplicados</td></tr>
</table>

<h2>6. Modelamiento matemático del caso</h2>
<p>Definimos tres conjuntos: L, las líneas móviles activas; P, las personas con DNI (incluido quien comete el fraude),
y C, los contratos validados con huella. Sobre ellos trabajamos con tres funciones:</p>
<table>
  <tr><th>Función</th><th>Significado</th><th>Quién la conoce</th></tr>
  <tr><td>f: L → P</td><td>Registro: titular que figura para cada línea</td><td>La operadora</td></tr>
  <tr><td>u: L → P</td><td>Uso real: quién usa de verdad cada línea</td><td>Nadie, salvo el usuario</td></tr>
  <tr><td>g: L → C</td><td>Contrato con huella asociado a cada línea</td><td>La operadora, si hay biometría</td></tr>
</table>
<p><b>f sí es función:</b> cada línea tiene un único titular registrado. <b>f no es inyectiva, y eso es
normal:</b> una persona puede tener legítimamente dos o tres chips, así que varias flechas llegan a ella. Exigir
inyectividad no tendría sentido y no resolvería el delito.</p>
<p><b>El fallo real es que f y u no coinciden:</b> existen líneas ℓ con f(ℓ) ≠ u(ℓ). Ese es el conjunto de líneas
fraudulentas. Rastrear una de ellas usando f lleva a un inocente.</p>
<p><b>El tope de líneas</b> se expresa como |f⁻¹(p)| ≤ k para toda persona p: la preimagen de cada DNI tiene como
máximo k elementos. Es el principio del palomar visto al revés: limita cuántas palomas (líneas) puede recibir cada
nido (persona). El tope no impide el fraude, pero sí que un solo DNI robado acumule cientos de chips.</p>

<h2>7. Solución propuesta</h2>
<p>Proponemos que cada línea activa quede asociada a exactamente un contrato validado con biometría, y cada contrato a
exactamente una línea. Es decir, que g: L → C sea <b>biyectiva</b>, y que la huella de cada contrato sea la de la
persona que usa la línea, de modo que f(ℓ) = u(ℓ).</p>
<ul>
  <li><b>Validación biométrica al contratar:</b> si la huella no coincide con el DNI presentado, la contratación se
  rechaza. Así no nacen nuevas líneas con f ≠ u.</li>
  <li><b>Auditoría de las líneas existentes:</b> las líneas cuyo titular no puede validarse se dan de baja; las demás
  reciben su contrato con huella. Al terminar, g pasa a ser biyectiva.</li>
  <li><b>Tope de k líneas por persona:</b> limita el daño de un DNI expuesto.</li>
</ul>
<p>Como g es biyectiva, existe g⁻¹: desde cualquier línea se llega a un contrato y, por la huella, a la persona real.
Esto coincide con las medidas que OSIPTEL empezó a aplicar en 2025: verificación biométrica presencial para quienes
tienen más de 10 líneas, baja de líneas con datos inconsistentes, la herramienta «Checa tus líneas» y la opción de
bloquear nuevas contrataciones a nombre propio (El Peruano, 2025).</p>

<h2 class="salto">8. Diseño del simulador SimuChip</h2>
<p>SimuChip es una aplicación de escritorio en Java 17+ con interfaz Swing dibujada a mano, sin librerías externas.
Se organiza en tres paquetes:</p>
<table>
  <tr><th>Paquete</th><th>Clases</th><th>Responsabilidad</th></tr>
  <tr><td>funciones</td><td>Flecha, Funciones, Diagnostico</td><td>Las definiciones matemáticas y su explicación en palabras</td></tr>
  <tr><td>caso</td><td>Simulador, Persona, Linea, Contrato, Rastreo, Evento, Resultado</td><td>El caso real: f, u, g, fraude, biometría, tope, auditoría y rastreo</td></tr>
  <tr><td>ui</td><td>Aplicacion, PanelInicio, PanelAprende, PanelSimula, Boton, Tema, Animador</td><td>La interfaz y las animaciones</td></tr>
</table>
<h3>8.1 El núcleo: contar flechas</h3>
<p>Toda la teoría se reduce a dos cuentas: cuántas flechas salen de cada elemento de A y cuántas llegan a cada
elemento de B. Una flecha es un registro de Java con origen y destino, y una relación es una lista de flechas. Este es
el método que decide si una relación es inyectiva:</p>
{codigo('''
public static <A, B> boolean esInyectiva(Set<A> dominio, Set<B> codominio, List<Flecha<A, B>> flechas) {
    if (!esFuncion(dominio, flechas)) {
        return false;
    }
    for (B b : codominio) {
        if (llegadas(b, dominio, flechas) > 1) {
            return false;          // dos elementos de A comparten a "b"
        }
    }
    return true;
}
''')}
<p>esFuncion verifica que salidas(a) sea 1 para todo a; esSobreyectiva busca algún b con llegadas(b) igual a 0;
esBiyectiva es la conjunción de las dos anteriores. El simulador del caso real reutiliza estas mismas funciones: por
ejemplo, las líneas a nombre de una persona se calculan como la preimagen f⁻¹(persona).</p>
<h3>8.2 Pruebas automáticas</h3>
<p>El proyecto incluye 74 pruebas sin dependencias externas, agrupadas en tres archivos: PruebasFunciones (los
dibujos de la teoría y casos que parecen pero no son, como una flecha que sale de un elemento fuera del dominio),
PruebasSimulador (contratación legal, fraude, biometría, tope, auditoría, reproducibilidad) y PruebasDiagnostico (las
explicaciones que ve el público). Todas pasan con <code>./probar.sh</code>.</p>
<h3>8.3 Modo Aprende</h3>
<p>El usuario dibuja flechas con clics entre A y B. Cada nodo muestra cuántas flechas salen o llegan, el problema se
marca en rojo y el panel derecho indica, para cada propiedad, si se cumple, por qué y qué línea del código lo decidió.</p>
{imagen("03-aprende-sobreyectiva.png", "Figura 1. Modo Aprende: la función es sobreyectiva pero no inyectiva, porque a «a» le llegan dos flechas. Se resalta el return false de esInyectiva.")}
{imagen("04-aprende-biyectiva-inversa.png", "Figura 2. Una biyección con su inversa f⁻¹ dibujada en líneas punteadas.")}
<h3>8.4 Modo Simula</h3>
<p>Cada día alguien intenta contratar una línea; con probabilidad 0,4 se trata del extorsionador usando el DNI de una
víctima. Los botones permiten activar la validación biométrica, el tope de tres líneas y la auditoría, y al hacer clic en
una línea se muestra el rastreo policial. La simulación usa una semilla fija, por lo que es reproducible.</p>

<h2>9. Simulación y resultados</h2>
<p>Ejecutamos dos escenarios con la semilla 19. En el primero se simulan 12 días sin controles. En el segundo, a
continuación, se activan la biometría y el tope, se simulan 4 días más y se realiza la auditoría.</p>
{imagen("06-simula-rastreo-falla.png", "Figura 3. Día 12 sin controles: 3 de 12 líneas son del extorsionador (flechas rojas), Elena supera el tope y el rastreo de L1 señala a Fabio, que es inocente.")}
{imagen("07-simula-solucion.png", "Figura 4. Día 16 con biometría, tope y auditoría: todas las líneas tienen contrato, el registro coincide al 100 % y g es biyectiva.")}
<table>
  <tr><th>Indicador</th><th>Sin control (día 12)</th><th>Con la solución (día 16)</th></tr>
  <tr><td>Registro = realidad (f = u)</td><td>75 %</td><td>100 %</td></tr>
  <tr><td>Líneas del extorsionador</td><td>3 de 12 (L1, L5, L11)</td><td>0 de 10</td></tr>
  <tr><td>Fraudes bloqueados por huella</td><td>0</td><td>2</td></tr>
  <tr><td>Contrataciones frenadas por el tope</td><td>0</td><td>1</td></tr>
  <tr><td>Rastrear una línea lleva a…</td><td>Un inocente</td><td>La persona real</td></tr>
  <tr><td>g: L → C</td><td>No es función (líneas sin contrato)</td><td>Biyectiva</td></tr>
</table>
<p>Un detalle que la simulación deja ver: Elena conserva cuatro líneas legítimas. El tope solo impide nuevas
contrataciones; las líneas antiguas que lo superan deben revisarse por separado, como hace OSIPTEL con los usuarios que
tienen más de diez.</p>

<h2>10. Limitaciones</h2>
<ul>
  <li>La simulación es un modelo simplificado: seis personas, un extorsionador y como máximo 16 líneas.</li>
  <li>La probabilidad de fraude (0,4) es un supuesto para hacer visible el problema, no una cifra oficial.</li>
  <li>Se asume que la biometría no falla; en la realidad existen errores de lectura y suplantación de huellas.</li>
  <li>Las cifras de OSIPTEL provienen de notas de prensa de 2025 y deben contrastarse con datos oficiales actualizados.</li>
</ul>

<h2>11. Conclusiones</h2>
<ol>
  <li>Que el registro de líneas sea una función no basta: puede estar bien definido y aun así no reflejar la realidad
  (f ≠ u).</li>
  <li>No toda función debe ser inyectiva. En el registro, la falta de inyectividad es normal; lo importante es elegir
  bien los conjuntos que se relacionan.</li>
  <li>El tope de líneas por persona es una aplicación directa de la preimagen y del principio del palomar.</li>
  <li>La biyección línea ↔ contrato con huella es la que da trazabilidad: gracias a su inversa, cada chip lleva a una
  persona real. En la simulación, la coincidencia entre registro y realidad pasó de 75 % a 100 %.</li>
  <li>Programar las definiciones obliga a entenderlas con precisión: toda la teoría se reduce a contar flechas que
  salen y que llegan.</li>
</ol>

<h2>12. Recomendaciones</h2>
<ul>
  <li>Revisar periódicamente las líneas a nombre propio con «Checa tus líneas» de OSIPTEL y reportar las desconocidas.</li>
  <li>Activar el bloqueo de nuevas contrataciones a nombre propio si no se planea contratar más líneas.</li>
  <li>Como mejora del simulador: incorporar errores de biometría y escenarios con miles de líneas para medir tiempos.</li>
</ul>

<h2>13. Referencias</h2>
<p>Canal N. (2025). <i>Osiptel detecta 200 mil usuarios con más de 10 líneas móviles</i>.
https://canaln.pe/actualidad/osiptel-detecta-200-mil-usuarios-mas-10-lineas-moviles-n488632</p>
<p>Congreso de la República del Perú. (2025). <i>OSIPTEL bloquea más de 650 mil celulares y alerta crecimiento del uso
delictivo de líneas móviles</i>. https://comunicaciones.congreso.gob.pe/damos-cuenta/osiptel-bloquea-mas-de-650-mil-celulares-y-alerta-crecimiento-del-uso-delictivo-de-lineas-moviles/</p>
<p>DPL News. (2025). <i>Perú: Osiptel dispuso la baja de más de 102 mil líneas con registros inconsistentes durante el
estado de emergencia</i>. https://dplnews.com/peru-osiptel-dispuso-baja-102-mil-lineas-registros-inconsistentes-estado-de-emergencia/</p>
<p>El Peruano. (2025). <i>Conoce los beneficios de contar solo con siete líneas por persona en el servicio móvil</i>.
https://www.elperuano.pe/noticia/294498-conoce-los-beneficios-de-contar-solo-con-siete-lineas-por-persona-en-el-servicio-movil</p>
<p>El Peruano. (2025). <i>¿Puedes bloquear nuevas líneas a tu nombre? Osiptel habilita restricción contra
contrataciones no autorizadas</i>. https://elperuano.pe/noticia/288845-puedes-bloquear-nuevas-lineas-a-tu-nombre-osiptel-habilita-restriccion-contra-contrataciones-no-autorizadas</p>
<p>Rosen, K. H. (2019). <i>Discrete mathematics and its applications</i> (8.ª ed.). McGraw-Hill Education.</p>

<h2 class="salto">Anexo A: guion de la exposición (10 a 12 minutos, 4 integrantes)</h2>
<table>
  <tr><th>Integrante</th><th>Diapositivas</th><th>Tiempo</th><th>Idea que debe quedar</th></tr>
  <tr><td>1</td><td>1 Portada · 2 Gancho · 3 Máquina · 4 Diagrama sagital · 5 No es función</td><td>2:30</td>
      <td>Una función es una máquina: cada entrada da exactamente una salida.</td></tr>
  <tr><td>2</td><td>6 Inyectiva · 7 Sobreyectiva · 8 Biyectiva · 9 Ninguna · 10 Resumen · 11 Otros casos · 12 Palomar</td><td>3:00</td>
      <td>Contar flechas: nadie comparte, nadie queda solo, parejas perfectas.</td></tr>
  <tr><td>3</td><td>13 Cifras · 14 El engaño · 15 Modelo · 16 Solución · 17 SimuChip · 18 Modo Aprende</td><td>3:00</td>
      <td>El problema no es tener varias líneas: es que f ≠ u. La solución es g biyectiva.</td></tr>
  <tr><td>4</td><td>19 Sin control · 20 Con solución · 21 Código · 22 Resultados · 23 Demo · 24 Conclusiones · 25 Fuentes</td><td>3:00</td>
      <td>La simulación lo demuestra: de 75 % a 100 %, y el rastreo llega a la persona real.</td></tr>
</table>
<h3>Antes de la exposición</h3>
<ul>
  <li>Completar la portada de la PPT y del informe con curso, docente, grupo e integrantes.</li>
  <li>Probar SimuChip en la laptop de la expo (<code>java -jar SimuChip.jar</code>, Java 17 o superior) y llevar las
  capturas por si falla.</li>
  <li>Ensayar con cronómetro: si pasan de 12 minutos, la diapositiva 23 (demo de «Checa tus líneas») es la primera en
  saltarse.</li>
  <li>Verificar en la web de OSIPTEL si hay cifras más recientes y el tope vigente de líneas por persona.</li>
  <li>Preparar respuestas: «¿por qué no exigir que f sea inyectiva?» (porque tener dos chips es legal); «¿y si la
  biometría falla?» (es una limitación que reconocemos); «¿por qué una semilla fija?» (para que la simulación sea
  reproducible).</li>
</ul>

<h2>Anexo B: cómo ejecutar SimuChip</h2>
{codigo('''
# Abrir la aplicación (Java 17 o superior)
java -jar SimuChip.jar          # o ./ejecutar.sh

# Compilar y correr las 74 pruebas
./probar.sh

# Regenerar las capturas del informe sin abrir ventanas
java -Djava.awt.headless=true -cp out simuchip.Capturas capturas
''')}
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
                                "font-family:sans-serif'>SimuChip · Matemática Discreta · "
                                "<span class='pageNumber'></span> / <span class='totalPages'></span></div>")
        nav.close()
    print(f"PDF listo: {SALIDA}")


if __name__ == "__main__":
    main()
