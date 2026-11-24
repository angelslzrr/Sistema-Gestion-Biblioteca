\# Product Requirements Document (PRD) - Sistema de Gestión de Biblioteca



## 1. Actores del Sistema

* **Estudiante / Usuario:** Solicita préstamos, realiza devoluciones, busca libros en el catálogo, notifica faltantes y gestiona la reserva de cubículos.

* **Bibliotecario / Personal de Atención:** Gestiona préstamos/devoluciones presenciales y supervisa incidencias.

* **Administrador del Sistema:** Configura parámetros, revisa las notificaciones de libros faltantes, y gestiona el catálogo y permisos.

* **Sistema de Autoservicio (QR/RFID):** Dispositivo que procesa préstamos y devoluciones autónomas.


## 2. Requisitos Funcionales Principales

* **RF-01 (CUS01):** Permitir registro y préstamos de ejemplares.

* **RF-02 (CUS02):** Gestionar devoluciones y actualizar stock.

* **RF-03 (CUS03):** Consultar catálogo y registrar notificaciones de indisponibilidad.

* **RF-04 (CUS04):** Procesar transacciones mediante autoservicio (simulado por String).

* **RF-05 (CUS05):** Monitorear y gestionar reserva de cubículos en tiempo real (< 2 seg).


## 3. Especificaciones de Casos de Uso (Reglas de Negocio Estrictas)

### CUS01: Registrar préstamo de ejemplar

* **Acción:** `estadoEjemplar = 'PRESTADO'`. 

* **Regla:** Máximo 3 libros prestados simultáneamente.

* **Plazo:** Asignación automática de 7 días (`fecha_devolucion = sysdate + 7`).

### CUS02: Devolver ejemplar

* **Acción:** `estadoEjemplar = 'DISPONIBLE'`.

* **Sanciones:** Si se devuelve tarde o dañado, `estadoEstudiante = 'SUSPENDIDO'` por exactamente 2 días (sin multa monetaria).

### CUS03: Búsqueda de Catálogo y Notificación al Administrador

* **Acción:** El estudiante puede buscar libros por título, autor o ISBN y visualizar en tiempo real si hay ejemplares DISPONIBLES.

* **Regla de Notificación:** Si el libro buscado no tiene ejemplares disponibles (o si no existe en el catálogo), el estudiante puede pulsar un botón para "Notificar al Administrador".

* **Persistencia:** El sistema debe registrar esta petición en una tabla de notificaciones vinculando al estudiante y el texto de su búsqueda para futura adquisición o revisión.

### CUS04: Préstamo/Devolución Autónoma (QR)

* **Acción:** Transacción sin intervención humana.

* **Técnica:** La lectura del hardware QR se simulará pasando un `String` (idEstudiante e idEjemplar) a los controladores.

### CUS05: Reserva de Cubículos

* **Acción:** `estadoCubiculo = 'RESERVADO'`.

* **Regla:** Máximo 2 horas exactas por reserva. 1 reserva activa por día por estudiante.

* **Concurrencia:** Obligatorio el uso de bloqueo pesimista a nivel de fila (`SELECT ... FOR UPDATE`) en la BD para evitar conflictos.
