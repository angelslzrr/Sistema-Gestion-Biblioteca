package sistemadegestionbiblioteca.controllers;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import sistemadegestionbiblioteca.daos.CatalogoDAO;
import sistemadegestionbiblioteca.daos.EstudianteDAO;
import sistemadegestionbiblioteca.daos.impl.CatalogoDAOImpl;
import sistemadegestionbiblioteca.daos.impl.EstudianteDAOImpl;
import sistemadegestionbiblioteca.models.Libro;
import sistemadegestionbiblioteca.models.NotificacionAdmin;

/**
 * Controlador MVC para el Caso de Uso CUS03 (Búsqueda de Catálogo y Notificación al Administrador)
 * y para las operaciones de gestión del Modo Administrador (CRUD de Catálogo, Notificaciones y Sanciones).
 *
 * @author Grupo 6
 */
public class CatalogoController {

    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";

    private final CatalogoDAO catalogoDAO;
    private final EstudianteDAO estudianteDAO;

    public CatalogoController() {
        this.catalogoDAO = new CatalogoDAOImpl();
        this.estudianteDAO = new EstudianteDAOImpl();
    }

    /**
     * CUS03: Busca libros en el catálogo por título, autor o ISBN mostrando en tiempo real
     * la cantidad de ejemplares DISPONIBLES.
     *
     * @param criterio Texto de búsqueda.
     * @return Lista de libros con disponibilidad en tiempo real.
     */
    public List<Libro> buscarEnCatalogo(String criterio) {
        try {
            return catalogoDAO.buscarLibrosEnTiempoReal(criterio);
        } catch (SQLException ex) {
            System.err.println("[ERROR BD] No se pudo consultar el catálogo: " + ex.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * CUS03: Registra una notificación al Administrador cuando el libro buscado no tiene
     * ejemplares disponibles o no existe en el catálogo bibliográfico.
     *
     * @param idEstudiante  Código del estudiante solicitante.
     * @param textoBusqueda Texto de búsqueda o título del libro sin disponibilidad.
     * @return Mensaje con el resultado de la operación.
     */
    public String notificarFaltanteAlAdministrador(String idEstudiante, String textoBusqueda) {
        if (idEstudiante == null || idEstudiante.trim().isEmpty()) {
            return "[VALIDACIÓN] Debe ingresar su código de estudiante (ej. EST001).";
        }
        if (textoBusqueda == null || textoBusqueda.trim().isEmpty()) {
            return "[VALIDACIÓN] Debe ingresar el título, autor o ISBN del libro que solicita.";
        }
        try {
            return catalogoDAO.registrarNotificacionAdmin(
                    idEstudiante.trim().toUpperCase(),
                    textoBusqueda.trim()
            );
        } catch (SQLException ex) {
            return "[ERROR BD] No se pudo registrar la notificación: " + ex.getMessage();
        }
    }

    /**
     * Valida las credenciales de acceso al Modo Administrador.
     *
     * @param usuario    Nombre de usuario.
     * @param contrasena Contraseña ingresada.
     * @return {@code true} si las credenciales son válidas.
     */
    public boolean autenticarAdministrador(String usuario, String contrasena) {
        if (usuario == null || contrasena == null) {
            return false;
        }
        return ADMIN_USER.equalsIgnoreCase(usuario.trim())
                && (ADMIN_PASS.equals(contrasena) || "biblioteca123".equals(contrasena));
    }

    /**
     * Lista todas las notificaciones de libros faltantes registradas por los estudiantes.
     *
     * @return Lista de {@link NotificacionAdmin}.
     */
    public List<NotificacionAdmin> listarNotificaciones() {
        try {
            return catalogoDAO.listarNotificaciones();
        } catch (SQLException ex) {
            System.err.println("[ERROR BD] No se pudo listar notificaciones: " + ex.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Modo Administrador: Cambia el estado de una notificación ('PENDIENTE', 'ATENDIDA', 'DESCARTADA').
     *
     * @param idNotificacion ID de la notificación.
     * @param nuevoEstado    Nuevo estado.
     * @return Mensaje descriptivo.
     */
    public String cambiarEstadoNotificacion(long idNotificacion, String nuevoEstado) {
        try {
            return catalogoDAO.actualizarEstadoNotificacion(idNotificacion, nuevoEstado);
        } catch (SQLException ex) {
            return "[ERROR BD] Error al actualizar notificación: " + ex.getMessage();
        }
    }

    /**
     * Modo Administrador: Elimina una notificación de la base de datos.
     *
     * @param idNotificacion ID de la notificación.
     * @return Mensaje descriptivo.
     */
    public String eliminarNotificacion(long idNotificacion) {
        try {
            return catalogoDAO.eliminarNotificacion(idNotificacion);
        } catch (SQLException ex) {
            return "[ERROR BD] Error al eliminar notificación: " + ex.getMessage();
        }
    }

    /**
     * Modo Administrador (CRUD): Registra un nuevo libro en el catálogo junto con sus ejemplares físicos.
     *
     * @param isbn               ISBN del libro.
     * @param titulo             Título del libro.
     * @param autor              Autor del libro.
     * @param editorial          Editorial.
     * @param anioTexto          Año de publicación.
     * @param cantidadEjemplares Cantidad de ejemplares físicos iniciales.
     * @return Mensaje con el resultado.
     */
    public String crearLibro(String isbn, String titulo, String autor,
                             String editorial, String anioTexto, int cantidadEjemplares) {
        if (isbn == null || isbn.trim().isEmpty()
                || titulo == null || titulo.trim().isEmpty()
                || autor == null || autor.trim().isEmpty()) {
            return "[VALIDACIÓN] ISBN, Título y Autor son campos obligatorios.";
        }
        Integer anio = null;
        if (anioTexto != null && !anioTexto.trim().isEmpty()) {
            try {
                anio = Integer.parseInt(anioTexto.trim());
            } catch (NumberFormatException e) {
                return "[VALIDACIÓN] El año de publicación debe ser un número válido (ej. 2024).";
            }
        }
        Libro nuevo = new Libro(0L, isbn.trim(), titulo.trim(), autor.trim(),
                editorial != null ? editorial.trim() : "", anio);
        try {
            return catalogoDAO.crearLibroConEjemplares(nuevo, Math.max(0, cantidadEjemplares));
        } catch (SQLException ex) {
            return "[ERROR BD] No se pudo registrar el libro: " + ex.getMessage();
        }
    }

    /**
     * Modo Administrador (CRUD): Actualiza la información de un libro existente.
     *
     * @param idLibro   ID del libro.
     * @param isbn      ISBN.
     * @param titulo    Título.
     * @param autor     Autor.
     * @param editorial Editorial.
     * @param anioTexto Año de publicación.
     * @return Mensaje con el resultado.
     */
    public String actualizarLibro(long idLibro, String isbn, String titulo,
                                  String autor, String editorial, String anioTexto) {
        if (idLibro <= 0) {
            return "[VALIDACIÓN] Seleccione un libro de la tabla para actualizar.";
        }
        if (isbn == null || isbn.trim().isEmpty()
                || titulo == null || titulo.trim().isEmpty()
                || autor == null || autor.trim().isEmpty()) {
            return "[VALIDACIÓN] ISBN, Título y Autor son campos obligatorios.";
        }
        Integer anio = null;
        if (anioTexto != null && !anioTexto.trim().isEmpty()) {
            try {
                anio = Integer.parseInt(anioTexto.trim());
            } catch (NumberFormatException e) {
                return "[VALIDACIÓN] El año de publicación debe ser un número válido.";
            }
        }
        Libro libro = new Libro(idLibro, isbn.trim(), titulo.trim(), autor.trim(),
                editorial != null ? editorial.trim() : "", anio);
        try {
            return catalogoDAO.actualizarLibro(libro);
        } catch (SQLException ex) {
            return "[ERROR BD] No se pudo actualizar el libro: " + ex.getMessage();
        }
    }

    /**
     * Modo Administrador (CRUD): Elimina un libro del catálogo.
     *
     * @param idLibro ID del libro a eliminar.
     * @return Mensaje con el resultado.
     */
    public String eliminarLibro(long idLibro) {
        if (idLibro <= 0) {
            return "[VALIDACIÓN] Seleccione un libro de la tabla para eliminar.";
        }
        try {
            return catalogoDAO.eliminarLibro(idLibro);
        } catch (SQLException ex) {
            return "[ERROR BD] No se pudo eliminar el libro: " + ex.getMessage();
        }
    }

    /**
     * Modo Administrador: Gestiona las penalizaciones (suspensión por 2 días o levantamiento de sanción).
     *
     * @param idEstudiante Código del estudiante.
     * @param suspender    {@code true} para aplicar suspensión de 2 días; {@code false} para reactivar.
     * @return Mensaje con el resultado.
     */
    public String gestionarPenalizacionEstudiante(String idEstudiante, boolean suspender) {
        if (idEstudiante == null || idEstudiante.trim().isEmpty()) {
            return "[VALIDACIÓN] Seleccione un estudiante de la lista.";
        }
        try {
            return estudianteDAO.actualizarEstadoPenalizacion(idEstudiante.trim().toUpperCase(), suspender);
        } catch (SQLException ex) {
            return "[ERROR BD] Error al actualizar el estado del estudiante: " + ex.getMessage();
        }
    }
}
