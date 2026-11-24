package sistemadegestionbiblioteca.daos.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import sistemadegestionbiblioteca.daos.EjemplarDAO;
import sistemadegestionbiblioteca.daos.EstudianteDAO;
import sistemadegestionbiblioteca.daos.PrestamoDAO;
import sistemadegestionbiblioteca.models.Prestamo;
import sistemadegestionbiblioteca.utils.ConexionBD;

/**
 * Implementación JDBC puro de {@link PrestamoDAO} para CUS01 (Préstamo con SYSDATE + 7),
 * CUS02 (Devolución con sanción SYSDATE + 2) y CUS04 (Autoservicio QR).
 *
 * @author Grupo 6
 */
public class PrestamoDAOImpl implements PrestamoDAO {

    private static final int LIMITE_MAXIMO_PRESTAMOS = 3;

    private final EstudianteDAO estudianteDAO;
    private final EjemplarDAO ejemplarDAO;

    public PrestamoDAOImpl() {
        this.estudianteDAO = new EstudianteDAOImpl();
        this.ejemplarDAO = new EjemplarDAOImpl();
    }

    public PrestamoDAOImpl(EstudianteDAO estudianteDAO, EjemplarDAO ejemplarDAO) {
        this.estudianteDAO = estudianteDAO;
        this.ejemplarDAO = ejemplarDAO;
    }

