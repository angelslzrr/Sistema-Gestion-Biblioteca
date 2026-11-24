package sistemadegestionbiblioteca.models;

/**
 * Entidad de dominio que representa la tabla EJEMPLAR en Oracle.
 *
 * @author Grupo 6
 */
public class Ejemplar {

    private String idEjemplar;
    private long idLibro;
    private String codigoInventario;
    private String estadoEjemplar; // 'DISPONIBLE' | 'PRESTADO' | 'RESERVADO' | 'MANTENIMIENTO'
    private String estadoFisico;   // 'BUENO' | 'DANADO'

    public Ejemplar() {
    }

    public Ejemplar(String idEjemplar, long idLibro, String codigoInventario,
                    String estadoEjemplar, String estadoFisico) {
        this.idEjemplar = idEjemplar;
        this.idLibro = idLibro;
        this.codigoInventario = codigoInventario;
        this.estadoEjemplar = estadoEjemplar;
        this.estadoFisico = estadoFisico;
    }

    public String getIdEjemplar() {
        return idEjemplar;
    }

    public void setIdEjemplar(String idEjemplar) {
        this.idEjemplar = idEjemplar;
    }

    public long getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(long idLibro) {
        this.idLibro = idLibro;
    }

    public String getCodigoInventario() {
        return codigoInventario;
    }

    public void setCodigoInventario(String codigoInventario) {
        this.codigoInventario = codigoInventario;
    }

    public String getEstadoEjemplar() {
        return estadoEjemplar;
    }

    public void setEstadoEjemplar(String estadoEjemplar) {
        this.estadoEjemplar = estadoEjemplar;
    }

    public String getEstadoFisico() {
        return estadoFisico;
    }

    public void setEstadoFisico(String estadoFisico) {
        this.estadoFisico = estadoFisico;
    }

    @Override
    public String toString() {
        return "Ejemplar{" +
                "idEjemplar='" + idEjemplar + '\'' +
                ", idLibro=" + idLibro +
                ", codigoInventario='" + codigoInventario + '\'' +
                ", estadoEjemplar='" + estadoEjemplar + '\'' +
                ", estadoFisico='" + estadoFisico + '\'' +
                '}';
    }
}
