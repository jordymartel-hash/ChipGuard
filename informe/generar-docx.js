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
function figura(archivo, titulo, { ancho = 15, fte = "Captura de la aplicación ChipGuard (elaboración propia)." } = {}) {
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
function celda(t, ancho, cab = false, keepNext = false) {
  return new d.TableCell({ width: { size: ancho, type: d.WidthType.DXA },
    shading: cab ? { fill: "E8E3E4", type: d.ShadingType.CLEAR, color: "auto" } : undefined,
    margins: { top: 50, bottom: 50, left: 90, right: 90 },
    children: String(t).split("\n").map((l) => new d.Paragraph({ keepNext, spacing: { line: 240, after: 0 }, children: runs(l, { size: 20, bold: cab }) })) });
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
      children: lineas.map((l) => new d.Paragraph({ spacing: { line: 250, after: 0 },
        children: [new d.TextRun({ text: l || " ", font: "Courier New", size: 19, color: l.trim().startsWith("//") ? "7A6E71" : "1F1F1F" })] })) })] })] }));
  C.push(new d.Paragraph({ spacing: { after: 160, line: 240 }, children: [] }));
}
function teorema(etq, enunciado, prueba) {
  P(`**${etq}.** ${enunciado}`);
  if (prueba) P(`*Demostración.* ${prueba} ∎`, { indent: { left: cm(0.75) } });
}

// ============================ CONTENIDO ============================
let TOTAL = 0, TSUITES = 0;                                       // pruebas que pasan; se calcula de ./probar.sh en construir()
function resumen() {
  H1("RESUMEN", { salto: false });
  P(`En el Perú, cada línea móvil se registra a nombre de una persona mediante su DNI; sin embargo, OSIPTEL detectó 309 454 líneas con datos inconsistentes entre mayo y octubre de 2025, y muchas se usan para extorsionar desde números cuyo titular es una persona inocente. El objetivo de este trabajo es modelar el registro de líneas como una función entre conjuntos finitos, identificar con precisión dónde está la falla y proponer y validar una solución. La metodología combina el modelado matemático —dos funciones de Chips en Personas, *f* (registro) y *u* (uso real), el conteo por fibras y el principio del palomar— con la implementación en Java de un verificador de propiedades de funciones y de una simulación de una tienda de chips. Se demuestra que la falla no está en el tipo de *f*, sino en que *f*(c) ≠ *u*(c) para los chips fraudulentos, y que la trazabilidad exige una biyección entre los chips activos y las autorizaciones de su titular. Con ese fundamento se propone ChipGuard, un servicio que solo activa un chip cuando el dueño del DNI lo aprueba. En la simulación, aun con decisiones perfectas en el mostrador, dos de seis atenciones terminan con un chip en manos criminales; con ChipGuard, ninguna. Las ${TOTAL} pruebas automáticas pasan y la medición del verificador confirma un costo cuadrático que orienta su diseño a escala real.`);
  P("**Palabras clave:** funciones biyectivas; principio del palomar; trazabilidad; telefonía móvil; simulación.", { alignment: d.AlignmentType.LEFT });
}

