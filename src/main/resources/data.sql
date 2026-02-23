-- Datos iniciales de cupones de descuento

-- Cupón de bienvenida (10% de descuento, sin expiración)
INSERT INTO cupon (nombre_clave, porcentaje_descuento, activo, fecha_expiracion, descripcion) 
VALUES ('BIENVENIDA10', 10, true, NULL, 'Cupón de bienvenida con 10% de descuento');

-- Cupón de Navidad (15% de descuento, vigente hasta fin de año)
INSERT INTO cupon (nombre_clave, porcentaje_descuento, activo, fecha_expiracion, descripcion) 
VALUES ('NAVIDAD2024', 15, true, '2024-12-31 23:59:59', 'Cupón especial de Navidad con 15% de descuento');

-- Cupón de cliente VIP (20% de descuento)
INSERT INTO cupon (nombre_clave, porcentaje_descuento, activo, fecha_expiracion, descripcion) 
VALUES ('VIP20', 20, true, NULL, 'Cupón VIP con 20% de descuento');

-- Cupón de promoción de verano (12% de descuento)
INSERT INTO cupon (nombre_clave, porcentaje_descuento, activo, fecha_expiracion, descripcion) 
VALUES ('VERANO12', 12, true, '2024-03-31 23:59:59', 'Cupón de promoción de verano');

-- Cupón expirado (inactivo)
INSERT INTO cupon (nombre_clave, porcentaje_descuento, activo, fecha_expiracion, descripcion) 
VALUES ('EXPIRADO', 25, false, '2023-12-31 23:59:59', 'Cupón expirado - No disponible');
