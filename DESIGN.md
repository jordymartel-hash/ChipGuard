---
name: ChipGuard
description: Exposición interactiva de escritorio (Java Swing) que explica los chips registrados con un DNI ajeno mediante funciones inyectivas, sobreyectivas y biyectivas; minimalismo de keynote al estilo Apple.
colors:
  action-blue: "#0071E3"
  action-blue-text: "#0066CC"
  action-blue-tint: "#E8F2FD"
  action-blue-on-dark: "#409CFF"
  page-white: "#FBFBFD"
  band-gray: "#F5F5F7"
  surface-white: "#FFFFFF"
  control-gray: "#E8E8ED"
  hairline: "#D2D2D7"
  quiet-gray: "#C7C7CC"
  ink: "#1D1D1F"
  ink-raised: "#2C2C2E"
  ink-secondary: "#6E6E73"
  ink-secondary-on-dark: "#A1A1A6"
  fraud-red: "#E5372C"
  fraud-red-deep: "#C4231A"
  fraud-red-tint: "#FDECEB"
  fraud-red-on-dark: "#FF6961"
  safe-green: "#248A3D"
  safe-green-tint: "#E4F6E8"
  safe-green-on-dark: "#30D158"
  switch-green: "#34C759"
  set-a-orange: "#FF9F0A"
  set-a-orange-text: "#B25000"
  set-a-orange-tint: "#FFF2DF"
  focus-yellow: "#FFD60A"
typography:
  display:
    fontFamily: "Geist, sans-serif"
    fontSize: "58px"
    fontWeight: 700
    letterSpacing: "-0.025em"
  stat:
    fontFamily: "Geist, sans-serif"
    fontSize: "48px"
    fontWeight: 700
    letterSpacing: "-0.025em"
  headline:
    fontFamily: "Geist, sans-serif"
    fontSize: "40px"
    fontWeight: 700
    letterSpacing: "-0.025em"
  title-lg:
    fontFamily: "Geist, sans-serif"
    fontSize: "34px"
    fontWeight: 700
    letterSpacing: "-0.01em"
  title:
    fontFamily: "Geist, sans-serif"
    fontSize: "21px"
    fontWeight: 700
    letterSpacing: "-0.01em"
  body-lg:
    fontFamily: "Geist, sans-serif"
    fontSize: "18px"
    fontWeight: 400
  body:
    fontFamily: "Geist, sans-serif"
    fontSize: "16px"
    fontWeight: 400
  body-strong:
    fontFamily: "Geist, sans-serif"
    fontSize: "16px"
    fontWeight: 600
  control:
    fontFamily: "Geist, sans-serif"
    fontSize: "14.5px"
    fontWeight: 500
  nav:
    fontFamily: "Geist, sans-serif"
    fontSize: "15px"
    fontWeight: 500
  label:
    fontFamily: "Geist, sans-serif"
    fontSize: "14px"
    fontWeight: 400
  code:
    fontFamily: "Geist Mono, monospace"
    fontSize: "14px"
    fontWeight: 400
rounded:
  card-sm: "16px"
  toast: "18px"
  card-dark: "22px"
  card: "24px"
  stage: "32px"
  pill: "9999px"
spacing:
  hairline: "1px"
  inset-control: "3px"
  gap-tight: "8px"
  gap-stack: "10px"
  inset-card: "16px"
  gutter-working: "24px"
  nav-edge: "28px"
  gutter-narrative: "88px"
