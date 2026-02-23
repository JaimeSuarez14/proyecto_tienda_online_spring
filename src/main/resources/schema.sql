-- Crear tabla cupon si no existe
CREATE TABLE IF NOT EXISTS cupon (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_clave VARCHAR(255) NOT NULL UNIQUE,
    porcentaje_descuento INT NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_expiracion TIMESTAMP,
    descripcion VARCHAR(500)
);
