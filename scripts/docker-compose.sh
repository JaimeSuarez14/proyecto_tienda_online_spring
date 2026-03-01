#!/bin/bash
# scripts/docker-compose.sh

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}=========================================${NC}"
echo -e "${GREEN}COMANDOS DOCKER-COMPOSE${NC}"
echo -e "${BLUE}=========================================${NC}"
echo "1. Iniciar servicios"
echo "2. Detener servicios"
echo "3. Ver estado"
echo "4. Ver logs"
echo "5. Reiniciar servicios"
echo "6. Eliminar todo (volúmenes incluidos)"
echo "7. Salir"
echo -e "${BLUE}=========================================${NC}"

read -p "Selecciona una opción: " option

case $option in
    1)
        echo -e "${GREEN}Iniciando servicios...${NC}"
        docker-compose up -d
        docker-compose ps
        echo -e "${GREEN}Aplicación: http://localhost:8081${NC}"
        echo -e "${GREEN}phpMyAdmin: http://localhost:8082${NC}"
        ;;
    2)
        echo -e "${GREEN}Deteniendo servicios...${NC}"
        docker-compose down
        ;;
    3)
        echo -e "${GREEN}Estado de servicios:${NC}"
        docker-compose ps
        ;;
    4)
        echo -e "${GREEN}Logs (Ctrl+C para salir):${NC}"
        docker-compose logs -f
        ;;
    5)
        echo -e "${GREEN}Reiniciando servicios...${NC}"
        docker-compose restart
        docker-compose ps
        ;;
    6)
        echo -e "${RED}Eliminando todo...${NC}"
        docker-compose down -v
        ;;
    7)
        echo "Saliendo..."
        exit 0
        ;;
    *)
        echo -e "${RED}Opción no válida${NC}"
        ;;
esac