components:
  button-primary:
    backgroundColor: "{colors.action-blue}"
    textColor: "{colors.surface-white}"
    typography: "{typography.control}"
    rounded: "{rounded.pill}"
    padding: "0 18px"
    height: "38px"
  button-primary-hover:
    backgroundColor: "#006AD5"
  button-primary-active:
    backgroundColor: "#0061C3"
  button-secondary:
    backgroundColor: "{colors.control-gray}"
    textColor: "{colors.ink}"
    typography: "{typography.control}"
    rounded: "{rounded.pill}"
    padding: "0 18px"
    height: "38px"
  button-secondary-hover:
    backgroundColor: "#DADADE"
  button-danger:
    backgroundColor: "{colors.control-gray}"
    textColor: "{colors.fraud-red-deep}"
    typography: "{typography.control}"
    rounded: "{rounded.pill}"
    height: "38px"
  button-success:
    backgroundColor: "{colors.safe-green}"
    textColor: "{colors.surface-white}"
    typography: "{typography.control}"
    rounded: "{rounded.pill}"
    height: "38px"
  switch-row:
    backgroundColor: "{colors.control-gray}"
    textColor: "{colors.ink}"
    typography: "{typography.control}"
    rounded: "{rounded.pill}"
    height: "38px"
  switch-track-on:
    backgroundColor: "{colors.switch-green}"
    rounded: "{rounded.pill}"
    width: "44px"
    height: "26px"
  switch-track-off:
    backgroundColor: "{colors.quiet-gray}"
    rounded: "{rounded.pill}"
    width: "44px"
    height: "26px"
  segmented-rail:
    backgroundColor: "{colors.control-gray}"
    textColor: "{colors.ink-secondary}"
    typography: "{typography.label}"
    rounded: "{rounded.pill}"
    height: "36px"
  segmented-selected:
    backgroundColor: "{colors.surface-white}"
    textColor: "{colors.ink}"
    rounded: "{rounded.pill}"
    height: "30px"
  stepper:
    backgroundColor: "{colors.control-gray}"
    textColor: "{colors.ink-secondary}"
    typography: "{typography.label}"
    rounded: "{rounded.pill}"
    height: "36px"
  nav-bar:
    backgroundColor: "{colors.surface-white}"
    textColor: "{colors.ink}"
    height: "60px"
  nav-tab:
    textColor: "{colors.ink-secondary}"
    typography: "{typography.nav}"
  nav-tab-active:
    textColor: "{colors.ink}"
  card:
    backgroundColor: "{colors.surface-white}"
    rounded: "{rounded.card}"
    padding: "{spacing.inset-card}"
  card-dark:
    backgroundColor: "{colors.ink}"
    textColor: "{colors.surface-white}"
    rounded: "{rounded.card-dark}"
  toast:
    backgroundColor: "{colors.surface-white}"
    textColor: "{colors.ink}"
    rounded: "{rounded.toast}"
  status-pill-fraud:
    backgroundColor: "{colors.fraud-red}"
    textColor: "{colors.surface-white}"
    rounded: "{rounded.pill}"
    padding: "4px 12px"
  status-pill-safe:
    backgroundColor: "{colors.safe-green}"
    textColor: "{colors.surface-white}"
    rounded: "{rounded.pill}"
    padding: "4px 12px"
  code-line-active:
    backgroundColor: "{colors.focus-yellow}"
    textColor: "{colors.ink}"
    typography: "{typography.code}"
---

# Design System: ChipGuard

## Overview

**Creative North Star: "La keynote que se puede tocar"**

ChipGuard se proyecta en un aula como una presentación de Apple: un capítulo, una idea, a pantalla completa. Cada capítulo abre con un titular Geist Bold que enuncia la idea con tracking negativo, y debajo la idea se demuestra en movimiento: chips que se pegan a un DNI, flechas que crecen, código que avanza renglón por renglón, un celular que vibra. La superficie es casi blanca y casi muda; el color aparece solo cuando significa algo.

La densidad cambia según el tipo de capítulo. Los capítulos narrativos (El problema, Cómo pasa) respiran sobre fondo blanco roto con márgenes amplios y titulares enormes. Los capítulos de trabajo (La matemática, Simulación) suben la densidad: titular de 40 px en la misma fila que sus controles, un escenario blanco grande a la izquierda y la explicación a la derecha. Todo está dibujado a mano en Java2D: no hay componentes Swing visibles, ni bordes de sistema, ni una sola barra de pestañas tipo navegador.

El rechazo confirmado por el usuario es la app de pestañas que «parece navegador», con barras de botones y tarjetas apiladas. Lo que la reemplaza son textos de capítulo con un punto bajo el activo, píldoras, controles segmentados e interruptores tipo macOS/iOS, y listas sin chrome de tarjeta.

