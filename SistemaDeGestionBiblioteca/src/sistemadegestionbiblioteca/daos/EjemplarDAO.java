package sistemadegestionbiblioteca.daos;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import sistemadegestionbiblioteca.models.Ejemplar;

/**
 * Interfaz DAO para las operaciones de persistencia de la entidad {@link Ejemplar}.
 *
 * @author Grupo 6
 */
public interface EjemplarDAO {

    /**
     * Obtiene un ejemplar físico por su identificador o código QR.
     *
     * @param idEjemplar Identificador del ejemplar (ej. "EJ001").
     * @return Instancia de {@link Ejemplar} o {@code null} si no existe.
     * @throws SQLException Si ocurre un error en la consulta JDBC.
     */
    Ejemplar obtenerPorId(String idEjemplar) throws SQLException;

    /**
     * Actualiza el estado de disponibilidad y el estado físico de un ejemplar dentro de una transacción.
     *
     * @param conn                Conexión JDBC transaccional activa.
     * @param idEjemplar          Identificador del ejemplar.
     * @param nuevoEstadoEjemplar Nuevo estado ('DISPONIBLE', 'PRESTADO', 'RESERVADO', 'MANTENIMIENTO').
     * @param estadoFisico        Nuevo estado físico ('BUENO', 'DANADO').
     * @throws SQLException Si ocurre un error al actualizar la fila.
     */
    void actualizarEstado(Connection conn, String idEjemplar, String nuevoEstadoEjemplar, String estadoFisico) throws SQLException;

    /**
     * Lista todos los ejemplares registrados en el inventario de la biblioteca.
     *
     * @return Lista de ejemplares.
     * @throws SQLException Si ocurre un error en la consulta JDBC.
     */
    List<Ejemplar> listarTodos() throws SQLException;
}
