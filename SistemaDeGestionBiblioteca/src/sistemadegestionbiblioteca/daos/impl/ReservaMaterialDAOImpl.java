package sistemadegestionbiblioteca.daos.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import sistemadegestionbiblioteca.daos.ReservaMaterialDAO;
import sistemadegestionbiblioteca.models.ReservaMaterial;
import sistemadegestionbiblioteca.utils.ConexionBD;

/**
 * Implementación JDBC puro de {@link ReservaMaterialDAO} para CUS03
 * (Reserva de material bibliográfico con plazo máximo de recojo de 1 día / 24 horas: {@code SYSDATE + 1}).
 *
 * @author Grupo 6
 */
public class ReservaMaterialDAOImpl implements ReservaMaterialDAO {

    @Override
    public String crearReserva(String idEstudiante, String idEjemplar) throws SQLException {
        Connection conn = null;
        PreparedStatement psEst = null;
        ResultSet rsEst = null;
        PreparedStatement psEj = null;
        ResultSet rsEj = null;
        PreparedStatement psResExistente = null;
        ResultSet rsResExistente = null;
        PreparedStatement psInsert = null;
        PreparedStatement psUpdEjemplar = null;
        PreparedStatement psFechaLimite = null;
        ResultSet rsFechaLimite = null;

        try {
            conn = ConexionBD.obtenerConexion();
            conn.setAutoCommit(false);

            // Primero expirar reservas vencidas (>24h) para mantener consistencia
            expirarReservasEnTransaccion(conn);

            // 1. Validar que el estudiante exista y esté ACTIVO
            psEst = conn.prepareStatement(
                    "SELECT estado_estudiante, fecha_fin_suspension FROM ESTUDIANTE WHERE id_estudiante = ?");
            psEst.setString(1, idEstudiante);
            rsEst = psEst.executeQuery();
            if (!rsEst.next()) {
                ConexionBD.rollback(conn);
                return "[ERROR - CUS03] El estudiante '" + idEstudiante + "' no existe.";
            }
            if ("SUSPENDIDO".equalsIgnoreCase(rsEst.getString("estado_estudiante"))) {
                ConexionBD.rollback(conn);
                return "[RECHAZADO - CUS03] El estudiante '" + idEstudiante + "' se encuentra SUSPENDIDO y no puede reservar material.";
            }

            // 2. Bloquear y validar ejemplar (RF-03 permite reservar material disponible o prestado)
            psEj = conn.prepareStatement(
                    "SELECT estado_ejemplar, estado_fisico FROM EJEMPLAR WHERE id_ejemplar = ? FOR UPDATE");
            psEj.setString(1, idEjemplar);
            rsEj = psEj.executeQuery();
            if (!rsEj.next()) {
                ConexionBD.rollback(conn);
                return "[ERROR - CUS03] El ejemplar '" + idEjemplar + "' no existe.";
            }

            String estadoEjemplar = rsEj.getString("estado_ejemplar");
            String estadoFisico = rsEj.getString("estado_fisico");

            if ("DANADO".equalsIgnoreCase(estadoFisico) || "MANTENIMIENTO".equalsIgnoreCase(estadoEjemplar)) {
                ConexionBD.rollback(conn);
                return "[RECHAZADO - CUS03] El ejemplar '" + idEjemplar + "' está en mantenimiento o dañado.";
            }

            // 3. Verificar que no tenga ya una reserva PENDIENTE activa sobre el mismo ejemplar
            psResExistente = conn.prepareStatement(
                    "SELECT id_estudiante, fecha_limite_recojo FROM RESERVA_MATERIAL " +
                    "WHERE id_ejemplar = ? AND estado_reserva = 'PENDIENTE' AND fecha_limite_recojo >= SYSDATE");
            psResExistente.setString(1, idEjemplar);
            rsResExistente = psResExistente.executeQuery();
            if (rsResExistente.next()) {
                String estReservante = rsResExistente.getString("id_estudiante");
                ConexionBD.rollback(conn);
                return "[RECHAZADO - CUS03] El ejemplar '" + idEjemplar
                        + "' ya cuenta con una reserva activa del estudiante '" + estReservante + "'.";
            }

            // 4. Insertar en RESERVA_MATERIAL con fecha_reserva = SYSDATE y fecha_limite_recojo = SYSDATE + 1 (24 horas exactas)
            psInsert = conn.prepareStatement(
                    "INSERT INTO RESERVA_MATERIAL (id_estudiante, id_ejemplar, fecha_reserva, fecha_limite_recojo, estado_reserva) " +
                    "VALUES (?, ?, SYSDATE, SYSDATE + 1, 'PENDIENTE')");
            psInsert.setString(1, idEstudiante);
            psInsert.setString(2, idEjemplar);
            psInsert.executeUpdate();

            // 5. Si el ejemplar estaba DISPONIBLE, cambiarlo a 'RESERVADO' para priorizar su entrega al estudiante
            if ("DISPONIBLE".equalsIgnoreCase(estadoEjemplar)) {
                psUpdEjemplar = conn.prepareStatement(
                        "UPDATE EJEMPLAR SET estado_ejemplar = 'RESERVADO' WHERE id_ejemplar = ?");
                psUpdEjemplar.setString(1, idEjemplar);
                psUpdEjemplar.executeUpdate();
            }

            psFechaLimite = conn.prepareStatement(
                    "SELECT TO_CHAR(SYSDATE + 1, 'YYYY-MM-DD HH24:MI:SS') AS limite FROM DUAL");
            rsFechaLimite = psFechaLimite.executeQuery();
            String limiteStr = rsFechaLimite.next() ? rsFechaLimite.getString("limite") : "24 horas (SYSDATE + 1)";

            ConexionBD.commit(conn);
            return "[ÉXITO - CUS03] Reserva de material registrada para el estudiante '" + idEstudiante
                    + "' sobre el ejemplar '" + idEjemplar + "'. Plazo máximo de recojo (24 horas / SYSDATE + 1): " + limiteStr;

        } catch (SQLException ex) {
            ConexionBD.rollback(conn);
            throw ex;
        } finally {
            ConexionBD.cerrar(rsFechaLimite);
            ConexionBD.cerrar(psFechaLimite);
            ConexionBD.cerrar(psUpdEjemplar);
            ConexionBD.cerrar(psInsert);
            ConexionBD.cerrar(rsResExistente);
            ConexionBD.cerrar(psResExistente);
            ConexionBD.cerrar(rsEj);
            ConexionBD.cerrar(psEj);
            ConexionBD.cerrar(rsEst);
            ConexionBD.cerrar(psEst);
            ConexionBD.cerrar(conn);
        }
    }

