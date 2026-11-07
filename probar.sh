#!/usr/bin/env bash
# Compila todo y corre las pruebas. Uso: ./probar.sh
set -e
cd "$(dirname "$0")"
rm -rf out
javac -encoding UTF-8 -d out $(find src test -name '*.java')
java -cp out simuchip.PruebasFunciones
java -cp out simuchip.PruebasSimulador
