\# Design Document - Sistema de Gestión de Biblioteca



\## 1. Patrón Arquitectónico: MVC (Modelo - Vista - Controlador)

El proyecto se estructurará separando la lógica para facilitar la mantenibilidad y el trabajo colaborativo en ramas de Git:

\* \*\*Vista (View):\*\* Interfaces en Java Swing para el módulo del bibliotecario y la pantalla de autoservicio.

\* \*\*Controlador (Controller):\*\* Clases que capturan los eventos de la vista, validan reglas de negocio (ej. límite de 3 libros) y orquestan las operaciones.

\* \*\*Modelo (Model):\*\* Entidades de dominio (Estudiante, Ejemplar, Cubiculo) y lógica de persistencia.



\## 2. Patrón de Persistencia: DAO (Data Access Object)

Todo acceso a Oracle DB se centralizará mediante el patrón DAO para no contaminar los controladores con código SQL.

\* Se crearán interfaces (ej. `EstudianteDAO`) y clases concretas (`EstudianteDAOImpl`) que manejarán las conexiones JDBC, los `PreparedStatement` y los `ResultSet`.



\## 3. Estrategia de Concurrencia

\* \*\*Gestión de Cubículos (CUS05):\*\* Se implementará \*Pessimistic Locking\* directo en Oracle. El DAO del cubículo ejecutará `SELECT ... FOR UPDATE` al consultar la disponibilidad, asegurando que si dos hilos de ejecución intentan reservar el mismo cubículo en el mismo milisegundo, la base de datos encole las peticiones y evite inconsistencias de aforo.

