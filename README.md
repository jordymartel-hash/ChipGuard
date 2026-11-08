# ChipGuard — ¿a quién le pertenece tu chip?

Expo de Matemática Discreta (UPCH): funciones inyectivas, sobreyectivas y biyectivas aplicadas a las líneas móviles
registradas a nombre de terceros en el Perú, y la propuesta **ChipGuard**: ningún chip se activa con tu DNI sin tu permiso.

## Abrir
```bash
java -jar ChipGuard.jar      # Java 17+  (o ./ejecutar.sh)
```
Flechas ← → avanzan escenas y capítulos. Cronómetro: clic para empezar o pausar, clic derecho para reiniciar.

## Capítulos
1. La historia: el caso de Ana (la doble huella, el chip extra, la extorsión y la policía).
2. La matemática: función, inyectiva, sobreyectiva y biyectiva con casos reales (bus, grupos, DNI); registro ≠ uso real; palomar.
3. Qué es ChipGuard: app + web + servidor, usos, actores, pantallas y comparación con «Checa tus líneas».
4. En acción: la alerta en el celular.
5. Por dentro: la arquitectura y el flujo de 7 pasos.
6. La tienda: un juego en 2 rondas, sin y con ChipGuard (3 → 0 chips en manos criminales).
7. Cierre y referencias APA.

## Desarrollo
```bash
./probar.sh                                                              # 136 pruebas
java -Djava.awt.headless=true -cp out:recursos simuchip.Capturas capturas  # capturas
python3 informe/generar.py                                               # informe PDF
./empaquetar.sh                                                          # ChipGuard.jar
```
