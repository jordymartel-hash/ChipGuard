#!/usr/bin/env bash
# Crea SimuChip.jar (doble clic o: java -jar SimuChip.jar). Uso: ./empaquetar.sh
set -e
cd "$(dirname "$0")"
rm -rf out-app && mkdir out-app
javac -encoding UTF-8 --release 17 -d out-app $(find src -name '*.java')
jar --create --file SimuChip.jar --main-class simuchip.Principal -C out-app .
rm -rf out-app
echo "Listo: SimuChip.jar"
