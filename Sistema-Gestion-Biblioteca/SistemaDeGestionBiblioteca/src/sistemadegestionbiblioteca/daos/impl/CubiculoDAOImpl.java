package sistemadegestionbiblioteca.daos.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import sistemadegestionbiblioteca.daos.CubiculoDAO;
import sistemadegestionbiblioteca.models.Cubiculo;
import sistemadegestionbiblioteca.models.ReservaCubiculo;
import sistemadegestionbiblioteca.utils.ConexionBD;

/**
 * Implementación JDBC puro de {@link CubiculoDAO} para CUS05 (Reserva de Cubículos en tiempo real).
 * Implementa bloqueo pesimista a nivel de fila ({@code SELECT ... FOR UPDATE}) en Oracle
 * con transacciones manuales para garantizar consistencia ante solicitudes concurrentes.
 *
 * @author Grupo 6
 */
public class CubiculoDAOImpl implements CubiculoDAO {

    /**
     * Sentencia obligatoria de bloqueo pesimista a nivel de fila en Oracle (CUS05).
     */
    private static final String SQL_SELECT_CUBICULO_FOR_UPDATE =
            "SELECT id_cubiculo, nombre_codigo, capacidad, estado_cubiculo " +
            "FROM CUBICULO WHERE id_cubiculo = ? FOR UPDATE";

