# Product

<!-- impeccable:product-schema 1 -->

> Escrito sin entrevista: Jordy pidió expresamente no preguntar en este proyecto. Los datos vienen de sus mensajes del 2026-11-07 y de las fuentes citadas en Evidence on Hand; lo inferido va marcado como (inferido). Actualizado el 2026-11-14 para coincidir con la app (`Aplicacion.java`) y con el informe (`informe/informe.html`), que es la fuente principal de los objetivos y del marco teórico.

## Platform

desktop (Java 17+, Swing dibujado a mano; se proyecta en clase desde una laptop Linux o Windows)

## Users

- Público de la exposición: estudiantes universitarios y el profesor de Estructuras Discretas I, mirando un proyector durante 10–12 minutos.
- Presentadores: un grupo de 4 estudiantes que maneja la app en vivo y pide un voluntario para el juego.

## Product Purpose

ChipGuard explica un problema real del Perú, los chips registrados con el DNI de otra persona y usados para extorsionar, y lo modela con funciones inyectivas, sobreyectivas y biyectivas. La exposición sigue este orden: primero el caso de Ana (la problemática), luego la matemática, después la propuesta ChipGuard (qué es, cómo actúa y cómo está hecha), luego el juego «La tienda», que simula el antes y el después, y al final el cierre. El éxito es que el público entienda el problema sin saber matemática y que el profesor vea el modelo formal y el código.

## Objetivos

Tomados del informe (§3). Si cambian, se actualizan primero allá.

- **Objetivo general:** modelar el registro de líneas móviles del Perú como una función entre conjuntos finitos, para explicar por qué permite la extorsión con chips a nombre de terceros, y proponer y simular una solución basada en las propiedades de las funciones.
- **Objetivos específicos:**
  1. Representar el registro y el uso real de las líneas como dos funciones y clasificarlas.
  2. Explicar la inyectividad, la sobreyectividad, la biyectividad y el principio del palomar con casos cotidianos.
  3. Diseñar ChipGuard, describiendo qué es, qué funciones ofrece, su interfaz y su arquitectura.
  4. Desarrollar una aplicación en Java que presente el caso y simule el efecto de ChipGuard con pruebas automáticas.

## Marco teórico

Resumen del informe (§4 y §5, basado en Rosen, 2019). Cada símbolo va con su frase en palabras.

- **Función** f : A → B: a cada elemento de A le asigna exactamente uno de B. A es el dominio, B el codominio, y los valores que f alcanza forman su imagen. En el diagrama sale una y solo una flecha de cada elemento de A.
- **Inyectiva:** si x₁ ≠ x₂ entonces f(x₁) ≠ f(x₂). A cada elemento de B le llega como máximo una flecha. Ejemplo: pasajero → asiento de bus. La sobreventa la rompe.
- **Sobreyectiva:** todo y ∈ B recibe al menos una flecha. Ejemplo: alumno → grupo de exposición, donde ningún grupo queda vacío.
- **Biyectiva e inversa:** inyectiva y sobreyectiva a la vez. Solo entonces existe la inversa f⁻¹ : B → A. Ejemplo: persona ↔ DNI.
- **Preimagen:** f⁻¹({b}) = { a ∈ A : f(a) = b }, el conjunto de todo lo que llega a b. Existe aunque f no sea biyectiva. Por eso «las líneas a nombre de Ana» se escriben f⁻¹({Ana}), y no como una inversa.
- **Principio del palomar:** si se reparten n objetos en k cajones y n > k, algún cajón recibe más de uno. Dicho de otra forma, si |A| > |B|, ninguna función de A en B es inyectiva. Aplicado a una persona: si su DNI tiene k cupos de línea, el chip k + 1 no cabe en un cupo propio y no puede registrarse. Esa es la base del tope de k líneas por persona.
- **Modelo del caso:** C = chips activos y P = personas con DNI.
  - f : C → P es el **registro** («dueño en papel»): lo conocen la operadora y la policía.
  - u : C → P es el **uso real** («quien de verdad lo usa»): no lo conoce nadie salvo quien usa el chip.
  - Ninguna de las dos es inyectiva (tener varias líneas es legal) ni sobreyectiva (no todos tienen chip).
  - f(c) ≠ u(c) no siempre es fraude: un padre puede contratar el chip de su hijo. El fraude es que exista c ∈ C con f(c) ≠ u(c) **sin que f(c) lo haya autorizado**. Así, la policía rastrea f(c) y llega a Ana, no al extorsionador.
  - Simplificación a propósito: se supone que quien usa el chip también está en P.
