package sistemadegestionbiblioteca.models;

import java.sql.Timestamp;

/**
 * Entidad de dominio que representa la tabla ESTUDIANTE en Oracle.
 *
 * @author Grupo 6
 */
public class Estudiante {

    private String idEstudiante;
    private String nombres;
    private String apellidos;
    private String correo;
    private String estadoEstudiante; // 'ACTIVO' | 'SUSPENDIDO'
    private Timestamp fechaFinSuspension;

    public Estudiante() {
    }

    public Estudiante(String idEstudiante, String nombres, String apellidos,
                      String correo, String estadoEstudiante, Timestamp fechaFinSuspension) {
        this.idEstudiante = idEstudiante;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.estadoEstudiante = estadoEstudiante;
        this.fechaFinSuspension = fechaFinSuspension;
    }

    public String getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(String idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getEstadoEstudiante() {
        return estadoEstudiante;
    }

    public void setEstadoEstudiante(String estadoEstudiante) {
        this.estadoEstudiante = estadoEstudiante;
    }

    public Timestamp getFechaFinSuspension() {
        return fechaFinSuspension;
    }

    public void setFechaFinSuspension(Timestamp fechaFinSuspension) {
        this.fechaFinSuspension = fechaFinSuspension;
    }

    public String getNombreCompleto() {
        return (nombres != null ? nombres : "") + " " + (apellidos != null ? apellidos : "");
    }

    @Override
    public String toString() {
        return "Estudiante{" +
                "idEstudiante='" + idEstudiante + '\'' +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", correo='" + correo + '\'' +
                ", estadoEstudiante='" + estadoEstudiante + '\'' +
                ", fechaFinSuspension=" + fechaFinSuspension +
                '}';
    }
}
