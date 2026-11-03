package sistemadegestionbiblioteca.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import sistemadegestionbiblioteca.controllers.AutoservicioQRController;
import sistemadegestionbiblioteca.controllers.CubiculoController;
import sistemadegestionbiblioteca.controllers.PrestamoController;
import sistemadegestionbiblioteca.controllers.ReservaMaterialController;
import sistemadegestionbiblioteca.models.Cubiculo;
import sistemadegestionbiblioteca.models.Ejemplar;
import sistemadegestionbiblioteca.models.Estudiante;
import sistemadegestionbiblioteca.models.Prestamo;
import sistemadegestionbiblioteca.models.ReservaCubiculo;
import sistemadegestionbiblioteca.models.ReservaMaterial;

/**
 * Ventana principal del Sistema de Gestión de Biblioteca con diseño centrado en el usuario,
 * libre de jerga técnica y adaptado al estilo visual nativo del sistema operativo.
 *
 * @author Grupo 6
 */
public class VentanaPrincipalBiblioteca extends JFrame {

    private static final Color COLOR_PRIMARIO = new Color(30, 64, 110);
    private static final Color COLOR_FONDO_SUAVE = new Color(248, 250, 252);
    private static final Font FUENTE_TITULO_VENTANA = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font FUENTE_SECCION = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FUENTE_BOTON_ACCION = new Font("Segoe UI", Font.BOLD, 13);

    private final PrestamoController prestamoController;
    private final ReservaMaterialController reservaMaterialController;
    private final AutoservicioQRController autoservicioQRController;
    private final CubiculoController cubiculoController;

    // Modelos de tablas
    private DefaultTableModel modeloPrestamosActivos;
    private DefaultTableModel modeloEjemplares;
    private DefaultTableModel modeloEstudiantes;
    private DefaultTableModel modeloReservasMaterial;
    private DefaultTableModel modeloCubiculos;
    private DefaultTableModel modeloReservasCubiculo;

    public VentanaPrincipalBiblioteca() {
        aplicarLookAndFeelNativo();
        this.prestamoController = new PrestamoController();
        this.reservaMaterialController = new ReservaMaterialController();
        this.autoservicioQRController = new AutoservicioQRController();
        this.cubiculoController = new CubiculoController();

        configurarVentana();
        inicializarComponentes();
        refrescarTablas();
    }