    @Override
    public String reservarCubiculoConBloqueoPesimista(String idCubiculo, String idEstudiante, int duracionHoras) throws SQLException {
        if (duracionHoras < 1 || duracionHoras > 2) {
            return "[RECHAZADO] La duración de la reserva debe ser de 1 o máximo 2 horas exactas.";
        }

        Connection conn = null;
        PreparedStatement psEst = null;
        ResultSet rsEst = null;
        PreparedStatement psContDia = null;
        ResultSet rsContDia = null;
        PreparedStatement psLockCubiculo = null;
        ResultSet rsLockCubiculo = null;
        PreparedStatement psUpdCubiculo = null;
        PreparedStatement psInsReserva = null;
        PreparedStatement psFechas = null;
        ResultSet rsFechas = null;

        try {
            conn = ConexionBD.obtenerConexion();
            // Iniciar transacción manual para sostener el bloqueo SELECT ... FOR UPDATE
            conn.setAutoCommit(false);

            // Liberar automáticamente cubículos cuyas reservas ya cumplieron su tiempo (fecha_hora_fin <= SYSDATE)
            liberarReservasVencidasEnTransaccion(conn);

            // 1. Validar que el estudiante exista y esté ACTIVO (levantando sanción si ya cumplió sus 2 días)
            try (PreparedStatement psLevantar = conn.prepareStatement(
                    "UPDATE ESTUDIANTE SET estado_estudiante = 'ACTIVO', fecha_fin_suspension = NULL " +
                    "WHERE id_estudiante = ? AND estado_estudiante = 'SUSPENDIDO' AND fecha_fin_suspension <= SYSDATE")) {
                psLevantar.setString(1, idEstudiante);
                psLevantar.executeUpdate();
            }

            psEst = conn.prepareStatement(
                    "SELECT estado_estudiante, fecha_fin_suspension FROM ESTUDIANTE WHERE id_estudiante = ?");
            psEst.setString(1, idEstudiante);
            rsEst = psEst.executeQuery();
            if (!rsEst.next()) {
                ConexionBD.rollback(conn);
                return "[ERROR] El estudiante '" + idEstudiante + "' no existe.";
            }
            if ("SUSPENDIDO".equalsIgnoreCase(rsEst.getString("estado_estudiante"))) {
                ConexionBD.rollback(conn);
                return "[RECHAZADO] El estudiante '" + idEstudiante + "' está SUSPENDIDO y no puede reservar cubículos.";
            }

            // 2. Regla estricta CUS05: Máximo 1 reserva activa por día por estudiante
            psContDia = conn.prepareStatement(
                    "SELECT COUNT(*) FROM RESERVA_CUBICULO " +
                    "WHERE id_estudiante = ? AND estado_reserva = 'ACTIVA' " +
                    "AND TRUNC(fecha_hora_inicio) = TRUNC(SYSDATE)");
            psContDia.setString(1, idEstudiante);
            rsContDia = psContDia.executeQuery();
            int reservasHoy = rsContDia.next() ? rsContDia.getInt(1) : 0;
            if (reservasHoy >= 1) {
                ConexionBD.rollback(conn);
                return "[RECHAZADO] El estudiante '" + idEstudiante
                        + "' ya posee 1 reserva activa de cubículo el día de hoy (límite máximo: 1 por día).";
            }

            // 3. BLOQUEO PESIMISTA OBLIGATORIO A NIVEL DE FILA (SELECT ... FOR UPDATE)
            // Si dos hilos intentan reservar el mismo cubículo simultáneamente, Oracle encola
            // la segunda transacción hasta que la primera confirme (COMMIT) o revierta (ROLLBACK).
            psLockCubiculo = conn.prepareStatement(SQL_SELECT_CUBICULO_FOR_UPDATE);
            psLockCubiculo.setString(1, idCubiculo);
            rsLockCubiculo = psLockCubiculo.executeQuery();

            if (!rsLockCubiculo.next()) {
                ConexionBD.rollback(conn);
                return "[ERROR] El cubículo '" + idCubiculo + "' no existe.";
            }

            String nombreCodigo = rsLockCubiculo.getString("nombre_codigo");
            String estadoCubiculo = rsLockCubiculo.getString("estado_cubiculo");

            if (!"DISPONIBLE".equalsIgnoreCase(estadoCubiculo)) {
                ConexionBD.rollback(conn);
                return "[RECHAZADO] El cubículo '" + idCubiculo + "' (" + nombreCodigo
                        + ") no está disponible (estado actual: " + estadoCubiculo + ").";
            }

            // 4. Actualizar estado del cubículo a 'RESERVADO' (CUS05)
            psUpdCubiculo = conn.prepareStatement(
                    "UPDATE CUBICULO SET estado_cubiculo = 'RESERVADO' WHERE id_cubiculo = ?");
            psUpdCubiculo.setString(1, idCubiculo);
            psUpdCubiculo.executeUpdate();

            // 5. Insertar en RESERVA_CUBICULO con fecha_hora_inicio = SYSDATE y fecha_hora_fin = SYSDATE + (? / 24)
            psInsReserva = conn.prepareStatement(
                    "INSERT INTO RESERVA_CUBICULO (id_cubiculo, id_estudiante, fecha_hora_inicio, fecha_hora_fin, estado_reserva) " +
                    "VALUES (?, ?, SYSDATE, SYSDATE + (? / 24), 'ACTIVA')");
            psInsReserva.setString(1, idCubiculo);
            psInsReserva.setString(2, idEstudiante);
            psInsReserva.setInt(3, duracionHoras);
            psInsReserva.executeUpdate();

            psFechas = conn.prepareStatement(
                    "SELECT TO_CHAR(SYSDATE, 'HH24:MI') AS h_ini, " +
                    "TO_CHAR(SYSDATE + (? / 24), 'HH24:MI') AS h_fin FROM DUAL");
            psFechas.setInt(1, duracionHoras);
            rsFechas = psFechas.executeQuery();
            String horario = rsFechas.next()
                    ? (rsFechas.getString("h_ini") + " a " + rsFechas.getString("h_fin"))
                    : (duracionHoras + " hora(s)");

            // 6. Confirmar transacción y liberar el bloqueo FOR UPDATE en Oracle
            ConexionBD.commit(conn);
            return "Cubículo '" + idCubiculo + "' (" + nombreCodigo + ") reservado con éxito.\n"
                    + "Estudiante: '" + idEstudiante + "'\n"
                    + "Duración asignada: " + duracionHoras + " hora(s) (" + horario + ").";

        } catch (SQLException ex) {
            ConexionBD.rollback(conn);
            throw ex;
        } finally {
            ConexionBD.cerrar(rsFechas);
            ConexionBD.cerrar(psFechas);
            ConexionBD.cerrar(psInsReserva);
            ConexionBD.cerrar(psUpdCubiculo);
            ConexionBD.cerrar(rsLockCubiculo);
            ConexionBD.cerrar(psLockCubiculo);
            ConexionBD.cerrar(rsContDia);
            ConexionBD.cerrar(psContDia);
            ConexionBD.cerrar(rsEst);
            ConexionBD.cerrar(psEst);
            ConexionBD.cerrar(conn);
        }
    }

