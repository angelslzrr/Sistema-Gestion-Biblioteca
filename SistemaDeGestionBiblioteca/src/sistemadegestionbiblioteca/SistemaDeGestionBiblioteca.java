package sistemadegestionbiblioteca;

import java.awt.GraphicsEnvironment;
import java.util.List;
import java.util.Scanner;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import sistemadegestionbiblioteca.controllers.AutoservicioQRController;
import sistemadegestionbiblioteca.controllers.CatalogoController;
import sistemadegestionbiblioteca.controllers.CubiculoController;
import sistemadegestionbiblioteca.controllers.PrestamoController;
import sistemadegestionbiblioteca.models.Libro;
import sistemadegestionbiblioteca.views.VentanaPrincipalBiblioteca;

/**
 * Clase principal del Sistema de Gestión de Biblioteca (UNMSM).
 * Inicia el Dashboard principal en Java Swing o el modo consola de respaldo.
 *
 * @author Angel Salazar / Grupo 6
 */
public class SistemaDeGestionBiblioteca {

    /**
     * Punto de entrada principal de la aplicación.
     *
     * @param args Argumentos de línea de comandos (opcional: {@code --consola}).
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        boolean modoConsola = false;
        if (args != null) {
            for (String arg : args) {
                if ("--consola".equalsIgnoreCase(arg) || "-c".equalsIgnoreCase(arg)) {
                    modoConsola = true;
                    break;
                }
            }
        }

        if (modoConsola || GraphicsEnvironment.isHeadless()) {
            ejecutarMenuConsola();
        } else {
            SwingUtilities.invokeLater(() -> {
                VentanaPrincipalBiblioteca ventana = new VentanaPrincipalBiblioteca();
                ventana.setVisible(true);
            });
        }
    }

    /**
     * Menú interactivo de respaldo en consola alineado con los casos de uso del sistema.
     */
    private static void ejecutarMenuConsola() {
        PrestamoController prestamoController = new PrestamoController();
        CatalogoController catalogoController = new CatalogoController();
        AutoservicioQRController autoservicioQRController = new AutoservicioQRController();
        CubiculoController cubiculoController = new CubiculoController();

        try (Scanner sc = new Scanner(System.in)) {
            boolean salir = false;
            while (!salir) {
                System.out.println("\n==============================================================");
                System.out.println("          SISTEMA DE GESTIÓN DE BIBLIOTECA - UNMSM            ");
                System.out.println("==============================================================");
                System.out.println("1. Préstamo de Libros");
                System.out.println("2. Devolución de Libros");
                System.out.println("3. Buscar en Catálogo y Notificar al Administrador");
                System.out.println("4. Autoservicio QR - Solicitar Préstamo");
                System.out.println("5. Autoservicio QR - Devolver Libro");
                System.out.println("6. Reservar Cubículo");
                System.out.println("7. Finalizar Uso de Cubículo");
                System.out.println("0. Salir");
                System.out.print("Seleccione una opción: ");

                String opcion = sc.nextLine().trim();
                switch (opcion) {
                    case "1":
                        System.out.print("Código del Estudiante: ");
                        String estP = sc.nextLine();
                        System.out.print("Código del Ejemplar: ");
                        String ejP = sc.nextLine();
                        System.out.println(prestamoController.procesarPrestamo(estP, ejP));
                        break;
                    case "2":
                        System.out.print("Código del Ejemplar a devolver: ");
                        String ejD = sc.nextLine();
                        System.out.print("¿El libro presenta daño físico? (S/N): ");
                        boolean danado = "S".equalsIgnoreCase(sc.nextLine().trim());
                        System.out.println(prestamoController.procesarDevolucion(ejD, danado));
                        break;
                    case "3":
                        System.out.print("Ingrese título, autor o ISBN a buscar: ");
                        String filtro = sc.nextLine();
                        List<Libro> encontrados = catalogoController.buscarEnCatalogo(filtro);
                        if (encontrados.isEmpty()) {
                            System.out.println("No se encontraron libros que coincidan con '" + filtro + "'.");
                        } else {
                            for (Libro l : encontrados) {
                                System.out.println(" - [" + l.getIsbn() + "] " + l.getTitulo() + " (" + l.getAutor()
                                        + ") | Disponibles: " + l.getEjemplaresDisponibles() + "/" + l.getTotalEjemplares());
                            }
                        }
                        System.out.print("¿Desea notificar faltante al Administrador? (S/N): ");
                        if ("S".equalsIgnoreCase(sc.nextLine().trim())) {
                            System.out.print("Su código de Estudiante: ");
                            String estNotif = sc.nextLine();
                            System.out.println(catalogoController.notificarFaltanteAlAdministrador(estNotif, filtro));
                        }
                        break;
                    case "4":
                        System.out.print("Escanee su carnet y luego el libro (ESTUDIANTE|EJEMPLAR): ");
                        String qrP = sc.nextLine();
                        System.out.println(autoservicioQRController.procesarLecturaQRPrestamo(qrP));
                        break;
                    case "5":
                        System.out.print("Escanee su carnet y luego el libro (ESTUDIANTE|EJEMPLAR): ");
                        String qrD = sc.nextLine();
                        System.out.print("¿Reportar daño físico en el libro? (S/N): ");
                        boolean danoQR = "S".equalsIgnoreCase(sc.nextLine().trim());
                        System.out.println(autoservicioQRController.procesarLecturaQRDevolucion(qrD, danoQR));
                        break;
                    case "6":
                        System.out.print("Código del Cubículo: ");
                        String idCub = sc.nextLine();
                        System.out.print("Código del Estudiante: ");
                        String estCub = sc.nextLine();
                        System.out.print("Tiempo de uso en horas (1 o 2): ");
                        int horas = Integer.parseInt(sc.nextLine().trim());
                        System.out.println(cubiculoController.reservarCubiculo(idCub, estCub, horas));
                        break;
                    case "7":
                        System.out.print("Código del Cubículo a liberar: ");
                        String idLib = sc.nextLine();
                        System.out.println(cubiculoController.liberarCubiculo(idLib));
                        break;
                    case "0":
                        salir = true;
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }
            }
        }
    }
}
