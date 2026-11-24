package sistemadegestionbiblioteca.controllers;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import sistemadegestionbiblioteca.daos.ReservaMaterialDAO;
import sistemadegestionbiblioteca.daos.impl.ReservaMaterialDAOImpl;
import sistemadegestionbiblioteca.models.ReservaMaterial;

/**
 * Controlador MVC para el Caso de Uso CUS03 (Reservar material bibliográfico).
 * Prioriza la entrega del libro con un plazo máximo de recojo de 1 día (24 horas: {@code SYSDATE + 1}).
 *
 * @author Grupo 6
 */
public class ReservaMaterialController {

    private final ReservaMaterialDAO reservaMaterialDAO;

    public ReservaMaterialController() {
        this.reservaMaterialDAO = new ReservaMaterialDAOImpl();
    }

    /**
     * CUS03: Registra una reserva de material bibliográfico con vigencia máxima de 24 horas ({@code SYSDATE + 1}).
     *
     * @param idEstudiante Código del estudiante solicitante.
     * @param idEjemplar   Código del ejemplar a reservar.
     * @return Mensaje con el resultado de la operación.
     */
    public String reservarMaterial(String idEstudiante, String idEjemplar) {
        if (idEstudiante == null || idEstudiante.trim().isEmpty()) {
            return "[VALIDACIÓN - CUS03] Debe ingresar el ID del estudiante.";
        }
        if (idEjemplar == null || idEjemplar.trim().isEmpty()) {
            return "[VALIDACIÓN - CUS03] Debe ingresar el ID del ejemplar a reservar.";
        }
        try {
            return reservaMaterialDAO.crearReserva(idEstudiante.trim().toUpperCase(), idEjemplar.trim().toUpperCase());
        } catch (SQLException ex) {
            return "[ERROR BD - CUS03] Error al registrar reserva en Oracle: " + ex.getMessage();
        }
    }

    /**
     * Expira manualmente las reservas cuyo plazo de 24 horas ya venció ({@code fecha_limite_recojo < SYSDATE}).
     *
     * @return Mensaje indicando la cantidad de reservas expiradas.
     */
    public String depurarReservasVencidas() {
        try {
            int total = reservaMaterialDAO.expirarReservasVencidas();
            return "[INFO - CUS03] Se expiraron " + total + " reserva(s) con plazo mayor a 24 horas.";
        } catch (SQLException ex) {
            return "[ERROR BD - CUS03] Error al expirar reservas vencidas: " + ex.getMessage();
        }
    }

    /**
     * Lista las reservas de material bibliográfico pendientes de recojo.
     *
     * @return Lista de reservas pendientes.
     */
    public List<ReservaMaterial> listarReservasPendientes() {
        try {
            return reservaMaterialDAO.listarReservasPendientes();
        } catch (SQLException ex) {
            System.err.println("[ERROR BD] No se pudo listar reservas de material: " + ex.getMessage());
            return Collections.emptyList();
        }
    }
}