    @Override
    public String liberarCubiculo(String idCubiculo) throws SQLException {
        Connection conn = null;
        PreparedStatement psLock = null;
        ResultSet rsLock = null;
        PreparedStatement psUpdRes = null;
        PreparedStatement psUpdCub = null;

        try {
            conn = ConexionBD.obtenerConexion();
            conn.setAutoCommit(false);

            psLock = conn.prepareStatement(SQL_SELECT_CUBICULO_FOR_UPDATE);
            psLock.setString(1, idCubiculo);
            rsLock = psLock.executeQuery();

            if (!rsLock.next()) {
                ConexionBD.rollback(conn);
                return "[ERROR] El cubículo '" + idCubiculo + "' no existe.";
            }

            psUpdRes = conn.prepareStatement(
                    "UPDATE RESERVA_CUBICULO SET estado_reserva = 'FINALIZADA' " +
                    "WHERE id_cubiculo = ? AND estado_reserva = 'ACTIVA'");
            psUpdRes.setString(1, idCubiculo);
            psUpdRes.executeUpdate();

            psUpdCub = conn.prepareStatement(
                    "UPDATE CUBICULO SET estado_cubiculo = 'DISPONIBLE' WHERE id_cubiculo = ?");
            psUpdCub.setString(1, idCubiculo);
            psUpdCub.executeUpdate();

            ConexionBD.commit(conn);
            return "El cubículo '" + idCubiculo + "' ha sido liberado y se encuentra DISPONIBLE.";
        } catch (SQLException ex) {
            ConexionBD.rollback(conn);
            throw ex;
        } finally {
            ConexionBD.cerrar(psUpdCub);
            ConexionBD.cerrar(psUpdRes);
            ConexionBD.cerrar(rsLock);
            ConexionBD.cerrar(psLock);
            ConexionBD.cerrar(conn);
        }
    }

    private void liberarReservasVencidasEnTransaccion(Connection conn) throws SQLException {
        try (PreparedStatement psLibCub = conn.prepareStatement(
                "UPDATE CUBICULO SET estado_cubiculo = 'DISPONIBLE' " +
                "WHERE estado_cubiculo = 'RESERVADO' AND id_cubiculo IN (" +
                "  SELECT id_cubiculo FROM RESERVA_CUBICULO WHERE estado_reserva = 'ACTIVA' AND fecha_hora_fin <= SYSDATE" +
                ")")) {
            psLibCub.executeUpdate();
        }
        try (PreparedStatement psFinRes = conn.prepareStatement(
                "UPDATE RESERVA_CUBICULO SET estado_reserva = 'FINALIZADA' " +
                "WHERE estado_reserva = 'ACTIVA' AND fecha_hora_fin <= SYSDATE")) {
            psFinRes.executeUpdate();
        }
    }

    @Override
    public List<Cubiculo> listarCubiculos() throws SQLException {
        List<Cubiculo> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.obtenerConexion();
            ps = conn.prepareStatement(
                    "SELECT id_cubiculo, nombre_codigo, capacidad, estado_cubiculo " +
                    "FROM CUBICULO ORDER BY id_cubiculo");
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Cubiculo(
                        rs.getString("id_cubiculo"),
                        rs.getString("nombre_codigo"),
                        rs.getInt("capacidad"),
                        rs.getString("estado_cubiculo")
                ));
            }
            return lista;
        } finally {
            ConexionBD.cerrarRecursos(rs, ps, conn);
        }
    }

    @Override
    public List<ReservaCubiculo> listarReservasActivas() throws SQLException {
        List<ReservaCubiculo> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.obtenerConexion();
            ps = conn.prepareStatement(
                    "SELECT id_reserva_cub, id_cubiculo, id_estudiante, fecha_hora_inicio, fecha_hora_fin, estado_reserva " +
                    "FROM RESERVA_CUBICULO WHERE estado_reserva = 'ACTIVA' ORDER BY fecha_hora_inicio DESC");
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new ReservaCubiculo(
                        rs.getLong("id_reserva_cub"),
                        rs.getString("id_cubiculo"),
                        rs.getString("id_estudiante"),
                        rs.getTimestamp("fecha_hora_inicio"),
                        rs.getTimestamp("fecha_hora_fin"),
                        rs.getString("estado_reserva")
                ));
            }
            return lista;
        } finally {
            ConexionBD.cerrarRecursos(rs, ps, conn);
        }
    }
}
