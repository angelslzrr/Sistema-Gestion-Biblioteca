package sistemadegestionbiblioteca.daos.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import sistemadegestionbiblioteca.daos.EstudianteDAO;
import sistemadegestionbiblioteca.models.Estudiante;
import sistemadegestionbiblioteca.utils.ConexionBD;

/**
 * Implementación JDBC puro de {@link EstudianteDAO} compatible con el esquema Oracle FREEPDB1.
 *
 * @author Grupo 6
 */
public class EstudianteDAOImpl implements EstudianteDAO {

    private static final String SQL_OBTENER_POR_ID =
            "SELECT id_estudiante, nombres, apellidos, correo, estado_estudiante, fecha_fin_suspension " +
            "FROM ESTUDIANTE WHERE id_estudiante = ?";

    private static final String SQL_CONTAR_PRESTAMOS_ACTIVOS =
            "SELECT COUNT(*) FROM PRESTAMO WHERE id_estudiante = ? AND estado_prestamo = 'ACTIVO'";

    private static final String SQL_SUSPENDER_DOS_DIAS =
            "UPDATE ESTUDIANTE " +
            "SET estado_estudiante = 'SUSPENDIDO', fecha_fin_suspension = SYSDATE + 2 " +
            "WHERE id_estudiante = ?";

    private static final String SQL_LEVANTAR_SUSPENSION_VENCIDA =
            "UPDATE ESTUDIANTE " +
            "SET estado_estudiante = 'ACTIVO', fecha_fin_suspension = NULL " +
            "WHERE id_estudiante = ? AND estado_estudiante = 'SUSPENDIDO' AND fecha_fin_suspension <= SYSDATE";

    private static final String SQL_LISTAR_TODOS =
            "SELECT id_estudiante, nombres, apellidos, correo, estado_estudiante, fecha_fin_suspension " +
            "FROM ESTUDIANTE ORDER BY id_estudiante";

    @Override
    public Estudiante obtenerPorId(String idEstudiante) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.obtenerConexion();
            ps = conn.prepareStatement(SQL_OBTENER_POR_ID);
            ps.setString(1, idEstudiante);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapearEstudiante(rs);
            }
            return null;
        } finally {
            ConexionBD.cerrarRecursos(rs, ps, conn);
        }
    }

    @Override
    public int contarPrestamosActivos(String idEstudiante) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.obtenerConexion();
            ps = conn.prepareStatement(SQL_CONTAR_PRESTAMOS_ACTIVOS);
            ps.setString(1, idEstudiante);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            ConexionBD.cerrarRecursos(rs, ps, conn);
        }
    }

    @Override
    public void suspenderEstudiantePorDosDias(Connection conn, String idEstudiante) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(SQL_SUSPENDER_DOS_DIAS);
            ps.setString(1, idEstudiante);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se encontró el estudiante con ID '" + idEstudiante + "' para aplicar suspensión.");
            }
        } finally {
            ConexionBD.cerrar(ps);
        }
    }

    @Override
    public boolean verificarYLevantarSuspensionVencida(String idEstudiante) throws SQLException {
        Connection conn = null;
        PreparedStatement psUpdate = null;
        PreparedStatement psSelect = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.obtenerConexion();
            psUpdate = conn.prepareStatement(SQL_LEVANTAR_SUSPENSION_VENCIDA);
            psUpdate.setString(1, idEstudiante);
            psUpdate.executeUpdate();

            psSelect = conn.prepareStatement(SQL_OBTENER_POR_ID);
            psSelect.setString(1, idEstudiante);
            rs = psSelect.executeQuery();
            if (rs.next()) {
                String estado = rs.getString("estado_estudiante");
                return "ACTIVO".equalsIgnoreCase(estado);
            }
            return false;
        } finally {
            ConexionBD.cerrar(rs);
            ConexionBD.cerrar(psSelect);
            ConexionBD.cerrar(psUpdate);
            ConexionBD.cerrar(conn);
        }
    }

    @Override
    public List<Estudiante> listarTodos() throws SQLException {
        List<Estudiante> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.obtenerConexion();
            ps = conn.prepareStatement(SQL_LISTAR_TODOS);
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearEstudiante(rs));
            }
            return lista;
        } finally {
            ConexionBD.cerrarRecursos(rs, ps, conn);
        }
    }

    private Estudiante mapearEstudiante(ResultSet rs) throws SQLException {
        String id = rs.getString("id_estudiante");
        String nombres = rs.getString("nombres");
        String apellidos = rs.getString("apellidos");
        String correo = rs.getString("correo");
        String estado = rs.getString("estado_estudiante");
        Timestamp fechaFinSusp = rs.getTimestamp("fecha_fin_suspension");
        return new Estudiante(id, nombres, apellidos, correo, estado, fechaFinSusp);
    }
}
