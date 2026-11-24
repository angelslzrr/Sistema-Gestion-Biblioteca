package sistemadegestionbiblioteca.daos.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import sistemadegestionbiblioteca.daos.EjemplarDAO;
import sistemadegestionbiblioteca.models.Ejemplar;
import sistemadegestionbiblioteca.utils.ConexionBD;

/**
 * Implementación JDBC puro de {@link EjemplarDAO} compatible con el esquema Oracle FREEPDB1.
 *
 * @author Grupo 6
 */
public class EjemplarDAOImpl implements EjemplarDAO {

    private static final String SQL_OBTENER_POR_ID =
            "SELECT id_ejemplar, id_libro, codigo_inventario, estado_ejemplar, estado_fisico " +
            "FROM EJEMPLAR WHERE id_ejemplar = ?";

    private static final String SQL_ACTUALIZAR_ESTADO =
            "UPDATE EJEMPLAR SET estado_ejemplar = ?, estado_fisico = ? WHERE id_ejemplar = ?";

    private static final String SQL_LISTAR_TODOS =
            "SELECT id_ejemplar, id_libro, codigo_inventario, estado_ejemplar, estado_fisico " +
            "FROM EJEMPLAR ORDER BY id_ejemplar";

    @Override
    public Ejemplar obtenerPorId(String idEjemplar) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.obtenerConexion();
            ps = conn.prepareStatement(SQL_OBTENER_POR_ID);
            ps.setString(1, idEjemplar);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapearEjemplar(rs);
            }
            return null;
        } finally {
            ConexionBD.cerrarRecursos(rs, ps, conn);
        }
    }

    @Override
    public void actualizarEstado(Connection conn, String idEjemplar,
                                 String nuevoEstadoEjemplar, String estadoFisico) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(SQL_ACTUALIZAR_ESTADO);
            ps.setString(1, nuevoEstadoEjemplar);
            ps.setString(2, estadoFisico);
            ps.setString(3, idEjemplar);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se encontró el ejemplar con ID '" + idEjemplar + "' para actualizar estado.");
            }
        } finally {
            ConexionBD.cerrar(ps);
        }
    }

    @Override
    public List<Ejemplar> listarTodos() throws SQLException {
        List<Ejemplar> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.obtenerConexion();
            ps = conn.prepareStatement(SQL_LISTAR_TODOS);
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearEjemplar(rs));
            }
            return lista;
        } finally {
            ConexionBD.cerrarRecursos(rs, ps, conn);
        }
    }

    private Ejemplar mapearEjemplar(ResultSet rs) throws SQLException {
        return new Ejemplar(
                rs.getString("id_ejemplar"),
                rs.getLong("id_libro"),
                rs.getString("codigo_inventario"),
                rs.getString("estado_ejemplar"),
                rs.getString("estado_fisico")
        );
    }
}