**Key Characteristics:**
- Fondo blanco roto, bandas grises a sangre y un solo azul de acción.
- Colores de sistema solo con significado: rojo = fraude, verde = seguro, naranja = conjunto A, azul = acción y conjunto B.
- Titulares Geist Bold con tracking negativo; el texto corrido en Geist Regular, el énfasis en SemiBold.
- Controles en píldora completa; superficies con esquinas amplias (16–32 px) y sombra suave hacia abajo.
- Íconos vectoriales dibujados con trazo redondeado; nunca glifos de fuente.
- Transición de capítulo: fundido desde blanco y subida de 18 px; ← → avanzan como en una keynote.

## Colors

Una paleta neutra de grises Apple con un azul de acción y cuatro colores de sistema que funcionan como leyenda, no como decoración.

### Primary
- **Azul de acción** (action-blue): relleno de toda píldora que avanza o ejecuta (el capítulo siguiente en la barra, «Simular 1 día», «Ejecutar paso a paso»), el punto bajo el capítulo activo, la marca ChipGuard y los nodos del conjunto B. Su texto sobre fondo claro usa **Azul enlace** (action-blue-text); su tinte (action-blue-tint) rellena el óvalo del codominio y la fila seleccionada de un análisis. Sobre fondo oscuro pasa a **Azul nocturno** (action-blue-on-dark).

### Secondary
- **Rojo fraude** (fraud-red): todo lo que es fraude o fallo (chip ajeno, «Extorsionador», insignia de equis, flecha punteada de quien usa el chip). El texto rojo sobre fondo claro usa **Rojo fraude profundo** (fraud-red-deep); el tinte (fraud-red-tint) marca la fila con problema. Sobre oscuro, **Rojo coral** (fraud-red-on-dark).
- **Verde seguro** (safe-green): lo verificado o protegido (insignia de visto, «Aprobar», «Tuyo», contrato con huella). Lleva texto blanco. Tinte en safe-green-tint; sobre oscuro, safe-green-on-dark. El **Verde interruptor** (switch-green) es exclusivo del riel encendido del interruptor iOS.

### Tertiary
- **Naranja conjunto A** (set-a-orange): los nodos del dominio A y las líneas L; su texto usa **Naranja tostado** (set-a-orange-text) y su óvalo, set-a-orange-tint.
- **Amarillo resaltador** (focus-yellow): el resaltado de editor. Marca el renglón de código que se está ejecutando, el elemento que se está revisando (nodo con halo, línea rastreada) y el título del rastreo sobre panel oscuro.

### Neutral
- **Blanco página** (page-white): fondo de los capítulos narrativos y del juego.
- **Gris banda** (band-gray): bandas a sangre que separan un segundo bloque (la banda «¿Y la biometría?») y fondo completo de los capítulos de trabajo.
- **Blanco superficie** (surface-white): barra superior, escenarios, tarjetas, avisos, segmento elegido.
- **Gris control** (control-gray): relleno de píldoras secundarias, rieles segmentados, steppers y filas con interruptor.
- **Filete** (hairline): línea de 1 px bajo la barra superior, separadores de lista y borde de los avisos.
- **Gris quieto** (quiet-gray): riel apagado del interruptor, divisores del segmentado, flechas inactivas.
- **Tinta** (ink): texto principal y paneles oscuros (código, rastreo, narración del paso). **Tinta elevada** (ink-raised) para capas dentro de esos paneles.
- **Tinta secundaria** (ink-secondary): bajadas, leyendas, pestañas inactivas. Sobre oscuro, **Gris niebla** (ink-secondary-on-dark).

### Named Rules
**The Color-With-Meaning Rule.** Rojo es fraude, verde es seguro, naranja es el conjunto A, azul es acción o conjunto B. Ningún color de sistema aparece como adorno; si un elemento no significa nada de eso, va en grises.

**The Highlighter Rule.** El amarillo resaltador solo marca foco: el renglón activo y el elemento bajo revisión, como el resaltado de un editor. No es color de alerta, de premio ni de notificación.

