package sistemadegestionbiblioteca.daos;

import java.sql.SQLException;
import java.util.List;
import sistemadegestionbiblioteca.models.Cubiculo;
import sistemadegestionbiblioteca.models.ReservaCubiculo;

/**
 * Interfaz DAO para gestionar los cubículos de estudio y sus reservas concurrentes
 * mediante bloqueo pesimista {@code SELECT ... FOR UPDATE} (CUS05).
 *
 * @author Grupo 6
 */
public interface CubiculoDAO {

    /**
     * Reserva un cubículo en tiempo real aplicando bloqueo pesimista a nivel de fila
     * ({@code SELECT ... FOR UPDATE}) en Oracle para evitar condiciones de carrera entre hilos.
     * Cumple con las reglas estrictas de CUS05:
     * <ul>
     *   <li>Máximo 2 horas exactas por reserva ({@code SYSDATE + (duracionHoras / 24)}).</li>
     *   <li>Máximo 1 reserva activa por día por estudiante.</li>
     *   <li>Actualiza {@code estado_cubiculo = 'RESERVADO'}.</li>
     * </ul>
     *
     * @param idCubiculo    Identificador del cubículo (ej. "CUB01").
     * @param idEstudiante  Identificador del estudiante solicitante.
     * @param duracionHoras Duración solicitada en horas (1 o 2 horas exactas).
     * @return Mensaje con el resultado de la reserva transaccional.
     * @throws SQLException Si ocurre un error de concurrencia o de base de datos.
     */
    String reservarCubiculoConBloqueoPesimista(String idCubiculo, String idEstudiante, int duracionHoras) throws SQLException;

    /**
     * Finaliza la reserva activa de un cubículo y restablece su estado a {@code 'DISPONIBLE'}.
     *
     * @param idCubiculo Identificador del cubículo a liberar.
     * @return Mensaje confirmando la liberación del cubículo.
     * @throws SQLException Si ocurre un error en la transacción JDBC.
     */
    String liberarCubiculo(String idCubiculo) throws SQLException;

    /**
     * Lista todos los cubículos registrados y su estado en tiempo real.
     *
     * @return Lista de cubículos.
     * @throws SQLException Si ocurre un error en la consulta JDBC.
     */
    List<Cubiculo> listarCubiculos() throws SQLException;

    /**
     * Lista las reservas de cubículos que se encuentran actualmente en estado {@code 'ACTIVA'}.
     *
     * @return Lista de reservas de cubículo activas.
     * @throws SQLException Si ocurre un error en la consulta JDBC.
     */
    List<ReservaCubiculo> listarReservasActivas() throws SQLException;
}
