# 🛒 PlazaChina - Plataforma de E-Commerce

![Java](https://img.shields.io/badge/Java-21-orange?logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-green?logo=spring-boot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.6%2B-C71A36?logo=apache-maven&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-ES6%2B-yellow?logo=javascript&logoColor=white)
[![Java CI with Maven](https://github.com/DesarrolloDigitalPeru/proyecto_tienda_online_spring/actions/workflows/build.yml/badge.svg?event=pull_request)](https://github.com/DesarrolloDigitalPeru/proyecto_tienda_online_spring/actions/workflows/build.yml)

Una aplicación web completa de e-commerce construida con **Spring Boot 4.0.0** que permite gestionar productos, carrito de compras, usuarios y pagos integrados con múltiples pasarelas de pago.

---

## 📋 Tabla de Contenidos

- [Descripción General](#descripción-general)
- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Requisitos Previos](#requisitos-previos)
- [Instalación y Configuración](#instalación-y-configuración)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Módulos Principales](#módulos-principales)
- [Funcionamiento](#funcionamiento)
- [Configuración de Base de Datos](#configuración-de-base-de-datos)
- [Pasarelas de Pago](#pasarelas-de-pago)
- [Cómo Usar](#cómo-usar)
- [Contribuir](#contribuir)

---

## 📚 Descripción General

**PlazaChina** es una plataforma de comercio electrónico que proporciona:

✅ **Registro e Inicio de Sesión** - Sistema seguro de autenticación de usuarios
✅ **Catálogo de Productos** - Visualización pública y gestión administrativa
✅ **Carrito de Compras** - Agregar, modificar y eliminar productos
✅ **Sistema de Pagos Integrado** - Soporte para múltiples pasarelas
✅ **Panel Administrativo** - Gestión completa de usuarios, productos, descuentos y ventas
✅ **Gestión de Descuentos** - Aplicar promociones a productos
✅ **Historial de Ventas** - Registro completo de transacciones

La aplicación utiliza una arquitectura **MVC (Modelo-Vista-Controlador)** con **Spring Boot** en el backend y **Thymeleaf** para las vistas del lado del servidor.

---

## 🛠️ Tecnologías Utilizadas

### Backend
- **Java 17** - Lenguaje de programación principal
- **Spring Boot 3.5.0** - Framework web y base para toda la aplicación
- **Spring Data JPA** - ORM para manejo de datos
- **Spring Security** - Seguridad y autenticación
- **Maven** - Gestor de dependencias y build

### Base de Datos
- **MySQL 8** - Base de datos relacional
- **Hibernate** - ORM integrado con JPA

### Frontend
- **Thymeleaf** - Motor de plantillas HTML del lado del servidor
- **HTML5** - Estructura
- **CSS3** - Estilos
- **JavaScript** - Interactividad en el cliente

### Pasarelas de Pago
- **Yape** - Billetera digital peruana
- **Plin** - Sistema de pagos peruano
- **PayPal** - Plataforma de pagos internacional

---

## ✅ Requisitos Previos

Antes de instalar el proyecto, asegúrate de tener:

1. **Java JDK 17** o superior instalado
   ```bash
   java -version
   ```

2. **MySQL 8** instalado y ejecutándose
   ```bash
   mysql --version
   ```

3. **Maven 3.8+** instalado
   ```bash
   mvn --version
   ```

4. **Git** (opcional, para clonar repositorios)

---

## 🚀 Instalación y Configuración

### 1. Clonar o Descargar el Proyecto
```bash
cd tu/ruta/de/proyecto
```

### 2. Configurar la Base de Datos

Abre MySQL y crea la base de datos:
```sql
CREATE DATABASE plazachina;
```

### 3. Configurar `application.properties`

Edita el archivo `src/main/resources/application.properties`:

```properties
# Conexión a MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/plazachina
spring.datasource.username=root
spring.datasource.password=tu_contraseña
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect

# Puerto del servidor
server.port=8080
```

### 4. Compilar el Proyecto

Usa Maven para compilar:

**En Windows:**
```bash
mvnw.cmd clean install
```

**En Linux/Mac:**
```bash
./mvnw clean install
```

### 5. Ejecutar la Aplicación

**Con Maven:**
```bash
mvnw.cmd spring-boot:run
```

O ejecuta el JAR generado en `target/`:
```bash
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### 6. Acceder a la Aplicación

Abre tu navegador y ve a:
```
http://localhost:8080
```

---

## 📁 Estructura del Proyecto

```
proyectoIn/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── DemoApplication.java          # Clase principal de Spring Boot
│   │   │   │
│   │   │   ├── configuracion/                # Configuraciones de la aplicación
│   │   │   │   ├── PasarelaPagoConfig.java   # Config de pasarelas de pago
│   │   │   │   └── SecurityConfig.java       # Config de seguridad
│   │   │   │
│   │   │   ├── controlador/                  # Controllers (MVC)
│   │   │   │   ├── AdminController.java
│   │   │   │   ├── AdminDescuentoController.java
│   │   │   │   ├── AdminProductoController.java
│   │   │   │   ├── AdminUsuarioController.java
│   │   │   │   ├── AdminVentasController.java
│   │   │   │   ├── BienvenidaController.java
│   │   │   │   ├── CarritoController.java
│   │   │   │   ├── CarritoRestController.java
│   │   │   │   ├── HomeController.java
│   │   │   │   ├── LoginController.java
│   │   │   │   ├── NosotrosController.java
│   │   │   │   ├── PagoController.java
│   │   │   │   ├── PagoRestController.java
│   │   │   │   ├── ProductoController.java
│   │   │   │   ├── ProductoPublicoController.java
│   │   │   │   └── RegistroController.java
│   │   │   │
│   │   │   ├── modelo/                       # Entidades JPA
│   │   │   │   ├── Usuario.java
│   │   │   │   ├── Producto.java
│   │   │   │   ├── Carrito.java
│   │   │   │   ├── Pago.java
│   │   │   │   └── DetalleVenta.java
│   │   │   │
│   │   │   ├── repositorio/                  # Interfaces Repository (DAO)
│   │   │   │   ├── UsuarioRepository.java
│   │   │   │   ├── ProductoRepository.java
│   │   │   │   ├── CarritoRepository.java
│   │   │   │   ├── PagoRepository.java
│   │   │   │   └── DetalleVentaRepository.java
│   │   │   │
│   │   │   └── servicio/                     # Lógica de negocio
│   │   │       ├── UsuarioService.java
│   │   │       ├── UsuarioServiceImpl.java
│   │   │       ├── ProductoService.java
│   │   │       ├── ProductoServiceImpl.java
│   │   │       ├── CarritoService.java
│   │   │       ├── CarritoServiceImpl.java
│   │   │       ├── PagoService.java
│   │   │       ├── PagoServiceImpl.java
│   │   │       └── PasarelaPagoService.java
│   │   │
│   │   └── resources/
│   │       ├── application.properties        # Config principal
│   │       │
│   │       ├── static/                       # Archivos estáticos
│   │       │   ├── css/
│   │       │   │   └── style.css
│   │       │   ├── images/                   # Imágenes del proyecto
│   │       │   └── js/
│   │       │       └── pago.js               # Scripts de pagos
│   │       │
│   │       └── templates/                    # Vistas Thymeleaf
│   │           ├── admin_bienvenida.html
│   │           ├── admin_descuentos.html
│   │           ├── admin_productos.html
│   │           ├── admin_usuarios.html
│   │           ├── admin_ventas.html
│   │           ├── bienvenida.html
│   │           ├── carrito.html
│   │           ├── index.html
│   │           ├── login.html
│   │           ├── nosotros.html
│   │           ├── pago.html
│   │           ├── pago_exitoso.html
│   │           ├── productos.html
│   │           ├── productos_publicos.html
│   │           └── registro.html
│   │
│   └── test/
│       └── java/com/example/demo/
│           └── DemoApplicationTests.java
│
├── pom.xml                                   # Dependencias Maven
├── mvnw                                      # Maven wrapper (Linux/Mac)
├── mvnw.cmd                                  # Maven wrapper (Windows)
└── README.md                                 # Este archivo
```

---

## 🎯 Módulos Principales

### 1. **Módulo de Autenticación y Usuarios** 👥

**Ubicación:** `controlador/LoginController.java`, `controlador/RegistroController.java`

**Descripción:** Gestiona el registro, login y manejo de sesiones de usuarios.

**Características:**
- Registro de nuevos usuarios
- Autenticación segura con Spring Security
- Validación de credenciales
- Gestión de sesiones

**Endpoints principales:**
- `GET /login` - Formulario de inicio de sesión
- `POST /login` - Procesar login
- `GET /registro` - Formulario de registro
- `POST /registro` - Registrar nuevo usuario

---

### 2. **Módulo de Productos** 📦

**Ubicación:** `controlador/ProductoController.java`, `controlador/ProductoPublicoController.java`

**Descripción:** Gestión del catálogo de productos con vistas públicas y administración.

**Características:**
- Visualización de productos públicos
- CRUD de productos (administración)
- Búsqueda y filtrado
- Gestión de imágenes
- Control de inventario

**Endpoints principales:**
- `GET /productos` - Listar todos los productos
- `GET /productos/{id}` - Detalle de producto
- `POST /admin/productos` - Crear producto (admin)
- `PUT /admin/productos/{id}` - Actualizar producto (admin)
- `DELETE /admin/productos/{id}` - Eliminar producto (admin)

---

### 3. **Módulo de Carrito de Compras** 🛒

**Ubicación:** `controlador/CarritoController.java`, `controlador/CarritoRestController.java`

**Descripción:** Gestión del carrito de compras con operaciones AJAX.

**Características:**
- Agregar productos al carrito
- Modificar cantidades
- Eliminar productos
- Visualización del carrito
- Cálculo automático de totales
- Persistencia en sesión/base de datos

**Endpoints principales:**
- `GET /carrito` - Ver carrito
- `POST /api/carrito/agregar` - Agregar producto (REST)
- `PUT /api/carrito/actualizar` - Actualizar cantidad (REST)
- `DELETE /api/carrito/eliminar/{id}` - Eliminar producto (REST)

---

### 4. **Módulo de Pagos** 💳

**Ubicación:** `controlador/PagoController.java`, `servicio/PagoService.java`

**Descripción:** Orquesta el proceso de pago e integración con pasarelas.

**Características:**
- Selección de método de pago
- Integración con múltiples pasarelas
- Generación de órdenes de pago
- Confirmación y validación de pagos
- Historial de transacciones

**Endpoints principales:**
- `GET /pago` - Formulario de pago
- `POST /pago/procesar` - Procesar pago
- `GET /pago/exitoso` - Confirmación de pago
- `POST /api/pago/validar` - Validar pago (REST)

---

### 5. **Módulo de Descuentos** 🏷️

**Ubicación:** `controlador/AdminDescuentoController.java`

**Descripción:** Gestión de promociones y descuentos aplicables a productos.

**Características:**
- Crear y eliminar descuentos
- Aplicar descuentos a productos
- Configurar porcentajes de descuento
- Historial de descuentos aplicados

**Endpoints principales:**
- `GET /admin/descuentos` - Listar descuentos
- `POST /admin/descuentos` - Crear descuento (admin)
- `DELETE /admin/descuentos/{id}` - Eliminar descuento (admin)

---

### 6. **Módulo Administrativo** ⚙️

**Ubicación:** `controlador/Admin*.java`

**Descripción:** Panel de control para administradores del sistema.

**Submódulos:**

**6.1 Gestión de Usuarios**
- Listar todos los usuarios
- Ver detalles de usuario
- Editar información de usuario
- Cambiar roles/permisos

**6.2 Gestión de Productos**
- CRUD completo de productos
- Administrar inventario
- Subir imágenes

**6.3 Gestión de Ventas**
- Ver todas las ventas realizadas
- Filtrar por rango de fechas
- Exportar reportes
- Detalles de cada venta

**6.4 Gestión de Descuentos**
- Crear promociones
- Aplicar a productos
- Ver descuentos activos

---

## 🔄 Funcionamiento

### Flujo de Compra Típico

```
1. Usuario se registra/inicia sesión
        ↓
2. Navega por productos públicos
        ↓
3. Agrega productos al carrito
        ↓
4. Revisa el carrito (agregar/quitar)
        ↓
5. Procede al pago
        ↓
6. Selecciona pasarela de pago
        ↓
7. Completa la transacción
        ↓
8. Recibe confirmación de compra
        ↓
9. Venta se registra en historial
```

### Flujo de Administración

```
Admin inicia sesión
        ↓
Accede al panel administrativo
        ↓
Puede:
  • Gestionar usuarios
  • Crear/editar/eliminar productos
  • Ver historial de ventas
  • Administrar descuentos
```

---

## 🗄️ Configuración de Base de Datos

### Entidades Principales

**Usuario**
```sql
CREATE TABLE usuario (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    contraseña VARCHAR(255),
    rol VARCHAR(50),
    fecha_registro TIMESTAMP
);
```

**Producto**
```sql
CREATE TABLE producto (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100),
    descripcion TEXT,
    precio DECIMAL(10, 2),
    cantidad_disponible INT,
    imagen_url VARCHAR(255),
    creado_en TIMESTAMP
);
```

**Carrito**
```sql
CREATE TABLE carrito (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT,
    producto_id BIGINT,
    cantidad INT,
    fecha_agregado TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (producto_id) REFERENCES producto(id)
);
```

**Pago**
```sql
CREATE TABLE pago (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT,
    monto DECIMAL(10, 2),
    estado VARCHAR(50),
    metodo_pago VARCHAR(50),
    fecha_pago TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);
```

**DetalleVenta**
```sql
CREATE TABLE detalle_venta (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pago_id BIGINT,
    producto_id BIGINT,
    cantidad INT,
    precio_unitario DECIMAL(10, 2),
    subtotal DECIMAL(10, 2),
    FOREIGN KEY (pago_id) REFERENCES pago(id),
    FOREIGN KEY (producto_id) REFERENCES producto(id)
);
```

---

## 💰 Pasarelas de Pago

La aplicación soporta tres pasarelas de pago integradas:

### 1. **Yape** 📱
- Billetera digital peruana
- Transferencia instantánea
- Configuración en `application.properties`:
  ```properties
  pasarela.pago.yape.telefono=999999999
  pasarela.pago.yape.api-key=tu-api-key
  pasarela.pago.yape.api-url=https://api.yape.pe
  pasarela.pago.yape.habilitado=true
  ```

### 2. **Plin** 📲
- Sistema de pagos peruano
- Soporte para transferencias bancarias
- Configuración en `application.properties`:
  ```properties
  pasarela.pago.plin.telefono=999999999
  pasarela.pago.plin.api-key=tu-api-key
  pasarela.pago.plin.api-url=https://api.plin.pe
  pasarela.pago.plin.habilitado=true
  ```

### 3. **PayPal** 🌐
- Plataforma de pagos internacional
- Soporte para múltiples monedas
- Configuración en `application.properties`:
  ```properties
  pasarela.pago.paypal.business-email=tu-email@paypal.com
  pasarela.pago.paypal.client-id=tu-client-id
  pasarela.pago.paypal.client-secret=tu-client-secret
  pasarela.pago.paypal.api-url=https://www.paypal.com
  pasarela.pago.paypal.habilitado=true
  ```

---

## 📖 Cómo Usar

### Usuario Regular

1. **Registrarse:**
   - Ir a `/registro`
   - Llenar el formulario con nombre, email y contraseña
   - Clickear en "Registrarse"

2. **Iniciar Sesión:**
   - Ir a `/login`
   - Ingresar email y contraseña
   - Clickear en "Iniciar Sesión"

3. **Comprar Productos:**
   - Ir a "Productos"
   - Hacer click en "Agregar al carrito"
   - Ir a "Mi carrito"
   - Hacer click en "Proceder al pago"
   - Seleccionar método de pago
   - Completar la transacción

### Administrador

1. **Acceder al Panel Admin:**
   - Iniciar sesión con cuenta de administrador
   - Acceder a `/admin`

2. **Gestionar Productos:**
   - Ir a "Administración" → "Productos"
   - Crear, editar o eliminar productos
   - Cargar imágenes

3. **Ver Ventas:**
   - Ir a "Administración" → "Ventas"
   - Filtrar por fecha si es necesario
   - Ver detalles de cada venta

4. **Gestionar Usuarios:**
   - Ir a "Administración" → "Usuarios"
   - Ver lista de usuarios registrados
   - Editar información

5. **Administrar Descuentos:**
   - Ir a "Administración" → "Descuentos"
   - Crear nuevas promociones
   - Aplicar a productos

---

## 🤝 Contribuir

Las contribuciones son bienvenidas. Para contribuir:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Haz commit a tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## 📝 Licencia

Este proyecto está bajo la licencia MIT. Ver el archivo `LICENSE` para más detalles.

---

## 📧 Contacto y Soporte

Para preguntas o soporte, contacta al equipo de desarrollo o abre un issue en el repositorio.

---

## 📊 Información de Versión

- **Versión:** 0.0.1-SNAPSHOT
- **Java:** 17
- **Spring Boot:** 3.5.0
- **MySQL:** 8.0+
- **Estado:** En desarrollo

---

**Última actualización:** Enero 2026

¡Gracias por usar PlazaChina! 🙏
##Integracion continua