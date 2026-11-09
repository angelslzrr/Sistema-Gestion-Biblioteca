-- =============================================================================
-- PROYECTO: Sistema de Gestión de Biblioteca (Grupo 6)
-- MOTOR: Oracle Database (19c / 21c / 23ai - Servicio: FREEPDB1)
-- ESQUEMA / USUARIO: biblioteca_grupo6
-- ARCHIVO: 02_Datos_Prueba.sql
-- DESCRIPCIÓN: Carga de datos iniciales de prueba para validar CUS01 a CUS05.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. ESTUDIANTES (4 ACTIVOS y 1 SUSPENDIDO por 2 días según CUS02)
-- -----------------------------------------------------------------------------
INSERT INTO ESTUDIANTE (id_estudiante, nombres, apellidos, correo, estado_estudiante, fecha_fin_suspension)
VALUES ('EST001', 'Angel Eduardo Daniel', 'Salazar Ruiz', 'angel.salazarr@unmsm.edu.pe', 'ACTIVO', NULL);

INSERT INTO ESTUDIANTE (id_estudiante, nombres, apellidos, correo, estado_estudiante, fecha_fin_suspension)
VALUES ('EST002', 'Miguel Angel', 'Villegas Torres', 'miguel.villegast@unmsm.edu.pe', 'ACTIVO', NULL);

INSERT INTO ESTUDIANTE (id_estudiante, nombres, apellidos, correo, estado_estudiante, fecha_fin_suspension)
VALUES ('EST003', 'Alejandro', 'Sachahuaman Jaramillo', 'alejandro.sachahuamanj@unmsm.edu.pe', 'ACTIVO', NULL);

INSERT INTO ESTUDIANTE (id_estudiante, nombres, apellidos, correo, estado_estudiante, fecha_fin_suspension)
VALUES ('EST004', 'José David', 'Calla Chavez', 'jose.callac@unmsm.edu.pe', 'ACTIVO', NULL);

INSERT INTO ESTUDIANTE (id_estudiante, nombres, apellidos, correo, estado_estudiante, fecha_fin_suspension)
VALUES ('EST005', 'Italo Josué', 'Hurtado Flores', 'italo.hurtadof@unmsm.edu.pe', 'SUSPENDIDO', SYSDATE + 2);

INSERT INTO ESTUDIANTE (id_estudiante, nombres, apellidos, correo, estado_estudiante, fecha_fin_suspension)
VALUES ('EST005', 'Christopher', 'Saccaco Oscco', 'christopher.saccacoo@unmsm.edu.pe', 'SUSPENDIDO', SYSDATE + 2);

-- -----------------------------------------------------------------------------
-- 2. LIBROS CLÁSICOS DE INGENIERÍA DE SOFTWARE Y BASES DE DATOS
-- -----------------------------------------------------------------------------
INSERT INTO LIBRO (id_libro, isbn, titulo, autor, editorial, anio_publicacion)
VALUES (1, '978-0201707106', 'The Rational Unified Process: An Introduction', 'Philippe Kruchten', 'Addison-Wesley', 2003);

INSERT INTO LIBRO (id_libro, isbn, titulo, autor, editorial, anio_publicacion)
VALUES (2, '978-0132350884', 'Clean Code: A Handbook of Agile Software Craftsmanship', 'Robert C. Martin', 'Prentice Hall', 2008);

INSERT INTO LIBRO (id_libro, isbn, titulo, autor, editorial, anio_publicacion)
VALUES (3, '978-0133970777', 'Fundamentals of Database Systems', 'Ramez Elmasri, Shamkant B. Navathe', 'Pearson', 2015);

INSERT INTO LIBRO (id_libro, isbn, titulo, autor, editorial, anio_publicacion)
VALUES (4, '978-0201633610', 'Design Patterns: Elements of Reusable Object-Oriented Software', 'Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides', 'Addison-Wesley', 1994);

INSERT INTO LIBRO (id_libro, isbn, titulo, autor, editorial, anio_publicacion)
VALUES (5, '978-0133943030', 'Software Engineering', 'Ian Sommerville', 'Pearson', 2015);

-- -----------------------------------------------------------------------------
-- 3. EJEMPLARES FÍSICOS (EJM-001 a EJM-008)
-- Estados sincronizados con los préstamos y reservas de prueba
-- -----------------------------------------------------------------------------
-- Libro 1: RUP Kruchten (2 ejemplares: 1 disponible, 1 prestado)
INSERT INTO EJEMPLAR (id_ejemplar, id_libro, codigo_inventario, estado_ejemplar, estado_fisico)
VALUES ('EJM-001', 1, 'INV-BIB-2026-001', 'DISPONIBLE', 'BUENO');

INSERT INTO EJEMPLAR (id_ejemplar, id_libro, codigo_inventario, estado_ejemplar, estado_fisico)
VALUES ('EJM-002', 1, 'INV-BIB-2026-002', 'PRESTADO', 'BUENO');

-- Libro 2: Clean Code (2 ejemplares: 1 disponible, 1 prestado con retraso)
INSERT INTO EJEMPLAR (id_ejemplar, id_libro, codigo_inventario, estado_ejemplar, estado_fisico)
VALUES ('EJM-003', 2, 'INV-BIB-2026-003', 'DISPONIBLE', 'BUENO');

INSERT INTO EJEMPLAR (id_ejemplar, id_libro, codigo_inventario, estado_ejemplar, estado_fisico)
VALUES ('EJM-004', 2, 'INV-BIB-2026-004', 'PRESTADO', 'BUENO');

