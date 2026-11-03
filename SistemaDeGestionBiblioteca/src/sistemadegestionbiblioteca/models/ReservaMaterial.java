package sistemadegestionbiblioteca.models;

import java.sql.Timestamp;

/**
 * Entidad de dominio que representa la tabla RESERVA_MATERIAL en Oracle (CUS03).
 *
 * @author Grupo 6
 */
public class ReservaMaterial {

    private long idReservaMat;
    private String idEstudiante;
    private String idEjemplar;
    private Timestamp fechaReserva;
    private Timestamp fechaLimiteRecojo;
    private String estadoReserva; // 'PENDIENTE' | 'COMPLETADA' | 'EXPIRADA' | 'CANCELADA'

    public ReservaMaterial() {
    }

    public ReservaMaterial(long idReservaMat, String idEstudiante, String idEjemplar,
                           Timestamp fechaReserva, Timestamp fechaLimiteRecojo, String estadoReserva) {
        this.idReservaMat = idReservaMat;
        this.idEstudiante = idEstudiante;
        this.idEjemplar = idEjemplar;
        this.fechaReserva = fechaReserva;
        this.fechaLimiteRecojo = fechaLimiteRecojo;
        this.estadoReserva = estadoReserva;
    }

    public long getIdReservaMat() {
        return idReservaMat;
    }

    public void setIdReservaMat(long idReservaMat) {
        this.idReservaMat = idReservaMat;
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

    public Timestamp getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(Timestamp fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public Timestamp getFechaLimiteRecojo() {
        return fechaLimiteRecojo;
    }

    public void setFechaLimiteRecojo(Timestamp fechaLimiteRecojo) {
        this.fechaLimiteRecojo = fechaLimiteRecojo;
    }

    public String getEstadoReserva() {
        return estadoReserva;
    }

    public void setEstadoReserva(String estadoReserva) {
        this.estadoReserva = estadoReserva;
    }

    @Override
    public String toString() {
        return "ReservaMaterial{" +
                "idReservaMat=" + idReservaMat +
                ", idEstudiante='" + idEstudiante + '\'' +
                ", idEjemplar='" + idEjemplar + '\'' +
                ", fechaReserva=" + fechaReserva +
                ", fechaLimiteRecojo=" + fechaLimiteRecojo +
                ", estadoReserva='" + estadoReserva + '\'' +
                '}';
    }
}
