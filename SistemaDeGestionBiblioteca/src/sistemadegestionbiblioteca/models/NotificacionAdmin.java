package sistemadegestionbiblioteca.models;

import java.sql.Timestamp;

/**
 * Entidad de dominio que representa la tabla NOTIFICACION_ADMIN en Oracle (CUS03).
 * Registra las peticiones de libros sin ejemplares disponibles o inexistentes en el catálogo.
 *
 * @author Grupo 6
 */
public class NotificacionAdmin {

    private long idNotificacion;
    private String idEstudiante;
    private String textoBusqueda;
    private Timestamp fechaSolicitud;
    private String estado; // 'PENDIENTE' | 'ATENDIDA' | 'DESCARTADA'

    public NotificacionAdmin() {
    }

    public NotificacionAdmin(long idNotificacion, String idEstudiante,
                             String textoBusqueda, Timestamp fechaSolicitud, String estado) {
        this.idNotificacion = idNotificacion;
        this.idEstudiante = idEstudiante;
        this.textoBusqueda = textoBusqueda;
        this.fechaSolicitud = fechaSolicitud;
        this.estado = estado;
    }

    public long getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(long idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public String getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(String idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public String getTextoBusqueda() {
        return textoBusqueda;
    }

    public void setTextoBusqueda(String textoBusqueda) {
        this.textoBusqueda = textoBusqueda;
    }

    public Timestamp getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(Timestamp fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "NotificacionAdmin{" +
                "idNotificacion=" + idNotificacion +
                ", idEstudiante='" + idEstudiante + '\'' +
                ", textoBusqueda='" + textoBusqueda + '\'' +
                ", fechaSolicitud=" + fechaSolicitud +
                ", estado='" + estado + '\'' +
                '}';
    }
}
