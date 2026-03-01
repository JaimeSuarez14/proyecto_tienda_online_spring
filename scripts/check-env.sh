#!/bin/bash
# scripts/check-env.sh

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${YELLOW}=========================================${NC}"
echo -e "${GREEN}PUNTO 3: VERIFICAR VARIABLES DE ENTORNO${NC}"
echo -e "${YELLOW}=========================================${NC}"

# Verificar si existe .env
if [ ! -f .env ]; then
    echo -e "${RED}❌ Archivo .env no encontrado${NC}"
    echo -e "${YELLOW}Creando .env desde .env.example...${NC}"
    cp .env.example .env
    echo -e "${GREEN}✅ .env creado. Revísalo y ajústalo si es necesario.${NC}"
else
    echo -e "${GREEN}✅ Archivo .env encontrado${NC}"
fi

# Mostrar configuración actual
echo -e "\n${YELLOW}Configuración actual:${NC}"
echo "----------------------------------------"
grep -v "^#" .env | grep -v "^$" | while read line; do
    echo "  $line"
done
echo "----------------------------------------"

echo -e "\n${GREEN}✅ Verificación completada${NC}"
echo -e "${YELLOW}=========================================${NC}"