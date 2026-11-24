# Design Document - Sistema de Gestión de Biblioteca


## 1. Patrón Arquitectónico: MVC (Modelo - Vista - Controlador)

El proyecto se estructurará separando la lógica para facilitar la mantenibilidad y el trabajo colaborativo en ramas de Git:

* **Vista (View):** Interfaces en Java Swing para el módulo del bibliotecario y la pantalla de autoservicio.

* **Controlador (Controller):** Clases que capturan los eventos de la vista, validan reglas de negocio (ej. límite de 3 libros) y orquestan las operaciones.

* **Modelo (Model):** Entidades de dominio (Estudiante, Ejemplar, Cubiculo) y lógica de persistencia.


## 2. Patrón de Persistencia: DAO (Data Access Object)

Todo acceso a Oracle DB se centralizará mediante el patrón DAO para no contaminar los controladores con código SQL.

* Se crearán interfaces (ej. `EstudianteDAO`) y clases concretas (`EstudianteDAOImpl`) que manejarán las conexiones JDBC, los `PreparedStatement` y los `ResultSet`.


## 3. Estrategia de Concurrencia

* **Gestión de Cubículos (CUS05):** Se implementará *Pessimistic Locking* directo en Oracle. El DAO del cubículo ejecutará `SELECT ... FOR UPDATE` al consultar la disponibilidad, asegurando que si dos hilos de ejecución intentan reservar el mismo cubículo en el mismo 
milisegundo, la base de datos encole las peticiones y evite inconsistencias de aforo.


## 4. Arquitectura de Interfaz de Usuario (UI/UX)

El diseño visual abandonará el tradicional `JTabbedPane` y la barra lateral a favor de un "Dashboard" (Panel de control) moderno y
simplificado, enfocado en la experiencia del usuario de la UNMSM.


### 4.1. Gestión de Recursos (Assets)

* Todas las imágenes (logos, iconos, fondos) residirán en el paquete `src/recursos/`.


### 4.2. Pantalla Principal (Dashboard)

* **Fondo Personalizado:** Se usará un `JPanel` sobrescribiendo el método `paintComponent()` para dibujar una fotografía de la universidad (UNMSM/FISI) con un filtro de opacidad (semi-transparente) que le dé un aspecto profesional sin dificultar la lectura.

* **Cabecera (Header):**

  * **Izquierda:** Logo oficial de la UNMSM.

  * **Centro:** Título del Sistema.

  * **Derecha:** Un botón discreto o enlace "Modo Administrador" para acceder a configuraciones y mantenimiento del catálogo.

* **Cuerpo (Grid de Opciones):**

  Un contenedor centrado (`GridLayout` o `FlowLayout`) que agrupe "Tarjetas" (Cards). Cada tarjeta será un botón grande (`JButton` o `JPanel` clicable) con la siguiente estructura:

  1. **Icono descriptivo mediano** (centrado).

  2. **Texto de acción clara** (Ej. "Préstamo de Libros", "Devolución", "Buscar en Catálogo", "Autoservicio QR", "Reservar Cubículo"). *No se usarán nomenclaturas técnicas como "CUS01".*


### 4.3. Navegación

* Al hacer clic en cualquiera de las tarjetas principales, el sistema abrirá un `JDialog` modal (o reemplazará el panel central) con el formulario específico de esa acción, manteniendo el diseño limpio.

* **Modo Administrador:** Requerirá autenticación básica. Una vez dentro, mostrará opciones CRUD (Crear, Leer, Actualizar, Borrar) para gestionar el catálogo de libros, notificaciones de faltantes y penalizaciones.