**The Tinted-Text Rule.** El texto de color sobre fondo claro usa la variante oscura (action-blue-text, fraud-red-deep, set-a-orange-text), nunca el relleno puro. Medido: blanco sobre action-blue da 4.7:1; blanco sobre safe-green, 4.4:1; blanco sobre fraud-red, 4.27:1; safe-green como texto sobre page-white, 4.25:1. Estas tres últimas combinaciones quedan bajo 4.5:1, así que en texto pequeño son el valor que trae el build, no un objetivo.

## Typography

**Display Font:** Geist Bold (incluida en `recursos/fuentes`, OFL; respaldo: sans-serif del sistema)
**Body Font:** Geist Regular, Medium y SemiBold
**Label/Mono Font:** Geist Mono Regular

**Character:** una grotesca neutra y precisa que lee como San Francisco en un proyector sin serlo. Los titulares se cierran con tracking negativo; el cuerpo queda abierto y tranquilo.

### Hierarchy
- **Display** (Bold, 58 px, tracking −0.025 em): titular del capítulo narrativo de apertura, en varias líneas cortas; la segunda frase va en tinta secundaria. Escala con el ancho y nunca pasa de 58 px. Las cifras protagonistas de una sola palabra («100 %») llegan a 64 px con la misma regla.
- **Stat** (Bold, 48 px, tracking −0.025 em): las cifras de OSIPTEL en fila a todo el ancho y los contadores grandes.
- **Headline** (Bold, 40 px, tracking −0.025 em): titular de los capítulos de trabajo, en la misma fila que sus controles. Cómo pasa usa la misma familia a 52 px.
- **Title-lg** (Bold, 34–36 px, tracking −0.01 em): títulos de escena en ChipGuard y en el juego, y de bandas secundarias (30 px).
- **Title** (Bold, 20–21 px, tracking −0.01 em): títulos de panel («¿Qué tipo de función es?», «Lo que ve la operadora») y la marca en la barra (20 px).
- **Body-lg** (Regular, 18 px): bajadas bajo el titular en capítulos narrativos.
- **Body** (Regular, 16 px; SemiBold 16–17 px para el nombre de cada fila): texto corrido y filas de análisis.
- **Control** (Medium, 14.5 px) y **Nav** (Medium, 15 px): texto de píldoras y de los capítulos de la barra.
- **Label** (Regular, 14 px): leyendas, segundas líneas de fila, texto de avisos.
- **Code** (Geist Mono, 14 px): el panel de código y la línea de variables.

### Named Rules
**The SemiBold-Is-Bold Rule.** En texto corrido, «negrita» significa Geist SemiBold. Geist Bold queda reservada a titulares, cifras y marca, siempre con tracking negativo (−0.025 em desde 40 px, −0.01 em por debajo).

**The Headline-Is-the-Idea Rule.** Cada capítulo abre con un titular que enuncia la idea en una frase («Cuenta las flechas.», «Ahora, cómo se arregla.»). Nada va encima del titular: ni etiqueta, ni número de capítulo, ni categoría.

**The No-Tofu Rule.** Todo texto pasa por el dibujante con respaldo: si Geist no tiene un símbolo matemático (⁻¹, ∀, ∈, ≠), ese carácter se dibuja con la sans-serif del sistema para que nunca salga un cuadradito.

## Layout

La ventana es una pila fija: barra superior de 60 px (blanca, con filete de 1 px abajo) y debajo el escenario del capítulo. La barra lleva la marca a la izquierda (escudo a 42 px del borde), los capítulos centrados como texto y, a 28 px del borde derecho, una píldora azul con el nombre del capítulo siguiente y la flecha después del texto.

Los capítulos narrativos usan un margen lateral de 88 px y una composición de dos columnas: titular y texto a la izquierda, ilustración animada a la derecha, y una fila a todo el ancho debajo (cifras, estaciones de la historia). Los bloques secundarios entran como bandas gris banda a sangre, no como tarjetas.

Los capítulos de trabajo usan un margen de 16–24 px: el titular y sus controles comparten la fila superior, y debajo hay un escenario blanco de esquinas de 32 px (unos dos tercios del ancho) junto a una columna de explicación sin tarjeta. Las filas de esa columna se separan con filetes o solo con aire.

