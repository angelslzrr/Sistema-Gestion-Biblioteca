package sistemadegestionbiblioteca.controllers;

import java.sql.SQLException;
import sistemadegestionbiblioteca.daos.PrestamoDAO;
import sistemadegestionbiblioteca.daos.impl.PrestamoDAOImpl;

/**
 * Controlador MVC para el Caso de Uso CUS04 (Préstamo y Devolución Autónoma mediante QR/RFID).
 * Simula la lectura del hardware QR recibiendo un {@code String} con {@code idEstudiante} e {@code idEjemplar}
 * y procesa la transacción sin intervención humana bajo el canal {@code 'AUTOSERVICIO_QR'}.
 *
 * @author Grupo 6
 */
public class AutoservicioQRController {

    private final PrestamoDAO prestamoDAO;

    public AutoservicioQRController() {
        this.prestamoDAO = new PrestamoDAOImpl();
    }

    /**
     * CUS04: Procesa un préstamo autónomo a partir de una trama QR simulada en formato
     * {@code "ID_ESTUDIANTE|ID_EJEMPLAR"} (también acepta separadores {@code ;} o {@code ,}).
     *
     * @param qrPayload Cadena leída por el escáner QR (ej. {@code "EST001|EJ001"}).
     * @return Mensaje de confirmación o rechazo de la terminal de autoservicio.
     */
    public String procesarLecturaQRPrestamo(String qrPayload) {
        if (qrPayload == null || qrPayload.trim().isEmpty()) {
            return "[ERROR QR - CUS04] Trama QR vacía. Formato esperado: ID_ESTUDIANTE|ID_EJEMPLAR (ej. EST001|EJ001).";
        }
        String[] partes = qrPayload.trim().split("[|;,]");
        if (partes.length < 2) {
            return "[ERROR QR - CUS04] Formato de QR inválido ('" + qrPayload
                    + "'). Use el formato: ID_ESTUDIANTE|ID_EJEMPLAR (ej. EST001|EJ001).";
        }
        return procesarLecturaQRPrestamo(partes[0].trim(), partes[1].trim());
    }

    /**
     * CUS04: Sobrecarga que recibe los identificadores extraídos del código QR para registrar el préstamo autónomo.
     *
     * @param idEstudiante Identificador del estudiante leído del QR.
     * @param idEjemplar   Identificador del ejemplar leído del QR/RFID.
     * @return Mensaje del resultado transaccional.
     */
    public String procesarLecturaQRPrestamo(String idEstudiante, String idEjemplar) {
        if (idEstudiante == null || idEstudiante.trim().isEmpty()
                || idEjemplar == null || idEjemplar.trim().isEmpty()) {
            return "[ERROR QR - CUS04] Tanto idEstudiante como idEjemplar son obligatorios en la lectura QR.";
        }
        try {
            return "[TERMINAL AUTOSERVICIO QR - CUS04] " + prestamoDAO.registrarPrestamo(
                    idEstudiante.trim().toUpperCase(),
                    idEjemplar.trim().toUpperCase(),
                    "AUTOSERVICIO_QR");
        } catch (SQLException ex) {
            return "[ERROR BD QR - CUS04] Fallo en préstamo autónomo: " + ex.getMessage();
        }
    }

    /**
     * CUS04: Procesa una devolución autónoma mediante lectura QR. Acepta tanto {@code "ID_ESTUDIANTE|ID_EJEMPLAR"}
     * como directamente {@code "ID_EJEMPLAR"}.
     *
     * @param qrPayload   Trama leída por el lector QR/RFID.
     * @param reportaDano {@code true} si el sensor/usuario reporta daño en el ejemplar.
     * @return Mensaje del resultado transaccional (incluyendo sanción automática de 2 días si aplica).
     */
    public String procesarLecturaQRDevolucion(String qrPayload, boolean reportaDano) {
        if (qrPayload == null || qrPayload.trim().isEmpty()) {
            return "[ERROR QR - CUS04] Trama QR vacía para devolución.";
        }
        String[] partes = qrPayload.trim().split("[|;,]");
        String idEjemplar = (partes.length >= 2) ? partes[1].trim() : partes[0].trim();

        try {
            return "[TERMINAL AUTOSERVICIO QR - CUS04] " + prestamoDAO.registrarDevolucion(
                    idEjemplar.toUpperCase(),
                    reportaDano,
                    "AUTOSERVICIO_QR");
        } catch (SQLException ex) {
            return "[ERROR BD QR - CUS04] Fallo en devolución autónoma: " + ex.getMessage();
        }
    }
}
