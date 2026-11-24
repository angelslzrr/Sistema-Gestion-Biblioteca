package sistemadegestionbiblioteca.daos.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import sistemadegestionbiblioteca.daos.CatalogoDAO;
import sistemadegestionbiblioteca.models.Libro;
import sistemadegestionbiblioteca.models.NotificacionAdmin;
import sistemadegestionbiblioteca.utils.ConexionBD;

/**
 * Implementación JDBC puro de {@link CatalogoDAO} con manejo manual de transacciones
 * para el Caso de Uso CUS03 (Búsqueda de Catálogo y Notificación al Administrador)
 * y el Modo Administrador (CRUD de Catálogo y Gestión de Notificaciones).
 *
 * @author Grupo 6
 */
public class CatalogoDAOImpl implements CatalogoDAO {

    private static final String SQL_BUSCAR_CATALOGO =
            "SELECT l.id_libro, l.isbn, l.titulo, l.autor, l.editorial, l.anio_publicacion, " +
            "       COUNT(e.id_ejemplar) AS total_ejemplares, " +
            "       SUM(CASE WHEN e.estado_ejemplar = 'DISPONIBLE' AND e.estado_fisico = 'BUENO' THEN 1 ELSE 0 END) AS disponibles " +
            "FROM LIBRO l " +
            "LEFT JOIN EJEMPLAR e ON l.id_libro = e.id_libro " +
            "WHERE (? IS NULL OR UPPER(l.titulo) LIKE ? OR UPPER(l.autor) LIKE ? OR UPPER(l.isbn) LIKE ?) " +
            "GROUP BY l.id_libro, l.isbn, l.titulo, l.autor, l.editorial, l.anio_publicacion " +
            "ORDER BY l.id_libro ASC";