El ritmo interno se apoya en 8 px (separación entre ícono y texto en las píldoras), 10 px entre avisos apilados y 16 px de margen interior en las superficies. La resolución de referencia es 1500 × 800; los titulares se encogen con el ancho (ancho / 25) hasta su tope, y en Linux el escalado de Java solo admite factores enteros.

### Named Rules
**The One-Idea-Per-Viewport Rule.** Un capítulo es una sola pantalla sin desplazamiento. Si una idea necesita más, se vuelve una escena siguiente (Anterior / Siguiente), no un scroll.

## Elevation & Depth

La profundidad es híbrida: casi todo es plano y tonal (blanco sobre blanco roto, gris control sobre blanco) y unas pocas superficies flotan con una sombra suave, difusa y desplazada unos 4 px hacia abajo. La sombra se pinta en seis capas concéntricas de negro con opacidad decreciente, que imitan un desenfoque; nunca es una sombra dura ni lateral.

### Shadow Vocabulary
- **Elevación de superficie** (fuerza 18; en CSS se aproxima a `0 5px 14px rgba(0,0,0,0.08)`): tarjetas, escenarios, avisos, paneles oscuros, el celular y los círculos de estación.
- **Elevación de segmento** (fuerza 14; se aproxima a `0 4px 10px rgba(0,0,0,0.06)`): solo el segmento elegido del control segmentado.
- **Sombra de perilla** (círculo negro al 12 % desplazado 1 px): la perilla blanca del interruptor.

### Named Rules
**The Soft-Lift Rule.** Las sombras son suaves, difusas y caen hacia abajo. Una superficie con sombra no lleva además borde, salvo el filete del aviso flotante.

## Shapes

Hay dos familias de forma. Todo lo que se pulsa es una píldora completa (radio igual a la altura): botones, segmentos, steppers, filas con interruptor, nodos de persona, etiquetas de estado. Todo lo que contiene es un rectángulo de esquinas amplias: 16 px en piezas internas pequeñas, 18 px en avisos, 22 px en paneles oscuros, 24 px en tarjetas blancas y 32 px en el escenario de trabajo. Los conjuntos matemáticos son óvalos con un tinte y un trazo de 2 px del mismo color al 60 %, y las flechas tienen trazo redondeado con punta triangular rellena; quien usa el chip va en trazo punteado (9/7).

Los íconos (visto, equis, play, pausa, siguiente, anterior, reiniciar, alerta, escudo, chip, huella, documento, teléfono, lupa, persona, tienda) se dibujan como vectores con trazo redondeado y se escalan con el texto que acompañan.

## Components

### Buttons
Píldoras táctiles y sobrias, con relleno sólido y sin borde.
- **Shape:** píldora completa (radio = altura; 38 px mínimo, 2.5 × el cuerpo del texto).
- **Primary:** azul de acción con texto blanco Medium de 14.5 px y 18 px de aire a cada lado. El ícono va antes del texto, salvo la flecha «siguiente», que va después.
- **Hover / Active:** el relleno se oscurece un 6 % al pasar y un 14 % al presionar; no hay movimiento ni sombra.
- **Secondary:** gris control con texto en tinta («Ver de nuevo», «Anterior», «Paso»).
- **Danger:** gris control con texto en rojo fraude profundo; la acción destructiva no se rellena de rojo.
- **Success:** verde seguro con texto blanco; solo en «Aprobar» del juego.
- **Solo ícono:** la píldora secundaria se vuelve círculo (reiniciar la simulación).

### Chips (Status pills)
- **Style:** píldora rellena de rojo fraude o verde seguro con texto blanco SemiBold y 12 px de aire («Tuyo», «¿Tuyo?»). Los nodos de persona y de línea son también píldoras con su color de conjunto y un contador circular blanco.
- **State:** el elemento revisado recibe un halo de amarillo resaltador; el que falla, un anillo rojo.

