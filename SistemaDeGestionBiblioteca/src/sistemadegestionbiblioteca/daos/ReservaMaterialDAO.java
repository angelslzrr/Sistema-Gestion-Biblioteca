package sistemadegestionbiblioteca.daos;

import java.sql.SQLException;
import java.util.List;
import sistemadegestionbiblioteca.models.ReservaMaterial;

/**
 * Interfaz DAO para gestionar la reserva de material bibliográfico con vigencia de 24 horas (CUS03).
 *
 * @author Grupo 6
 */
public interface ReservaMaterialDAO {

    /**
     * Registra una reserva de material bibliográfico asignando {@code fecha_reserva = SYSDATE},
     * {@code fecha_limite_recojo = SYSDATE + 1} (24 horas exactas) y {@code estado_reserva = 'PENDIENTE'}.
     * Si el ejemplar estaba {@code 'DISPONIBLE'}, lo pasa a {@code 'RESERVADO'} para priorizar su entrega (CUS03).
     *
     * @param idEstudiante Código del estudiante solicitante.
     * @param idEjemplar   Código del ejemplar a reservar.
     * @return Mensaje con la confirmación de la reserva y fecha límite de recojo.
     * @throws SQLException Si ocurre un error en la transacción JDBC.
     */
    String crearReserva(String idEstudiante, String idEjemplar) throws SQLException;

    /**
     * Marca como {@code 'EXPIRADA'} todas las reservas pendientes cuyo {@code fecha_limite_recojo < SYSDATE}
     * y libera los ejemplares reservados de vuelta a {@code 'DISPONIBLE'}.
     *
     * @return Cantidad de reservas expiradas procesadas.
     * @throws SQLException Si ocurre un error en la transacción JDBC.
     */
    int expirarReservasVencidas() throws SQLException;

    /**
     * Obtiene la reserva pendiente vigente asociada a un ejemplar.
     *
     * @param idEjemplar Código del ejemplar.
     * @return Instancia de {@link ReservaMaterial} pendiente o {@code null} si no existe.
     * @throws SQLException Si ocurre un error en la consulta JDBC.
     */
    ReservaMaterial obtenerReservaPendientePorEjemplar(String idEjemplar) throws SQLException;

    /**
     * Lista todas las reservas de material que se encuentran en estado {@code 'PENDIENTE'}.
     *
     * @return Lista de reservas pendientes.
     * @throws SQLException Si ocurre un error en la consulta JDBC.
     */
    List<ReservaMaterial> listarReservasPendientes() throws SQLException;
}
