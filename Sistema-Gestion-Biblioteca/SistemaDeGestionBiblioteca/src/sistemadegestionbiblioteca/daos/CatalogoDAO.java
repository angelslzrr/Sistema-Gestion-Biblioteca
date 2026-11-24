package sistemadegestionbiblioteca.daos;

import java.sql.SQLException;
import java.util.List;
import sistemadegestionbiblioteca.models.Libro;
import sistemadegestionbiblioteca.models.NotificacionAdmin;

/**
 * Interfaz DAO para gestionar el Catálogo Bibliográfico y las Notificaciones al Administrador (CUS03)
 * así como las operaciones CRUD del Modo Administrador.
 *
 * @author Grupo 6
 */
public interface CatalogoDAO {

    /**
     * CUS03: Busca libros en el catálogo por título, autor o ISBN y calcula en tiempo real
     * la cantidad total de ejemplares y cuántos están DISPONIBLES.
     *
     * @param criterio Texto de búsqueda (si es vacío o nulo, lista todo el catálogo).
     * @return Lista de {@link Libro} con el stock disponible actualizado en tiempo real.
     * @throws SQLException Si ocurre un error en la consulta JDBC.
     */
    List<Libro> buscarLibrosEnTiempoReal(String criterio) throws SQLException;

    /**
     * CUS03: Registra una notificación al administrador en la tabla {@code NOTIFICACION_ADMIN}
     * cuando el libro buscado no tiene ejemplares disponibles o no existe en el catálogo.
     * Utiliza transacción manual explícita.
     *
     * @param idEstudiante  Código del estudiante solicitante.
     * @param textoBusqueda Texto de búsqueda o título del libro reportado como faltante.
     * @return Mensaje confirmando el registro de la notificación.
     * @throws SQLException Si ocurre un error en la transacción JDBC.
     */
    String registrarNotificacionAdmin(String idEstudiante, String textoBusqueda) throws SQLException;

    /**
     * Lista todas las notificaciones registradas en {@code NOTIFICACION_ADMIN}.
     *
     * @return Lista de notificaciones para revisión del Administrador.
     * @throws SQLException Si ocurre un error en la consulta JDBC.
     */
    List<NotificacionAdmin> listarNotificaciones() throws SQLException;

    /**
     * Actualiza el estado de una notificación ('PENDIENTE', 'ATENDIDA', 'DESCARTADA') en Modo Administrador.
     *
     * @param idNotificacion ID de la notificación.
     * @param nuevoEstado    Nuevo estado a asignar.
     * @return Mensaje con el resultado de la actualización.
     * @throws SQLException Si ocurre un error en la transacción JDBC.
     */
    String actualizarEstadoNotificacion(long idNotificacion, String nuevoEstado) throws SQLException;

    /**
     * Elimina una notificación de la tabla {@code NOTIFICACION_ADMIN} (Modo Administrador).
     *
     * @param idNotificacion ID de la notificación a eliminar.
     * @return Mensaje con el resultado de la eliminación.
     * @throws SQLException Si ocurre un error en la transacción JDBC.
     */
    String eliminarNotificacion(long idNotificacion) throws SQLException;

    /**
     * CRUD Modo Administrador: Crea un nuevo libro en el catálogo y opcionalmente genera
     * sus ejemplares físicos iniciales de forma transaccional.
     *
     * @param libro              Datos del libro a registrar.
     * @param cantidadEjemplares Cantidad de ejemplares físicos iniciales a generar en inventario.
     * @return Mensaje con el resultado de la creación.
     * @throws SQLException Si ocurre un error en la transacción JDBC.
     */
    String crearLibroConEjemplares(Libro libro, int cantidadEjemplares) throws SQLException;

    /**
     * CRUD Modo Administrador: Actualiza los datos de un libro existente.
     *
     * @param libro Datos actualizados del libro.
     * @return Mensaje con el resultado de la actualización.
     * @throws SQLException Si ocurre un error en la transacción JDBC.
     */
    String actualizarLibro(Libro libro) throws SQLException;

    /**
     * CRUD Modo Administrador: Elimina un libro del catálogo verificando que no posea préstamos activos.
     *
     * @param idLibro ID del libro a eliminar.
     * @return Mensaje con el resultado de la eliminación.
     * @throws SQLException Si ocurre un error en la transacción JDBC.
     */
    String eliminarLibro(long idLibro) throws SQLException;
}
