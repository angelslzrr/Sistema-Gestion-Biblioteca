package sistemadegestionbiblioteca.controllers;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import sistemadegestionbiblioteca.daos.CubiculoDAO;
import sistemadegestionbiblioteca.daos.impl.CubiculoDAOImpl;
import sistemadegestionbiblioteca.models.Cubiculo;
import sistemadegestionbiblioteca.models.ReservaCubiculo;

/**
 * Controlador MVC para el Caso de Uso CUS05 (Reserva de Cubículos en tiempo real).
 * Garantiza las reglas de negocio (máximo 2 horas exactas, 1 reserva activa por día por estudiante)
 * y delega en {@link CubiculoDAO#reservarCubiculoConBloqueoPesimista(String, String, int)}
 * para aplicar {@code SELECT ... FOR UPDATE} en Oracle.
 *
 * @author Grupo 6
 */
public class CubiculoController {

    private final CubiculoDAO cubiculoDAO;

    public CubiculoController() {
        this.cubiculoDAO = new CubiculoDAOImpl();
    }

    /**
     * CUS05: Procesa la reserva de un cubículo con bloqueo pesimista a nivel de fila.
     *
     * @param idCubiculo   Identificador del cubículo (ej. "CUB01").
     * @param idEstudiante Identificador del estudiante (ej. "EST001").
     * @param horas        Duración en horas (1 o máximo 2 horas exactas).
     * @return Mensaje con el resultado de la operación.
     */
    public String reservarCubiculo(String idCubiculo, String idEstudiante, int horas) {
        if (idCubiculo == null || idCubiculo.trim().isEmpty()) {
            return "[VALIDACIÓN - CUS05] Debe seleccionar o ingresar un ID de cubículo.";
        }
        if (idEstudiante == null || idEstudiante.trim().isEmpty()) {
            return "[VALIDACIÓN - CUS05] Debe ingresar el ID del estudiante.";
        }
        if (horas < 1 || horas > 2) {
            return "[VALIDACIÓN - CUS05] Regla de negocio: La reserva de cubículo es por máximo 2 horas exactas (1 o 2 horas).";
        }
        try {
            return cubiculoDAO.reservarCubiculoConBloqueoPesimista(
                    idCubiculo.trim().toUpperCase(),
                    idEstudiante.trim().toUpperCase(),
                    horas);
        } catch (SQLException ex) {
            return "[ERROR BD - CUS05] Error en transacción con bloqueo pesimista: " + ex.getMessage();
        }
    }

    /**
     * Finaliza la ocupación de un cubículo y lo devuelve a estado {@code 'DISPONIBLE'}.
     *
     * @param idCubiculo Identificador del cubículo.
     * @return Mensaje con el resultado de la liberación.
     */
    public String liberarCubiculo(String idCubiculo) {
        if (idCubiculo == null || idCubiculo.trim().isEmpty()) {
            return "[VALIDACIÓN - CUS05] Debe indicar el ID del cubículo a liberar.";
        }
        try {
            return cubiculoDAO.liberarCubiculo(idCubiculo.trim().toUpperCase());
        } catch (SQLException ex) {
            return "[ERROR BD - CUS05] Error al liberar cubículo: " + ex.getMessage();
        }
    }

    /**
     * Lista todos los cubículos y su disponibilidad en tiempo real.
     *
     * @return Lista de cubículos.
     */
    public List<Cubiculo> listarCubiculos() {
        try {
            return cubiculoDAO.listarCubiculos();
        } catch (SQLException ex) {
            System.err.println("[ERROR BD] No se pudo listar cubículos: " + ex.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Lista las reservas de cubículos activas en este momento.
     *
     * @return Lista de reservas activas.
     */
    public List<ReservaCubiculo> listarReservasActivas() {
        try {
            return cubiculoDAO.listarReservasActivas();
        } catch (SQLException ex) {
            System.err.println("[ERROR BD] No se pudo listar reservas de cubículo: " + ex.getMessage());
            return Collections.emptyList();
        }
    }
}