    @Override
    public String registrarPrestamo(String idEstudiante, String idEjemplar, String canal) throws SQLException {
        Connection conn = null;
        PreparedStatement psEst = null;
        ResultSet rsEst = null;
        PreparedStatement psCont = null;
        ResultSet rsCont = null;
        PreparedStatement psEj = null;
        ResultSet rsEj = null;
        PreparedStatement psRes = null;
        ResultSet rsRes = null;
        PreparedStatement psUpdRes = null;
        PreparedStatement psInsPrestamo = null;
        PreparedStatement psFechas = null;
        ResultSet rsFechas = null;

        try {
            conn = ConexionBD.obtenerConexion();
            conn.setAutoCommit(false);

            // 1. Verificar estudiante y levantar suspensión vencida si ya cumplió sus 2 días
            try (PreparedStatement psLevantar = conn.prepareStatement(
                    "UPDATE ESTUDIANTE SET estado_estudiante = 'ACTIVO', fecha_fin_suspension = NULL " +
                    "WHERE id_estudiante = ? AND estado_estudiante = 'SUSPENDIDO' AND fecha_fin_suspension <= SYSDATE")) {
                psLevantar.setString(1, idEstudiante);
                psLevantar.executeUpdate();
            }

            psEst = conn.prepareStatement(
                    "SELECT nombres, apellidos, estado_estudiante, fecha_fin_suspension " +
                    "FROM ESTUDIANTE WHERE id_estudiante = ? FOR UPDATE");
            psEst.setString(1, idEstudiante);
            rsEst = psEst.executeQuery();

            if (!rsEst.next()) {
                ConexionBD.rollback(conn);
                return "[ERROR - CUS01] El estudiante con ID '" + idEstudiante + "' no existe.";
            }

            String estadoEstudiante = rsEst.getString("estado_estudiante");
            Timestamp fechaFinSusp = rsEst.getTimestamp("fecha_fin_suspension");
            if ("SUSPENDIDO".equalsIgnoreCase(estadoEstudiante)) {
                ConexionBD.rollback(conn);
                return "[RECHAZADO - CUS01/CUS02] El estudiante '" + idEstudiante + "' se encuentra SUSPENDIDO hasta: "
                        + (fechaFinSusp != null ? fechaFinSusp.toString() : "2 días desde la sanción") + ".";
            }

            // 2. Validar regla de negocio CUS01: Máximo 3 libros prestados simultáneamente
            psCont = conn.prepareStatement(
                    "SELECT COUNT(*) FROM PRESTAMO WHERE id_estudiante = ? AND estado_prestamo = 'ACTIVO'");
            psCont.setString(1, idEstudiante);
            rsCont = psCont.executeQuery();
            int prestamosActivos = rsCont.next() ? rsCont.getInt(1) : 0;

            if (prestamosActivos >= LIMITE_MAXIMO_PRESTAMOS) {
                ConexionBD.rollback(conn);
                return "[RECHAZADO - CUS01] Límite alcanzado: El estudiante '" + idEstudiante
                        + "' ya posee " + prestamosActivos + " préstamos activos (máximo permitido: 3).";
            }

            // 3. Bloquear y verificar ejemplar
            psEj = conn.prepareStatement(
                    "SELECT estado_ejemplar, estado_fisico FROM EJEMPLAR WHERE id_ejemplar = ? FOR UPDATE");
            psEj.setString(1, idEjemplar);
            rsEj = psEj.executeQuery();

            if (!rsEj.next()) {
                ConexionBD.rollback(conn);
                return "[ERROR - CUS01] El ejemplar con ID '" + idEjemplar + "' no existe en el inventario.";
            }

            String estadoEjemplar = rsEj.getString("estado_ejemplar");
            String estadoFisico = rsEj.getString("estado_fisico");

            if ("DANADO".equalsIgnoreCase(estadoFisico) || "MANTENIMIENTO".equalsIgnoreCase(estadoEjemplar)) {
                ConexionBD.rollback(conn);
                return "[RECHAZADO - CUS01] El ejemplar '" + idEjemplar + "' no está apto para préstamo (estado: "
                        + estadoEjemplar + ", físico: " + estadoFisico + ").";
            }

            if ("PRESTADO".equalsIgnoreCase(estadoEjemplar)) {
                ConexionBD.rollback(conn);
                return "[RECHAZADO - CUS01] El ejemplar '" + idEjemplar + "' ya se encuentra PRESTADO actualmente.";
            }

            // 4. Verificar si existe reserva pendiente vigente (CUS03: 24h) sobre este ejemplar
            psRes = conn.prepareStatement(
                    "SELECT id_reserva_mat, id_estudiante, fecha_limite_recojo " +
                    "FROM RESERVA_MATERIAL " +
                    "WHERE id_ejemplar = ? AND estado_reserva = 'PENDIENTE' AND fecha_limite_recojo >= SYSDATE " +
                    "ORDER BY fecha_reserva ASC FOR UPDATE");
            psRes.setString(1, idEjemplar);
            rsRes = psRes.executeQuery();

            if (rsRes.next()) {
                long idReservaMat = rsRes.getLong("id_reserva_mat");
                String idEstReserva = rsRes.getString("id_estudiante");
                if (!idEstudiante.equalsIgnoreCase(idEstReserva)) {
                    ConexionBD.rollback(conn);
                    return "[RECHAZADO - CUS03] El ejemplar '" + idEjemplar
                            + "' está RESERVADO prioritariamente para el estudiante '" + idEstReserva + "'.";
                }
                // Si la reserva pertenece al mismo estudiante, se marca como COMPLETADA
                psUpdRes = conn.prepareStatement(
                        "UPDATE RESERVA_MATERIAL SET estado_reserva = 'COMPLETADA' WHERE id_reserva_mat = ?");
                psUpdRes.setLong(1, idReservaMat);
                psUpdRes.executeUpdate();
            } else if ("RESERVADO".equalsIgnoreCase(estadoEjemplar)) {
                // Tenía estado RESERVADO pero la reserva ya expiró (>24h); expiramos las vencidas y continuamos
                try (PreparedStatement psExp = conn.prepareStatement(
                        "UPDATE RESERVA_MATERIAL SET estado_reserva = 'EXPIRADA' " +
                        "WHERE id_ejemplar = ? AND estado_reserva = 'PENDIENTE' AND fecha_limite_recojo < SYSDATE")) {
                    psExp.setString(1, idEjemplar);
                    psExp.executeUpdate();
                }
            }

            // 5. Insertar en PRESTAMO asignando explícitamente SYSDATE y SYSDATE + 7 (CUS01)
            String canalNormalizado = ("AUTOSERVICIO_QR".equalsIgnoreCase(canal)) ? "AUTOSERVICIO_QR" : "PRESENCIAL";
            psInsPrestamo = conn.prepareStatement(
                    "INSERT INTO PRESTAMO (id_estudiante, id_ejemplar, fecha_prestamo, fecha_devolucion_esperada, canal, devuelto_danado, estado_prestamo) " +
                    "VALUES (?, ?, SYSDATE, SYSDATE + 7, ?, 'N', 'ACTIVO')");
            psInsPrestamo.setString(1, idEstudiante);
            psInsPrestamo.setString(2, idEjemplar);
            psInsPrestamo.setString(3, canalNormalizado);
            psInsPrestamo.executeUpdate();

            // 6. Actualizar estado del ejemplar a 'PRESTADO' (CUS01)
            ejemplarDAO.actualizarEstado(conn, idEjemplar, "PRESTADO", estadoFisico);

            // Consultar fecha_devolucion_esperada generada en Oracle (SYSDATE + 7)
            psFechas = conn.prepareStatement(
                    "SELECT TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') AS f_ini, " +
                    "TO_CHAR(SYSDATE + 7, 'YYYY-MM-DD HH24:MI:SS') AS f_fin FROM DUAL");
            rsFechas = psFechas.executeQuery();
            String fechaDevolucionStr = rsFechas.next() ? rsFechas.getString("f_fin") : "SYSDATE + 7 días";

            ConexionBD.commit(conn);
            return "[ÉXITO - CUS01] Préstamo registrado por canal " + canalNormalizado
                    + ". Ejemplar '" + idEjemplar + "' -> 'PRESTADO'. Estudiante: '" + idEstudiante
                    + "' (" + (prestamosActivos + 1) + "/3 libros). Fecha devolución (SYSDATE + 7): " + fechaDevolucionStr;

        } catch (SQLException ex) {
            ConexionBD.rollback(conn);
            throw ex;
        } finally {
            ConexionBD.cerrar(rsFechas);
            ConexionBD.cerrar(psFechas);
            ConexionBD.cerrar(psInsPrestamo);
            ConexionBD.cerrar(psUpdRes);
            ConexionBD.cerrar(rsRes);
            ConexionBD.cerrar(psRes);
            ConexionBD.cerrar(rsEj);
            ConexionBD.cerrar(psEj);
            ConexionBD.cerrar(rsCont);
            ConexionBD.cerrar(psCont);
            ConexionBD.cerrar(rsEst);
            ConexionBD.cerrar(psEst);
            ConexionBD.cerrar(conn);
        }
    }

