# Product

<!-- impeccable:product-schema 1 -->

> Escrito sin entrevista: Jordy pidió expresamente no preguntar en este proyecto. Cada dato viene de sus mensajes del 2026-11-07; lo inferido va marcado como (inferido).

## Platform

desktop (Java 17+, Swing dibujado a mano; se proyecta en clase desde una laptop Linux o Windows)

## Users

- Público de la exposición: estudiantes universitarios y el profesor de Matemática Discreta, mirando un proyector durante 10–12 minutos.
- Presentadores: un grupo de 4 estudiantes que maneja la app en vivo y pide un voluntario para el juego.

## Product Purpose

ChipGuard explica un problema real del Perú, los chips registrados con el DNI de otra persona y usados para extorsionar, y lo modela con funciones inyectivas, sobreyectivas y biyectivas. Sigue este orden: primero la problemática, luego los casos, después la simulación y al final la solución. El éxito es que el público entienda el problema sin saber matemática y que el profesor vea el modelo formal y el código.

## Positioning

La propuesta ChipGuard no reemplaza la biometría que OSIPTEL ya exige en la tienda. Le agrega la confirmación del dueño real del DNI desde su propio celular. Hoy la operadora conoce f⁻¹(DNI) y el ciudadano no; con ChipGuard, la inversa queda en el bolsillo del ciudadano.

## Operating Context

- Se usa en una exposición en vivo con proyector, mouse y teclado.
- Tiene que verse grande y legible desde el fondo del aula (inferido).
- Se combina con una PPT y un informe en PDF.

## Capabilities and Constraints

- Capítulos actuales:
  - Aprende: dibujar flechas A→B; el código corre paso a paso.
  - Simula: días de contrataciones, fraude, biometría, tope de 3 y auditoría.
  - Historia con celular simulado.
  - Juego Detective.
- 119 pruebas sin librerías externas.
- La app debe correr con `java -jar`, sin dependencias. Las fuentes van dentro del proyecto.
- En Linux, Java solo escala con números enteros.

## Brand Commitments

- Nombre: **ChipGuard** (lo decidió Jordy; reemplaza a SimuChip y a «Yo Autorizo»).
- Estética fijada por Jordy: minimalista al estilo Apple. Que «no parezca navegador», que sea más visual, más animada y más gráfica.

## Evidence on Hand

- Cifras de OSIPTEL de 2025 (notas de prensa): 309 454 líneas inconsistentes, más de 102 mil dadas de baja, más de 650 mil celulares bloqueados y unos 200 mil usuarios con más de 10 líneas.
- Hoy ya existe la biometría con huella contra RENIEC en la tienda. En la compra web o por app, el chip llega por delivery y se activa con huella en la app.
- Las extorsiones concretas de la historia de Ana son ilustrativas, no casos reales.
- No hay testimonios ni clientes. No hay que inventarlos.

## Product Principles

1. La problemática va antes que la matemática: nadie se interesa por una función sin conocer a la víctima.
2. Cada símbolo aparece junto a su frase en palabras («dueño en papel» = f).
3. Mostrar en vez de afirmar: cada idea se ve en movimiento, ya sea con flechas, el celular o el código corriendo.
4. Ser honesto con los datos: los casos ilustrativos se marcan como tales.
