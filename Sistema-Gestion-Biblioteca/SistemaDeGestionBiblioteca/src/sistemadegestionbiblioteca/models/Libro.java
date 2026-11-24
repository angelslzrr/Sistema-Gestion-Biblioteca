package sistemadegestionbiblioteca.models;

/**
 * Entidad de dominio que representa la tabla LIBRO en Oracle (CUS03 y Modo Administrador).
 * Incluye atributos calculados de stock total y ejemplares disponibles en tiempo real.
 *
 * @author Grupo 6
 */
public class Libro {

    private long idLibro;
    private String isbn;
    private String titulo;
    private String autor;
    private String editorial;
    private Integer anioPublicacion;
    private int totalEjemplares;
    private int ejemplaresDisponibles;

    public Libro() {
    }

    public Libro(long idLibro, String isbn, String titulo, String autor,
                 String editorial, Integer anioPublicacion) {
        this(idLibro, isbn, titulo, autor, editorial, anioPublicacion, 0, 0);
    }

    public Libro(long idLibro, String isbn, String titulo, String autor,
                 String editorial, Integer anioPublicacion,
                 int totalEjemplares, int ejemplaresDisponibles) {
        this.idLibro = idLibro;
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.anioPublicacion = anioPublicacion;
        this.totalEjemplares = totalEjemplares;
        this.ejemplaresDisponibles = ejemplaresDisponibles;
    }

    public long getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(long idLibro) {
        this.idLibro = idLibro;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public Integer getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(Integer anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public int getTotalEjemplares() {
        return totalEjemplares;
    }

    public void setTotalEjemplares(int totalEjemplares) {
        this.totalEjemplares = totalEjemplares;
    }

    public int getEjemplaresDisponibles() {
        return ejemplaresDisponibles;
    }

    public void setEjemplaresDisponibles(int ejemplaresDisponibles) {
        this.ejemplaresDisponibles = ejemplaresDisponibles;
    }

    @Override
    public String toString() {
        return "Libro{" +
                "idLibro=" + idLibro +
                ", isbn='" + isbn + '\'' +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", editorial='" + editorial + '\'' +
                ", anioPublicacion=" + anioPublicacion +
                ", totalEjemplares=" + totalEjemplares +
                ", ejemplaresDisponibles=" + ejemplaresDisponibles +
                '}';
    }
}
