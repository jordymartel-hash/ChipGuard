#!/usr/bin/env bash
# Crea SimuChip.jar (doble clic o: java -jar SimuChip.jar). Uso: ./empaquetar.sh
set -e
cd "$(dirname "$0")"
rm -rf out-app && mkdir out-app
javac -encoding UTF-8 --release 17 -d out-app $(find src -name '*.java')
jar --create --file ChipGuard.jar --main-class simuchip.Principal -C out-app . -C recursos .
rm -rf out-app
echo "Listo: ChipGuard.jar"