    @Override
    public String registrarDevolucion(String idEjemplar, boolean devueltoDanado, String canal) throws SQLException {
        Connection conn = null;
        PreparedStatement psPrestamo = null;
        ResultSet rsPrestamo = null;
        PreparedStatement psUpdPrestamo = null;
        PreparedStatement psCheckReserva = null;
        ResultSet rsCheckReserva = null;

        try {
            conn = ConexionBD.obtenerConexion();
            conn.setAutoCommit(false);

            // 1. Obtener el préstamo ACTIVO del ejemplar y verificar en Oracle si SYSDATE > fecha_devolucion_esperada
            psPrestamo = conn.prepareStatement(
                    "SELECT id_prestamo, id_estudiante, fecha_prestamo, fecha_devolucion_esperada, " +
                    "CASE WHEN SYSDATE > fecha_devolucion_esperada THEN 1 ELSE 0 END AS es_tarde " +
                    "FROM PRESTAMO " +
                    "WHERE id_ejemplar = ? AND estado_prestamo = 'ACTIVO' " +
                    "FOR UPDATE");
            psPrestamo.setString(1, idEjemplar);
            rsPrestamo = psPrestamo.executeQuery();

            if (!rsPrestamo.next()) {
                ConexionBD.rollback(conn);
                return "[ERROR - CUS02] No existe ningún préstamo ACTIVO para el ejemplar '" + idEjemplar + "'.";
            }

            long idPrestamo = rsPrestamo.getLong("id_prestamo");
            String idEstudiante = rsPrestamo.getString("id_estudiante");
            boolean entregaTardia = rsPrestamo.getInt("es_tarde") == 1;

            // 2. Actualizar registro en PRESTAMO a 'DEVUELTO' con fecha_devolucion_real = SYSDATE
            psUpdPrestamo = conn.prepareStatement(
                    "UPDATE PRESTAMO " +
                    "SET fecha_devolucion_real = SYSDATE, devuelto_danado = ?, estado_prestamo = 'DEVUELTO' " +
                    "WHERE id_prestamo = ?");
            psUpdPrestamo.setString(1, devueltoDanado ? "S" : "N");
            psUpdPrestamo.setLong(2, idPrestamo);
            psUpdPrestamo.executeUpdate();

            // 3. Verificar si existe una reserva PENDIENTE vigente (CUS03) esperando este ejemplar
            String nuevoEstadoEjemplar = "DISPONIBLE";
            if (!devueltoDanado) {
                psCheckReserva = conn.prepareStatement(
                        "SELECT id_reserva_mat FROM RESERVA_MATERIAL " +
                        "WHERE id_ejemplar = ? AND estado_reserva = 'PENDIENTE' AND fecha_limite_recojo >= SYSDATE");
                psCheckReserva.setString(1, idEjemplar);
                rsCheckReserva = psCheckReserva.executeQuery();
                if (rsCheckReserva.next()) {
                    // Regla CUS02 pide estadoEjemplar = 'DISPONIBLE'; si no hay daño queda DISPONIBLE
                    nuevoEstadoEjemplar = "DISPONIBLE";
                }
            }

            // 4. Actualizar EJEMPLAR a estado_ejemplar = 'DISPONIBLE' (regla explícita CUS02) y estado_fisico ('BUENO' o 'DANADO')
            String nuevoEstadoFisico = devueltoDanado ? "DANADO" : "BUENO";
            ejemplarDAO.actualizarEstado(conn, idEjemplar, nuevoEstadoEjemplar, nuevoEstadoFisico);

            // 5. Regla estricta CUS02: Si se devuelve tarde o dañado -> estadoEstudiante = 'SUSPENDIDO' por exactamente 2 días (SYSDATE + 2), sin multa monetaria
            String detalleSancion;
            if (entregaTardia || devueltoDanado) {
                estudianteDAO.suspenderEstudiantePorDosDias(conn, idEstudiante);
                String motivo = (entregaTardia && devueltoDanado) ? "entrega tardía y daño físico"
                        : (entregaTardia ? "entrega tardía" : "daño físico en el ejemplar");
                detalleSancion = " [SANCIÓN CUS02 APLICADA]: Estudiante '" + idEstudiante
                        + "' pasa a estado 'SUSPENDIDO' por exactamente 2 días (SYSDATE + 2) debido a "
                        + motivo + " (sin multa monetaria).";
            } else {
                detalleSancion = " Devolución a tiempo y en buen estado. Sin sanciones.";
            }

            ConexionBD.commit(conn);
            return "[ÉXITO - CUS02] Devolución procesada (" + canal + "). Ejemplar '" + idEjemplar
                    + "' actualizado a '" + nuevoEstadoEjemplar + "' (Físico: " + nuevoEstadoFisico + ")."
                    + detalleSancion;

        } catch (SQLException ex) {
            ConexionBD.rollback(conn);
            throw ex;
        } finally {
            ConexionBD.cerrar(rsCheckReserva);
            ConexionBD.cerrar(psCheckReserva);
            ConexionBD.cerrar(psUpdPrestamo);
            ConexionBD.cerrar(rsPrestamo);
            ConexionBD.cerrar(psPrestamo);
            ConexionBD.cerrar(conn);
        }
    }