- **Condición de la solución** (informe §5.1): todo chip activo c cumple que f(c) = u(c) o que f(c) lo autorizó expresamente. ChipGuard lo garantiza por construcción: el chip no se activa hasta que f(c), el titular en papel, lo confirma desde su celular. Así, la flecha del registro siempre lleva a una persona que dijo «sí, soy yo» o «sí, es para mi hijo».
- **Tope:** además, |f⁻¹({p})| ≤ k para toda persona p, es decir, nadie acumula más de k líneas (palomar). k es un parámetro. En la app de ChipGuard el titular ve su tope en «Mis chips». Como referencia real, la Resolución 000116-2025 pide validar la identidad desde las 10 líneas. La simulación anterior usaba k = 3 (`caso/Simulador.java`).

## Positioning

La propuesta ChipGuard no reemplaza la biometría que OSIPTEL ya exige en la tienda ni el portal «Checa tus líneas». Hoy el ciudadano ya puede consultar f⁻¹({su DNI}), es decir, las líneas a su nombre. Pero lo hace después y por iniciativa propia, y el chip fraudulento ya está activo cuando lo detecta. ChipGuard cambia el momento: la alerta llega al celular del titular en cuanto alguien contrata una línea con su DNI, y el chip no se activa sin su confirmación. Frente a la restricción de contratación que ya existe (en «Checa tus líneas» y en la Resolución 000116-2025), la diferencia es el nivel de detalle. La restricción es un interruptor general: o bloquea todas las líneas nuevas o no bloquea ninguna. ChipGuard decide chip por chip, en el momento y con aviso, y además reporta la tienda a OSIPTEL de forma automática (informe, Tabla 4).

## Operating Context

- Se usa en una exposición en vivo con proyector, mouse y teclado.
- Tiene que verse grande y legible desde el fondo del aula (inferido).
- Se combina con una PPT y un informe en PDF.

## Capabilities and Constraints

- Capítulos actuales (`src/simuchip/ui/Aplicacion.java`, en orden):
  1. La historia: el caso de Ana. La «doble huella» es el truco que la biometría no detecta: el vendedor dice que la huella «no salió», pide una segunda lectura y con ella registra un chip extra a nombre de Ana (informe §2.2). Luego vienen la extorsión y la policía.
  2. La matemática: función, inyectiva, sobreyectiva y biyectiva con casos reales; registro ≠ uso real; principio del palomar.
  3. Qué es ChipGuard: app, web y servidor; usos, actores, pantallas y comparación con «Checa tus líneas».
  4. En acción: la alerta llega al celular.
  5. Por dentro: la arquitectura y el flujo de 7 pasos.
  6. La tienda: juego en 2 rondas, sin y con ChipGuard (de 3 chips en manos criminales a 0).
  7. Cierre: conclusiones y referencias.
- 119 pruebas sin librerías externas.
- La app debe correr con `java -jar`, sin dependencias. Las fuentes van dentro del proyecto.
- En Linux, Java solo escala con números enteros.

## Brand Commitments

- Nombre: **ChipGuard** (lo decidió Jordy; reemplaza a SimuChip y a «Yo Autorizo»).
- Estética fijada por Jordy: minimalista al estilo Apple. Que «no parezca navegador», que sea más visual, más animada y más gráfica.

## Evidence on Hand

- Cifras de OSIPTEL de 2025 (notas de prensa): 309 454 líneas inconsistentes, más de 102 mil dadas de baja, más de 650 mil celulares bloqueados y unos 200 mil usuarios con más de 10 líneas.
- Hoy ya existe la biometría con huella contra RENIEC en la tienda. En la compra web o por app, el chip llega por delivery y se activa con huella en la app.
- OSIPTEL ofrece desde 2023 el portal «Checa tus líneas» (checatuslineas.osiptel.gob.pe). Ahí cualquier persona consulta las líneas registradas con su DNI y reporta las que no reconoce (nota de prensa de OSIPTEL, 2023-07-06; El Peruano). También permite restringir la contratación de nuevas líneas a nombre propio (El Peruano, 2025; ver informe §2.3). Verificado el 2026-11-14.
- Las extorsiones concretas de la historia de Ana son ilustrativas, no casos reales.
- Normas de 2025, verificadas en prensa jurídica el 2026-11-07:
  - La Resolución 000116-2025-CD/OSIPTEL exige validar la identidad a quien tiene más de 10 líneas y permite pedir que se restrinjan los canales de contratación (fuentes: LP Derecho, «Osiptel: medidas para fortalecer la seguridad en la contratación del servicio móvil»; La República, 28/11/2025).
  - Los más de 650 mil celulares bloqueados durante el estado de emergencia vienen de una nota del Congreso de la República de 2025 («OSIPTEL bloquea más de 650 mil celulares…»), recogida también por Gestión.
- No hay testimonios ni clientes. No hay que inventarlos.

## Product Principles

1. La problemática va antes que la matemática: nadie se interesa por una función sin conocer a la víctima.
2. Cada símbolo aparece junto a su frase en palabras («dueño en papel» = f).
3. Mostrar en vez de afirmar: cada idea se ve en movimiento, ya sea con flechas, el celular o el código corriendo.
4. Ser honesto con los datos: los casos ilustrativos se marcan como tales.
