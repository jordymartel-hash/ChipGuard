#!/usr/bin/env bash
# Compila (si hace falta) y abre SimuChip. Uso: ./ejecutar.sh
set -e
cd "$(dirname "$0")"
if [ ! -f SimuChip.jar ]; then ./empaquetar.sh; fi
java -jar SimuChip.jar