function cuerpo(tiempos, suites, traza, escenarios) {
  // ======================= I =======================
  H1("I. INTRODUCCIÓN", { salto: false });
  H2("1.1 Contextualización del problema");
  P(`En el Perú, cada línea móvil se registra a nombre de una persona mediante su DNI y la verificación de su huella contra la base de datos de RENIEC. Ese registro debería permitir que, ante un delito cometido desde un número, las autoridades lleguen a la persona responsable. En la práctica no siempre ocurre así: entre mayo y octubre de 2025 se detectaron 309 454 líneas con datos inconsistentes {congreso}, se dieron de baja más de 102 mil {dplbaja} y unas 200 mil personas tenían más de diez líneas a su nombre {canaln}. Esas líneas se usan para extorsionar y estafar, y cuando la policía rastrea el número llega a una persona que nunca contrató el chip.`);
  P(`Desde el punto de vista informático, se trata de un problema de **integridad de datos y trazabilidad** en un sistema de registro: la tabla que asocia cada línea con su titular contiene pares que no corresponden a la realidad. La herramienta natural para describirlo es la de las funciones entre conjuntos finitos, estudiada en el curso de Estructuras Discretas I.`);
  tabla("Cifras oficiales sobre líneas móviles con datos inconsistentes en el Perú", [
    ["Dato", "Fuente"],
    ["309 454 líneas con datos inconsistentes detectadas entre mayo y octubre de 2025", "Congreso de la República {congreso}"],
    ["Más de 102 mil líneas dadas de baja por registros inconsistentes", "DPL News {dplbaja}"],
    ["Más de 650 mil celulares bloqueados durante el estado de emergencia", "Congreso de la República {congreso}"],
    ["Alrededor de 200 mil personas con más de 10 líneas a su nombre", "Canal N {canaln}"],
  ], [3, 1.3], { fte: "Elaboración propia con datos de las fuentes citadas." });
  P(`Existen varias formas de registrar líneas a nombre de otra persona. La más sencilla de explicar, sobre la que OSIPTEL ha advertido en sus operativos contra la venta ambulatoria {dplambulante}, es la **doble huella**: el vendedor le dice al cliente que la huella «no salió» y le pide ponerla otra vez; con esa segunda lectura, que es válida, activa un chip adicional a su nombre y lo vende aparte (Figura 1). También se han documentado bandas que activaban chips con datos de terceros {americatv} y casos de huellas clonadas en moldes de silicona {bloomberg}.`);
  figura("1c-historia.png", "La doble huella: con la segunda lectura se registra un chip extra a nombre de Ana.");
  H2("1.2 Justificación");
  P(`El Estado ya tomó medidas: el portal «Checa tus líneas» permite consultar las líneas registradas con el propio DNI {peruanocheca}, se habilitó la restricción de nuevas contrataciones {peruanobloqueo} y la Resolución 000116-2025-CD/OSIPTEL dispuso validar la identidad de quienes tienen más de diez líneas {osiptel116}. Todas comparten una limitación: el ciudadano debe tomar la iniciativa de revisar y el chip fraudulento ya está activo cuando se detecta. Un modelo matemático preciso permite separar qué parte del problema es inevitable (por ejemplo, que una persona tenga varias líneas) y qué parte es la falla real, y deducir de ahí qué propiedad debe garantizar cualquier solución. Además, el trabajo integra la teoría del curso con un desarrollo computacional verificable.`);
  H2("1.3 Delimitación");
  lista([
    "**Ámbito:** el registro de líneas móviles en el Perú y las cifras públicas de 2025.",
    "**Modelo:** conjuntos finitos de chips y personas; funciones, fibras (preimágenes), biyecciones y el principio del palomar.",
    "**Desarrollo:** una aplicación en Java que verifica propiedades de funciones y simula una tienda de chips con una fila fija de seis clientes. ChipGuard se diseña a nivel de arquitectura; su servidor real no se implementa.",
    "**Fuera de alcance:** estimar tasas reales de fraude, la integración con operadoras y aspectos legales de una norma de OSIPTEL.",
  ]);
  H2("1.4 Pregunta de investigación");
  P("¿Qué propiedad, expresada en términos de funciones entre conjuntos finitos, debe cumplir el registro de líneas móviles para que el rastreo de un número lleve a una persona responsable, y en qué medida un mecanismo de autorización del titular (ChipGuard) la garantiza en una simulación?");

  // ======================= II =======================
  H1("II. ANTECEDENTES HISTÓRICOS Y APORTES DE AUTORES");
  P(`La noción moderna de función como **asignación arbitraria** —no necesariamente dada por una fórmula— se atribuye a Dirichlet, que en 1837 la formuló al estudiar series de Fourier: a cada valor de *x* le corresponde un único valor de *y*, sin importar cómo se calcule {dirichlet1837}. Es exactamente la idea que usamos: el registro de líneas no es una fórmula, es una tabla, y aun así es una función porque cada chip tiene un único titular.`);
  P(`El **principio del palomar** se conoce en alemán como *Schubfachprinzip* («principio de los cajones») por su uso en un trabajo de Dirichlet de 1842 sobre aproximación de números reales {dirichlet1842}. Rittaud y Heeffer mostraron, sin embargo, que el argumento aparece en textos franceses del siglo XVII, unos doscientos años antes {rittaud}; el nombre de Dirichlet se debe más a su uso sistemático que a la invención. En este trabajo, el palomar fundamenta el tope de líneas por DNI.`);
  P(`Cantor usó en 1878 las **correspondencias uno a uno** (biyecciones) para comparar el tamaño de conjuntos: dos conjuntos tienen la misma potencia si existe una biyección entre ellos, y con esa idea probó que el segmento y el cuadrado tienen tantos puntos como el otro {cantor}. Dedekind, en 1888, trató las aplicaciones (*Abbildungen*) como objetos en sí mismos, distinguió las que no envían dos elementos al mismo (*ähnliche Abbildungen*, hoy inyectivas) y definió con ellas los conjuntos infinitos {dedekind}. Los términos **inyectiva, sobreyectiva y biyectiva** que usamos hoy fueron fijados por el grupo Bourbaki en su teoría de conjuntos {bourbaki}.`);
  P(`En informática, el palomar explica por qué las funciones de dispersión (*hash*) tienen colisiones inevitables cuando hay más claves que posiciones, y por qué una tabla de dispersión debe resolverlas {knuth}. El registro de líneas es, en el fondo, una tabla que asigna claves (números) a titulares; la diferencia es que aquí una «colisión» —varias líneas para una persona— es legal y lo ilegal es que la tabla apunte a quien no corresponde.`);
  tabla("Aportes de autores y su conexión con el trabajo", [
    ["Autor y año", "Aporte", "Uso en este trabajo"],
    ["Dirichlet (1837) {dirichlet1837}", "Función como asignación arbitraria", "El registro es una función aunque sea una tabla"],
    ["Dirichlet (1842) {dirichlet1842}; Rittaud y Heeffer (2014) {rittaud}", "Principio del palomar y su historia", "Fundamento del tope de líneas por DNI"],
    ["Cantor (1878) {cantor}", "Biyecciones para comparar conjuntos", "La solución exige una biyección chip ↔ autorización"],
    ["Dedekind (1888) {dedekind}", "Aplicaciones inyectivas como objeto de estudio", "Clasificación de f y u; inversa"],
    ["Bourbaki (1954) {bourbaki}", "Terminología inyectiva, sobreyectiva, biyectiva", "Notación del marco teórico"],
    ["Knuth (1998) {knuth}", "Colisiones en tablas de dispersión", "Analogía registro–tabla y análisis de costo"],
  ], [1.4, 1.5, 1.6]);

  // ======================= III =======================
  H1("III. MARCO TEÓRICO");
  P(`Se sigue la notación de Rosen {rosen} y Epp {epp}. Para un conjunto finito *A*, |*A*| es su número de elementos.`);
  H2("3.1 Función, imagen y fibra");
  teorema("Definición 1 (función)", "Sean *A* y *B* conjuntos. Una función *f* : *A* → *B* asigna a cada elemento de *A* exactamente un elemento de *B*. *A* es el dominio, *B* el codominio y *f*(*A*) = {*f*(*a*) : *a* ∈ *A*} la imagen.");
  formula("f : A → B   tal que   ∀a ∈ A, ∃! b ∈ B : f(a) = b");
  teorema("Definición 2 (fibra o preimagen)", "Para *b* ∈ *B*, la fibra de *b* es *f*⁻¹(*b*) = {*a* ∈ *A* : *f*(*a*) = *b*}, el conjunto de elementos de *A* cuya flecha llega a *b*. En un diagrama sagital, |*f*⁻¹(*b*)| es el número de flechas que llegan a *b*.");
  P(`Ejemplo: si *C* = {c₁, c₂, c₃} son chips, *P* = {Ana, Luis} personas y *f*(c₁) = *f*(c₂) = Ana, *f*(c₃) = Luis, entonces *f*⁻¹(Ana) = {c₁, c₂} y *f*⁻¹(Luis) = {c₃}. Una relación en la que c₁ tuviera dos titulares, o c₃ ninguno, no sería función (Figura ${nFig + 1}).`);
  figura("2a-matematica.png", "El registro de chips como función: de cada chip sale exactamente una flecha.");
  H2("3.2 Funciones inyectivas");
  teorema("Definición 3", "*f* es inyectiva si elementos distintos tienen imágenes distintas: *x*₁ ≠ *x*₂ ⇒ *f*(*x*₁) ≠ *f*(*x*₂). Equivalentemente, por contraposición, *f*(*x*₁) = *f*(*x*₂) ⇒ *x*₁ = *x*₂.");
  P(`Caso cotidiano: la asignación pasajero → asiento de un bus es inyectiva; pueden sobrar asientos vacíos. Si la empresa vende el mismo asiento dos veces (sobreventa), a ese asiento le llegan dos flechas y la función deja de ser inyectiva (Figura ${nFig + 1}).`);
  figura("2d-matematica.png", "Caso real de función inyectiva y su ruptura por sobreventa.");
  H2("3.3 Funciones sobreyectivas");
  teorema("Definición 4", "*f* es sobreyectiva si su imagen es todo el codominio: ∀*y* ∈ *B*, ∃*x* ∈ *A* : *f*(*x*) = *y*.");
  P(`Caso cotidiano: la asignación alumno → grupo de exposición es sobreyectiva porque ningún grupo queda vacío; como hay más alumnos que grupos, no puede ser inyectiva (Figura ${nFig + 1}).`);
  figura("2f-matematica.png", "Caso real de función sobreyectiva.");
  H2("3.4 Funciones biyectivas e inversa");
  teorema("Definición 5", "*f* es biyectiva si es inyectiva y sobreyectiva. Una función *g* : *B* → *A* es inversa de *f* si *g*(*f*(*a*)) = *a* para todo *a* ∈ *A* y *f*(*g*(*b*)) = *b* para todo *b* ∈ *B*; se escribe *g* = *f*⁻¹.");
  teorema("Teorema 1", "*f* : *A* → *B* tiene inversa si y solo si es biyectiva.",
    "(⇒) Sea *g* la inversa. Si *f*(*a*₁) = *f*(*a*₂), aplicando *g* se obtiene *a*₁ = *g*(*f*(*a*₁)) = *g*(*f*(*a*₂)) = *a*₂, luego *f* es inyectiva. Para *b* ∈ *B*, el elemento *a* = *g*(*b*) cumple *f*(*a*) = *b*, luego *f* es sobreyectiva. (⇐) Si *f* es biyectiva, para cada *b* existe un *a* con *f*(*a*) = *b* (sobreyectividad) y es único (inyectividad); definir *g*(*b*) = *a* da una función que cumple ambas igualdades.");
  P(`Caso cotidiano: persona ↔ DNI. Cada persona tiene un DNI y cada DNI pertenece a una sola persona; por eso con el DNI se encuentra a la persona y viceversa (Figura ${nFig + 1}). Es la propiedad que la policía supone, equivocadamente, en el registro de líneas.`);
  figura("2h-matematica.png", "Caso real de función biyectiva.");
  H2("3.5 Conteo por fibras");
  teorema("Teorema 2", "Si *A* y *B* son finitos y *f* : *A* → *B*, entonces |*A*| = Σ |*f*⁻¹(*b*)|, donde la suma recorre todos los *b* ∈ *B*.",
    "Como *f* es función, cada *a* ∈ *A* pertenece a exactamente una fibra, la de *f*(*a*). Por tanto las fibras son disjuntas dos a dos y su unión es *A*. Por la regla de la suma, el tamaño de *A* es la suma de los tamaños de las fibras.");
  teorema("Corolario 1", "*f* es inyectiva si y solo si |*f*⁻¹(*b*)| ≤ 1 para todo *b*; es sobreyectiva si y solo si |*f*⁻¹(*b*)| ≥ 1 para todo *b*. En consecuencia, si *f* es inyectiva, |*A*| ≤ |*B*|; si es sobreyectiva, |*A*| ≥ |*B*|; y si es biyectiva, |*A*| = |*B*|.",
    "La equivalencia es la definición leída sobre las fibras: dos elementos con la misma imagen *b* son dos elementos en *f*⁻¹(*b*), y *b* está en la imagen si y solo si su fibra no es vacía. Las desigualdades salen de sumar las cotas en el Teorema 2: Σ|*f*⁻¹(*b*)| ≤ Σ1 = |*B*|, o ≥ |*B*|.");
  P("Este corolario es la base del verificador implementado (sección 5.4): para decidir si una relación es inyectiva o sobreyectiva basta **contar las flechas que llegan** a cada elemento de *B*.");
  H2("3.6 Principio del palomar");
  teorema("Teorema 3 (palomar)", "Si |*A*| > |*B*|, ninguna función *f* : *A* → *B* es inyectiva. Dicho con cajones: si *n* objetos se reparten en *k* cajones y *n* > *k*, algún cajón recibe más de un objeto {rosen}.",
    "Si *f* fuera inyectiva, por el Corolario 1 sería |*A*| ≤ |*B*|, lo que contradice la hipótesis.");
  teorema("Teorema 4 (palomar generalizado)", "Si |*A*| = *n*, |*B*| = *k* ≥ 1 y *f* : *A* → *B*, existe *b* ∈ *B* con |*f*⁻¹(*b*)| ≥ ⌈*n*/*k*⌉.",
    "Supóngase que toda fibra cumple |*f*⁻¹(*b*)| ≤ ⌈*n*/*k*⌉ − 1 < *n*/*k*. Por el Teorema 2, *n* = Σ|*f*⁻¹(*b*)| < *k* · *n*/*k* = *n*, contradicción.");
  teorema("Corolario 2 (tope de líneas)", "Si el registro garantiza |*f*⁻¹(*p*)| ≤ *k* para toda persona *p*, entonces |*C*| ≤ *k*·|*P*|, y para una persona que ya tiene *k* chips, el chip *k* + 1 no puede registrarse a su nombre.",
    "La primera parte es el Teorema 2 con cada sumando acotado por *k*. Para la segunda, registrar un chip más a nombre de *p* haría |*f*⁻¹(*p*)| = *k* + 1 > *k*.");
  formula("|f⁻¹(p)| ≤ k   para toda persona p ∈ P");
  figura("2j-matematica.png", "El palomar explicado con cajones: 5 chips y 3 cajones.");
  H2("3.7 Composición");
  teorema("Teorema 5", "Si *g* : *A* → *B* y *h* : *B* → *C* son inyectivas, *h* ∘ *g* es inyectiva.",
    "Si *h*(*g*(*a*₁)) = *h*(*g*(*a*₂)), por inyectividad de *h* se tiene *g*(*a*₁) = *g*(*a*₂), y por la de *g*, *a*₁ = *a*₂.");
  P("Este resultado se usa en la sección 5.2: encadenar «chip → autorización» y «autorización → solicitud» conserva la inyectividad, de modo que ninguna autorización puede servir para dos chips.");

  H2("3.8 Inducción matemática");
  teorema("Teorema 6 (principio de inducción)", "Sea *P*(*n*) una afirmación sobre los enteros *n* ≥ 1. Si *P*(1) es verdadera (caso base) y, para todo *n* ≥ 1, la verdad de *P*(1), …, *P*(*n*) implica la de *P*(*n* + 1) (paso inductivo), entonces *P*(*n*) es verdadera para todo *n* ≥ 1 {rosen}.");
  P("Se usa en la sección 5.2 para garantizar que **todo** chip de una persona, no solo el primero, queda ligado a una validación hecha por ella misma.");
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
  H2("5.1 Enfoque");
  P("El trabajo es aplicado y sigue cuatro pasos: (1) revisión de fuentes oficiales y periodísticas para caracterizar el problema; (2) modelado con funciones y demostración de las propiedades necesarias; (3) diseño de la solución a partir de la condición matemática; y (4) implementación y validación con pruebas automáticas y una simulación. Cada resultado computacional se contrasta con un resultado teórico (sección 7.4).");
  H2("5.2 Modelo matemático");
  P("Sean *C* el conjunto de chips activos y *P* el conjunto de personas con DNI. Se definen dos funciones:");
  tabla("Las dos funciones del modelo", [
    ["Función", "Qué asigna", "Quién la conoce"],
    ["*f* : *C* → *P* (registro)", "A cada chip, la persona a cuyo nombre está", "La operadora y la policía"],
    ["*u* : *C* → *P* (uso real)", "A cada chip, la persona que realmente lo usa", "Nadie, salvo quien lo usa"],
  ], [1.2, 1.8, 1.2]);
  P("Ambas son funciones (cada chip tiene un único titular y un único usuario). Ninguna debe ser inyectiva, porque tener varias líneas es legal, ni sobreyectiva, porque no toda persona tiene un chip. El problema no está en el tipo de *f*, sino en el conjunto de chips donde las dos funciones no coinciden:");
  formula("D = { c ∈ C : f(c) ≠ u(c) }  ≠  ∅");
  P(`Cuando la policía rastrea un número aplica *f* y llega a *f*(c), el titular en papel, en lugar de *u*(c), el responsable. En el caso de Ana, *f*(…935) = Ana pero *u*(…935) = extorsionador (Figura ${nFig + 1}). No todo chip de *D* es delito (un padre puede registrar la línea de su hijo), por lo que la condición correcta no es *f* = *u*, sino que todo chip activo haya sido **autorizado por su titular**.`);
  figura("2i-matematica.png", "El problema: las dos flechas de un mismo chip no llegan a la misma persona.");
  P("Sea *K* el conjunto de autorizaciones usadas; cada autorización κ ∈ *K* la emite una persona *t*(κ) para una solicitud concreta y es de un solo uso. ChipGuard impone la regla: **un chip c se activa solo si existe una autorización κ emitida por f(c) para c**.");
  teorema("Proposición 1", "Bajo esa regla, la función *g* : *C* → *K* que asigna a cada chip la autorización que lo activó es biyectiva y cumple *t*(*g*(c)) = *f*(c) para todo c ∈ *C*.",
    "*g* es función porque cada chip activo se activó con exactamente una autorización. Es inyectiva porque una autorización es de un solo uso y está ligada a una sola solicitud (Teorema 5 sobre «chip → autorización → solicitud»): dos chips no pueden compartirla. Es sobreyectiva porque *K* contiene solo autorizaciones usadas, y cada una activó un chip. La igualdad *t*(*g*(c)) = *f*(c) es la regla misma. Por el Teorema 1 existe *g*⁻¹: de cada autorización se llega a su chip y de cada chip a una persona real que dijo «sí, fui yo».");
  P("Falta un caso: la persona que saca su **primer chip** no tiene otro celular con línea donde recibir la alerta. La respuesta es que el canal de aprobación es el propio chip nuevo, validado en el mostrador, y desde ahí cada chip nuevo se aprueba desde uno ya validado.");
  teorema("Proposición 2 (primer chip, por inducción)", "Sea *p* una persona y c₁, c₂, … sus chips en orden de activación. Si (i) c₁ se activa solo cuando una llamada al número nuevo es contestada en el mostrador por *p*, con su huella validada, y un DNI sin líneas puede activar un solo chip en sus primeras 72 horas, y (ii) cada cₙ₊₁ se activa solo con la aprobación de *p* desde un chip cⱼ con *j* ≤ *n*, entonces todo chip cₙ está ligado por una cadena de aprobaciones a una validación presencial de *p*.",
    "Por inducción sobre *n* (Teorema 6). Caso base: c₁ fue validado en persona por (i). Paso inductivo: si c₁, …, cₙ están ligados a una validación de *p*, cₙ₊₁ se aprobó desde algún cⱼ con *j* ≤ *n*, que por hipótesis está ligado, y por tanto cₙ₊₁ también. Además, la doble huella sobre un DNI nuevo pediría dos chips en las primeras 72 horas; con tope temporal *k* = 1, por el palomar (Teorema 3) el segundo no tiene «cajón» y no se activa");
  P(`Con la regla y el tope del Corolario 2 se obtiene el diseño: un chip fraudulento requeriría la aprobación del titular, y aunque la obtuviera mediante engaño, no podría superar *k* líneas por DNI (Figura ${nFig + 1}).`);
  figura("2k-matematica.png", "La solución: las flechas del registro y del uso real llegan a la misma persona.");
  H2("5.3 Datos y entradas");
  P("Las entradas son de tres tipos: (a) las cifras públicas de la Tabla 1, que caracterizan el problema; (b) conjuntos finitos pequeños para el verificador (por ejemplo *A* = {1, 2, 3} y *B* = {a, b, c, d}, más casos borde como el dominio vacío); y (c) la fila fija de seis clientes de la simulación, diseñada para cubrir los cuatro casos posibles: dueño honesto, impostor con DNI ajeno, dueño honesto al que el vendedor le saca un chip extra y dueño al que se rechaza por error.");
  const tFila = tabla("La fila de clientes de la simulación", [
    ["Cliente", "DNI que entrega", "¿Es el dueño?", "¿El vendedor saca un chip extra?"],
    ["Luis", "Luis", "Sí", "No"], ["Encapuchado", "Pedro", "No", "No"], ["Rosa", "Rosa", "Sí", "Sí (doble huella)"],
    ["Carmen", "Carmen", "Sí", "No"], ["Impostora", "Juana", "No", "No"], ["Jorge", "Jorge", "Sí", "Sí (doble huella)"],
  ], [1, 1, 1, 1.6], { fte: "Elaboración propia (`Tienda.filaDelDia()` en el código)." });
  H2("5.4 Algoritmos");
  P("El verificador representa una relación como una lista de flechas (*a*, *b*) y aplica el Corolario 1: cuenta las flechas que salen de cada elemento de *A* y las que llegan a cada elemento de *B*.");
  codigo("Pseudocódigo del verificador: todo se reduce a contar flechas", `¿ES FUNCIÓN?
   Para cada elemento a de A:
      contar las flechas que SALEN de a
      si no sale exactamente 1   →   NO es función

¿ES INYECTIVA?   (primero debe ser función)
   Para cada elemento b de B:
      contar las flechas que LLEGAN a b         // es |f⁻¹(b)|
      si llegan 2 o más          →   NO es inyectiva

¿ES SOBREYECTIVA?   (primero debe ser función)
   Para cada elemento b de B:
      si no le llega ninguna     →   NO es sobreyectiva

¿ES BIYECTIVA?
   es inyectiva   Y   es sobreyectiva`);
  P("**Corrección.** Si esFuncion devuelve VERDADERO, la relación es una función de *A* en *B*, y llegadas(*b*) = |*f*⁻¹(*b*)|; entonces esInyectiva y esSobreyectiva son traducciones literales del Corolario 1. **Costo.** Cada llamada a salidas o llegadas recorre las |*F*| flechas, así que esFuncion cuesta O(|*A*|·|*F*|) y esInyectiva O((|*A*| + |*B*|)·|*F*|). Si *f* es función, |*F*| = |*A*|, y para |*A*| = |*B*| = *n* el costo es O(*n*²). Contando todas las llegadas en un mapa de dispersión en una sola pasada se obtendría O(*n*) {cormen}; la versión cuadrática se eligió por legibilidad didáctica y su costo se mide en la sección 7.5.");
  codigo("Pseudocódigo de ChipGuard cuando llega una solicitud de chip", `1. ¿La huella es válida en RENIEC?
      no  →  RECHAZAR
2. ¿El DNI ya tiene k chips?                    // Corolario 2
      sí  →  RECHAZAR por tope
3. ¿Es su PRIMER chip?                          // caso base
      sí  →  llamar al número nuevo en el mostrador
             contesta el cliente  →  ACTIVAR (solo 1 chip en 72 h)
             no contesta          →  NO SE ACTIVA
4. Si no es el primero:                         // paso inductivo
      enviar alerta a su chip ya validado
      «Sí, fui yo» + huella   →  ACTIVAR
      «No fui yo»             →  BLOQUEAR y reportar a OSIPTEL
      sin respuesta 10 min    →  NO SE ACTIVA`);
  H2("5.5 Diseño de la propuesta ChipGuard");
  P("ChipGuard es un servicio de protección de identidad móvil con una sola regla: **ningún chip se activa con tu DNI sin tu permiso**. No reemplaza la verificación biométrica de RENIEC; añade un paso posterior que materializa la Proposición 1.");
  tabla("Las tres partes de ChipGuard", [
    ["Parte", "Para quién", "Qué hace"],
    ["App móvil (Android e iPhone)", "El ciudadano", "Recibe alertas al instante, permite aprobar con la huella del celular o bloquear, y muestra todos los chips a su nombre"],
    ["Página web", "Quien no tiene smartphone", "Consulta y bloqueo con DNI y un código enviado por SMS"],
    ["Servidor central", "Operadoras y OSIPTEL", "Recibe cada solicitud, espera la respuesta del dueño y ordena activar o bloquear"],
  ], [1.2, 1, 2.4]);
  figura("Qa-que-es.png", "Las tres partes de ChipGuard.");
  P(`El sistema tiene seis piezas: cuatro ya existen en el Perú (la tienda con lector de huella, la operadora, RENIEC y OSIPTEL) y ChipGuard agrega dos, el servidor y la app del dueño (Figura ${nFig + 1}). Una solicitud sigue siete pasos (Figura ${nFig + 2}):`);
  figura("3f-arquitectura.png", "Componentes de ChipGuard; en verde, lo que se agrega.");
  lista([
    "La tienda envía la solicitud a la operadora: DNI, huella, tienda y hora.",
    "La operadora valida la huella con RENIEC, como ocurre hoy.",
    "La operadora consulta al servidor ChipGuard; la línea queda en espera.",
    "ChipGuard envía una notificación al celular del dueño del DNI.",
    "El dueño responde «Sí, fui yo» (con su huella en el celular) o «No fui yo».",
    "ChipGuard responde a la operadora: activar o bloquear.",
    "Queda el registro; si fue un bloqueo, OSIPTEL recibe un reporte con la tienda y la hora.",
  ], true);
  figura("3g-arquitectura.png", "Los siete pasos de una solicitud aprobada.");
  P("**Primer chip.** Quien saca su primer chip siempre tiene un celular donde ponerlo, y ese chip nuevo se vuelve su canal de ChipGuard: (1) en el mostrador, ChipGuard llama al número nuevo y el cliente contesta en su celular, lo que prueba que el chip está en su mano; (2) un DNI sin líneas solo puede activar un chip en sus primeras 72 horas, de modo que el chip extra de la doble huella no se activa; y (3) desde ahí, cada chip nuevo se aprueba desde el ya validado (Proposición 2). Para el cliente el costo es una llamada de unos 30 segundos; si necesita dos chips el primer día, los aprueba en una agencia con su huella.");
  figura("3j-arquitectura.png", "El caso del primer chip: el chip nuevo se vuelve la llave.");
  P("**Casos especiales.** Sin respuesta en 10 minutos, la línea no se activa. Sin smartphone, se usa un SMS con código o una llamada automática. Sin celular, el dueño aprueba en persona, con su huella, en una agencia. **Datos y privacidad.** ChipGuard guarda el DNI cifrado, el identificador del celular para enviar avisos y el historial de solicitudes; nunca guarda la huella, que sigue validándose en RENIEC, ni llamadas, mensajes o ubicación.");
  figura("Qd-que-es.png", "Las cuatro pantallas de la app ChipGuard.");
  tabla("Comparación entre las medidas actuales y ChipGuard", [
    ["Aspecto", "Hoy («Checa tus líneas» y restricción)", "Con ChipGuard"],
    ["Cómo ves tus chips", "Entrando a la web cuando te acuerdas", "Siempre en tu celular"],
    ["Cuándo te enteras", "Cuando revisas, o por un SMS si llega", "Al instante, antes de la activación"],
    ["El chip fraudulento", "Se activa y luego se reclama", "Espera tu permiso; si no apruebas, no se activa"],
    ["Bloquear nuevas líneas", "Restricción general", "Restricción general y decisión chip por chip"],
    ["La tienda", "Reclamo aparte", "Reporte automático a OSIPTEL"],
  ], [1, 1.6, 1.6]);
  tabla("Tecnologías propuestas para cada capa", [
    ["Capa", "Tecnología"],
    ["App del dueño", "Android e iOS; notificaciones push; aprobación con la huella del propio celular"],
    ["Notificaciones", "Firebase Cloud Messaging (Android), APNs (iPhone) y SMS de respaldo"],
    ["Servidor", "API REST en Java (Spring Boot) que decide esperar, activar o bloquear"],
    ["Base de datos", "PostgreSQL: DNI cifrado, historial de solicitudes y tope de chips por DNI"],
    ["Integración", "API segura (HTTPS y firma digital) con operadoras y OSIPTEL"],
  ], [1, 3]);
  H2("5.6 Herramientas y decisiones");
  tabla("Herramientas elegidas y justificación", [
    ["Decisión", "Elección", "Justificación"],
    ["Lenguaje", "Java 17", "Tipado estático y genéricos para representar *A* y *B* de cualquier tipo; lenguaje que domina todo el equipo"],
    ["Interfaz", "Swing y Java2D dibujado a mano", "Sin dependencias externas: funciona sin internet en el aula y en cualquier sistema"],
    ["Pruebas", "Ejecutor propio (`Prueba.java`) con `./probar.sh`", "Cero dependencias; cada prueba imprime su nombre en lenguaje claro, útil como evidencia"],
    ["Estructura de datos", "Lista de flechas (*a*, *b*)", "Refleja el diagrama sagital y permite representar relaciones que no son función (casos negativos)"],
    ["Distribución", "JAR y paquete Windows con Java incluido", "Se abre con doble clic en la laptop de la exposición sin instalar nada"],
    ["Versionado", "Git y GitHub", "Historial verificable del trabajo y descarga pública {repo}"],
  ], [0.9, 1.3, 2.4]);

  // ======================= VI =======================
  H1("VI. IMPLEMENTACIÓN Y SIMULACIÓN");
  H2("6.1 Arquitectura mínima");
  P("La aplicación sigue una separación en tres paquetes: la teoría (sin interfaz), el caso de estudio (modelo de la tienda) y la interfaz. Las pruebas solo dependen de los dos primeros.");
  tabla("Paquetes del proyecto", [
    ["Paquete", "Archivos", "Líneas", "Responsabilidad"],
    ["`simuchip.funciones`", "5", "354", "Flecha, verificador de propiedades (Funciones), traza paso a paso y diagnóstico"],
    ["`simuchip.caso`", "10", "655", "Modelo del caso: persona, línea, solicitud, tienda y simulador"],
    ["`simuchip.ui`", "20", "4813", "Capítulos de la exposición, personajes, diagramas y tema visual"],
    ["`test/simuchip`", "8", "493", "Siete suites de pruebas y el ejecutor"],
  ], [1.3, 0.6, 0.6, 2.4], { fte: "Elaboración propia (conteo con `wc -l`)." });
  H2("6.2 Fragmentos esenciales de código");
  codigo("`Funciones.java`: ¿es inyectiva?", `public static <A, B> boolean esInyectiva(Set<A> dominio,
        Set<B> codominio, List<Flecha<A, B>> flechas) {

    if (!esFuncion(dominio, flechas)) {
        return false;          // si no es función, no es inyectiva
    }
    for (B b : codominio) {
        if (llegadas(b, dominio, flechas) > 1) {
            return false;      // a "b" le llegan 2 o más flechas
        }
    }
    return true;               // a nadie le llegan 2: es inyectiva
}`);
  codigo("`Tienda.java` (fragmento): el chip extra, con y sin ChipGuard", `// el cliente es el dueño: se le vende su chip
vendidos++;
if (c.vendedorSacaExtra()) {          // la doble huella: nadie la ve
    eventos.add(fraude(EXTRA_BLOQUEADO, EXTRA_PASO));
}

private Evento fraude(Evento siBloquea, Evento siPasa) {
    if (chipGuard) {                  // al dueño le llega la alerta
        bloqueados++;                 // y toca «No fui yo»
        return siBloquea;
    }
    enManosCriminales++;              // sin ChipGuard, el chip se va
    return siPasa;
}`);
  H2("6.3 Procedimiento de reproducción");
  P("Requisitos: Java 17 o superior y Git. Desde una terminal:");
  codigo("Reproducir las pruebas y abrir la aplicación", `git clone https://github.com/jordymartel-hash/ChipGuard.git
cd ChipGuard

./probar.sh                 # compila y corre las ${TOTAL} pruebas
./empaquetar.sh             # crea ChipGuard.jar
java -jar ChipGuard.jar     # abre la aplicación`);
  P("En Windows basta descargar `ChipGuard-Windows.zip` de la sección de descargas del repositorio y abrir `Abrir ChipGuard.bat` (Anexo A).");
  H2("6.4 Entradas, salidas y casos de prueba");
  tabla("Entradas y salidas de cada componente", [
    ["Componente", "Entrada", "Salida"],
    ["Verificador", "Conjuntos *A*, *B* y lista de flechas", "Verdadero o falso para función, inyectiva, sobreyectiva y biyectiva"],
    ["Traza", "Los mismos datos", "Lista de pasos: línea de código, variables y mensaje"],
    ["Tienda", "Fila de clientes, con o sin ChipGuard, y la decisión vender o no por cliente", "Eventos por cliente y contadores: vendidos, en manos criminales, bloqueados, atrapados y perdidos"],
  ], [1, 1.6, 2]);
  tabla("Casos normales y casos límite verificados", [
    ["Tipo", "Caso (texto literal de la prueba)", "Resultado esperado"],
    ["Normal", "1→a, 2→b, 3→c con 'd' sola", "Inyectiva, no sobreyectiva"],
    ["Normal", "a, b y c reciben flecha (A con 4 elementos)", "Sobreyectiva, no inyectiva"],
    ["Normal", "1→a, 2→b, 3→c con |A| = |B| = 3", "Biyectiva; su inversa también lo es"],
    ["Límite", "dominio vacío → función vacía", "Es función"],
    ["Límite", "el 1 tiene dos flechas y el 3 ninguna", "No es función (y por tanto no es inyectiva)"],
    ["Límite", "flecha repetida 3→c, 3→c", "No es función: salen dos flechas del 3"],
    ["Límite", "flecha desde un elemento fuera de A (7→c)", "No cuenta como llegada; no es sobreyectiva"],
    ["Límite", "decidir con la fila vacía", "Lanza `IllegalStateException`"],
    ["Simulación", "vender a los 6 clientes con ChipGuard", "0 chips en manos criminales, 4 bloqueados, 4 vendidos"],
  ], [0.8, 2.2, 1.6], { fte: "Elaboración propia a partir de `PruebasFunciones.java` y `PruebasTienda.java`." });
  H2("6.5 La aplicación de exposición");
  P(`La aplicación se usa para exponer: avanza con las flechas del teclado como una presentación, tiene un cronómetro para el expositor y está organizada en ocho capítulos (Tabla ${nTab + 1}).`);
  tabla("Capítulos de la aplicación", [
    ["Capítulo", "Contenido"],
    ["La historia", "Portada con el equipo y el caso de Ana en escenas animadas, de la doble huella a la policía, con las cifras de OSIPTEL"],
    ["Objetivos", "El objetivo general y los tres específicos, cada uno con el capítulo donde se cumple, y el marco teórico (función, inyectiva, sobreyectiva, biyectiva y palomar) con sus diagramas"],
    ["La matemática", "Función, inyectiva, sobreyectiva y biyectiva con personas y casos reales; f(c) ≠ u(c); el palomar; la solución"],
    ["Qué es ChipGuard", "Las tres partes, sus usos, sus actores, las pantallas de la app, la web y la comparación con lo que existe"],
    ["En acción", "El celular de Ana recibe la alerta y bloquea el chip"],
    ["Por dentro", "La arquitectura, el flujo de siete pasos, los casos especiales, los datos y las tecnologías"],
    ["La tienda", "La simulación jugable en dos rondas"],
    ["Cierre", "Ideas clave, el reto «Checa tus líneas», las referencias y el agradecimiento"],
  ], [1, 3.2]);
  H2("6.6 Atribución del código y de los recursos");
  P(`El código fuente de la aplicación y de las pruebas fue escrito para este trabajo y está publicado en GitHub {repo}; no reutiliza código de terceros ni usa librerías externas, solo la biblioteca estándar de Java. Se incluyen dos recursos de terceros con licencia libre: la tipografía Geist de Vercel, bajo SIL Open Font License 1.1, cuyo texto de licencia se distribuye en \`recursos/fuentes/OFL-Geist.txt\` {geist}, y, solo en el paquete para Windows, el entorno de ejecución Eclipse Temurin 21 {temurin}. El logo institucional pertenece a la Universidad Peruana Cayetano Heredia y se usa con fines académicos.`);

  // ======================= VII =======================
  H1("VII. RESULTADOS Y ANÁLISIS");
  H2("7.1 Pruebas automáticas");
  P(`Las ${suites.reduce((a, s) => a + s.n, 0)} pruebas pasan sin fallos (Tabla ${nTab + 1}). La lista completa, con el texto que imprime cada prueba, está en el Anexo B.`);
  const DESC = { PruebasFunciones: "Definiciones de función, inyectiva, sobreyectiva, biyectiva e inversa", PruebasSimulador: "Simulador por días del registro con biometría, tope y auditoría", PruebasDiagnostico: "Explicación de por qué falla una propiedad", PruebasTraza: "Pasos del código que muestra la aplicación", PruebasDetective: "Minijuego de detección de líneas fraudulentas", PruebasTienda: "Simulación de la tienda con y sin ChipGuard", PruebasDisenio: "Que los símbolos matemáticos se dibujen bien (sin «cuadraditos» en Windows)" };
  TSUITES = tabla("Resultado de las suites de pruebas", [["Suite", "Qué verifica", "Pruebas", "Fallos"],
    ...suites.map((s) => [s.nombre, DESC[s.nombre] || "", String(s.n), "0"]),
    ["**Total**", "", `**${suites.reduce((a, s) => a + s.n, 0)}**`, "**0**"]], [1.3, 2.6, 0.6, 0.5], { fte: "Salida de `./probar.sh` (elaboración propia)." });
  H2("7.2 Traza de ejecución");
  P(`La Tabla ${nTab + 1} muestra la traza real que produce \`Traza.esInyectiva\` para la sobreventa de un bus: *A* = {Luis, Rosa, Jorge}, *B* = {A1, A2, A3, A4} y flechas Luis→A1, Rosa→A2, Jorge→A2. El algoritmo se detiene en el primer asiento cuya fibra tiene dos elementos, tal como predice el Corolario 1.`);
  const tTraza = tabla("Traza de esInyectiva para la sobreventa del bus", [["Paso", "Línea", "Variables", "Mensaje"],
    ...traza.map((t, i) => [String(i + 1), t.linea, t.vars, t.msg])], [0.5, 0.5, 1.8, 2.4], { fte: "Salida del programa (elaboración propia)." });
  H2("7.3 Simulación de la tienda");
  P(`Para comprobar el efecto de la propuesta, el público atiende una tienda de chips: llegan los seis clientes de la Tabla ${tFila}, cada uno entrega un DNI y hay que decidir si venderle comparando su cara con la foto del documento. La misma fila se juega sin y con ChipGuard. La Tabla ${nTab + 1} resume tres estrategias de decisión, ejecutadas con el código de \`Tienda.decidir\` (Código 4).`);
  const tSim = tabla("Resultado de la simulación según la estrategia de quien atiende", [["Estrategia", "ChipGuard", "En manos criminales", "Bloqueados por el dueño", "Ventas legítimas", "Clientes perdidos"],
    ...escenarios.map((e) => [e.estr, e.cg, String(e.crim), String(e.bloq), String(e.vend), String(e.perd)])], [1.6, 0.8, 0.9, 0.9, 0.8, 0.8], { fte: "Ejecución de `Tienda` (elaboración propia)." });
  P("La fila clave es la primera: **aun decidiendo todo bien, dos chips terminan en manos criminales**, porque el chip extra de la doble huella no es visible desde el mostrador. Esto confirma el modelo: el control en la tienda reduce los impostores, pero no puede eliminar los chips de *D* = {c : *f*(c) ≠ *u*(c)} creados por el propio vendedor. Con ChipGuard, la decisión pasa a la única persona que sabe si el chip es suyo y el número de chips en manos criminales es 0 en las tres estrategias, incluso vendiendo a todos.");
  figura("4e-tienda-extra.png", "Ronda 1: el vendedor saca un chip extra que el público no puede ver.");
  figura("4g-tienda-bloqueado.png", "Ronda 2: el dueño del DNI bloquea el chip desde su celular.");
  H2("7.4 Contraste con los resultados matemáticos");
  tabla("Resultado teórico frente a la evidencia de ejecución", [["Resultado teórico", "Evidencia en la ejecución"],
    ["Corolario 1: inyectiva ⇔ toda fibra tiene ≤ 1 elemento", `La traza se detiene con llegadas(A2) = 2 y devuelve falso (Tabla ${tTraza})`],
    ["Teorema 3 (palomar): |A| > |B| ⇒ no inyectiva", "Con |A| = 4 y |B| = 3 la prueba «la 'a' recibe dos, NO es inyectiva» pasa"],
    ["Corolario 1: biyectiva ⇒ |A| = |B|", "«sobra la 'd' → NO biyectiva» con |A| = 3, |B| = 4"],
    ["Teorema 1: biyectiva ⇒ existe inversa biyectiva", "«la inversa de una biyectiva también es biyectiva» pasa"],
    ["Proposición 1: con autorización del titular, ningún chip de D se activa sin su permiso", `Con ChipGuard, 0 chips en manos criminales en las tres estrategias (Tabla ${tSim})`],
  ], [2, 2.4]);
  P("La evidencia de ejecución no sustituye a la demostración: las pruebas verifican casos concretos, mientras que los teoremas garantizan el resultado para todo conjunto finito. Su valor está en comprobar que el código implementa fielmente las definiciones.");
  H2("7.5 Complejidad y escalabilidad");
  P(`Para verificar el costo O(*n*²) de la sección 5.4 se midió \`esBiyectiva\` sobre la función identidad con |*A*| = |*B*| = *n* (mejor de cinco ejecuciones, tras una ejecución de calentamiento, en ${CPU} con Java ${JAVA}).`);
  tabla("Tiempo de esBiyectiva según el tamaño de los conjuntos", [["n", "Tiempo (ms)", "Razón frente a n/2"],
    ...tiempos.map((t, i) => [String(t.n), t.ms.toFixed(1).replace(".", ","), i ? (t.ms / tiempos[i - 1].ms).toFixed(1).replace(".", ",") : "—"])], [1, 1, 1.2], { fte: "Medición propia." });
  const razones = tiempos.slice(1).map((t, i) => t.ms / tiempos[i].ms);
  const media = Math.exp(razones.reduce((a, r) => a + Math.log(r), 0) / razones.length);
  const coma = (x, dec = 1) => x.toFixed(dec).replace(".", ",");
  const minutos = Math.round((309454 / tiempos[0].n) ** 2 * tiempos[0].ms / 60000);
  P(`Al duplicar *n* el tiempo se multiplica en promedio (media geométrica) por ${coma(media)}, cercano al factor 4 de un algoritmo cuadrático; las razones individuales van de ${coma(Math.min(...razones))} a ${coma(Math.max(...razones))} por la compilación en tiempo de ejecución y la memoria caché del procesador. Extrapolando cuadráticamente desde *n* = ${tiempos[0].n}, verificar solo las 309 454 líneas inconsistentes tomaría del orden de (309 454 / ${tiempos[0].n})² × ${coma(tiempos[0].ms)} ms ≈ ${minutos} minutos. Para el padrón completo, el servidor de ChipGuard no recorrería el registro entero: le basta consultar |*f*⁻¹(*p*)| de una sola persona por solicitud, operación que con un índice por DNI en la base de datos es de tiempo casi constante. El verificador cuadrático es adecuado para enseñar, no para el registro nacional.`);

  // ======================= VIII =======================
  H1("VIII. DISCUSIÓN");
  H2("8.1 Interpretación");
  P("El aporte principal del modelo es desplazar la pregunta: el registro no falla por ser o no ser inyectivo o sobreyectivo, sino porque dos funciones que deberían estar ligadas, *f* y *u*, se separan en el conjunto *D*. Las medidas actuales atacan los síntomas (contar líneas, revisar a quienes tienen más de diez) y por eso llegan tarde; la Proposición 1 muestra que la propiedad que restituye la trazabilidad es una biyección entre chips y autorizaciones del titular, y la simulación muestra que esa propiedad elimina el fraude que el mostrador no puede ver.");
  H2("8.2 Limitaciones y posibles errores");
  lista([
    "**Simulación didáctica:** usa una fila fija de seis clientes y no estima tasas reales de fraude; sus números ilustran el mecanismo, no lo cuantifican.",
    "**Supuesto del dueño honesto y con su celular:** si el celular del titular fue robado, el atacante podría aprobar; por eso la aprobación exige la huella en el propio celular, lo que reduce pero no elimina el riesgo.",
    "**Engaño al titular:** el vendedor podría convencer al cliente de aprobar dos veces; el tope del Corolario 2 y el historial visible en la app acotan ese caso.",
    "**Primer chip con vendedor cómplice:** si el vendedor activa el chip extra **antes** que el del cliente, el tope de 1 hace que el chip del cliente no se active y la llamada de bienvenida suene en otro celular; el cliente lo nota en el mostrador, pero depende de que no se vaya sin comprobarlo.",
    "**Dependencia institucional:** ChipGuard exige que las operadoras consulten al servidor antes de activar, lo que requiere una norma de OSIPTEL.",
    "**Fuentes:** varias cifras provienen de notas de prensa y algunas no tienen fecha de publicación (s. f.); deben contrastarse con los informes oficiales de OSIPTEL.",
    "**Mediciones:** los tiempos dependen del equipo y de la máquina virtual de Java; se reportan como tendencia, no como valores absolutos.",
  ]);
  H2("8.3 Comparación con la bibliografía");
  P(`Rosen {rosen} y Epp {epp} presentan el palomar con ejemplos de conteo; aquí se usa como regla de diseño (un tope que vuelve imposible el chip *k* + 1). La analogía con las tablas de dispersión {knuth} es útil pero inversa: en una tabla de dispersión las colisiones son el problema y se resuelven; en el registro las «colisiones» (varias líneas por persona) son legítimas y el problema es la asignación incorrecta. Frente a las medidas de OSIPTEL {osiptel116}, {peruanocheca}, {peruanobloqueo}, ChipGuard no las reemplaza: las complementa pasando de la detección posterior a la autorización previa.`);
  H2("8.4 Mejoras del diseño");
  lista([
    "Reemplazar el conteo cuadrático por un conteo en una pasada con un mapa de dispersión, O(*n*).",
    "Simular con miles de solicitudes y semillas aleatorias para estimar el tiempo de respuesta del servidor y la proporción de solicitudes que expiran.",
    "Prototipar el servidor (API REST) y la app con notificaciones reales.",
  ]);

  // ======================= IX =======================
  H1("IX. CONCLUSIONES");
  lista([
    "**Respecto al OE1.** El registro de líneas es una función *f* : *C* → *P* cuyo defecto no es de tipo —no debe ser inyectiva ni sobreyectiva—, sino que no coincide con la función de uso real *u* en el conjunto *D* de chips fraudulentos. Se demostró que solo una biyección admite inversa (Teorema 1) y que el tope de *k* líneas por DNI impide el chip *k* + 1 (Corolario 2).",
    "**Respecto al OE2.** ChipGuard implementa la condición de la Proposición 1: un chip se activa solo con una autorización de un solo uso emitida por su titular, lo que forma una biyección entre chips activos y autorizaciones y hace que el rastreo lleve a una persona que aprobó la línea. Su arquitectura agrega dos piezas (servidor y app) a las cuatro existentes y cubre los casos sin respuesta, sin smartphone, sin celular y el del primer chip, que se resuelve por inducción (Proposición 2).",
    `**Respecto al OE3.** El verificador y la simulación en Java pasan ${TOTAL} pruebas automáticas y sus resultados coinciden con los teoremas. En la simulación, aun con decisiones perfectas en el mostrador, 2 chips terminan en manos criminales; con ChipGuard, 0 en todas las estrategias. La medición confirma el costo cuadrático del verificador, adecuado para fines didácticos.`,
    "**Respuesta a la pregunta de investigación.** La propiedad necesaria es que cada chip activo esté en correspondencia biyectiva con una autorización de su titular registrado; en la simulación, ChipGuard la garantiza y elimina los chips fraudulentos, con las limitaciones señaladas en la sección 8.2.",
  ]);

  // ======================= X =======================
  H1("X. REFERENCIAS BIBLIOGRÁFICAS");
  for (const [i, k] of orden.entries())
    C.push(new d.Paragraph({ alignment: d.AlignmentType.LEFT, spacing: { after: 80, line: 252 }, indent: { left: cm(1), hanging: cm(1) },
      children: [new d.TextRun({ text: `[${i + 1}]\t`, size: 20 }), ...runs(REFS[k], { size: 20 })], tabStops: [{ type: d.TabStopType.LEFT, position: cm(1) }] }));
}

