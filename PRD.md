\# Product Requirements Document (PRD) - Sistema de Gestión de Biblioteca



\## 1. Actores del Sistema

\* \*\*Estudiante / Usuario:\*\* Solicita préstamos, realiza devoluciones, reserva material bibliográfico y gestiona la reserva de cubículos.

\* \*\*Bibliotecario / Personal de Atención:\*\* Gestiona préstamos/devoluciones presenciales, supervisa reservas e incidencias.

\* \*\*Administrador del Sistema:\*\* Configura parámetros, aforos, catálogo y permisos.

\* \*\*Sistema de Autoservicio (QR/RFID):\*\* Dispositivo que procesa préstamos y devoluciones autónomas.



\## 2. Requisitos Funcionales Principales

\* \*\*RF-01 (CUS01):\*\* Permitir registro y préstamos de ejemplares.

\* \*\*RF-02 (CUS02):\*\* Gestionar devoluciones y actualizar stock.

\* \*\*RF-03 (CUS03):\*\* Permitir reserva de material disponible o prestado.

\* \*\*RF-04 (CUS04):\*\* Procesar transacciones mediante autoservicio (simulado por String).

\* \*\*RF-05 (CUS05):\*\* Monitorear y gestionar reserva de cubículos en tiempo real (< 2 seg).



\## 3. Especificaciones de Casos de Uso (Reglas de Negocio Estrictas)



\### CUS01: Registrar préstamo de ejemplar

\* \*\*Acción:\*\* `estadoEjemplar = 'PRESTADO'`. 

\* \*\*Regla:\*\* Máximo 3 libros prestados simultáneamente.

\* \*\*Plazo:\*\* Asignación automática de 7 días (`fecha\_devolucion = sysdate + 7`).



\### CUS02: Devolver ejemplar

\* \*\*Acción:\*\* `estadoEjemplar = 'DISPONIBLE'`.

\* \*\*Sanciones:\*\* Si se devuelve tarde o dañado, `estadoEstudiante = 'SUSPENDIDO'` por exactamente 2 días (sin multa monetaria).



\### CUS03: Reservar material bibliográfico

\* \*\*Acción:\*\* Priorizar entrega de libro.

\* \*\*Regla:\*\* El estudiante tiene un plazo máximo de 1 día (24 horas) para recoger el material desde que se procesa la reserva.



\### CUS04: Préstamo/Devolución Autónoma (QR)

\* \*\*Acción:\*\* Transacción sin intervención humana.

\* \*\*Técnica:\*\* La lectura del hardware QR se simulará pasando un `String` (idEstudiante e idEjemplar) a los controladores.



\### CUS05: Reserva de Cubículos

\* \*\*Acción:\*\* `estadoCubiculo = 'RESERVADO'`.

\* \*\*Regla:\*\* Máximo 2 horas exactas por reserva. 1 reserva activa por día por estudiante.

\* \*\*Concurrencia:\*\* Obligatorio el uso de bloqueo pesimista a nivel de fila (`SELECT ... FOR UPDATE`) en la BD para evitar conflictos.

