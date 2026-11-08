#!/usr/bin/env bash
# Compila todo y corre las pruebas. Uso: ./probar.sh
set -e
cd "$(dirname "$0")"
rm -rf out
javac -encoding UTF-8 -d out $(find src test -name '*.java')
java -cp out simuchip.PruebasFunciones
java -cp out simuchip.PruebasSimulador
java -cp out simuchip.PruebasDiagnostico
java -cp out simuchip.PruebasTraza
java -cp out simuchip.PruebasDetective
java -cp out simuchip.PruebasTienda
java -cp out simuchip.ui.PruebasDisenio
