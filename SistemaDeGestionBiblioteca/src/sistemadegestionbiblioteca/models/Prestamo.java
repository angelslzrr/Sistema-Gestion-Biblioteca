package sistemadegestionbiblioteca.models;

import java.sql.Timestamp;

/**
 * Entidad de dominio que representa la tabla PRESTAMO en Oracle (CUS01, CUS02, CUS04).
 *
 * @author Grupo 6
 */
public class Prestamo {

    private long idPrestamo;
    private String idEstudiante;
    private String idEjemplar;
    private Timestamp fechaPrestamo;
    private Timestamp fechaDevolucionEsperada;
    private Timestamp fechaDevolucionReal;
    private String canal;          // 'PRESENCIAL' | 'AUTOSERVICIO_QR'
    private String devueltoDanado; // 'S' | 'N'
    private String estadoPrestamo; // 'ACTIVO' | 'DEVUELTO' | 'VENCIDO'

    public Prestamo() {
    }

    public Prestamo(long idPrestamo, String idEstudiante, String idEjemplar,
                    Timestamp fechaPrestamo, Timestamp fechaDevolucionEsperada,
                    Timestamp fechaDevolucionReal, String canal,
                    String devueltoDanado, String estadoPrestamo) {
        this.idPrestamo = idPrestamo;
        this.idEstudiante = idEstudiante;
        this.idEjemplar = idEjemplar;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        this.fechaDevolucionReal = fechaDevolucionReal;
        this.canal = canal;
        this.devueltoDanado = devueltoDanado;
        this.estadoPrestamo = estadoPrestamo;
    }

    public long getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(long idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public String getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(String idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public String getIdEjemplar() {
        return idEjemplar;
    }

    public void setIdEjemplar(String idEjemplar) {
        this.idEjemplar = idEjemplar;
    }

    public Timestamp getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(Timestamp fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public Timestamp getFechaDevolucionEsperada() {
        return fechaDevolucionEsperada;
    }

    public void setFechaDevolucionEsperada(Timestamp fechaDevolucionEsperada) {
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
    }

    public Timestamp getFechaDevolucionReal() {
        return fechaDevolucionReal;
    }

    public void setFechaDevolucionReal(Timestamp fechaDevolucionReal) {
        this.fechaDevolucionReal = fechaDevolucionReal;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getDevueltoDanado() {
        return devueltoDanado;
    }

    public void setDevueltoDanado(String devueltoDanado) {
        this.devueltoDanado = devueltoDanado;
    }

    public String getEstadoPrestamo() {
        return estadoPrestamo;
    }

    public void setEstadoPrestamo(String estadoPrestamo) {
        this.estadoPrestamo = estadoPrestamo;
    }

    @Override
    public String toString() {
        return "Prestamo{" +
                "idPrestamo=" + idPrestamo +
                ", idEstudiante='" + idEstudiante + '\'' +
                ", idEjemplar='" + idEjemplar + '\'' +
                ", fechaPrestamo=" + fechaPrestamo +
                ", fechaDevolucionEsperada=" + fechaDevolucionEsperada +
                ", fechaDevolucionReal=" + fechaDevolucionReal +
                ", canal='" + canal + '\'' +
                ", devueltoDanado='" + devueltoDanado + '\'' +
                ", estadoPrestamo='" + estadoPrestamo + '\'' +
                '}';
    }
}