    @Override
    public Prestamo obtenerPrestamoActivoPorEjemplar(String idEjemplar) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.obtenerConexion();
            ps = conn.prepareStatement(
                    "SELECT id_prestamo, id_estudiante, id_ejemplar, fecha_prestamo, " +
                    "fecha_devolucion_esperada, fecha_devolucion_real, canal, devuelto_danado, estado_prestamo " +
                    "FROM PRESTAMO WHERE id_ejemplar = ? AND estado_prestamo = 'ACTIVO'");
            ps.setString(1, idEjemplar);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapearPrestamo(rs);
            }
            return null;
        } finally {
            ConexionBD.cerrarRecursos(rs, ps, conn);
        }
    }

    @Override
    public List<Prestamo> listarPrestamosActivos() throws SQLException {
        List<Prestamo> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.obtenerConexion();
            ps = conn.prepareStatement(
                    "SELECT id_prestamo, id_estudiante, id_ejemplar, fecha_prestamo, " +
                    "fecha_devolucion_esperada, fecha_devolucion_real, canal, devuelto_danado, estado_prestamo " +
                    "FROM PRESTAMO WHERE estado_prestamo = 'ACTIVO' ORDER BY id_prestamo DESC");
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearPrestamo(rs));
            }
            return lista;
        } finally {
            ConexionBD.cerrarRecursos(rs, ps, conn);
        }
    }

    private Prestamo mapearPrestamo(ResultSet rs) throws SQLException {
        return new Prestamo(
                rs.getLong("id_prestamo"),
                rs.getString("id_estudiante"),
                rs.getString("id_ejemplar"),
                rs.getTimestamp("fecha_prestamo"),
                rs.getTimestamp("fecha_devolucion_esperada"),
                rs.getTimestamp("fecha_devolucion_real"),
                rs.getString("canal"),
                rs.getString("devuelto_danado"),
                rs.getString("estado_prestamo")
        );
    }
}
