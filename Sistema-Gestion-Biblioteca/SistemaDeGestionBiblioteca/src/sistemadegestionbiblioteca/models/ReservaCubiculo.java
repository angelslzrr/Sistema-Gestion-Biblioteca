package sistemadegestionbiblioteca.models;

import java.sql.Timestamp;

/**
 * Entidad de dominio que representa la tabla RESERVA_CUBICULO en Oracle (CUS05).
 *
 * @author Grupo 6
 */
public class ReservaCubiculo {

    private long idReservaCub;
    private String idCubiculo;
    private String idEstudiante;
    private Timestamp fechaHoraInicio;
    private Timestamp fechaHoraFin;
    private String estadoReserva; // 'ACTIVA' | 'FINALIZADA' | 'CANCELADA'

    public ReservaCubiculo() {
    }

    public ReservaCubiculo(long idReservaCub, String idCubiculo, String idEstudiante,
                           Timestamp fechaHoraInicio, Timestamp fechaHoraFin, String estadoReserva) {
        this.idReservaCub = idReservaCub;
        this.idCubiculo = idCubiculo;
        this.idEstudiante = idEstudiante;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.estadoReserva = estadoReserva;
    }

    public long getIdReservaCub() {
        return idReservaCub;
    }

    public void setIdReservaCub(long idReservaCub) {
        this.idReservaCub = idReservaCub;
    }

    public String getIdCubiculo() {
        return idCubiculo;
    }

    public void setIdCubiculo(String idCubiculo) {
        this.idCubiculo = idCubiculo;
    }

    public String getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(String idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public Timestamp getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(Timestamp fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public Timestamp getFechaHoraFin() {
        return fechaHoraFin;
    }

    public void setFechaHoraFin(Timestamp fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public String getEstadoReserva() {
        return estadoReserva;
    }

    public void setEstadoReserva(String estadoReserva) {
        this.estadoReserva = estadoReserva;
    }

    @Override
    public String toString() {
        return "ReservaCubiculo{" +
                "idReservaCub=" + idReservaCub +
                ", idCubiculo='" + idCubiculo + '\'' +
                ", idEstudiante='" + idEstudiante + '\'' +
                ", fechaHoraInicio=" + fechaHoraInicio +
                ", fechaHoraFin=" + fechaHoraFin +
                ", estadoReserva='" + estadoReserva + '\'' +
                '}';
    }
}