function anexos(pruebasTexto) {
  H1("XI. ANEXOS", { salto: false });
  H2("Anexo A. Repositorio y manual mínimo");
  P("**Repositorio:** https://github.com/jordymartel-hash/ChipGuard (código completo, informe y descargas).");
  P("**En Windows:** entrar a https://github.com/jordymartel-hash/ChipGuard/releases, bajar `ChipGuard-Windows.zip`, descomprimirlo y abrir `Abrir ChipGuard.bat`. No hace falta instalar Java porque viene incluido. Si aparece «Windows protegió su PC», elegir «Más información» y luego «Ejecutar de todas formas».");
  P("**En cualquier sistema con Java 17 o superior:** bajar `ChipGuard.jar` de la misma página y abrirlo con doble clic o con `java -jar ChipGuard.jar`.");
  P("**Controles:** las flechas izquierda y derecha avanzan o retroceden escenas y capítulos; un clic en la barra superior salta a un capítulo; el cronómetro se inicia o pausa con un clic y se reinicia con clic derecho.");
  H2("Anexo B. Pruebas automáticas del verificador y de la tienda");
  P("Salida de `./probar.sh` para las dos suites centrales del trabajo. Las otras cinco suites (Tabla " + TSUITES + ") se pueden ejecutar desde el repositorio.");
  {   // dos columnas sin bordes: las líneas son cortas y en una sola columna ocupan casi una página
    const ls = pruebasTexto.split("\n").filter(Boolean);
    let corte = Math.ceil(ls.length / 2);
    while (corte < ls.length && /^\s/.test(ls[corte])) corte++;      // la 2.ª columna empieza en un título de grupo
    const col = (arr) => arr.map((l) => new d.Paragraph({ spacing: { line: 200, after: 0 },
      children: [new d.TextRun({ text: l.replace(/\s+$/, ""), font: "Courier New", size: 14, bold: !l.startsWith(" ") })] }));
    const media = Math.floor(ANCHO_TEXTO / 2), nada = { style: d.BorderStyle.NONE, size: 0, color: "FFFFFF" };
    C.push(new d.Table({ width: { size: ANCHO_TEXTO, type: d.WidthType.DXA }, columnWidths: [media, ANCHO_TEXTO - media],
      borders: { top: nada, bottom: nada, left: nada, right: nada, insideHorizontal: nada, insideVertical: nada },
      rows: [new d.TableRow({ children: [ls.slice(0, corte), ls.slice(corte)].map((a, k) => new d.TableCell({ width: { size: k ? ANCHO_TEXTO - media : media, type: d.WidthType.DXA },
        margins: { left: 40, right: 80 }, children: col(a) })) })] }));
  }
  H2("Anexo C. Distribución de tareas y bitácora");
  tabla("Distribución de tareas del trabajo", [
    ["Integrante", "Aporte", "Evidencia"],
    ["Martel Vasquez, Jordy Joel", "Desarrollo completo de la aplicación, el verificador de funciones, la simulación de la tienda, las pruebas automáticas, el repositorio y las versiones publicadas", `Historial del repositorio {repo} y versiones v1.0 a v1.3`],
    ["Sanga Limache, Angeles Jemina", "Exposición del capítulo La matemática", `Reparto de la exposición (Tabla ${nTab + 2})`],
    ["Tangoa Alva, Richard José Hernan", "Exposición de los capítulos La historia y Objetivos", `Reparto de la exposición (Tabla ${nTab + 2})`],
    ["Vidalon Ampudia, Eduardo Josias", "Exposición de los capítulos La tienda (con el público) y Cierre", `Reparto de la exposición (Tabla ${nTab + 2})`],
  ], [1.4, 2.4, 1.6]);
  tabla("Reparto de la exposición (16 minutos, 4 integrantes)", [
    ["Integrante", "Capítulos", "Tiempo", "Idea que debe quedar"],
    ["Tangoa Alva, Richard José Hernan", "La historia · Objetivos", "4:00", "A Ana le sacaron un chip con su segunda huella, y la policía llegó a ella; nuestra misión es modelarlo y arreglarlo."],
    ["Sanga Limache, Angeles Jemina", "La matemática", "4:00", "El registro es una función; el problema es que registro ≠ uso real."],
    ["Martel Vasquez, Jordy Joel", "Qué es ChipGuard · En acción · Por dentro", "4:30", "Ningún chip se activa sin el permiso del dueño del DNI."],
    ["Vidalon Ampudia, Eduardo Josias", "La tienda (con el público) · Cierre", "3:30", "Aun decidiendo bien, sin ChipGuard 2 chips llegan a criminales; con ChipGuard, 0."],
  ], [1.4, 1.3, 0.7, 1.8]);
  tabla("Bitácora de versiones del repositorio", [
    ["Fecha", "Versión", "Cambio principal"],
    ["2026-11-07", "v1 a v3", "Verificador de funciones, simulador, primeras pruebas; rediseño y nombre ChipGuard"],
    ["2026-11-08", "v6", "Exposición en capítulos con personajes; juego «La tienda»; arquitectura"],
    ["2026-11-08", "v1.0 y v1.1", "Publicación en GitHub, paquete Windows con Java incluido, corrección de símbolos"],
    ["2026-11-12", "v1.2", "Curso Estructuras Discretas I; caso del primer chip; informe con el formato del curso y referencias IEEE"],
    ["2026-11-15", "v1.3", "Capítulo Objetivos con objetivo y marco teórico; letra para proyector; nuevo reparto del guion"],
    ["2026-11-20", "v1.3.1", "Objetivos de la app alineados a OE1–OE3; informe generado también en Windows"],
  ], [0.8, 0.8, 3], { fte: "Historial de commits del repositorio {repo}." });
  H2("Anexo D. Galería de la aplicación");
  const gal = [
    ["1a-historia.png", "Portada con el docente y los integrantes."], ["1e-historia.png", "La extorsión desde el chip de Ana."],
    ["1f-historia.png", "La policía llega a Ana."], ["1g-historia.png", "Las cifras de OSIPTEL."],
    ["2b-matematica.png", "Lo que no es función."], ["Qe-que-es.png", "La web, sin smartphone."],
    ["3c-chipguard.png", "El chip bloqueado desde el celular."], ["3d-chipguard.png", "El mapa de chips de Ana."],
    ["3h-arquitectura.png", "Si el dueño dice «No fui yo»."], ["3k-arquitectura.png", "Qué guarda ChipGuard."],
    ["4b-tienda-cliente.png", "Llega un cliente con su DNI."], ["4h-tienda-final.png", "Resultado de las dos rondas."],
  ];
  const mitad = Math.floor(ANCHO_TEXTO / 2);   // galería en 2 columnas: capturas legibles
  const sinBorde = { style: d.BorderStyle.NONE, size: 0, color: "FFFFFF" };
  const filas = [];
  for (let i = 0; i < gal.length; i += 2) {
    filas.push(new d.TableRow({ cantSplit: true, children: gal.slice(i, i + 2).map(([f, t]) => {
      nFig++;
      entradas.push({ tipo: "fig", texto: `Figura ${nFig}. ${t}`, clave: `Figura ${nFig}. ${t.slice(0, 18)}` });
      return new d.TableCell({ width: { size: mitad, type: d.WidthType.DXA }, margins: { top: 60, bottom: 120, left: 60, right: 60 }, children: [
        new d.Paragraph({ alignment: d.AlignmentType.CENTER, spacing: { line: 240, after: 40 }, children: [imagenRun(CAP(f), 7.3)] }),
        new d.Paragraph({ alignment: d.AlignmentType.CENTER, spacing: { line: 240, after: 0 }, children: [new d.TextRun({ text: `Figura ${nFig}. `, bold: true, size: 18 }), new d.TextRun({ text: t, italics: true, size: 18 })] }),
      ] });
    }) }));
  }
  C.push(new d.Table({ width: { size: mitad * 2, type: d.WidthType.DXA }, columnWidths: [mitad, mitad],
    borders: { top: sinBorde, bottom: sinBorde, left: sinBorde, right: sinBorde, insideHorizontal: sinBorde, insideVertical: sinBorde }, rows: filas }));
  P("*Fuente:* capturas de la aplicación ChipGuard (elaboración propia).", { run: { size: 18 } });
  // el registro no se parte: todo va «con el siguiente» y la tabla con filas indivisibles
  H2("Anexo E. Registro de evaluación adjunto");
  C.push(new d.Paragraph({ alignment: d.AlignmentType.CENTER, keepNext: true, spacing: { after: 120 }, children: [new d.TextRun({ text: "REGISTRO DE EVALUACIÓN ADJUNTA AL INFORME DE INVESTIGACIÓN", bold: true })] }));
  P(`**Equipo:** ChipGuard${GRUPO ? ` (Grupo ${GRUPO})` : ""}`, { alignment: d.AlignmentType.LEFT, keepNext: true, spacing: { after: 40 } });
  P("**Tema:** ChipGuard: el registro de líneas móviles modelado con funciones", { alignment: d.AlignmentType.LEFT, keepNext: true, spacing: { after: 40 } });
  P("**Integrantes evaluados:** 1) Martel Vasquez, Jordy Joel; 2) Sanga Limache, Angeles Jemina; 3) Tangoa Alva, Richard José Hernan; 4) Vidalon Ampudia, Eduardo Josias.", { alignment: d.AlignmentType.LEFT, keepNext: true });
  const crit = [["Rigor de la investigación y marco teórico", 4], ["Diseño, programación y simulación", 4], ["Estructura y calidad del informe", 2], ["Citación, referencias e integridad académica", 1], ["Exposición oral y dominio conceptual", 3], ["Vestimenta formal y presentación profesional", 2], ["Trabajo en equipo y contribución verificable", 1], ["Defensa, preguntas y pensamiento crítico", 3]];
  nTab++;
  entradas.push({ tipo: "tab", texto: `Tabla ${nTab}. Registro de evaluación (para uso del docente)`, clave: `Tabla ${nTab}. Registro de eval` });
  C.push(new d.Paragraph({ keepNext: true, spacing: { before: 120, after: 80, line: 240 }, children: [new d.TextRun({ text: `Tabla ${nTab}. `, bold: true, size: 20 }), new d.TextRun({ text: "Registro de evaluación (para uso del docente)", italics: true, size: 20 })] }));
  const an = [Math.floor(ANCHO_TEXTO * 0.42), Math.floor(ANCHO_TEXTO * 0.1), Math.floor(ANCHO_TEXTO * 0.28)];
  an.push(ANCHO_TEXTO - an.reduce((a, b) => a + b, 0));
  C.push(new d.Table({ width: { size: ANCHO_TEXTO, type: d.WidthType.DXA }, columnWidths: an,
    borders: { top: borde, bottom: borde, left: borde, right: borde, insideHorizontal: borde, insideVertical: borde },
    rows: [["Criterio", "Peso", "Observaciones", "Puntaje obtenido"], ...crit.map(([c, p]) => [c, String(p), "", ""]), ["**TOTAL**", "**20**", "", ""]]
      .map((f, i, filas) => new d.TableRow({ tableHeader: i === 0, cantSplit: true, height: { value: i ? 520 : 300, rule: d.HeightRule.ATLEAST },
        children: f.map((t, j) => celda(t, an[j], i === 0, i < filas.length - 1)) })) }));
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
  const fila = (texto, pag, sangria, bold) => out.push(new d.Paragraph({ alignment: d.AlignmentType.LEFT, spacing: { line: 228, after: 0 },
    indent: { left: cm(sangria + 0.9), hanging: cm(0.9), right: cm(0.9) },
    tabStops: [{ type: d.TabStopType.RIGHT, position: ANCHO_TEXTO, leader: "dot" }],
    children: [new d.TextRun({ text: texto, bold, size: 20 }), new d.TextRun({ size: 20, children: [new d.Tab(), pag] })] }));
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
