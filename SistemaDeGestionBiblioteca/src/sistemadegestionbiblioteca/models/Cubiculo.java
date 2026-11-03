package sistemadegestionbiblioteca.models;

/**
 * Entidad de dominio que representa la tabla CUBICULO en Oracle (CUS05).
 *
 * @author Grupo 6
 */
public class Cubiculo {

    private String idCubiculo;
    private String nombreCodigo;
    private int capacidad;
    private String estadoCubiculo; // 'DISPONIBLE' | 'RESERVADO' | 'MANTENIMIENTO'

    public Cubiculo() {
    }

    public Cubiculo(String idCubiculo, String nombreCodigo, int capacidad, String estadoCubiculo) {
        this.idCubiculo = idCubiculo;
        this.nombreCodigo = nombreCodigo;
        this.capacidad = capacidad;
        this.estadoCubiculo = estadoCubiculo;
    }

    public String getIdCubiculo() {
        return idCubiculo;
    }

    public void setIdCubiculo(String idCubiculo) {
        this.idCubiculo = idCubiculo;
    }

    public String getNombreCodigo() {
        return nombreCodigo;
    }

    public void setNombreCodigo(String nombreCodigo) {
        this.nombreCodigo = nombreCodigo;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getEstadoCubiculo() {
        return estadoCubiculo;
    }

    public void setEstadoCubiculo(String estadoCubiculo) {
        this.estadoCubiculo = estadoCubiculo;
    }

    @Override
    public String toString() {
        return "Cubiculo{" +
                "idCubiculo='" + idCubiculo + '\'' +
                ", nombreCodigo='" + nombreCodigo + '\'' +
                ", capacidad=" + capacidad +
                ", estadoCubiculo='" + estadoCubiculo + '\'' +
                '}';
    }
}