### Cards / Containers
- **Corner Style:** 24 px las tarjetas blancas; 32 px el escenario; 22 px los paneles oscuros.
- **Background:** blanco superficie, o tinta para paneles de código, de rastreo y de narración del paso.
- **Shadow Strategy:** elevación de superficie (ver Elevation & Depth).
- **Border:** ninguno.
- **Internal Padding:** 16 px o más; el escenario de trabajo se aparta 16 px del borde de la ventana.
- **Panel oscuro:** tinta con texto blanco y gris niebla; en el panel de código el renglón activo se pinta en amarillo resaltador con un triángulo de play en el margen.

### Inputs / Fields
No hay campos de texto. La entrada es directa: tocar un número de A y una letra de B dibuja la flecha, y los valores se cambian con steppers e interruptores.
- **Stepper:** píldora gris control de 36 px con la etiqueta en tinta secundaria y dos botones circulares blancos (− y +) dibujados con barras de 2 px; el valor va en SemiBold 15 px.
- **Interruptor:** fila en píldora gris control con el texto a la izquierda y un riel de 44 × 26 px a la derecha, verde interruptor encendido y gris quieto apagado; perilla blanca de 22 px con sombra.

### Navigation
- **Barra:** blanca, 60 px, filete inferior, marca con escudo azul y «ChipGuard» en Bold 20 px.
- **Capítulos:** texto Medium de 15 px en tinta secundaria; al pasar el cursor pasa a tinta; el activo va en SemiBold con un punto azul de 5 px debajo. No hay subrayados, cajas ni pestañas.
- **Avance:** píldora azul a la derecha con el nombre del capítulo siguiente; las teclas ← → hacen lo mismo.
- **Transición:** el capítulo entra con un fundido desde blanco y una subida de 18 px en 420 ms, con desaceleración exponencial (curva de cuarto grado).
- **Control segmentado:** riel gris control de 36 px; el segmento elegido es una píldora blanca inset 3 px con sombra de segmento y texto SemiBold en tinta; los demás, Medium en tinta secundaria separados por divisores de 1 px.

### Avisos (toasts)
Tarjeta blanca de 18 px de radio con elevación y filete al 63 %, alineada a la derecha. Un punto de 14 px del color con significado, título SemiBold 15 px y texto Regular 14 px en tinta secundaria. Entra deslizándose 40 px en 260 ms, vive 3.6 s y se desvanece en 400 ms; caben como máximo tres apilados con 10 px entre ellos.

### Escenario animado (signature)
Lo que define el sistema es la demostración en movimiento: cada flecha, chip o nodo «nace» y crece con desaceleración cúbica, lo nuevo late una vez y las cifras cuentan hacia arriba. Para las capturas del informe todo se dibuja ya terminado.

## Do's and Don'ts

### Do:
- **Do** abrir cada capítulo con un titular Geist Bold que enuncie la idea (58 px narrativo, 40 px de trabajo), con tracking negativo.
- **Do** usar el azul de acción solo en lo que avanza, ejecuta o pertenece al conjunto B.
- **Do** reservar rojo, verde y naranja a fraude, seguro y conjunto A; lo demás va en grises.
- **Do** usar el amarillo resaltador solo para el renglón activo y el elemento revisado.
- **Do** dar a todo control la forma de píldora completa y a toda superficie esquinas de 16–32 px.
- **Do** dibujar los íconos como vectores con trazo redondeado y escribir todo texto con el dibujante con respaldo de símbolos.
- **Do** separar bloques con bandas gris banda a sangre o con aire, y las filas de una lista con filetes.

### Don't:
- **Don't** construir barras de pestañas tipo navegador ni filas de botones; los capítulos son texto con un punto bajo el activo.
- **Don't** poner etiquetas, números de capítulo ni categorías encima de un titular. Las mayúsculas pequeñas solo existen dentro de objetos dibujados que las llevan en la realidad (el encabezado impreso del DNI, la cabecera de una notificación de iOS).
- **Don't** usar bordes de color, franjas laterales de color ni tarjetas por cada elemento de una lista.
- **Don't** usar sombras duras, desplazadas de lado o sin desenfoque.
- **Don't** usar glifos de fuente ni emoji como íconos.
- **Don't** usar el amarillo resaltador como color de alerta, premio o notificación.
- **Don't** rellenar de rojo un botón destructivo; va en gris control con texto rojo fraude profundo.