-- Libro 3: Fundamentals of Database Systems (2 ejemplares: 1 disponible, 1 reservado)
INSERT INTO EJEMPLAR (id_ejemplar, id_libro, codigo_inventario, estado_ejemplar, estado_fisico)
VALUES ('EJM-005', 3, 'INV-BIB-2026-005', 'DISPONIBLE', 'BUENO');

INSERT INTO EJEMPLAR (id_ejemplar, id_libro, codigo_inventario, estado_ejemplar, estado_fisico)
VALUES ('EJM-006', 3, 'INV-BIB-2026-006', 'RESERVADO', 'BUENO');

-- Libro 4: Design Patterns GoF (1 ejemplar disponible)
INSERT INTO EJEMPLAR (id_ejemplar, id_libro, codigo_inventario, estado_ejemplar, estado_fisico)
VALUES ('EJM-007', 4, 'INV-BIB-2026-007', 'DISPONIBLE', 'BUENO');

-- Libro 5: Software Engineering Sommerville (1 ejemplar disponible)
INSERT INTO EJEMPLAR (id_ejemplar, id_libro, codigo_inventario, estado_ejemplar, estado_fisico)
VALUES ('EJM-008', 5, 'INV-BIB-2026-008', 'DISPONIBLE', 'BUENO');

-- -----------------------------------------------------------------------------
-- 4. CUBÍCULOS DE ESTUDIO (CUB-01 a CUB-05) - CUS05
-- -----------------------------------------------------------------------------
INSERT INTO CUBICULO (id_cubiculo, nombre_codigo, capacidad, estado_cubiculo)
VALUES ('CUB-01', 'Cubículo A-101 (Piso 1)', 4, 'DISPONIBLE');

INSERT INTO CUBICULO (id_cubiculo, nombre_codigo, capacidad, estado_cubiculo)
VALUES ('CUB-02', 'Cubículo A-102 (Piso 1)', 6, 'RESERVADO');

INSERT INTO CUBICULO (id_cubiculo, nombre_codigo, capacidad, estado_cubiculo)
VALUES ('CUB-03', 'Cubículo B-201 (Piso 2)', 4, 'DISPONIBLE');

INSERT INTO CUBICULO (id_cubiculo, nombre_codigo, capacidad, estado_cubiculo)
VALUES ('CUB-04', 'Cubículo B-202 (Piso 2)', 8, 'DISPONIBLE');

INSERT INTO CUBICULO (id_cubiculo, nombre_codigo, capacidad, estado_cubiculo)
VALUES ('CUB-05', 'Cubículo C-301 (Sala Silenciosa)', 2, 'MANTENIMIENTO');

-- -----------------------------------------------------------------------------
-- 5. PRÉSTAMOS DE EJEMPLO (CUS01, CUS02, CUS04)
-- -----------------------------------------------------------------------------
-- Caso 1: Préstamo ACTIVO en plazo vigente por canal AUTOSERVICIO_QR (EST001 -> EJM-002)
INSERT INTO PRESTAMO (
    id_estudiante, id_ejemplar, fecha_prestamo, fecha_devolucion_esperada,
    fecha_devolucion_real, canal, devuelto_danado, estado_prestamo
) VALUES (
    'EST001', 'EJM-002', SYSDATE - 2, SYSDATE + 5,
    NULL, 'AUTOSERVICIO_QR', 'N', 'ACTIVO'
);

-- Caso 2: Préstamo ACTIVO pero con fecha esperada ya vencida (hace 1 día) para probar
--         de inmediato en CUS02 la devolución tardía y sanción de 2 días (EST002 -> EJM-004)
INSERT INTO PRESTAMO (
    id_estudiante, id_ejemplar, fecha_prestamo, fecha_devolucion_esperada,
    fecha_devolucion_real, canal, devuelto_danado, estado_prestamo
) VALUES (
    'EST002', 'EJM-004', SYSDATE - 8, SYSDATE - 1,
    NULL, 'PRESENCIAL', 'N', 'ACTIVO'
);

-- Caso 3: Préstamo histórico DEVUELTO con retraso que originó la suspensión de EST005
INSERT INTO PRESTAMO (
    id_estudiante, id_ejemplar, fecha_prestamo, fecha_devolucion_esperada,
    fecha_devolucion_real, canal, devuelto_danado, estado_prestamo
) VALUES (
    'EST005', 'EJM-001', SYSDATE - 9, SYSDATE - 2,
    SYSDATE, 'PRESENCIAL', 'N', 'DEVUELTO'
);

-- -----------------------------------------------------------------------------
-- 6. RESERVAS DE MATERIAL BIBLIOGRÁFICO (CUS03)
-- -----------------------------------------------------------------------------
-- Reserva PENDIENTE de recojo dentro de las 24 horas (EST003 -> EJM-006)
INSERT INTO RESERVA_MATERIAL (
    id_estudiante, id_ejemplar, fecha_reserva, fecha_limite_recojo, estado_reserva
) VALUES (
    'EST003', 'EJM-006', SYSDATE - (2/24), SYSDATE + (22/24), 'PENDIENTE'
);

-- -----------------------------------------------------------------------------
-- 7. RESERVAS DE CUBÍCULO (CUS05)
-- -----------------------------------------------------------------------------
-- Reserva ACTIVA de exactamente 2 horas sobre CUB-02 para EST004
INSERT INTO RESERVA_CUBICULO (
    id_cubiculo, id_estudiante, fecha_hora_inicio, fecha_hora_fin, estado_reserva
) VALUES (
    'CUB-02', 'EST004', SYSDATE, SYSDATE + (2/24), 'ACTIVA'
);

COMMIT;
