#!/usr/bin/env bash
# Compila (si hace falta) y abre SimuChip. Uso: ./ejecutar.sh
set -e
cd "$(dirname "$0")"
if [ ! -f ChipGuard.jar ]; then ./empaquetar.sh; fi
java -jar ChipGuard.jar
