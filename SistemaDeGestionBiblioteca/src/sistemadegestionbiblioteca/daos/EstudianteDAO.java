package sistemadegestionbiblioteca.daos;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import sistemadegestionbiblioteca.models.Estudiante;

/**
 * Interfaz DAO para las operaciones de persistencia de la entidad {@link Estudiante}.
 *
 * @author Grupo 6
 */
public interface EstudianteDAO {

    /**
     * Busca un estudiante por su identificador único.
     *
     * @param idEstudiante Código del estudiante (ej. "EST001").
     * @return Instancia de {@link Estudiante} o {@code null} si no existe.
     * @throws SQLException Si ocurre un error en la consulta JDBC.
     */
    Estudiante obtenerPorId(String idEstudiante) throws SQLException;

    /**
     * Cuenta la cantidad de préstamos activos de un estudiante para validar el límite
     * estricto de máximo 3 libros prestados simultáneamente (CUS01).
     *
     * @param idEstudiante Código del estudiante.
     * @return Número de préstamos con {@code estado_prestamo = 'ACTIVO'}.
     * @throws SQLException Si ocurre un error en la consulta JDBC.
     */
    int contarPrestamosActivos(String idEstudiante) throws SQLException;

    /**
     * Aplica la sanción de suspensión estricta por exactamente 2 días (SYSDATE + 2)
     * dentro de una transacción activa cuando un ejemplar se devuelve tarde o dañado (CUS02).
     *
     * @param conn         Conexión JDBC transaccional activa.
     * @param idEstudiante Código del estudiante a sancionar.
     * @throws SQLException Si falla la actualización en Oracle.
     */
    void suspenderEstudiantePorDosDias(Connection conn, String idEstudiante) throws SQLException;

    /**
     * Verifica si la suspensión de un estudiante ya cumplió su plazo ({@code fecha_fin_suspension <= SYSDATE})
     * y, de ser así, restablece su estado a {@code 'ACTIVO'}.
     *
     * @param idEstudiante Código del estudiante.
     * @return {@code true} si el estudiante fue reactivado o ya estaba activo; {@code false} si sigue suspendido.
     * @throws SQLException Si ocurre un error en la consulta/actualización JDBC.
     */
    boolean verificarYLevantarSuspensionVencida(String idEstudiante) throws SQLException;

    /**
     * Lista todos los estudiantes registrados en el sistema.
     *
     * @return Lista de estudiantes.
     * @throws SQLException Si ocurre un error en la consulta JDBC.
     */
    List<Estudiante> listarTodos() throws SQLException;
}
