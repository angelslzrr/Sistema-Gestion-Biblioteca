package sistemadegestionbiblioteca.controllers;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import sistemadegestionbiblioteca.daos.EjemplarDAO;
import sistemadegestionbiblioteca.daos.EstudianteDAO;
import sistemadegestionbiblioteca.daos.PrestamoDAO;
import sistemadegestionbiblioteca.daos.impl.EjemplarDAOImpl;
import sistemadegestionbiblioteca.daos.impl.EstudianteDAOImpl;
import sistemadegestionbiblioteca.daos.impl.PrestamoDAOImpl;
import sistemadegestionbiblioteca.models.Ejemplar;
import sistemadegestionbiblioteca.models.Estudiante;
import sistemadegestionbiblioteca.models.Prestamo;

/**
 * Controlador MVC para gestionar los Casos de Uso CUS01 (Registrar préstamo de ejemplar)
 * y CUS02 (Devolver ejemplar).
 *
 * @author Grupo 6
 */
public class PrestamoController {

    private final PrestamoDAO prestamoDAO;
    private final EstudianteDAO estudianteDAO;
    private final EjemplarDAO ejemplarDAO;

    public PrestamoController() {
        this.estudianteDAO = new EstudianteDAOImpl();
        this.ejemplarDAO = new EjemplarDAOImpl();
        this.prestamoDAO = new PrestamoDAOImpl(this.estudianteDAO, this.ejemplarDAO);
    }

    /**
     * CUS01: Procesa el préstamo presencial de un ejemplar validando:
     * <ul>
     *   <li>Campos obligatorios no vacíos.</li>
     *   <li>Estudiante activo (no suspendido).</li>
     *   <li>Máximo 3 libros prestados simultáneamente.</li>
     *   <li>Asignación automática de 7 días ({@code SYSDATE + 7}).</li>
     *   <li>Cambio de estado a {@code estadoEjemplar = 'PRESTADO'}.</li>
     * </ul>
     *
     * @param idEstudiante Código del estudiante (ej. "EST001").
     * @param idEjemplar   Código del ejemplar (ej. "EJ001").
     * @return Mensaje descriptivo con el resultado de la operación.
     */
    public String procesarPrestamo(String idEstudiante, String idEjemplar) {
        if (idEstudiante == null || idEstudiante.trim().isEmpty()) {
            return "[VALIDACIÓN - CUS01] Debe ingresar un ID de estudiante válido.";
        }
        if (idEjemplar == null || idEjemplar.trim().isEmpty()) {
            return "[VALIDACIÓN - CUS01] Debe ingresar un ID de ejemplar válido.";
        }
        try {
            return prestamoDAO.registrarPrestamo(idEstudiante.trim().toUpperCase(), idEjemplar.trim().toUpperCase(), "PRESENCIAL");
        } catch (SQLException ex) {
            return "[ERROR BD - CUS01] Error al procesar el préstamo en Oracle: " + ex.getMessage();
        }
    }

    /**
     * CUS02: Procesa la devolución presencial de un ejemplar validando:
     * <ul>
     *   <li>Actualización de {@code estadoEjemplar = 'DISPONIBLE'}.</li>
     *   <li>Si se devuelve tarde ({@code SYSDATE > fecha_devolucion_esperada}) o dañado,
     *       sanciona al estudiante con {@code estadoEstudiante = 'SUSPENDIDO'} por exactamente 2 días
     *       ({@code SYSDATE + 2}) sin multa monetaria.</li>
     * </ul>
     *
     * @param idEjemplar     Código del ejemplar devuelto.
     * @param devueltoDanado {@code true} si el ejemplar presenta daño físico.
     * @return Mensaje descriptivo con el resultado de la devolución.
     */
    public String procesarDevolucion(String idEjemplar, boolean devueltoDanado) {
        if (idEjemplar == null || idEjemplar.trim().isEmpty()) {
            return "[VALIDACIÓN - CUS02] Debe ingresar el ID del ejemplar a devolver.";
        }
        try {
            return prestamoDAO.registrarDevolucion(idEjemplar.trim().toUpperCase(), devueltoDanado, "PRESENCIAL");
        } catch (SQLException ex) {
            return "[ERROR BD - CUS02] Error al procesar la devolución en Oracle: " + ex.getMessage();
        }
    }

    /**
     * Obtiene la lista de préstamos activos para mostrar en la vista.
     *
     * @return Lista de préstamos activos.
     */
    public List<Prestamo> listarPrestamosActivos() {
        try {
            return prestamoDAO.listarPrestamosActivos();
        } catch (SQLException ex) {
            System.err.println("[ERROR BD] No se pudo listar préstamos activos: " + ex.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Obtiene el catálogo de ejemplares con su estado actual.
     *
     * @return Lista de ejemplares.
     */
    public List<Ejemplar> listarEjemplares() {
        try {
            return ejemplarDAO.listarTodos();
        } catch (SQLException ex) {
            System.err.println("[ERROR BD] No se pudo listar ejemplares: " + ex.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Obtiene la lista de estudiantes registrados y su estado ('ACTIVO' o 'SUSPENDIDO').
     *
     * @return Lista de estudiantes.
     */
    public List<Estudiante> listarEstudiantes() {
        try {
            return estudianteDAO.listarTodos();
        } catch (SQLException ex) {
            System.err.println("[ERROR BD] No se pudo listar estudiantes: " + ex.getMessage());
            return Collections.emptyList();
        }
    }
}
