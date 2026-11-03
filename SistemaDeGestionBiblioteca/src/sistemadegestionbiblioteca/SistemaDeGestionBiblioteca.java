package sistemadegestionbiblioteca;

import java.awt.GraphicsEnvironment;
import java.util.Scanner;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import sistemadegestionbiblioteca.controllers.AutoservicioQRController;
import sistemadegestionbiblioteca.controllers.CubiculoController;
import sistemadegestionbiblioteca.controllers.PrestamoController;
import sistemadegestionbiblioteca.controllers.ReservaMaterialController;
import sistemadegestionbiblioteca.views.VentanaPrincipalBiblioteca;

/**
 * Clase principal del Sistema de Gestión de Biblioteca.
 * Configura el estilo visual nativo del sistema operativo e inicia la ventana principal.
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
     * Menú interactivo de respaldo en consola con lenguaje orientado al usuario.
     */
    private static void ejecutarMenuConsola() {
        PrestamoController prestamoController = new PrestamoController();
        ReservaMaterialController reservaMaterialController = new ReservaMaterialController();
        AutoservicioQRController autoservicioQRController = new AutoservicioQRController();
        CubiculoController cubiculoController = new CubiculoController();

        try (Scanner sc = new Scanner(System.in)) {
            boolean salir = false;
            while (!salir) {
                System.out.println("\n==============================================================");
                System.out.println("              SISTEMA DE GESTIÓN DE BIBLIOTECA                ");
                System.out.println("==============================================================");
                System.out.println("1. Registrar Nuevo Préstamo");
                System.out.println("2. Procesar Devolución de Libro");
                System.out.println("3. Reservar Material Bibliográfico");
                System.out.println("4. Autoservicio QR - Solicitar Préstamo");
                System.out.println("5. Autoservicio QR - Devolver Libro");
                System.out.println("6. Reserva de Cubículos");
                System.out.println("7. Finalizar Uso de Cubículo");
                System.out.println("0. Salir");
                System.out.print("Seleccione una opción: ");

                String opcion = sc.nextLine().trim();
                switch (opcion) {
                    case "1":
                        System.out.print("Código del Estudiante: ");
                        String estP = sc.nextLine();
                        System.out.print("Código del Libro: ");
                        String ejP = sc.nextLine();
                        System.out.println(prestamoController.procesarPrestamo(estP, ejP));
                        break;
                    case "2":
                        System.out.print("Código del Libro a devolver: ");
                        String ejD = sc.nextLine();
                        System.out.print("¿El libro presenta daño físico? (S/N): ");
                        boolean danado = "S".equalsIgnoreCase(sc.nextLine().trim());
                        System.out.println(prestamoController.procesarDevolucion(ejD, danado));
                        break;
                    case "3":
                        System.out.print("Código del Estudiante: ");
                        String estR = sc.nextLine();
                        System.out.print("Código del Libro: ");
                        String ejR = sc.nextLine();
                        System.out.println(reservaMaterialController.reservarMaterial(estR, ejR));
                        break;
                    case "4":
                        System.out.print("Escanee su carnet y luego el libro (ESTUDIANTE|LIBRO): ");
                        String qrP = sc.nextLine();
                        System.out.println(autoservicioQRController.procesarLecturaQRPrestamo(qrP));
                        break;
                    case "5":
                        System.out.print("Escanee su carnet y luego el libro (ESTUDIANTE|LIBRO): ");
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
