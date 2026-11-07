# SimuChip

Simulador visual en Java que explica **funciones inyectivas, sobreyectivas y biyectivas** con un problema real del Perú: líneas móviles registradas a nombre de otra persona (usadas en extorsiones).

## Cómo abrirlo

Se necesita Java 17 o superior.

```bash
./ejecutar.sh          # o: java -jar SimuChip.jar
```

Otros comandos:

```bash
./probar.sh            # compila y corre las 74 pruebas
./empaquetar.sh        # vuelve a crear SimuChip.jar
java -Djava.awt.headless=true -cp out simuchip.Capturas capturas   # capturas PNG (antes correr ./probar.sh)
```

## Qué hace cada pestaña

| Pestaña | Para qué sirve |
|---|---|
| Inicio | Presenta la pregunta: ¿a quién le pertenece tu chip? |
| 1 · Aprende | Dibujas flechas entre A = {1, 2, 3…} y B = {a, b, c…}. La app dice si es función, inyectiva, sobreyectiva o biyectiva, explica por qué y resalta la línea de código que lo decidió. |
| 2 · Simula | El caso real. Cada día alguien contrata una línea; a veces es un extorsionador con un DNI robado. Se ven el registro f, el uso real u, el tope de líneas y la solución con biometría (g: Línea ↔ Contrato biyectiva). Clic en una línea = rastreo policial. |

## Modelo matemático

| Símbolo | Significado |
|---|---|
| L | líneas activas (chips) |
| P | personas con DNI (incluye al extorsionador) |
| C | contratos validados con huella |
| f: L → P | registro: lo que dice la operadora |
| u: L → P | uso real: quién usa el chip |
| g: L → C | contrato biométrico |

- El problema: hay líneas con f(l) ≠ u(l).
- El tope de líneas por persona es |f⁻¹(p)| ≤ k (principio del palomar).
- La solución: g biyectiva, con la huella del contrato igual a u(l). Gracias a g⁻¹, cada chip lleva a una persona real.

## Código

```
src/simuchip/
  funciones/Funciones.java     ← el corazón: contar flechas que salen y llegan
  funciones/Diagnostico.java   ← explica en palabras por qué cumple o no
  funciones/Flecha.java
  caso/Simulador.java          ← f, u, g, fraude, biometría, tope, auditoría, rastreo
  caso/Persona, Linea, Contrato, Rastreo, Evento, Resultado
  ui/                          ← interfaz Swing dibujada a mano (sin librerías)
  Principal.java               ← abre la ventana
  Capturas.java                ← genera PNG sin abrir ventana
test/simuchip/                 ← 74 pruebas sin librerías externas
```