    private void aplicarLookAndFeelNativo() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    private void configurarVentana() {
        setTitle("Sistema de Gestión de Biblioteca");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1060, 680);
        setMinimumSize(new Dimension(920, 580));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));
    }

    private void inicializarComponentes() {
        // Cabecera limpia y amigable
        JPanel panelCabecera = new JPanel(new BorderLayout());
        panelCabecera.setBackground(COLOR_PRIMARIO);
        panelCabecera.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel lblTitulo = new JLabel("Sistema de Gestión de Biblioteca", SwingConstants.LEFT);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(FUENTE_TITULO_VENTANA);

        JButton btnActualizar = new JButton("Actualizar Catálogo");
        btnActualizar.setFont(FUENTE_NORMAL);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizar.addActionListener(e -> {
            refrescarTablas();
            JOptionPane.showMessageDialog(
                    this,
                    "La información del catálogo y las reservas se ha actualizado correctamente.",
                    "Catálogo Actualizado",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        panelCabecera.add(lblTitulo, BorderLayout.WEST);
        panelCabecera.add(btnActualizar, BorderLayout.EAST);
        add(panelCabecera, BorderLayout.NORTH);

        // Contenedor central con márgenes respirables
        JPanel contenedorPrincipal = new JPanel(new BorderLayout());
        contenedorPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FUENTE_SECCION);

        tabbedPane.addTab("Registrar Nuevo Préstamo", crearPanelPrestamo());
        tabbedPane.addTab("Devolución de Libros", crearPanelDevolucion());
        tabbedPane.addTab("Reserva de Material", crearPanelReservaMaterial());
        tabbedPane.addTab("Autoservicio QR", crearPanelAutoservicioKiosco());
        tabbedPane.addTab("Reserva de Cubículos", crearPanelReservaCubiculos());

        contenedorPrincipal.add(tabbedPane, BorderLayout.CENTER);
        add(contenedorPrincipal, BorderLayout.CENTER);
    }

    /**
     * Panel 1: Registrar Nuevo Préstamo.
     */
    private JPanel crearPanelPrestamo() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel cardFormulario = new JPanel(new BorderLayout(10, 10));
        cardFormulario.setBorder(crearBordeSeccion("Registrar Nuevo Préstamo"));

        JPanel camposPanel = new JPanel(new GridBagLayout());
        camposPanel.setBorder(new EmptyBorder(8, 12, 8, 12));
        GridBagConstraints gbc = crearGbcBase();

        JTextField txtIdEstudiante = crearCampoTexto("EST001", 16);
        JTextField txtIdEjemplar = crearCampoTexto("EJM-001", 16);

        gbc.gridx = 0;
        gbc.gridy = 0;
        camposPanel.add(crearEtiqueta("Código del Estudiante:"), gbc);
        gbc.gridx = 1;
        camposPanel.add(txtIdEstudiante, gbc);

        gbc.gridx = 2;
        camposPanel.add(Box.createHorizontalStrut(15), gbc);

        gbc.gridx = 3;
        camposPanel.add(crearEtiqueta("Código del Libro (Ejemplar):"), gbc);
        gbc.gridx = 4;
        camposPanel.add(txtIdEjemplar, gbc);

        JButton btnRegistrarPrestamo = crearBotonPrincipal("Registrar Nuevo Préstamo");
        JPanel panelAccion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 6));
        panelAccion.add(btnRegistrarPrestamo);

        cardFormulario.add(camposPanel, BorderLayout.WEST);
        cardFormulario.add(panelAccion, BorderLayout.EAST);

        btnRegistrarPrestamo.addActionListener(e -> {
            String respuesta = prestamoController.procesarPrestamo(
                    txtIdEstudiante.getText(),
                    txtIdEjemplar.getText()
            );
            mostrarDialogoResultado("Registro de Préstamo", respuesta);
            refrescarTablas();
        });

        panel.add(cardFormulario, BorderLayout.NORTH);

        // Tablas de consulta compactas y proporcionadas
        modeloEjemplares = crearModeloNoEditable(
                new Object[]{"Código Ejemplar", "Libro", "N.º Inventario", "Disponibilidad", "Condición"}
        );
        JTable tablaEjemplares = crearTablaEstilizada(modeloEjemplares, new int[]{110, 60, 110, 110, 90});

        modeloEstudiantes = crearModeloNoEditable(
                new Object[]{"Código", "Estudiante", "Correo Electrónico", "Estado", "Fin de Sanción"}
        );
        JTable tablaEstudiantes = crearTablaEstilizada(modeloEstudiantes, new int[]{80, 160, 180, 95, 110});

        // Selección rápida al hacer clic en las tablas
        tablaEjemplares.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaEjemplares.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0) {
                txtIdEjemplar.setText(String.valueOf(modeloEjemplares.getValueAt(fila, 0)));
            }
        });

        tablaEstudiantes.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaEstudiantes.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0) {
                txtIdEstudiante.setText(String.valueOf(modeloEstudiantes.getValueAt(fila, 0)));
            }
        });

        JPanel panelIzq = new JPanel(new BorderLayout());
        panelIzq.setBorder(crearBordeSeccion("Catálogo de Ejemplares"));
        panelIzq.add(new JScrollPane(tablaEjemplares), BorderLayout.CENTER);

        JPanel panelDer = new JPanel(new BorderLayout());
        panelDer.setBorder(crearBordeSeccion("Directorio de Estudiantes"));
        panelDer.add(new JScrollPane(tablaEstudiantes), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzq, panelDer);
        split.setResizeWeight(0.48);
        split.setBorder(null);
        panel.add(split, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Panel 2: Devolución de Libros.
     */
    private JPanel crearPanelDevolucion() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel cardFormulario = new JPanel(new BorderLayout(10, 10));
        cardFormulario.setBorder(crearBordeSeccion("Recepción y Devolución de Libros"));

        JPanel camposPanel = new JPanel(new GridBagLayout());
        camposPanel.setBorder(new EmptyBorder(8, 12, 8, 12));
        GridBagConstraints gbc = crearGbcBase();

        JTextField txtIdEjemplarDev = crearCampoTexto("EJM-002", 16);
        JCheckBox chkDanado = new JCheckBox("El libro presenta deterioro o daño físico");
        chkDanado.setFont(FUENTE_NORMAL);

        gbc.gridx = 0;
        gbc.gridy = 0;
        camposPanel.add(crearEtiqueta("Código del Libro a Devolver:"), gbc);
        gbc.gridx = 1;
        camposPanel.add(txtIdEjemplarDev, gbc);
        gbc.gridx = 2;
        camposPanel.add(Box.createHorizontalStrut(15), gbc);
        gbc.gridx = 3;
        camposPanel.add(chkDanado, gbc);

        JButton btnDevolver = crearBotonPrincipal("Procesar Devolución");
        JPanel panelAccion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 6));
        panelAccion.add(btnDevolver);

        cardFormulario.add(camposPanel, BorderLayout.WEST);
        cardFormulario.add(panelAccion, BorderLayout.EAST);

        btnDevolver.addActionListener(e -> {
            String respuesta = prestamoController.procesarDevolucion(
                    txtIdEjemplarDev.getText(),
                    chkDanado.isSelected()
            );
            mostrarDialogoResultado("Devolución de Libro", respuesta);
            chkDanado.setSelected(false);
            refrescarTablas();
        });

        panel.add(cardFormulario, BorderLayout.NORTH);

        modeloPrestamosActivos = crearModeloNoEditable(
                new Object[]{"N.º Préstamo", "Estudiante", "Código Libro", "Fecha de Préstamo", "Fecha Límite de Entrega", "Modalidad", "Estado"}
        );
        JTable tablaPrestamos = crearTablaEstilizada(modeloPrestamosActivos, new int[]{85, 100, 110, 130, 145, 120, 90});

        tablaPrestamos.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaPrestamos.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0) {
                txtIdEjemplarDev.setText(String.valueOf(modeloPrestamosActivos.getValueAt(fila, 2)));
            }
        });

        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(crearBordeSeccion("Préstamos en Curso (Seleccione una fila para autocompletar)"));
        panelTabla.add(new JScrollPane(tablaPrestamos), BorderLayout.CENTER);

        panel.add(panelTabla, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Panel 3: Reserva de Material Bibliográfico.
     */
    private JPanel crearPanelReservaMaterial() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel cardFormulario = new JPanel(new BorderLayout(10, 10));
        cardFormulario.setBorder(crearBordeSeccion("Reservar Material Bibliográfico"));

        JPanel camposPanel = new JPanel(new GridBagLayout());
        camposPanel.setBorder(new EmptyBorder(8, 12, 8, 12));
        GridBagConstraints gbc = crearGbcBase();

        JTextField txtEstReserva = crearCampoTexto("EST002", 15);
        JTextField txtEjReserva = crearCampoTexto("EJM-003", 15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        camposPanel.add(crearEtiqueta("Código del Estudiante:"), gbc);
        gbc.gridx = 1;
        camposPanel.add(txtEstReserva, gbc);
        gbc.gridx = 2;
        camposPanel.add(Box.createHorizontalStrut(15), gbc);
        gbc.gridx = 3;
        camposPanel.add(crearEtiqueta("Código del Libro:"), gbc);
        gbc.gridx = 4;
        camposPanel.add(txtEjReserva, gbc);

        JButton btnReservarMat = crearBotonPrincipal("Reservar Material");
        JButton btnLimpiarVencidas = crearBotonSecundario("Liberar Reservas No Recogidas");

        JPanel panelAccion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        panelAccion.add(btnLimpiarVencidas);
        panelAccion.add(btnReservarMat);

        cardFormulario.add(camposPanel, BorderLayout.WEST);
        cardFormulario.add(panelAccion, BorderLayout.EAST);

        btnReservarMat.addActionListener(e -> {
            String respuesta = reservaMaterialController.reservarMaterial(
                    txtEstReserva.getText(),
                    txtEjReserva.getText()
            );
            mostrarDialogoResultado("Reserva de Material", respuesta);
            refrescarTablas();
        });

        btnLimpiarVencidas.addActionListener(e -> {
            String respuesta = reservaMaterialController.depurarReservasVencidas();
            mostrarDialogoResultado("Actualización de Reservas", respuesta);
            refrescarTablas();
        });

        panel.add(cardFormulario, BorderLayout.NORTH);

        modeloReservasMaterial = crearModeloNoEditable(
                new Object[]{"N.º Reserva", "Estudiante", "Código Libro", "Fecha de Solicitud", "Disponible para Recojo Hasta", "Estado"}
        );
        JTable tablaReservas = crearTablaEstilizada(modeloReservasMaterial, new int[]{90, 110, 110, 145, 165, 100});

        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(crearBordeSeccion("Reservas Pendientes de Recojo"));
        panelTabla.add(new JScrollPane(tablaReservas), BorderLayout.CENTER);

        panel.add(panelTabla, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Panel 4: Módulo de Autoservicio QR con diseño de Quiosco Interactivo.
     */
    private JPanel crearPanelAutoservicioKiosco() {
        JPanel panelExterior = new JPanel(new GridBagLayout());
        panelExterior.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelExterior.setBackground(COLOR_FONDO_SUAVE);

        JPanel tarjetaKiosco = new JPanel();
        tarjetaKiosco.setLayout(new BoxLayout(tarjetaKiosco, BoxLayout.Y_AXIS));
        tarjetaKiosco.setBackground(Color.WHITE);
        tarjetaKiosco.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 215, 230), 2, true),
                new EmptyBorder(36, 48, 36, 48)
        ));
        tarjetaKiosco.setPreferredSize(new Dimension(680, 420));

        JLabel lblIconoKiosco = new JLabel("[ Lector Óptico Activo ]", SwingConstants.CENTER);
        lblIconoKiosco.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblIconoKiosco.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblIconoKiosco.setForeground(COLOR_PRIMARIO);

        JLabel lblTituloKiosco = new JLabel("Escanee su carnet y luego el libro", SwingConstants.CENTER);
        lblTituloKiosco.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTituloKiosco.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTituloKiosco.setForeground(new Color(25, 35, 50));

        JLabel lblSubtitulo = new JLabel(
                "<html><div style='text-align: center;'>Aproxime su carnet universitario al lector óptico y luego escanee el código de barras de su libro.</div></html>",
                SwingConstants.CENTER
        );
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(95, 105, 120));

        JTextField txtLecturaQR = new JTextField("", 22);
        txtLecturaQR.setToolTipText("Ejemplo trama: EST001|EJM-003");
        txtLecturaQR.setHorizontalAlignment(JTextField.CENTER);
        txtLecturaQR.setFont(new Font("Segoe UI", Font.BOLD, 20));
        txtLecturaQR.setMaximumSize(new Dimension(440, 48));
        txtLecturaQR.setPreferredSize(new Dimension(440, 48));
        txtLecturaQR.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnKioscoPrestamo = crearBotonPrincipal("Solicitar Préstamo");
        btnKioscoPrestamo.setPreferredSize(new Dimension(210, 46));
        btnKioscoPrestamo.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JButton btnKioscoDevolucion = crearBotonSecundario("Devolver Libro");
        btnKioscoDevolucion.setPreferredSize(new Dimension(210, 46));
        btnKioscoDevolucion.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JPanel panelBotonesKiosco = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 8));
        panelBotonesKiosco.setOpaque(false);
        panelBotonesKiosco.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelBotonesKiosco.add(btnKioscoPrestamo);
        panelBotonesKiosco.add(btnKioscoDevolucion);

        btnKioscoPrestamo.addActionListener(e -> {
            String respuesta = autoservicioQRController.procesarLecturaQRPrestamo(txtLecturaQR.getText());
            mostrarDialogoResultado("Autoservicio - Préstamo", respuesta);
            refrescarTablas();
        });

        btnKioscoDevolucion.addActionListener(e -> {
            String respuesta = autoservicioQRController.procesarLecturaQRDevolucion(
                    txtLecturaQR.getText(),
                    false
            );
            mostrarDialogoResultado("Autoservicio - Devolución", respuesta);
            refrescarTablas();
        });

        tarjetaKiosco.add(lblIconoKiosco);
        tarjetaKiosco.add(Box.createVerticalStrut(10));
        tarjetaKiosco.add(lblTituloKiosco);
        tarjetaKiosco.add(Box.createVerticalStrut(8));
        tarjetaKiosco.add(lblSubtitulo);
        tarjetaKiosco.add(Box.createVerticalStrut(28));
        tarjetaKiosco.add(txtLecturaQR);
        tarjetaKiosco.add(Box.createVerticalStrut(24));
        tarjetaKiosco.add(panelBotonesKiosco);

        panelExterior.add(tarjetaKiosco);
        return panelExterior;
    }

    /**
     * Panel 5: Reserva de Cubículos.
     */
    private JPanel crearPanelReservaCubiculos() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel cardFormulario = new JPanel(new BorderLayout(10, 10));
        cardFormulario.setBorder(crearBordeSeccion("Reserva de Cubículos de Estudio"));

        JPanel camposPanel = new JPanel(new GridBagLayout());
        camposPanel.setBorder(new EmptyBorder(8, 12, 8, 12));
        GridBagConstraints gbc = crearGbcBase();

        JTextField txtIdCubiculo = crearCampoTexto("CUB-01", 10);
        JTextField txtEstCubiculo = crearCampoTexto("EST001", 12);
        JComboBox<String> cmbDuracion = new JComboBox<>(new String[]{"1 hora", "2 horas"});
        cmbDuracion.setSelectedIndex(1);
        cmbDuracion.setFont(FUENTE_NORMAL);

        gbc.gridx = 0;
        gbc.gridy = 0;
        camposPanel.add(crearEtiqueta("Cubículo:"), gbc);
        gbc.gridx = 1;
        camposPanel.add(txtIdCubiculo, gbc);

        gbc.gridx = 2;
        camposPanel.add(Box.createHorizontalStrut(10), gbc);

        gbc.gridx = 3;
        camposPanel.add(crearEtiqueta("Estudiante:"), gbc);
        gbc.gridx = 4;
        camposPanel.add(txtEstCubiculo, gbc);

        gbc.gridx = 5;
        camposPanel.add(Box.createHorizontalStrut(10), gbc);

        gbc.gridx = 6;
        camposPanel.add(crearEtiqueta("Tiempo de Uso:"), gbc);
        gbc.gridx = 7;
        camposPanel.add(cmbDuracion, gbc);

        JButton btnReservarCub = crearBotonPrincipal("Reservar Cubículo");
        JButton btnLiberarCub = crearBotonSecundario("Finalizar Uso");

        JPanel panelAccion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        panelAccion.add(btnLiberarCub);
        panelAccion.add(btnReservarCub);

        cardFormulario.add(camposPanel, BorderLayout.WEST);
        cardFormulario.add(panelAccion, BorderLayout.EAST);

        btnReservarCub.addActionListener(e -> {
            int horas = cmbDuracion.getSelectedIndex() + 1;
            String respuesta = cubiculoController.reservarCubiculo(
                    txtIdCubiculo.getText(),
                    txtEstCubiculo.getText(),
                    horas
            );
            mostrarDialogoResultado("Reserva de Cubículo", respuesta);
            refrescarTablas();
        });

        btnLiberarCub.addActionListener(e -> {
            String respuesta = cubiculoController.liberarCubiculo(txtIdCubiculo.getText());
            mostrarDialogoResultado("Liberación de Cubículo", respuesta);
            refrescarTablas();
        });

        panel.add(cardFormulario, BorderLayout.NORTH);

        modeloCubiculos = crearModeloNoEditable(
                new Object[]{"Código", "Nombre del Espacio", "Capacidad", "Disponibilidad"}
        );
        JTable tablaCubiculos = crearTablaEstilizada(modeloCubiculos, new int[]{85, 150, 80, 110});

        tablaCubiculos.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaCubiculos.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0) {
                txtIdCubiculo.setText(String.valueOf(modeloCubiculos.getValueAt(fila, 0)));
            }
        });

        modeloReservasCubiculo = crearModeloNoEditable(
                new Object[]{"N.º Reserva", "Cubículo", "Estudiante", "Hora de Inicio", "Hora de Fin", "Estado"}
        );
        JTable tablaReservasCub = crearTablaEstilizada(modeloReservasCubiculo, new int[]{80, 85, 95, 130, 130, 90});

        JPanel panelIzq = new JPanel(new BorderLayout());
        panelIzq.setBorder(crearBordeSeccion("Espacios de Estudio Disponibles"));
        panelIzq.add(new JScrollPane(tablaCubiculos), BorderLayout.CENTER);

        JPanel panelDer = new JPanel(new BorderLayout());
        panelDer.setBorder(crearBordeSeccion("Ocupación Actual de Cubículos"));
        panelDer.add(new JScrollPane(tablaReservasCub), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzq, panelDer);
        split.setResizeWeight(0.42);
        split.setBorder(null);
        panel.add(split, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    // Componentes auxiliares de UI/UX
    // =========================================================================

    private TitledBorder crearBordeSeccion(String titulo) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(210, 218, 228), 1, true),
                " " + titulo + " ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                FUENTE_SECCION,
                COLOR_PRIMARIO
        );
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(FUENTE_NORMAL);
        return label;
    }

    private JTextField crearCampoTexto(String valorInicial, int columnas) {
        JTextField campo = new JTextField(valorInicial, columnas);
        campo.setFont(FUENTE_NORMAL);
        campo.setPreferredSize(new Dimension(campo.getPreferredSize().width, 30));
        return campo;
    }

    private JButton crearBotonPrincipal(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(FUENTE_BOTON_ACCION);
        boton.setPreferredSize(new Dimension(205, 36));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    private JButton crearBotonSecundario(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(FUENTE_NORMAL);
        boton.setPreferredSize(new Dimension(205, 36));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    private DefaultTableModel crearModeloNoEditable(Object[] columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable crearTablaEstilizada(DefaultTableModel modelo, int[] anchosColumnas) {
        JTable tabla = new JTable(modelo);
        tabla.setFont(FUENTE_NORMAL);
        tabla.setRowHeight(26);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setFillsViewportHeight(true);

        TableColumnModel columnModel = tabla.getColumnModel();
        for (int i = 0; i < anchosColumnas.length && i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setPreferredWidth(anchosColumnas[i]);
        }
        return tabla;
    }

    private GridBagConstraints crearGbcBase() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    /**
     * Presenta al usuario un cuadro de diálogo amigable, limpiando prefijos técnicos
     * y seleccionando el icono adecuado según el resultado de la operación.
     */
    private void mostrarDialogoResultado(String titulo, String mensajeOriginal) {
        if (mensajeOriginal == null || mensajeOriginal.trim().isEmpty()) {
            return;
        }

        int tipoIcono = JOptionPane.INFORMATION_MESSAGE;
        String textoMayus = mensajeOriginal.toUpperCase();

        if (textoMayus.contains("[ERROR") || textoMayus.contains("ERROR AL")) {
            tipoIcono = JOptionPane.ERROR_MESSAGE;
        } else if (textoMayus.contains("[VALIDACIÓN") || textoMayus.contains("[RECHAZADO")
                || textoMayus.contains("SUSPENDIDO") || textoMayus.contains("SANCIONADO")
                || textoMayus.contains("NO EXISTE") || textoMayus.contains("NO ESTÁ DISPONIBLE")
                || textoMayus.contains("LÍMITE")) {
            tipoIcono = JOptionPane.WARNING_MESSAGE;
        }

        // Limpiar etiquetas técnicas entre corchetes y términos SQL para ofrecer un mensaje natural
        String mensajeLimpio = mensajeOriginal
                .replaceAll("\\[[^\\]]+\\]\\s*", "")
                .replace("(SYSDATE + 7)", "(7 días)")
                .replace("(SYSDATE + 2)", "(2 días)")
                .replace("(SYSDATE + 1)", "(24 horas)")
                .replace("SELECT ... FOR UPDATE", "")
                .replace("en Oracle:", ":")
                .trim();

        JOptionPane.showMessageDialog(this, mensajeLimpio, titulo, tipoIcono);
    }

    private void refrescarTablas() {
        // Ejemplares
        if (modeloEjemplares != null) {
            modeloEjemplares.setRowCount(0);
            List<Ejemplar> ejemplares = prestamoController.listarEjemplares();
            for (Ejemplar ej : ejemplares) {
                modeloEjemplares.addRow(new Object[]{
                        ej.getIdEjemplar(),
                        "Libro #" + ej.getIdLibro(),
                        ej.getCodigoInventario(),
                        ej.getEstadoEjemplar(),
                        ej.getEstadoFisico()
                });
            }
        }

        // Estudiantes
        if (modeloEstudiantes != null) {
            modeloEstudiantes.setRowCount(0);
            List<Estudiante> estudiantes = prestamoController.listarEstudiantes();
            for (Estudiante est : estudiantes) {
                modeloEstudiantes.addRow(new Object[]{
                        est.getIdEstudiante(),
                        est.getNombreCompleto(),
                        est.getCorreo(),
                        est.getEstadoEstudiante(),
                        est.getFechaFinSuspension() != null ? est.getFechaFinSuspension() : "—"
                });
            }
        }

        // Préstamos activos
        if (modeloPrestamosActivos != null) {
            modeloPrestamosActivos.setRowCount(0);
            List<Prestamo> prestamos = prestamoController.listarPrestamosActivos();
            for (Prestamo p : prestamos) {
                modeloPrestamosActivos.addRow(new Object[]{
                        p.getIdPrestamo(),
                        p.getIdEstudiante(),
                        p.getIdEjemplar(),
                        p.getFechaPrestamo(),
                        p.getFechaDevolucionEsperada(),
                        "AUTOSERVICIO_QR".equals(p.getCanal()) ? "Autoservicio QR" : "Presencial",
                        p.getEstadoPrestamo()
                });
            }
        }

        // Reservas de material
        if (modeloReservasMaterial != null) {
            modeloReservasMaterial.setRowCount(0);
            List<ReservaMaterial> reservasMat = reservaMaterialController.listarReservasPendientes();
            for (ReservaMaterial rm : reservasMat) {
                modeloReservasMaterial.addRow(new Object[]{
                        rm.getIdReservaMat(),
                        rm.getIdEstudiante(),
                        rm.getIdEjemplar(),
                        rm.getFechaReserva(),
                        rm.getFechaLimiteRecojo(),
                        rm.getEstadoReserva()
                });
            }
        }

        // Cubículos
        if (modeloCubiculos != null) {
            modeloCubiculos.setRowCount(0);
            List<Cubiculo> cubiculos = cubiculoController.listarCubiculos();
            for (Cubiculo c : cubiculos) {
                modeloCubiculos.addRow(new Object[]{
                        c.getIdCubiculo(),
                        c.getNombreCodigo(),
                        c.getCapacidad() + " personas",
                        c.getEstadoCubiculo()
                });
            }
        }

        // Reservas de cubículos
        if (modeloReservasCubiculo != null) {
            modeloReservasCubiculo.setRowCount(0);
            List<ReservaCubiculo> reservasCub = cubiculoController.listarReservasActivas();
            for (ReservaCubiculo rc : reservasCub) {
                modeloReservasCubiculo.addRow(new Object[]{
                        rc.getIdReservaCub(),
                        rc.getIdCubiculo(),
                        rc.getIdEstudiante(),
                        rc.getFechaHoraInicio(),
                        rc.getFechaHoraFin(),
                        rc.getEstadoReserva()
                });
            }
        }
    }
}