    @Override
    public int expirarReservasVencidas() throws SQLException {
        Connection conn = null;
        try {
            conn = ConexionBD.obtenerConexion();
            conn.setAutoCommit(false);
            int expiradas = expirarReservasEnTransaccion(conn);
            ConexionBD.commit(conn);
            return expiradas;
        } catch (SQLException ex) {
            ConexionBD.rollback(conn);
            throw ex;
        } finally {
            ConexionBD.cerrar(conn);
        }
    }

    private int expirarReservasEnTransaccion(Connection conn) throws SQLException {
        // Liberar ejemplares que estaban RESERVADOS cuya reserva pendiente ya venció (< SYSDATE)
        try (PreparedStatement psLiberarEj = conn.prepareStatement(
                "UPDATE EJEMPLAR SET estado_ejemplar = 'DISPONIBLE' " +
                "WHERE estado_ejemplar = 'RESERVADO' AND id_ejemplar IN (" +
                "  SELECT id_ejemplar FROM RESERVA_MATERIAL " +
                "  WHERE estado_reserva = 'PENDIENTE' AND fecha_limite_recojo < SYSDATE" +
                ")")) {
            psLiberarEj.executeUpdate();
        }

        // Marcar las reservas vencidas como EXPIRADA
        try (PreparedStatement psExpirar = conn.prepareStatement(
                "UPDATE RESERVA_MATERIAL SET estado_reserva = 'EXPIRADA' " +
                "WHERE estado_reserva = 'PENDIENTE' AND fecha_limite_recojo < SYSDATE")) {
            return psExpirar.executeUpdate();
        }
    }

    @Override
    public ReservaMaterial obtenerReservaPendientePorEjemplar(String idEjemplar) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.obtenerConexion();
            ps = conn.prepareStatement(
                    "SELECT id_reserva_mat, id_estudiante, id_ejemplar, fecha_reserva, fecha_limite_recojo, estado_reserva " +
                    "FROM RESERVA_MATERIAL " +
                    "WHERE id_ejemplar = ? AND estado_reserva = 'PENDIENTE' AND fecha_limite_recojo >= SYSDATE");
            ps.setString(1, idEjemplar);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapearReserva(rs);
            }
            return null;
        } finally {
            ConexionBD.cerrarRecursos(rs, ps, conn);
        }
    }

    @Override
    public List<ReservaMaterial> listarReservasPendientes() throws SQLException {
        List<ReservaMaterial> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.obtenerConexion();
            ps = conn.prepareStatement(
                    "SELECT id_reserva_mat, id_estudiante, id_ejemplar, fecha_reserva, fecha_limite_recojo, estado_reserva " +
                    "FROM RESERVA_MATERIAL WHERE estado_reserva = 'PENDIENTE' ORDER BY fecha_reserva DESC");
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearReserva(rs));
            }
            return lista;
        } finally {
            ConexionBD.cerrarRecursos(rs, ps, conn);
        }
    }

    private ReservaMaterial mapearReserva(ResultSet rs) throws SQLException {
        return new ReservaMaterial(
                rs.getLong("id_reserva_mat"),
                rs.getString("id_estudiante"),
                rs.getString("id_ejemplar"),
                rs.getTimestamp("fecha_reserva"),
                rs.getTimestamp("fecha_limite_recojo"),
                rs.getString("estado_reserva")
        );
    }
}
