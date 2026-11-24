package sistemadegestionbiblioteca.daos;

import java.sql.SQLException;
import java.util.List;
import sistemadegestionbiblioteca.models.Prestamo;

/**
 * Interfaz DAO para gestionar préstamos y devoluciones de ejemplares (CUS01, CUS02, CUS04).
 *
 * @author Grupo 6
 */
public interface PrestamoDAO {

    /**
     * Registra un préstamo de forma transaccional asignando explícitamente
     * {@code fecha_prestamo = SYSDATE} y {@code fecha_devolucion_esperada = SYSDATE + 7},
     * y cambiando el ejemplar a {@code 'PRESTADO'} (CUS01 / CUS04).
     *
     * @param idEstudiante Código del estudiante solicitante.
     * @param idEjemplar   Código del ejemplar físico.
     * @param canal        Canal de atención ('PRESENCIAL' o 'AUTOSERVICIO_QR').
     * @return Mensaje detallado con el resultado de la operación.
     * @throws SQLException Si ocurre un error de base de datos o violación de reglas.
     */
    String registrarPrestamo(String idEstudiante, String idEjemplar, String canal) throws SQLException;

    /**
     * Registra la devolución de un ejemplar de forma transaccional, actualizando el ejemplar a
     * {@code 'DISPONIBLE'} y aplicando automáticamente la suspensión de 2 días ({@code SYSDATE + 2})
     * al estudiante si devuelve con atraso ({@code SYSDATE > fecha_devolucion_esperada}) o dañado (CUS02 / CUS04).
     *
     * @param idEjemplar     Código del ejemplar devuelto.
     * @param devueltoDanado {@code true} si se reporta daño físico en el ejemplar.
     * @param canal          Canal de devolución ('PRESENCIAL' o 'AUTOSERVICIO_QR').
     * @return Mensaje detallado con el resultado de la devolución y posibles sanciones.
     * @throws SQLException Si ocurre un error en la transacción JDBC.
     */
    String registrarDevolucion(String idEjemplar, boolean devueltoDanado, String canal) throws SQLException;

    /**
     * Obtiene el préstamo activo asociado a un ejemplar específico.
     *
     * @param idEjemplar Código del ejemplar.
     * @return Instancia de {@link Prestamo} activo o {@code null} si no está prestado.
     * @throws SQLException Si ocurre un error en la consulta JDBC.
     */
    Prestamo obtenerPrestamoActivoPorEjemplar(String idEjemplar) throws SQLException;

    /**
     * Lista todos los préstamos que se encuentran actualmente en estado {@code 'ACTIVO'}.
     *
     * @return Lista de préstamos activos.
     * @throws SQLException Si ocurre un error en la consulta JDBC.
     */
    List<Prestamo> listarPrestamosActivos() throws SQLException;
}
