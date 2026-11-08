---
version: 1
slug: "src-simuchip-ui-aplicacion-java"
primary_target: "src/simuchip/ui/Aplicacion.java"
related_targets: []
---

# Surface brief: aplicación ChipGuard (todos los capítulos)

Modo: Experience. El público está "dentro" de la demo proyectada; la obra dirige desde el primer cuadro.
Audiencia: aula universitaria + profesor. Tarea: entender problema → casos → modelo → simulación → solución.

## Direction contract

THESIS: un capítulo, una idea, a pantalla completa, como una keynote de Apple que se puede tocar. Cada capítulo abre con un titular que enuncia la idea. Rechaza la app de pestañas tipo navegador con barras de botones y tarjetas apiladas.

OWN-WORLD: fondo de página #FBFBFD y bandas a sangre en gris #F5F5F7; texto #1D1D1F, secundario #6E6E73, filetes #D2D2D7. Un azul de acción #0071E3. Colores de sistema solo con significado: rojo = fraude, verde = seguro, naranja = conjunto A. El amarillo de sistema #FFD60A se usa solo como resaltado de foco (renglón de código activo y elemento revisado), como el resaltado de un editor. Tipografía Geist incluida en el proyecto: titulares Bold con tracking negativo, de 56–64 px en los capítulos narrativos y de 40 px en los capítulos de trabajo, donde comparten fila con sus controles. El texto va de 15–19 px. Controles: píldora azul o gris #E8E8ED, control segmentado e interruptores tipo macOS/iOS. Listas sin chrome de tarjeta. Sombras suaves con desplazamiento. Sin bordes de color, franjas laterales ni etiquetas sobre los títulos.

STORY: el público ve primero que su DNI puede tener chips ajenos (cifras que cuentan hacia arriba), luego cómo pasa (casos y el hueco de la biometría), aprende a contar flechas, ve la simulación fallar y luego funcionar, y termina tocando el celular de ChipGuard o jugando.

FIRST VIEWPORT: capítulo 1. A la izquierda, un titular enorme: «Puede haber chips / a tu nombre.» y, en gris, «Y tú no lo sabes.» (adaptado: dos golpes cortos leen mejor en proyector que una sola frase larga). A la derecha, un DNI dibujado al que se le pegan chips animados, uno por uno; 3 se vuelven rojos y el contador sube de 1 a 4. Abajo, una fila a todo el ancho con las 4 cifras de OSIPTEL contando. Navegación mínima arriba con la marca ChipGuard y los capítulos como texto con un punto en el activo; a la derecha, botón azul con el nombre del capítulo siguiente y la flecha después del texto (adaptado: nombrar el destino orienta más que decir «Siguiente»).

FORM: dirección fijada por el usuario («minimalista como Apple» + «no preguntes, hazlo»), lista ordenada 1/1. No hubo tirada de concept-seed: la dirección fijada por el brief prevalece sobre la tirada, así que se omitió a propósito, no porque no estuviera disponible. seed: none (unavailable)

FINISH: unreviewed and undocumented is unfinished; this build ends with the finish review, the verdict, DESIGN.md, and every shipping raster carrying its provenance

Interacción firma: transición entre capítulos (fundido desde blanco + subida de 18 px, ease-out exponencial en 420 ms) y teclas ← → para avanzar como en una keynote.