    @Override
    public List<Libro> buscarLibrosEnTiempoReal(String criterio) throws SQLException {
        List<Libro> resultados = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String filtro = (criterio == null || criterio.trim().isEmpty())
                ? null
                : "%" + criterio.trim().toUpperCase() + "%";

        try {
            conn = ConexionBD.obtenerConexion();
            ps = conn.prepareStatement(SQL_BUSCAR_CATALOGO);
            if (filtro == null) {
                ps.setNull(1, Types.VARCHAR);
                ps.setNull(2, Types.VARCHAR);
                ps.setNull(3, Types.VARCHAR);
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(1, filtro);
                ps.setString(2, filtro);
                ps.setString(3, filtro);
                ps.setString(4, filtro);
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                int anio = rs.getInt("anio_publicacion");
                Integer anioObj = rs.wasNull() ? null : anio;
                resultados.add(new Libro(
                        rs.getLong("id_libro"),
                        rs.getString("isbn"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("editorial"),
                        anioObj,
                        rs.getInt("total_ejemplares"),
                        rs.getInt("disponibles")
                ));
            }
            return resultados;
        } finally {
            ConexionBD.cerrarRecursos(rs, ps, conn);
        }
    }

    @Override
    public String registrarNotificacionAdmin(String idEstudiante, String textoBusqueda) throws SQLException {
        Connection conn = null;
        PreparedStatement psEst = null;
        ResultSet rsEst = null;
        PreparedStatement psIns = null;

        try {
            conn = ConexionBD.obtenerConexion();
            conn.setAutoCommit(false);

            // 1. Verificar que el estudiante exista en la base de datos
            psEst = conn.prepareStatement("SELECT nombres, apellidos FROM ESTUDIANTE WHERE id_estudiante = ?");
            psEst.setString(1, idEstudiante);
            rsEst = psEst.executeQuery();
            if (!rsEst.next()) {
                ConexionBD.rollback(conn);
                return "[ERROR] El código de estudiante '" + idEstudiante + "' no existe en el padrón.";
            }

            // 2. Insertar notificación en NOTIFICACION_ADMIN con fecha_solicitud = SYSDATE y estado = 'PENDIENTE'
            psIns = conn.prepareStatement(
                    "INSERT INTO NOTIFICACION_ADMIN (id_estudiante, texto_busqueda, fecha_solicitud, estado) " +
                    "VALUES (?, ?, SYSDATE, 'PENDIENTE')");
            psIns.setString(1, idEstudiante);
            psIns.setString(2, textoBusqueda);
            psIns.executeUpdate();

            ConexionBD.commit(conn);
            return "Se ha enviado la notificación al Administrador exitosamente.\n"
                    + "Estudiante solicitante: " + idEstudiante + "\n"
                    + "Material reportado: \"" + textoBusqueda + "\"\n"
                    + "Estado: PENDIENTE de adquisición o reposición.";
        } catch (SQLException ex) {
            ConexionBD.rollback(conn);
            throw ex;
        } finally {
            ConexionBD.cerrar(psIns);
            ConexionBD.cerrar(rsEst);
            ConexionBD.cerrar(psEst);
            ConexionBD.cerrar(conn);
        }
    }

    @Override
    public List<NotificacionAdmin> listarNotificaciones() throws SQLException {
        List<NotificacionAdmin> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConexionBD.obtenerConexion();
            ps = conn.prepareStatement(
                    "SELECT id_notificacion, id_estudiante, texto_busqueda, fecha_solicitud, estado " +
                    "FROM NOTIFICACION_ADMIN ORDER BY fecha_solicitud DESC, id_notificacion DESC");
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new NotificacionAdmin(
                        rs.getLong("id_notificacion"),
                        rs.getString("id_estudiante"),
                        rs.getString("texto_busqueda"),
                        rs.getTimestamp("fecha_solicitud"),
                        rs.getString("estado")
                ));
            }
            return lista;
        } finally {
            ConexionBD.cerrarRecursos(rs, ps, conn);
        }
    }

    @Override
    public String actualizarEstadoNotificacion(long idNotificacion, String nuevoEstado) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConexionBD.obtenerConexion();
            conn.setAutoCommit(false);

            ps = conn.prepareStatement("UPDATE NOTIFICACION_ADMIN SET estado = ? WHERE id_notificacion = ?");
            ps.setString(1, nuevoEstado);
            ps.setLong(2, idNotificacion);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                ConexionBD.rollback(conn);
                return "[ERROR] No se encontró la notificación #" + idNotificacion + ".";
            }

            ConexionBD.commit(conn);
            return "Notificación #" + idNotificacion + " actualizada a estado '" + nuevoEstado + "'.";
        } catch (SQLException ex) {
            ConexionBD.rollback(conn);
            throw ex;
        } finally {
            ConexionBD.cerrarRecursos(null, ps, conn);
        }
    }

    @Override
    public String eliminarNotificacion(long idNotificacion) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConexionBD.obtenerConexion();
            conn.setAutoCommit(false);

            ps = conn.prepareStatement("DELETE FROM NOTIFICACION_ADMIN WHERE id_notificacion = ?");
            ps.setLong(1, idNotificacion);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                ConexionBD.rollback(conn);
                return "[ERROR] No se encontró la notificación #" + idNotificacion + " para eliminar.";
            }

            ConexionBD.commit(conn);
            return "Notificación #" + idNotificacion + " eliminada correctamente.";
        } catch (SQLException ex) {
            ConexionBD.rollback(conn);
            throw ex;
        } finally {
            ConexionBD.cerrarRecursos(null, ps, conn);
        }
    }

    @Override
    public String crearLibroConEjemplares(Libro libro, int cantidadEjemplares) throws SQLException {
        Connection conn = null;
        PreparedStatement psInsLibro = null;
        PreparedStatement psIdLibro = null;
        ResultSet rsIdLibro = null;
        PreparedStatement psSeqEj = null;
        ResultSet rsSeqEj = null;
        PreparedStatement psInsEj = null;

        try {
            conn = ConexionBD.obtenerConexion();
            conn.setAutoCommit(false);

            psInsLibro = conn.prepareStatement(
                    "INSERT INTO LIBRO (isbn, titulo, autor, editorial, anio_publicacion) VALUES (?, ?, ?, ?, ?)");
            psInsLibro.setString(1, libro.getIsbn());
            psInsLibro.setString(2, libro.getTitulo());
            psInsLibro.setString(3, libro.getAutor());
            psInsLibro.setString(4, libro.getEditorial());
            if (libro.getAnioPublicacion() != null) {
                psInsLibro.setInt(5, libro.getAnioPublicacion());
            } else {
                psInsLibro.setNull(5, Types.INTEGER);
            }
            psInsLibro.executeUpdate();

            psIdLibro = conn.prepareStatement("SELECT id_libro FROM LIBRO WHERE isbn = ?");
            psIdLibro.setString(1, libro.getIsbn());
            rsIdLibro = psIdLibro.executeQuery();
            long idLibroGenerado = rsIdLibro.next() ? rsIdLibro.getLong(1) : 0L;

            if (cantidadEjemplares > 0 && idLibroGenerado > 0) {
                psSeqEj = conn.prepareStatement(
                        "SELECT NVL(MAX(TO_NUMBER(REGEXP_SUBSTR(id_ejemplar, '[0-9]+'))), 0) FROM EJEMPLAR");
                rsSeqEj = psSeqEj.executeQuery();
                int correlativoBase = rsSeqEj.next() ? rsSeqEj.getInt(1) : 0;

                psInsEj = conn.prepareStatement(
                        "INSERT INTO EJEMPLAR (id_ejemplar, id_libro, codigo_inventario, estado_ejemplar, estado_fisico) " +
                        "VALUES (?, ?, ?, 'DISPONIBLE', 'BUENO')");
                for (int i = 1; i <= cantidadEjemplares; i++) {
                    int num = correlativoBase + i;
                    String idEj = String.format("EJM-%03d", num);
                    String codInv = String.format("INV-BIB-2026-%03d", num);
                    psInsEj.setString(1, idEj);
                    psInsEj.setLong(2, idLibroGenerado);
                    psInsEj.setString(3, codInv);
                    psInsEj.executeUpdate();
                }
            }

            ConexionBD.commit(conn);
            return "Libro \"" + libro.getTitulo() + "\" registrado exitosamente con "
                    + Math.max(0, cantidadEjemplares) + " ejemplar(es) disponible(s).";
        } catch (SQLException ex) {
            ConexionBD.rollback(conn);
            throw ex;
        } finally {
            ConexionBD.cerrar(psInsEj);
            ConexionBD.cerrar(rsSeqEj);
            ConexionBD.cerrar(psSeqEj);
            ConexionBD.cerrar(rsIdLibro);
            ConexionBD.cerrar(psIdLibro);
            ConexionBD.cerrar(psInsLibro);
            ConexionBD.cerrar(conn);
        }
    }

    @Override
    public String actualizarLibro(Libro libro) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConexionBD.obtenerConexion();
            conn.setAutoCommit(false);

            ps = conn.prepareStatement(
                    "UPDATE LIBRO SET isbn = ?, titulo = ?, autor = ?, editorial = ?, anio_publicacion = ? " +
                    "WHERE id_libro = ?");
            ps.setString(1, libro.getIsbn());
            ps.setString(2, libro.getTitulo());
            ps.setString(3, libro.getAutor());
            ps.setString(4, libro.getEditorial());
            if (libro.getAnioPublicacion() != null) {
                ps.setInt(5, libro.getAnioPublicacion());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setLong(6, libro.getIdLibro());

            int filas = ps.executeUpdate();
            if (filas == 0) {
                ConexionBD.rollback(conn);
                return "[ERROR] No se encontró el libro con ID " + libro.getIdLibro() + ".";
            }

            ConexionBD.commit(conn);
            return "Datos del libro \"" + libro.getTitulo() + "\" actualizados correctamente.";
        } catch (SQLException ex) {
            ConexionBD.rollback(conn);
            throw ex;
        } finally {
            ConexionBD.cerrarRecursos(null, ps, conn);
        }
    }

    @Override
    public String eliminarLibro(long idLibro) throws SQLException {
        Connection conn = null;
        PreparedStatement psCheck = null;
        ResultSet rsCheck = null;
        PreparedStatement psDelPrestamosHist = null;
        PreparedStatement psDelEj = null;
        PreparedStatement psDelLibro = null;
        try {
            conn = ConexionBD.obtenerConexion();
            conn.setAutoCommit(false);

            psCheck = conn.prepareStatement(
                    "SELECT COUNT(*) FROM PRESTAMO p " +
                    "JOIN EJEMPLAR e ON p.id_ejemplar = e.id_ejemplar " +
                    "WHERE e.id_libro = ? AND p.estado_prestamo = 'ACTIVO'");
            psCheck.setLong(1, idLibro);
            rsCheck = psCheck.executeQuery();
            if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                ConexionBD.rollback(conn);
                return "[RECHAZADO] No se puede eliminar el libro porque tiene ejemplares con préstamos ACTIVOS.";
            }

            psDelPrestamosHist = conn.prepareStatement(
                    "DELETE FROM PRESTAMO WHERE id_ejemplar IN (SELECT id_ejemplar FROM EJEMPLAR WHERE id_libro = ?)");
            psDelPrestamosHist.setLong(1, idLibro);
            psDelPrestamosHist.executeUpdate();

            psDelEj = conn.prepareStatement("DELETE FROM EJEMPLAR WHERE id_libro = ?");
            psDelEj.setLong(1, idLibro);
            psDelEj.executeUpdate();

            psDelLibro = conn.prepareStatement("DELETE FROM LIBRO WHERE id_libro = ?");
            psDelLibro.setLong(1, idLibro);
            int filas = psDelLibro.executeUpdate();
            if (filas == 0) {
                ConexionBD.rollback(conn);
                return "[ERROR] No se encontró el libro con ID " + idLibro + ".";
            }

            ConexionBD.commit(conn);
            return "El libro y sus ejemplares asociados fueron eliminados del catálogo.";
        } catch (SQLException ex) {
            ConexionBD.rollback(conn);
            throw ex;
        } finally {
            ConexionBD.cerrar(psDelLibro);
            ConexionBD.cerrar(psDelEj);
            ConexionBD.cerrar(psDelPrestamosHist);
            ConexionBD.cerrar(rsCheck);
            ConexionBD.cerrar(psCheck);
            ConexionBD.cerrar(conn);
        }
    }
}
