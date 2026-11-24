package sistemadegestionbiblioteca.views;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import sistemadegestionbiblioteca.controllers.AutoservicioQRController;
import sistemadegestionbiblioteca.controllers.CatalogoController;
import sistemadegestionbiblioteca.controllers.CubiculoController;
import sistemadegestionbiblioteca.controllers.PrestamoController;
import sistemadegestionbiblioteca.models.Cubiculo;
import sistemadegestionbiblioteca.models.Ejemplar;
import sistemadegestionbiblioteca.models.Estudiante;
import sistemadegestionbiblioteca.models.Libro;
import sistemadegestionbiblioteca.models.NotificacionAdmin;
import sistemadegestionbiblioteca.models.Prestamo;
import sistemadegestionbiblioteca.models.ReservaCubiculo;

/**
 * Pantalla Principal tipo Dashboard del Sistema de Gestión de Biblioteca (UNMSM).
 * Implementa navegación interna mediante {@link CardLayout} sobre un {@link PanelFondoDashboard}
 * global para que todos los paneles secundarios floten de forma transparente sobre la
 * imagen de fondo de la universidad.
 *
 * @author Grupo 6
 */
public class VentanaPrincipalBiblioteca extends JFrame {

    // Identificadores de vistas para el CardLayout central
    public static final String VISTA_DASHBOARD = "VISTA_DASHBOARD";
    public static final String VISTA_PRESTAMO = "VISTA_PRESTAMO";
    public static final String VISTA_DEVOLUCION = "VISTA_DEVOLUCION";
    public static final String VISTA_CATALOGO = "VISTA_CATALOGO";
    public static final String VISTA_QR = "VISTA_QR";
    public static final String VISTA_CUBICULO = "VISTA_CUBICULO";
    public static final String VISTA_ESTUDIANTES = "VISTA_ESTUDIANTES";
    public static final String VISTA_ADMIN = "VISTA_ADMIN";

    private static final Color COLOR_GUINDA_UNMSM = new Color(115, 22, 34);
    private static final Color COLOR_AZUL_INSTITUCIONAL = new Color(22, 48, 86);
    private static final Color COLOR_DORADO_UNMSM = new Color(212, 175, 55);
    private static final Color COLOR_TEXTO_OSCURO = new Color(28, 33, 43);
    private static final Color COLOR_BORDE_SUTIL = new Color(200, 200, 200);
    private static final Color COLOR_ENCABEZADO_TABLA = new Color(240, 240, 240);

    private static final Font FUENTE_TITULO_HEADER = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FUENTE_SUBTITULO_HEADER = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FUENTE_TITULO_TARJETA = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FUENTE_DESC_TARJETA = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FUENTE_SECCION = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FUENTE_ETIQUETA = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FUENTE_HEADER_TABLA = new Font("Segoe UI", Font.BOLD, 14);

    private final PrestamoController prestamoController;
    private final CatalogoController catalogoController;
    private final AutoservicioQRController autoservicioQRController;
    private final CubiculoController cubiculoController;

    private CardLayout cardLayoutCentral;
    private JPanel panelContenedorCards;

    // Acciones de refresco automático al cambiar de vista en el CardLayout
    private Runnable refrescarVistaPrestamo;
    private Runnable refrescarVistaDevolucion;
    private Runnable refrescarVistaCatalogo;
    private Runnable refrescarVistaCubiculos;
    private Runnable refrescarVistaEstudiantes;
    private Runnable refrescarVistaAdmin;

    private Image imagenFondoUniversidad;
    private ImageIcon iconoLogoUnmsm;

    public VentanaPrincipalBiblioteca() {
        aplicarLookAndFeelNativo();
        this.prestamoController = new PrestamoController();
        this.catalogoController = new CatalogoController();
        this.autoservicioQRController = new AutoservicioQRController();
        this.cubiculoController = new CubiculoController();

        cargarRecursosVisuales();
        configurarVentana();
        inicializarComponentes();
    }

    private void aplicarLookAndFeelNativo() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    private void cargarRecursosVisuales() {
        try {
            URL urlFondo = getClass().getResource("/recursos/fondo_unmsm.png");
            if (urlFondo != null) {
                imagenFondoUniversidad = ImageIO.read(urlFondo);
            }
        } catch (Exception ignored) {
        }

        try {
            URL urlLogo = getClass().getResource("/recursos/logo_unmsm.png");
            if (urlLogo != null) {
                Image img = ImageIO.read(urlLogo);
                if (img != null) {
                    iconoLogoUnmsm = new ImageIcon(img.getScaledInstance(48, 48, Image.SCALE_SMOOTH));
                }
            }
        } catch (Exception ignored) {
        }

        if (iconoLogoUnmsm == null) {
            iconoLogoUnmsm = crearEscudoUnmsmFallback(48);
        }
    }

    private void configurarVentana() {
        setTitle("Sistema de Gestión de Biblioteca - UNMSM");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1160, 750);
        setMinimumSize(new Dimension(1020, 660));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));
    }

    private void inicializarComponentes() {
        // 1. Cabecera Fija Superior (Header): Izquierda Logo UNMSM, Centro Título, Derecha Modo Administrador
        JPanel panelHeader = new JPanel(new BorderLayout(16, 0));
        panelHeader.setBackground(COLOR_GUINDA_UNMSM);
        panelHeader.setBorder(new EmptyBorder(12, 24, 12, 24));

        JPanel panelMarca = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panelMarca.setOpaque(false);
        JLabel lblLogo = new JLabel(iconoLogoUnmsm);
        lblLogo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogo.setToolTipText("Volver al Menú Principal");
        lblLogo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mostrarVista(VISTA_DASHBOARD);
            }
        });

        JPanel panelTextoMarca = new JPanel();
        panelTextoMarca.setOpaque(false);
        panelTextoMarca.setLayout(new BoxLayout(panelTextoMarca, BoxLayout.Y_AXIS));
        JLabel lblSiglas = new JLabel("UNMSM - FISI");
        lblSiglas.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSiglas.setForeground(COLOR_DORADO_UNMSM);
        JLabel lblDecana = new JLabel("Biblioteca Central y Especializada");
        lblDecana.setFont(FUENTE_SUBTITULO_HEADER);
        lblDecana.setForeground(new Color(235, 225, 225));
        panelTextoMarca.add(lblSiglas);
        panelTextoMarca.add(lblDecana);
        panelMarca.add(lblLogo);
        panelMarca.add(panelTextoMarca);

        JLabel lblTitulo = new JLabel("Sistema de Gestión de Biblioteca", SwingConstants.CENTER);
        lblTitulo.setFont(FUENTE_TITULO_HEADER);
        lblTitulo.setForeground(Color.WHITE);

        JPanel panelDerechaHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        panelDerechaHeader.setOpaque(false);
        JButton btnModoAdmin = new JButton("Modo Administrador");
        btnModoAdmin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnModoAdmin.setForeground(COLOR_GUINDA_UNMSM);
        btnModoAdmin.setBackground(new Color(248, 242, 232));
        btnModoAdmin.setFocusPainted(false);
        btnModoAdmin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnModoAdmin.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_DORADO_UNMSM, 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btnModoAdmin.addActionListener(e -> autenticarYAbrirModoAdministrador());
        panelDerechaHeader.add(btnModoAdmin);

        panelHeader.add(panelMarca, BorderLayout.WEST);
        panelHeader.add(lblTitulo, BorderLayout.CENTER);
        panelHeader.add(panelDerechaHeader, BorderLayout.EAST);

        add(panelHeader, BorderLayout.NORTH);

        // 2. Panel de Fondo Global + CardLayout Transparente para que todas las vistas floten sobre el fondo
        PanelFondoDashboard panelFondoGlobal = new PanelFondoDashboard();
        panelFondoGlobal.setLayout(new BorderLayout());

        cardLayoutCentral = new CardLayout();
        panelContenedorCards = new JPanel(cardLayoutCentral);
        panelContenedorCards.setOpaque(false);

        panelContenedorCards.add(crearPanelDashboardTarjetas(), VISTA_DASHBOARD);
        panelContenedorCards.add(crearPanelPrestamoLibros(), VISTA_PRESTAMO);
        panelContenedorCards.add(crearPanelDevolucionLibros(), VISTA_DEVOLUCION);
        panelContenedorCards.add(crearPanelBusquedaCatalogo(), VISTA_CATALOGO);
        panelContenedorCards.add(crearPanelAutoservicioQR(), VISTA_QR);
        panelContenedorCards.add(crearPanelReservaCubiculos(), VISTA_CUBICULO);
        panelContenedorCards.add(crearPanelDirectorioEstudiantes(), VISTA_ESTUDIANTES);
        panelContenedorCards.add(crearPanelModoAdministrador(), VISTA_ADMIN);

        panelFondoGlobal.add(panelContenedorCards, BorderLayout.CENTER);
        add(panelFondoGlobal, BorderLayout.CENTER);

        // 3. Pie de página institucional fijo
        JPanel panelFooter = new JPanel(new BorderLayout());
        panelFooter.setBackground(new Color(232, 236, 242));
        panelFooter.setBorder(new EmptyBorder(8, 24, 8, 24));
        JLabel lblFooterIzq = new JLabel("Universidad Nacional Mayor de San Marcos • Sistema de Gestión Bibliotecaria");
        lblFooterIzq.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFooterIzq.setForeground(new Color(65, 75, 90));
        JLabel lblFooterDer = new JLabel("Servicio en línea conectado a Oracle Database (FREEPDB1)");
        lblFooterDer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFooterDer.setForeground(new Color(65, 75, 90));
        panelFooter.add(lblFooterIzq, BorderLayout.WEST);
        panelFooter.add(lblFooterDer, BorderLayout.EAST);
        add(panelFooter, BorderLayout.SOUTH);

        mostrarVista(VISTA_DASHBOARD);
    }

    /**
     * Cambia la vista activa del {@link CardLayout} central y refresca los datos en tiempo real.
     */
    public void mostrarVista(String nombreVista) {
        if (VISTA_PRESTAMO.equals(nombreVista) && refrescarVistaPrestamo != null) {
            refrescarVistaPrestamo.run();
        } else if (VISTA_DEVOLUCION.equals(nombreVista) && refrescarVistaDevolucion != null) {
            refrescarVistaDevolucion.run();
        } else if (VISTA_CATALOGO.equals(nombreVista) && refrescarVistaCatalogo != null) {
            refrescarVistaCatalogo.run();
        } else if (VISTA_CUBICULO.equals(nombreVista) && refrescarVistaCubiculos != null) {
            refrescarVistaCubiculos.run();
        } else if (VISTA_ESTUDIANTES.equals(nombreVista) && refrescarVistaEstudiantes != null) {
            refrescarVistaEstudiantes.run();
        } else if (VISTA_ADMIN.equals(nombreVista) && refrescarVistaAdmin != null) {
            refrescarVistaAdmin.run();
        }

        cardLayoutCentral.show(panelContenedorCards, nombreVista);
        panelContenedorCards.revalidate();
        panelContenedorCards.repaint();
    }

    /**
     * Construye la vista principal del Dashboard con cuadrícula de tarjetas flotantes.
     */
    private JPanel crearPanelDashboardTarjetas() {
        JPanel panelDashboard = new JPanel(new GridBagLayout());
        panelDashboard.setOpaque(false);
        panelDashboard.setBorder(new EmptyBorder(24, 36, 24, 36));

        JPanel contenedorCentral = new JPanel(new BorderLayout(0, 22));
        contenedorCentral.setOpaque(false);

        JPanel panelBienvenida = new JPanel();
        panelBienvenida.setOpaque(false);
        panelBienvenida.setLayout(new BoxLayout(panelBienvenida, BoxLayout.Y_AXIS));
        JLabel lblBienvenida = new JLabel("Seleccione el servicio que desea utilizar", SwingConstants.CENTER);
        lblBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 21));
        lblBienvenida.setForeground(COLOR_AZUL_INSTITUCIONAL);

        JLabel lblInstruccion = new JLabel(
                "Atención integral de préstamos, devoluciones, consulta de catálogo en línea, terminal QR y cubículos de estudio",
                SwingConstants.CENTER
        );
        lblInstruccion.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblInstruccion.setFont(FUENTE_NORMAL);
        lblInstruccion.setForeground(COLOR_TEXTO_OSCURO);

        panelBienvenida.add(lblBienvenida);
        panelBienvenida.add(Box.createVerticalStrut(6));
        panelBienvenida.add(lblInstruccion);

        JPanel gridTarjetas = new JPanel(new GridLayout(2, 3, 24, 24));
        gridTarjetas.setOpaque(false);

        gridTarjetas.add(crearTarjetaDashboard(
                "Préstamo de Libros",
                "Solicitar ejemplares disponibles por 7 días",
                TipoIconoTarjeta.PRESTAMO,
                () -> mostrarVista(VISTA_PRESTAMO)
        ));

        gridTarjetas.add(crearTarjetaDashboard(
                "Devolución",
                "Registrar entrega de libros y actualizar stock",
                TipoIconoTarjeta.DEVOLUCION,
                () -> mostrarVista(VISTA_DEVOLUCION)
        ));

        gridTarjetas.add(crearTarjetaDashboard(
                "Buscar en Catálogo",
                "Consultar disponibilidad y notificar faltantes",
                TipoIconoTarjeta.CATALOGO,
                () -> mostrarVista(VISTA_CATALOGO)
        ));

        gridTarjetas.add(crearTarjetaDashboard(
                "Autoservicio QR",
                "Terminal rápida de préstamo y devolución con lector óptico",
                TipoIconoTarjeta.QR,
                () -> mostrarVista(VISTA_QR)
        ));

        gridTarjetas.add(crearTarjetaDashboard(
                "Reservar Cubículo",
                "Espacios grupales de estudio en tiempo real",
                TipoIconoTarjeta.CUBICULO,
                () -> mostrarVista(VISTA_CUBICULO)
        ));

        gridTarjetas.add(crearTarjetaDashboard(
                "Estado de Estudiantes",
                "Consultar préstamos vigentes y estado de habilitación",
                TipoIconoTarjeta.ESTUDIANTE,
                () -> mostrarVista(VISTA_ESTUDIANTES)
        ));

        contenedorCentral.add(panelBienvenida, BorderLayout.NORTH);
        contenedorCentral.add(gridTarjetas, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panelDashboard.add(contenedorCentral, gbc);

        return panelDashboard;
    }

    /**
     * Crea la barra superior transparente de cada módulo con el botón "← Volver al Menú Principal"
     * y el título de la vista activa.
     */
    private JPanel crearBarraNavegacionModulo(String tituloModulo, Runnable accionRefrescar) {
        JPanel barra = new JPanel(new BorderLayout(16, 0));
        barra.setOpaque(false);
        barra.setBorder(new EmptyBorder(0, 0, 8, 0));

        JButton btnVolver = crearBotonSecundario("← Volver al Menú Principal");
        btnVolver.setPreferredSize(new Dimension(230, 38));
        btnVolver.setForeground(COLOR_GUINDA_UNMSM);
        btnVolver.addActionListener(e -> mostrarVista(VISTA_DASHBOARD));

        JLabel lblTituloModulo = new JLabel(tituloModulo, SwingConstants.CENTER);
        lblTituloModulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloModulo.setForeground(COLOR_AZUL_INSTITUCIONAL);

        JPanel panelDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelDerecha.setOpaque(false);
        if (accionRefrescar != null) {
            JButton btnRefrescar = crearBotonSecundario("Actualizar Datos");
            btnRefrescar.setPreferredSize(new Dimension(165, 38));
            btnRefrescar.addActionListener(e -> accionRefrescar.run());
            panelDerecha.add(btnRefrescar);
        }

        barra.add(btnVolver, BorderLayout.WEST);
        barra.add(lblTituloModulo, BorderLayout.CENTER);
        barra.add(panelDerecha, BorderLayout.EAST);
        return barra;
    }

    private JButton crearTarjetaDashboard(String tituloAccion, String descripcion,
                                          TipoIconoTarjeta tipoIcono, Runnable accionAlClic) {
        JButton tarjeta = new JButton();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(new Color(255, 255, 255, 235));
        tarjeta.setFocusPainted(false);
        tarjeta.setContentAreaFilled(true);
        tarjeta.setOpaque(true);
        tarjeta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tarjeta.setPreferredSize(new Dimension(280, 195));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE_SUTIL, 1, true),
                new EmptyBorder(22, 20, 20, 20)
        ));

        JLabel lblIcono = new JLabel(new IconoTarjetaVectorial(tipoIcono, 62, 62));
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitulo = new JLabel(tituloAccion, SwingConstants.CENTER);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setFont(FUENTE_TITULO_TARJETA);
        lblTitulo.setForeground(COLOR_AZUL_INSTITUCIONAL);

        JLabel lblDesc = new JLabel(
                "<html><div style='text-align: center; width: 205px;'>" + descripcion + "</div></html>",
                SwingConstants.CENTER
        );
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDesc.setFont(FUENTE_DESC_TARJETA);
        lblDesc.setForeground(COLOR_TEXTO_OSCURO);

        tarjeta.add(Box.createVerticalGlue());
        tarjeta.add(lblIcono);
        tarjeta.add(Box.createVerticalStrut(14));
        tarjeta.add(lblTitulo);
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(lblDesc);
        tarjeta.add(Box.createVerticalGlue());

        tarjeta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                tarjeta.setBackground(new Color(252, 249, 242));
                tarjeta.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_GUINDA_UNMSM, 2, true),
                        new EmptyBorder(21, 19, 19, 19)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                tarjeta.setBackground(new Color(255, 255, 255, 235));
                tarjeta.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_BORDE_SUTIL, 1, true),
                        new EmptyBorder(22, 20, 20, 20)
                ));
            }
        });

        tarjeta.addActionListener(e -> accionAlClic.run());
        return tarjeta;
    }

    // =========================================================================
    // PANEL 1: Préstamo de Libros (100% transparente sobre el fondo)
    // =========================================================================

    private JPanel crearPanelPrestamoLibros() {
        JPanel panelRaiz = new JPanel(new BorderLayout(16, 16));
        panelRaiz.setOpaque(false);
        panelRaiz.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel panelContenido = new JPanel(new BorderLayout(16, 16));
        panelContenido.setOpaque(false);

        JPanel cardFormulario = new JPanel(new BorderLayout(12, 12));
        cardFormulario.setOpaque(false);
        cardFormulario.setBorder(crearBordeSeccion("Registrar Préstamo de Ejemplar (Plazo automático: 7 días | Máximo 3 libros)"));

        JPanel camposPanel = new JPanel(new GridBagLayout());
        camposPanel.setOpaque(false);
        camposPanel.setBorder(new EmptyBorder(10, 14, 10, 14));
        GridBagConstraints gbc = crearGbcBase();

        JTextField txtIdEstudiante = crearCampoTexto("EST001", 15);
        JTextField txtIdEjemplar = crearCampoTexto("EJM-001", 15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        camposPanel.add(crearEtiqueta("Código del Estudiante:"), gbc);
        gbc.gridx = 1;
        camposPanel.add(txtIdEstudiante, gbc);
        gbc.gridx = 2;
        camposPanel.add(Box.createHorizontalStrut(18), gbc);
        gbc.gridx = 3;
        camposPanel.add(crearEtiqueta("Código del Ejemplar:"), gbc);
        gbc.gridx = 4;
        camposPanel.add(txtIdEjemplar, gbc);

        JButton btnRegistrarPrestamo = crearBotonPrincipal("Registrar Préstamo");
        JPanel panelAccion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 8));
        panelAccion.setOpaque(false);
        panelAccion.add(btnRegistrarPrestamo);

        cardFormulario.add(camposPanel, BorderLayout.WEST);
        cardFormulario.add(panelAccion, BorderLayout.EAST);
        panelContenido.add(cardFormulario, BorderLayout.NORTH);

        DefaultTableModel modEjemplares = crearModeloNoEditable(
                new Object[]{"Código Ejemplar", "Libro", "N.º Inventario", "Disponibilidad", "Condición"}
        );
        JTable tablaEjemplares = crearTablaEstilizada(modEjemplares, new int[]{115, 75, 135, 115, 95});

        DefaultTableModel modEstudiantes = crearModeloNoEditable(
                new Object[]{"Código", "Estudiante", "Correo Institucional", "Estado", "Fin de Sanción"}
        );
        JTable tablaEstudiantes = crearTablaEstilizada(modEstudiantes, new int[]{85, 165, 180, 95, 115});

        refrescarVistaPrestamo = () -> {
            modEjemplares.setRowCount(0);
            for (Ejemplar ej : prestamoController.listarEjemplares()) {
                modEjemplares.addRow(new Object[]{
                        ej.getIdEjemplar(),
                        "Libro #" + ej.getIdLibro(),
                        ej.getCodigoInventario(),
                        ej.getEstadoEjemplar(),
                        ej.getEstadoFisico()
                });
            }
            modEstudiantes.setRowCount(0);
            for (Estudiante est : prestamoController.listarEstudiantes()) {
                modEstudiantes.addRow(new Object[]{
                        est.getIdEstudiante(),
                        est.getNombreCompleto(),
                        est.getCorreo(),
                        est.getEstadoEstudiante(),
                        est.getFechaFinSuspension() != null ? est.getFechaFinSuspension() : "—"
                });
            }
        };

        tablaEjemplares.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaEjemplares.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0) {
                txtIdEjemplar.setText(String.valueOf(modEjemplares.getValueAt(fila, 0)));
            }
        });

        tablaEstudiantes.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaEstudiantes.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0) {
                txtIdEstudiante.setText(String.valueOf(modEstudiantes.getValueAt(fila, 0)));
            }
        });

        btnRegistrarPrestamo.addActionListener(e -> {
            String respuesta = prestamoController.procesarPrestamo(
                    txtIdEstudiante.getText(),
                    txtIdEjemplar.getText()
            );
            mostrarDialogoResultado(this, "Préstamo de Libros", respuesta);
            refrescarVistaPrestamo.run();
        });

        JPanel panelIzq = new JPanel(new BorderLayout(0, 6));
        panelIzq.setOpaque(false);
        panelIzq.setBorder(crearBordeSeccion("Ejemplares en Inventario (Clic para seleccionar)"));
        panelIzq.add(crearScrollTablaEstilizado(tablaEjemplares), BorderLayout.CENTER);

        JPanel panelDer = new JPanel(new BorderLayout(0, 6));
        panelDer.setOpaque(false);
        panelDer.setBorder(crearBordeSeccion("Estudiantes Registrados (Clic para seleccionar)"));
        panelDer.add(crearScrollTablaEstilizado(tablaEstudiantes), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzq, panelDer);
        split.setOpaque(false);
        split.setResizeWeight(0.48);
        split.setBorder(null);
        panelContenido.add(split, BorderLayout.CENTER);

        panelRaiz.add(crearBarraNavegacionModulo("Módulo de Préstamo de Libros", refrescarVistaPrestamo), BorderLayout.NORTH);
        panelRaiz.add(panelContenido, BorderLayout.CENTER);
        return panelRaiz;
    }

    // =========================================================================
    // PANEL 2: Devolución de Libros (100% transparente sobre el fondo)
    // =========================================================================

    private JPanel crearPanelDevolucionLibros() {
        JPanel panelRaiz = new JPanel(new BorderLayout(16, 16));
        panelRaiz.setOpaque(false);
        panelRaiz.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel panelContenido = new JPanel(new BorderLayout(16, 16));
        panelContenido.setOpaque(false);

        JPanel cardFormulario = new JPanel(new BorderLayout(12, 12));
        cardFormulario.setOpaque(false);
        cardFormulario.setBorder(crearBordeSeccion("Recepción de Libros (Devolución tardía o con daño aplica suspensión de 2 días)"));

        JPanel camposPanel = new JPanel(new GridBagLayout());
        camposPanel.setOpaque(false);
        camposPanel.setBorder(new EmptyBorder(10, 14, 10, 14));
        GridBagConstraints gbc = crearGbcBase();

        JTextField txtIdEjemplarDev = crearCampoTexto("EJM-002", 15);
        JCheckBox chkDanado = new JCheckBox("El ejemplar presenta daño o deterioro físico");
        chkDanado.setOpaque(false);
        chkDanado.setFont(FUENTE_NORMAL);
        chkDanado.setForeground(COLOR_TEXTO_OSCURO);

        gbc.gridx = 0;
        gbc.gridy = 0;
        camposPanel.add(crearEtiqueta("Código del Ejemplar:"), gbc);
        gbc.gridx = 1;
        camposPanel.add(txtIdEjemplarDev, gbc);
        gbc.gridx = 2;
        camposPanel.add(Box.createHorizontalStrut(18), gbc);
        gbc.gridx = 3;
        camposPanel.add(chkDanado, gbc);

        JButton btnDevolver = crearBotonPrincipal("Confirmar Devolución");
        JPanel panelAccion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 8));
        panelAccion.setOpaque(false);
        panelAccion.add(btnDevolver);

        cardFormulario.add(camposPanel, BorderLayout.WEST);
        cardFormulario.add(panelAccion, BorderLayout.EAST);
        panelContenido.add(cardFormulario, BorderLayout.NORTH);

        DefaultTableModel modPrestamos = crearModeloNoEditable(
                new Object[]{"N.° Préstamo", "Estudiante", "Código Ejemplar", "Fecha de Préstamo", "Fecha Límite (7 días)", "Canal", "Estado"}
        );
        JTable tablaPrestamos = new JTable(modPrestamos);
        JScrollPane scrollPrestamos = new JScrollPane(tablaPrestamos);
        estilizarTablaPrestamos(tablaPrestamos, scrollPrestamos);

        refrescarVistaDevolucion = () -> {
            modPrestamos.setRowCount(0);
            for (Prestamo p : prestamoController.listarPrestamosActivos()) {
                modPrestamos.addRow(new Object[]{
                        p.getIdPrestamo(),
                        p.getIdEstudiante(),
                        p.getIdEjemplar(),
                        p.getFechaPrestamo(),
                        p.getFechaDevolucionEsperada(),
                        "AUTOSERVICIO_QR".equals(p.getCanal()) ? "Autoservicio QR" : "Presencial",
                        p.getEstadoPrestamo()
                });
            }
        };

        tablaPrestamos.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaPrestamos.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0) {
                txtIdEjemplarDev.setText(String.valueOf(modPrestamos.getValueAt(fila, 2)));
            }
        });

        btnDevolver.addActionListener(e -> {
            String respuesta = prestamoController.procesarDevolucion(
                    txtIdEjemplarDev.getText(),
                    chkDanado.isSelected()
            );
            mostrarDialogoResultado(this, "Devolución de Libro", respuesta);
            chkDanado.setSelected(false);
            refrescarVistaDevolucion.run();
        });

        JPanel panelTabla = new JPanel(new BorderLayout(0, 6));
        panelTabla.setOpaque(false);
        panelTabla.setBorder(crearBordeSeccion("Préstamos Activos (Seleccione una fila para autocompletar el ejemplar)"));
        panelTabla.add(scrollPrestamos, BorderLayout.CENTER);
        panelContenido.add(panelTabla, BorderLayout.CENTER);

        panelRaiz.add(crearBarraNavegacionModulo("Módulo de Devolución de Libros", refrescarVistaDevolucion), BorderLayout.NORTH);
        panelRaiz.add(panelContenido, BorderLayout.CENTER);
        return panelRaiz;
    }

    // =========================================================================
    // PANEL 3: Búsqueda de Catálogo y Notificación (Transparencia total y márgenes amplios)
    // =========================================================================

    private JPanel crearPanelBusquedaCatalogo() {
        JPanel panelRaiz = new JPanel(new BorderLayout(18, 18));
        panelRaiz.setOpaque(false);
        panelRaiz.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel panelContenido = new JPanel(new BorderLayout(16, 16));
        panelContenido.setOpaque(false);

        // 1. Panel superior de filtros (transparente)
        JPanel cardBusqueda = new JPanel(new BorderLayout(12, 12));
        cardBusqueda.setOpaque(false);
        cardBusqueda.setBorder(crearBordeSeccion("Consulta de Catálogo Bibliográfico en Tiempo Real"));

        JPanel panelFiltro = new JPanel(new GridBagLayout());
        panelFiltro.setOpaque(false);
        panelFiltro.setBorder(new EmptyBorder(10, 14, 10, 14));
        GridBagConstraints gbc = crearGbcBase();

        JTextField txtBusqueda = crearCampoTexto("", 28);
        txtBusqueda.setToolTipText("Escriba el título, autor o ISBN del libro que desea consultar");
        JButton btnBuscar = crearBotonPrincipal("Buscar en Catálogo");
        btnBuscar.setPreferredSize(new Dimension(190, 38));
        JButton btnMostrarTodo = crearBotonSecundario("Ver Todo");
        btnMostrarTodo.setPreferredSize(new Dimension(125, 38));

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFiltro.add(crearEtiqueta("Título, Autor o ISBN:"), gbc);
        gbc.gridx = 1;
        panelFiltro.add(txtBusqueda, gbc);
        gbc.gridx = 2;
        panelFiltro.add(btnBuscar, gbc);
        gbc.gridx = 3;
        panelFiltro.add(btnMostrarTodo, gbc);

        cardBusqueda.add(panelFiltro, BorderLayout.WEST);
        panelContenido.add(cardBusqueda, BorderLayout.NORTH);

        // 2. Tabla central estilizada (Fondo blanco, encabezado gris claro negrita 14, altura 30px)
        DefaultTableModel modCatalogo = crearModeloNoEditable(
                new Object[]{"ID", "ISBN", "Título del Libro", "Autor", "Editorial", "Año", "Total Ejemplares", "Disponibles Ahora", "Estado"}
        );
        JTable tablaCatalogo = crearTablaEstilizada(modCatalogo, new int[]{45, 115, 245, 170, 105, 60, 100, 115, 125});

        // 3. Panel inferior para Notificar al Administrador (transparente)
        JPanel panelNotificacion = new JPanel(new BorderLayout(12, 10));
        panelNotificacion.setOpaque(false);
        panelNotificacion.setBorder(crearBordeSeccion("¿No encontró el libro o no hay ejemplares disponibles? Notifique al Administrador"));

        JPanel formNotif = new JPanel(new GridBagLayout());
        formNotif.setOpaque(false);
        formNotif.setBorder(new EmptyBorder(8, 14, 6, 14));
        GridBagConstraints gbcN = crearGbcBase();

        JTextField txtEstudianteNotif = crearCampoTexto("EST001", 10);
        JTextField txtLibroFaltante = crearCampoTexto("", 25);
        JButton btnNotificarAdmin = crearBotonPrincipal("Notificar al Administrador");
        btnNotificarAdmin.setPreferredSize(new Dimension(230, 38));

        JLabel lblEstadoDisponibilidad = new JLabel(
                "Seleccione un libro sin stock o escriba su búsqueda para enviar una solicitud de adquisición."
        );
        lblEstadoDisponibilidad.setFont(FUENTE_NORMAL);
        lblEstadoDisponibilidad.setForeground(COLOR_TEXTO_OSCURO);

        gbcN.gridx = 0;
        gbcN.gridy = 0;
        formNotif.add(crearEtiqueta("Su Código de Estudiante:"), gbcN);
        gbcN.gridx = 1;
        formNotif.add(txtEstudianteNotif, gbcN);
        gbcN.gridx = 2;
        formNotif.add(Box.createHorizontalStrut(12), gbcN);
        gbcN.gridx = 3;
        formNotif.add(crearEtiqueta("Libro / Búsqueda a Notificar:"), gbcN);
        gbcN.gridx = 4;
        formNotif.add(txtLibroFaltante, gbcN);

        JPanel panelBtnNotif = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 8));
        panelBtnNotif.setOpaque(false);
        panelBtnNotif.add(btnNotificarAdmin);

        JPanel filaSuperiorNotif = new JPanel(new BorderLayout());
        filaSuperiorNotif.setOpaque(false);
        filaSuperiorNotif.add(formNotif, BorderLayout.WEST);
        filaSuperiorNotif.add(panelBtnNotif, BorderLayout.EAST);

        JPanel filaInferiorNotif = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 4));
        filaInferiorNotif.setOpaque(false);
        filaInferiorNotif.add(lblEstadoDisponibilidad);

        panelNotificacion.add(filaSuperiorNotif, BorderLayout.CENTER);
        panelNotificacion.add(filaInferiorNotif, BorderLayout.SOUTH);

        refrescarVistaCatalogo = () -> {
            String criterio = txtBusqueda.getText().trim();
            List<Libro> libros = catalogoController.buscarEnCatalogo(criterio);
            modCatalogo.setRowCount(0);
            for (Libro l : libros) {
                int disp = l.getEjemplaresDisponibles();
                String estado = disp > 0 ? "DISPONIBLE (" + disp + ")" : "SIN DISPONIBILIDAD";
                modCatalogo.addRow(new Object[]{
                        l.getIdLibro(),
                        l.getIsbn(),
                        l.getTitulo(),
                        l.getAutor(),
                        l.getEditorial() != null ? l.getEditorial() : "—",
                        l.getAnioPublicacion() != null ? l.getAnioPublicacion() : "—",
                        l.getTotalEjemplares(),
                        disp,
                        estado
                });
            }

            if (libros.isEmpty() && !criterio.isEmpty()) {
                txtLibroFaltante.setText(criterio + " (No existe en catálogo)");
                btnNotificarAdmin.setEnabled(true);
                lblEstadoDisponibilidad.setText(
                        "No se encontraron coincidencias en el catálogo. Puede pulsar 'Notificar al Administrador' para solicitar su adquisición."
                );
                lblEstadoDisponibilidad.setForeground(COLOR_GUINDA_UNMSM);
            } else {
                lblEstadoDisponibilidad.setText(
                        "Mostrando " + libros.size() + " libro(s). Si un título indica 'SIN DISPONIBILIDAD', selecciónelo para notificar al Administrador."
                );
                lblEstadoDisponibilidad.setForeground(COLOR_TEXTO_OSCURO);
            }
        };

        tablaCatalogo.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaCatalogo.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0) {
                String titulo = String.valueOf(modCatalogo.getValueAt(fila, 2));
                String autor = String.valueOf(modCatalogo.getValueAt(fila, 3));
                int disponibles = Integer.parseInt(String.valueOf(modCatalogo.getValueAt(fila, 7)));

                if (disponibles == 0) {
                    txtLibroFaltante.setText(titulo + " - " + autor + " (Sin ejemplares disponibles)");
                    btnNotificarAdmin.setEnabled(true);
                    lblEstadoDisponibilidad.setText(
                            "El libro seleccionado tiene 0 ejemplares disponibles. Puede pulsar 'Notificar al Administrador'."
                    );
                    lblEstadoDisponibilidad.setForeground(COLOR_GUINDA_UNMSM);
                } else {
                    txtLibroFaltante.setText(titulo + " - " + autor);
                    lblEstadoDisponibilidad.setText(
                            "El libro seleccionado cuenta con " + disponibles + " ejemplar(es) disponible(s) para préstamo inmediato."
                    );
                    lblEstadoDisponibilidad.setForeground(new Color(20, 110, 55));
                }
            }
        });

        btnBuscar.addActionListener(e -> refrescarVistaCatalogo.run());
        btnMostrarTodo.addActionListener(e -> {
            txtBusqueda.setText("");
            refrescarVistaCatalogo.run();
        });

        txtBusqueda.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                refrescarVistaCatalogo.run();
            }
        });

        btnNotificarAdmin.addActionListener(e -> {
            String textoNotificar = txtLibroFaltante.getText().trim();
            if (textoNotificar.isEmpty() && !txtBusqueda.getText().trim().isEmpty()) {
                textoNotificar = txtBusqueda.getText().trim();
            }
            int filaSel = tablaCatalogo.getSelectedRow();
            if (filaSel >= 0) {
                int disp = Integer.parseInt(String.valueOf(modCatalogo.getValueAt(filaSel, 7)));
                String tituloSel = String.valueOf(modCatalogo.getValueAt(filaSel, 2));
                if (disp > 0 && textoNotificar.startsWith(tituloSel)) {
                    JOptionPane.showMessageDialog(
                            this,
                            "El libro \"" + tituloSel + "\" sí cuenta con " + disp + " ejemplar(es) disponible(s).\n"
                                    + "La notificación al Administrador está destinada a libros agotados o que no existen en el catálogo.",
                            "Ejemplares Disponibles",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }
            }

            String respuesta = catalogoController.notificarFaltanteAlAdministrador(
                    txtEstudianteNotif.getText(),
                    textoNotificar
            );
            mostrarDialogoResultado(this, "Notificación al Administrador", respuesta);
        });

        JPanel panelTabla = new JPanel(new BorderLayout(0, 8));
        panelTabla.setOpaque(false);
        panelTabla.setBorder(crearBordeSeccion("Resultados del Catálogo y Disponibilidad en Tiempo Real"));
        panelTabla.add(crearScrollTablaEstilizado(tablaCatalogo), BorderLayout.CENTER);

        panelContenido.add(panelTabla, BorderLayout.CENTER);
        panelContenido.add(panelNotificacion, BorderLayout.SOUTH);

        panelRaiz.add(crearBarraNavegacionModulo("Búsqueda en Catálogo y Notificación de Faltantes", refrescarVistaCatalogo), BorderLayout.NORTH);
        panelRaiz.add(panelContenido, BorderLayout.CENTER);
        return panelRaiz;
    }

    // =========================================================================
    // PANEL 4: Autoservicio QR (100% transparente sobre el fondo)
    // =========================================================================

    private JPanel crearPanelAutoservicioQR() {
        JPanel panelRaiz = new JPanel(new BorderLayout(16, 16));
        panelRaiz.setOpaque(false);
        panelRaiz.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel panelExterior = new JPanel(new GridBagLayout());
        panelExterior.setOpaque(false);

        JPanel tarjetaKiosco = new JPanel();
        tarjetaKiosco.setLayout(new BoxLayout(tarjetaKiosco, BoxLayout.Y_AXIS));
        tarjetaKiosco.setBackground(new Color(255, 255, 255, 238));
        tarjetaKiosco.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE_SUTIL, 1, true),
                new EmptyBorder(32, 48, 32, 48)
        ));
        tarjetaKiosco.setPreferredSize(new Dimension(680, 430));

        JLabel lblIconoKiosco = new JLabel(new IconoTarjetaVectorial(TipoIconoTarjeta.QR, 58, 58));
        lblIconoKiosco.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblEstadoLector = new JLabel("LECTOR ÓPTICO QR / RFID ACTIVO", SwingConstants.CENTER);
        lblEstadoLector.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblEstadoLector.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEstadoLector.setForeground(COLOR_GUINDA_UNMSM);

        JLabel lblTituloKiosco = new JLabel("Escanee su carnet y luego el ejemplar", SwingConstants.CENTER);
        lblTituloKiosco.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTituloKiosco.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTituloKiosco.setForeground(COLOR_AZUL_INSTITUCIONAL);

        JLabel lblSubtitulo = new JLabel(
                "<html><div style='text-align: center;'>Ingrese o escanee la lectura combinada en formato <b>ESTUDIANTE|EJEMPLAR</b> (ejemplo: <code>EST001|EJM-003</code>) para procesar su transacción sin intervención humana.</div></html>",
                SwingConstants.CENTER
        );
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitulo.setFont(FUENTE_NORMAL);
        lblSubtitulo.setForeground(COLOR_TEXTO_OSCURO);

        JTextField txtLecturaQR = crearCampoTexto("EST001|EJM-003", 22);
        txtLecturaQR.setHorizontalAlignment(JTextField.CENTER);
        txtLecturaQR.setFont(new Font("Segoe UI", Font.BOLD, 19));
        txtLecturaQR.setMaximumSize(new Dimension(440, 48));
        txtLecturaQR.setPreferredSize(new Dimension(440, 48));
        txtLecturaQR.setAlignmentX(Component.CENTER_ALIGNMENT);

        JCheckBox chkDanoQR = new JCheckBox("Reportar daño físico al devolver ejemplar");
        chkDanoQR.setFont(FUENTE_NORMAL);
        chkDanoQR.setForeground(COLOR_TEXTO_OSCURO);
        chkDanoQR.setOpaque(false);
        chkDanoQR.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnKioscoPrestamo = crearBotonPrincipal("Solicitar Préstamo QR");
        btnKioscoPrestamo.setPreferredSize(new Dimension(220, 44));

        JButton btnKioscoDevolucion = crearBotonSecundario("Devolver Libro QR");
        btnKioscoDevolucion.setPreferredSize(new Dimension(220, 44));

        JPanel panelBotonesKiosco = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 6));
        panelBotonesKiosco.setOpaque(false);
        panelBotonesKiosco.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelBotonesKiosco.add(btnKioscoPrestamo);
        panelBotonesKiosco.add(btnKioscoDevolucion);

        btnKioscoPrestamo.addActionListener(e -> {
            String respuesta = autoservicioQRController.procesarLecturaQRPrestamo(txtLecturaQR.getText());
            mostrarDialogoResultado(this, "Autoservicio QR - Préstamo", respuesta);
        });

        btnKioscoDevolucion.addActionListener(e -> {
            String respuesta = autoservicioQRController.procesarLecturaQRDevolucion(
                    txtLecturaQR.getText(),
                    chkDanoQR.isSelected()
            );
            mostrarDialogoResultado(this, "Autoservicio QR - Devolución", respuesta);
            chkDanoQR.setSelected(false);
        });

        tarjetaKiosco.add(lblIconoKiosco);
        tarjetaKiosco.add(Box.createVerticalStrut(8));
        tarjetaKiosco.add(lblEstadoLector);
        tarjetaKiosco.add(Box.createVerticalStrut(6));
        tarjetaKiosco.add(lblTituloKiosco);
        tarjetaKiosco.add(Box.createVerticalStrut(8));
        tarjetaKiosco.add(lblSubtitulo);
        tarjetaKiosco.add(Box.createVerticalStrut(20));
        tarjetaKiosco.add(txtLecturaQR);
        tarjetaKiosco.add(Box.createVerticalStrut(10));
        tarjetaKiosco.add(chkDanoQR);
        tarjetaKiosco.add(Box.createVerticalStrut(18));
        tarjetaKiosco.add(panelBotonesKiosco);

        panelExterior.add(tarjetaKiosco);

        panelRaiz.add(crearBarraNavegacionModulo("Terminal de Autoservicio QR / RFID", null), BorderLayout.NORTH);
        panelRaiz.add(panelExterior, BorderLayout.CENTER);
        return panelRaiz;
    }

    // =========================================================================
    // PANEL 5: Reserva de Cubículos (100% transparente sobre el fondo)
    // =========================================================================

    private JPanel crearPanelReservaCubiculos() {
        JPanel panelRaiz = new JPanel(new BorderLayout(16, 16));
        panelRaiz.setOpaque(false);
        panelRaiz.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel panelContenido = new JPanel(new BorderLayout(16, 16));
        panelContenido.setOpaque(false);

        JPanel cardFormulario = new JPanel(new BorderLayout(12, 12));
        cardFormulario.setOpaque(false);
        cardFormulario.setBorder(crearBordeSeccion("Reservar Cubículo Grupal (Máximo 2 horas exactas | 1 reserva activa por día)"));

        JPanel camposPanel = new JPanel(new GridBagLayout());
        camposPanel.setOpaque(false);
        camposPanel.setBorder(new EmptyBorder(10, 14, 10, 14));
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
        camposPanel.add(Box.createHorizontalStrut(12), gbc);
        gbc.gridx = 3;
        camposPanel.add(crearEtiqueta("Estudiante:"), gbc);
        gbc.gridx = 4;
        camposPanel.add(txtEstCubiculo, gbc);
        gbc.gridx = 5;
        camposPanel.add(Box.createHorizontalStrut(12), gbc);
        gbc.gridx = 6;
        camposPanel.add(crearEtiqueta("Duración:"), gbc);
        gbc.gridx = 7;
        camposPanel.add(cmbDuracion, gbc);

        JButton btnReservarCub = crearBotonPrincipal("Reservar Cubículo");
        JButton btnLiberarCub = crearBotonSecundario("Finalizar Uso");

        JPanel panelAccion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        panelAccion.setOpaque(false);
        panelAccion.add(btnLiberarCub);
        panelAccion.add(btnReservarCub);

        cardFormulario.add(camposPanel, BorderLayout.WEST);
        cardFormulario.add(panelAccion, BorderLayout.EAST);
        panelContenido.add(cardFormulario, BorderLayout.NORTH);

        DefaultTableModel modCubiculos = crearModeloNoEditable(
                new Object[]{"Código", "Nombre del Espacio", "Capacidad", "Estado en Tiempo Real"}
        );
        JTable tablaCubiculos = crearTablaEstilizada(modCubiculos, new int[]{85, 165, 85, 125});

        DefaultTableModel modReservasCub = crearModeloNoEditable(
                new Object[]{"N.º Reserva", "Cubículo", "Estudiante", "Hora Inicio", "Hora Fin", "Estado"}
        );
        JTable tablaReservasCub = crearTablaEstilizada(modReservasCub, new int[]{85, 90, 95, 135, 135, 95});

        refrescarVistaCubiculos = () -> {
            modCubiculos.setRowCount(0);
            for (Cubiculo c : cubiculoController.listarCubiculos()) {
                modCubiculos.addRow(new Object[]{
                        c.getIdCubiculo(),
                        c.getNombreCodigo(),
                        c.getCapacidad() + " personas",
                        c.getEstadoCubiculo()
                });
            }
            modReservasCub.setRowCount(0);
            for (ReservaCubiculo rc : cubiculoController.listarReservasActivas()) {
                modReservasCub.addRow(new Object[]{
                        rc.getIdReservaCub(),
                        rc.getIdCubiculo(),
                        rc.getIdEstudiante(),
                        rc.getFechaHoraInicio(),
                        rc.getFechaHoraFin(),
                        rc.getEstadoReserva()
                });
            }
        };

        tablaCubiculos.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaCubiculos.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0) {
                txtIdCubiculo.setText(String.valueOf(modCubiculos.getValueAt(fila, 0)));
            }
        });

        btnReservarCub.addActionListener(e -> {
            int horas = cmbDuracion.getSelectedIndex() + 1;
            String respuesta = cubiculoController.reservarCubiculo(
                    txtIdCubiculo.getText(),
                    txtEstCubiculo.getText(),
                    horas
            );
            mostrarDialogoResultado(this, "Reserva de Cubículo", respuesta);
            refrescarVistaCubiculos.run();
        });

        btnLiberarCub.addActionListener(e -> {
            String respuesta = cubiculoController.liberarCubiculo(txtIdCubiculo.getText());
            mostrarDialogoResultado(this, "Liberación de Cubículo", respuesta);
            refrescarVistaCubiculos.run();
        });

        JPanel panelIzq = new JPanel(new BorderLayout(0, 6));
        panelIzq.setOpaque(false);
        panelIzq.setBorder(crearBordeSeccion("Disponibilidad de Cubículos (Clic para seleccionar)"));
        panelIzq.add(crearScrollTablaEstilizado(tablaCubiculos), BorderLayout.CENTER);

        JPanel panelDer = new JPanel(new BorderLayout(0, 6));
        panelDer.setOpaque(false);
        panelDer.setBorder(crearBordeSeccion("Reservas Activas en este Momento"));
        panelDer.add(crearScrollTablaEstilizado(tablaReservasCub), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzq, panelDer);
        split.setOpaque(false);
        split.setResizeWeight(0.45);
        split.setBorder(null);
        panelContenido.add(split, BorderLayout.CENTER);

        panelRaiz.add(crearBarraNavegacionModulo("Reserva de Cubículos de Estudio en Tiempo Real", refrescarVistaCubiculos), BorderLayout.NORTH);
        panelRaiz.add(panelContenido, BorderLayout.CENTER);
        return panelRaiz;
    }

    // =========================================================================
    // PANEL 6: Estado de Estudiantes y Préstamos Vigentes (100% transparente)
    // =========================================================================

    private JPanel crearPanelDirectorioEstudiantes() {
        JPanel panelRaiz = new JPanel(new BorderLayout(16, 16));
        panelRaiz.setOpaque(false);
        panelRaiz.setBorder(new EmptyBorder(20, 24, 20, 24));

        DefaultTableModel modEst = crearModeloNoEditable(
                new Object[]{"Código", "Nombre Completo", "Correo Institucional", "Estado Actual", "Fin de Suspensión"}
        );
        JTable tablaEst = crearTablaEstilizada(modEst, new int[]{90, 195, 215, 105, 145});

        DefaultTableModel modPrest = crearModeloNoEditable(
                new Object[]{"N.º Préstamo", "Estudiante", "Ejemplar", "Fecha Préstamo", "Fecha Devolución Esperada", "Modalidad"}
        );
        JTable tablaPrest = crearTablaEstilizada(modPrest, new int[]{90, 100, 105, 145, 155, 125});

        refrescarVistaEstudiantes = () -> {
            modEst.setRowCount(0);
            for (Estudiante est : prestamoController.listarEstudiantes()) {
                modEst.addRow(new Object[]{
                        est.getIdEstudiante(),
                        est.getNombreCompleto(),
                        est.getCorreo(),
                        est.getEstadoEstudiante(),
                        est.getFechaFinSuspension() != null ? est.getFechaFinSuspension() : "Sin sanción"
                });
            }
            modPrest.setRowCount(0);
            for (Prestamo p : prestamoController.listarPrestamosActivos()) {
                modPrest.addRow(new Object[]{
                        p.getIdPrestamo(),
                        p.getIdEstudiante(),
                        p.getIdEjemplar(),
                        p.getFechaPrestamo(),
                        p.getFechaDevolucionEsperada(),
                        p.getCanal()
                });
            }
        };

        JPanel pArriba = new JPanel(new BorderLayout(0, 6));
        pArriba.setOpaque(false);
        pArriba.setBorder(crearBordeSeccion("Padrón de Estudiantes y Estado de Habilitación"));
        pArriba.add(crearScrollTablaEstilizado(tablaEst), BorderLayout.CENTER);

        JPanel pAbajo = new JPanel(new BorderLayout(0, 6));
        pAbajo.setOpaque(false);
        pAbajo.setBorder(crearBordeSeccion("Préstamos Activos Registrados"));
        pAbajo.add(crearScrollTablaEstilizado(tablaPrest), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pArriba, pAbajo);
        split.setOpaque(false);
        split.setResizeWeight(0.5);
        split.setBorder(null);

        panelRaiz.add(crearBarraNavegacionModulo("Directorio de Estudiantes y Préstamos en Curso", refrescarVistaEstudiantes), BorderLayout.NORTH);
        panelRaiz.add(split, BorderLayout.CENTER);
        return panelRaiz;
    }

    // =========================================================================
    // PANEL 7: Modo Administrador (100% transparente sobre el fondo)
    // =========================================================================

    private void autenticarYAbrirModoAdministrador() {
        JPanel panelLogin = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = crearGbcBase();
        JTextField txtUsuario = crearCampoTexto("admin", 14);
        JPasswordField txtPassword = new JPasswordField("admin123", 14);
        txtPassword.setFont(FUENTE_NORMAL);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE_SUTIL, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelLogin.add(crearEtiqueta("Usuario Administrador:"), gbc);
        gbc.gridx = 1;
        panelLogin.add(txtUsuario, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelLogin.add(crearEtiqueta("Contraseña:"), gbc);
        gbc.gridx = 1;
        panelLogin.add(txtPassword, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JLabel lblHint = new JLabel("Credenciales por defecto: admin / admin123");
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblHint.setForeground(COLOR_TEXTO_OSCURO);
        panelLogin.add(lblHint, gbc);

        int opcion = JOptionPane.showConfirmDialog(
                this,
                panelLogin,
                "Autenticación - Modo Administrador",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (opcion == JOptionPane.OK_OPTION) {
            String user = txtUsuario.getText();
            String pass = new String(txtPassword.getPassword());
            if (catalogoController.autenticarAdministrador(user, pass)) {
                mostrarVista(VISTA_ADMIN);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Credenciales incorrectas. Acceso denegado al Modo Administrador.",
                        "Error de Autenticación",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private JPanel crearPanelModoAdministrador() {
        JPanel panelRaiz = new JPanel(new BorderLayout(14, 14));
        panelRaiz.setOpaque(false);
        panelRaiz.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel contenedor = new JPanel(new BorderLayout(14, 14));
        contenedor.setOpaque(false);

        JPanel barraVistasAdmin = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        barraVistasAdmin.setOpaque(false);

        JButton btnVistaCatalogo = crearBotonPrincipal("1. CRUD Catálogo de Libros");
        btnVistaCatalogo.setPreferredSize(new Dimension(235, 38));
        JButton btnVistaNotificaciones = crearBotonSecundario("2. Notificaciones de Faltantes");
        btnVistaNotificaciones.setPreferredSize(new Dimension(245, 38));
        JButton btnVistaPenalizaciones = crearBotonSecundario("3. Gestión de Penalizaciones");
        btnVistaPenalizaciones.setPreferredSize(new Dimension(235, 38));

        barraVistasAdmin.add(btnVistaCatalogo);
        barraVistasAdmin.add(btnVistaNotificaciones);
        barraVistasAdmin.add(btnVistaPenalizaciones);

        CardLayout cardAdmin = new CardLayout();
        JPanel panelCuerpoAdmin = new JPanel(cardAdmin);
        panelCuerpoAdmin.setOpaque(false);

        Runnable[] recargadoresSubVistas = new Runnable[3];
        panelCuerpoAdmin.add(crearSubPanelAdminCrudCatalogo(recargadoresSubVistas), "SUB_CRUD");
        panelCuerpoAdmin.add(crearSubPanelAdminNotificaciones(recargadoresSubVistas), "SUB_NOTIF");
        panelCuerpoAdmin.add(crearSubPanelAdminPenalizaciones(recargadoresSubVistas), "SUB_PENAL");

        refrescarVistaAdmin = () -> {
            for (Runnable r : recargadoresSubVistas) {
                if (r != null) {
                    r.run();
                }
            }
        };

        btnVistaCatalogo.addActionListener(e -> {
            if (recargadoresSubVistas[0] != null) recargadoresSubVistas[0].run();
            cardAdmin.show(panelCuerpoAdmin, "SUB_CRUD");
        });

        btnVistaNotificaciones.addActionListener(e -> {
            if (recargadoresSubVistas[1] != null) recargadoresSubVistas[1].run();
            cardAdmin.show(panelCuerpoAdmin, "SUB_NOTIF");
        });

        btnVistaPenalizaciones.addActionListener(e -> {
            if (recargadoresSubVistas[2] != null) recargadoresSubVistas[2].run();
            cardAdmin.show(panelCuerpoAdmin, "SUB_PENAL");
        });

        contenedor.add(barraVistasAdmin, BorderLayout.NORTH);
        contenedor.add(panelCuerpoAdmin, BorderLayout.CENTER);

        panelRaiz.add(crearBarraNavegacionModulo("Modo Administrador - Mantenimiento del Sistema", refrescarVistaAdmin), BorderLayout.NORTH);
        panelRaiz.add(contenedor, BorderLayout.CENTER);
        return panelRaiz;
    }

    private JPanel crearSubPanelAdminCrudCatalogo(Runnable[] recargadores) {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setOpaque(false);

        JPanel formCrud = new JPanel(new GridBagLayout());
        formCrud.setOpaque(false);
        formCrud.setBorder(crearBordeSeccion("Mantenimiento del Catálogo Bibliográfico (Crear, Actualizar y Eliminar Libros)"));
        GridBagConstraints gbc = crearGbcBase();

        JTextField txtIdLibro = crearCampoTexto("", 5);
        txtIdLibro.setEditable(false);
        JTextField txtIsbn = crearCampoTexto("", 13);
        JTextField txtTitulo = crearCampoTexto("", 22);
        JTextField txtAutor = crearCampoTexto("", 18);
        JTextField txtEditorial = crearCampoTexto("", 12);
        JTextField txtAnio = crearCampoTexto("2024", 6);
        JComboBox<Integer> cmbEjemplaresNuevos = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        cmbEjemplaresNuevos.setFont(FUENTE_NORMAL);

        gbc.gridx = 0; gbc.gridy = 0;
        formCrud.add(crearEtiqueta("ID:"), gbc);
        gbc.gridx = 1;
        formCrud.add(txtIdLibro, gbc);
        gbc.gridx = 2;
        formCrud.add(crearEtiqueta("ISBN:"), gbc);
        gbc.gridx = 3;
        formCrud.add(txtIsbn, gbc);
        gbc.gridx = 4;
        formCrud.add(crearEtiqueta("Título:"), gbc);
        gbc.gridx = 5;
        formCrud.add(txtTitulo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formCrud.add(crearEtiqueta("Autor:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        formCrud.add(txtAutor, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 4;
        formCrud.add(crearEtiqueta("Editorial / Año / Ejemplares (al crear):"), gbc);

        JPanel subPanelEdAnio = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        subPanelEdAnio.setOpaque(false);
        subPanelEdAnio.add(txtEditorial);
        subPanelEdAnio.add(txtAnio);
        subPanelEdAnio.add(cmbEjemplaresNuevos);
        gbc.gridx = 5;
        formCrud.add(subPanelEdAnio, gbc);

        JPanel panelBotonesCrud = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        panelBotonesCrud.setOpaque(false);
        JButton btnCrear = crearBotonPrincipal("Crear Libro");
        btnCrear.setPreferredSize(new Dimension(150, 38));
        JButton btnActualizar = crearBotonSecundario("Actualizar");
        btnActualizar.setPreferredSize(new Dimension(140, 38));
        JButton btnEliminar = crearBotonSecundario("Eliminar");
        btnEliminar.setPreferredSize(new Dimension(130, 38));
        JButton btnLimpiar = crearBotonSecundario("Limpiar");
        btnLimpiar.setPreferredSize(new Dimension(115, 38));

        panelBotonesCrud.add(btnLimpiar);
        panelBotonesCrud.add(btnEliminar);
        panelBotonesCrud.add(btnActualizar);
        panelBotonesCrud.add(btnCrear);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 6;
        gbc.anchor = GridBagConstraints.EAST;
        formCrud.add(panelBotonesCrud, gbc);

        DefaultTableModel modLibros = crearModeloNoEditable(
                new Object[]{"ID", "ISBN", "Título", "Autor", "Editorial", "Año", "Total Ejemplares", "Disponibles"}
        );
        JTable tablaLibros = crearTablaEstilizada(modLibros, new int[]{45, 115, 245, 175, 110, 60, 100, 95});

        Runnable recargarLibros = () -> {
            modLibros.setRowCount(0);
            for (Libro l : catalogoController.buscarEnCatalogo("")) {
                modLibros.addRow(new Object[]{
                        l.getIdLibro(),
                        l.getIsbn(),
                        l.getTitulo(),
                        l.getAutor(),
                        l.getEditorial() != null ? l.getEditorial() : "",
                        l.getAnioPublicacion() != null ? l.getAnioPublicacion() : "",
                        l.getTotalEjemplares(),
                        l.getEjemplaresDisponibles()
                });
            }
        };
        recargadores[0] = recargarLibros;

        tablaLibros.getSelectionModel().addListSelectionListener(e -> {
            int f = tablaLibros.getSelectedRow();
            if (!e.getValueIsAdjusting() && f >= 0) {
                txtIdLibro.setText(String.valueOf(modLibros.getValueAt(f, 0)));
                txtIsbn.setText(String.valueOf(modLibros.getValueAt(f, 1)));
                txtTitulo.setText(String.valueOf(modLibros.getValueAt(f, 2)));
                txtAutor.setText(String.valueOf(modLibros.getValueAt(f, 3)));
                txtEditorial.setText(String.valueOf(modLibros.getValueAt(f, 4)));
                txtAnio.setText(String.valueOf(modLibros.getValueAt(f, 5)));
            }
        });

        btnLimpiar.addActionListener(e -> {
            txtIdLibro.setText("");
            txtIsbn.setText("");
            txtTitulo.setText("");
            txtAutor.setText("");
            txtEditorial.setText("");
            txtAnio.setText("2024");
            tablaLibros.clearSelection();
        });

        btnCrear.addActionListener(e -> {
            int cant = (Integer) cmbEjemplaresNuevos.getSelectedItem();
            String r = catalogoController.crearLibro(
                    txtIsbn.getText(), txtTitulo.getText(), txtAutor.getText(),
                    txtEditorial.getText(), txtAnio.getText(), cant
            );
            mostrarDialogoResultado(this, "Crear Libro", r);
            recargarLibros.run();
        });

        btnActualizar.addActionListener(e -> {
            long id = txtIdLibro.getText().isEmpty() ? 0L : Long.parseLong(txtIdLibro.getText());
            String r = catalogoController.actualizarLibro(
                    id, txtIsbn.getText(), txtTitulo.getText(), txtAutor.getText(),
                    txtEditorial.getText(), txtAnio.getText()
            );
            mostrarDialogoResultado(this, "Actualizar Libro", r);
            recargarLibros.run();
        });

        btnEliminar.addActionListener(e -> {
            long id = txtIdLibro.getText().isEmpty() ? 0L : Long.parseLong(txtIdLibro.getText());
            String r = catalogoController.eliminarLibro(id);
            mostrarDialogoResultado(this, "Eliminar Libro", r);
            btnLimpiar.doClick();
            recargarLibros.run();
        });

        JPanel panelTabla = new JPanel(new BorderLayout(0, 6));
        panelTabla.setOpaque(false);
        panelTabla.setBorder(crearBordeSeccion("Catálogo Actual (Seleccione una fila para editar o eliminar)"));
        panelTabla.add(crearScrollTablaEstilizado(tablaLibros), BorderLayout.CENTER);

        panel.add(formCrud, BorderLayout.NORTH);
        panel.add(panelTabla, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearSubPanelAdminNotificaciones(Runnable[] recargadores) {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setOpaque(false);

        DefaultTableModel modNotif = crearModeloNoEditable(
                new Object[]{"ID Notificación", "Estudiante Solicitante", "Texto de Búsqueda / Libro Faltante", "Fecha de Solicitud", "Estado"}
        );
        JTable tablaNotif = crearTablaEstilizada(modNotif, new int[]{95, 130, 380, 150, 110});

        Runnable recargarNotif = () -> {
            modNotif.setRowCount(0);
            for (NotificacionAdmin n : catalogoController.listarNotificaciones()) {
                modNotif.addRow(new Object[]{
                        n.getIdNotificacion(),
                        n.getIdEstudiante(),
                        n.getTextoBusqueda(),
                        n.getFechaSolicitud(),
                        n.getEstado()
                });
            }
        };
        recargadores[1] = recargarNotif;

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        panelAcciones.setOpaque(false);
        JButton btnMarcarAtendida = crearBotonPrincipal("Marcar como ATENDIDA");
        btnMarcarAtendida.setPreferredSize(new Dimension(210, 38));
        JButton btnMarcarDescartada = crearBotonSecundario("Marcar como DESCARTADA");
        btnMarcarDescartada.setPreferredSize(new Dimension(225, 38));
        JButton btnEliminarNotif = crearBotonSecundario("Eliminar Registro");
        btnEliminarNotif.setPreferredSize(new Dimension(175, 38));

        panelAcciones.add(btnEliminarNotif);
        panelAcciones.add(btnMarcarDescartada);
        panelAcciones.add(btnMarcarAtendida);

        btnMarcarAtendida.addActionListener(e -> {
            int f = tablaNotif.getSelectedRow();
            if (f < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione una notificación de la tabla.");
                return;
            }
            long id = Long.parseLong(String.valueOf(modNotif.getValueAt(f, 0)));
            mostrarDialogoResultado(this, "Notificación", catalogoController.cambiarEstadoNotificacion(id, "ATENDIDA"));
            recargarNotif.run();
        });

        btnMarcarDescartada.addActionListener(e -> {
            int f = tablaNotif.getSelectedRow();
            if (f < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione una notificación de la tabla.");
                return;
            }
            long id = Long.parseLong(String.valueOf(modNotif.getValueAt(f, 0)));
            mostrarDialogoResultado(this, "Notificación", catalogoController.cambiarEstadoNotificacion(id, "DESCARTADA"));
            recargarNotif.run();
        });

        btnEliminarNotif.addActionListener(e -> {
            int f = tablaNotif.getSelectedRow();
            if (f < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione una notificación de la tabla.");
                return;
            }
            long id = Long.parseLong(String.valueOf(modNotif.getValueAt(f, 0)));
            mostrarDialogoResultado(this, "Eliminar Notificación", catalogoController.eliminarNotificacion(id));
            recargarNotif.run();
        });

        JPanel panelTabla = new JPanel(new BorderLayout(0, 6));
        panelTabla.setOpaque(false);
        panelTabla.setBorder(crearBordeSeccion("Notificaciones de Libros Faltantes Enviadas por Estudiantes (NOTIFICACION_ADMIN)"));
        panelTabla.add(crearScrollTablaEstilizado(tablaNotif), BorderLayout.CENTER);

        panel.add(panelTabla, BorderLayout.CENTER);
        panel.add(panelAcciones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearSubPanelAdminPenalizaciones(Runnable[] recargadores) {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setOpaque(false);

        DefaultTableModel modEst = crearModeloNoEditable(
                new Object[]{"Código", "Estudiante", "Correo", "Estado", "Fecha Fin de Suspensión"}
        );
        JTable tablaEst = crearTablaEstilizada(modEst, new int[]{90, 210, 220, 110, 160});

        Runnable recargarEst = () -> {
            modEst.setRowCount(0);
            for (Estudiante est : prestamoController.listarEstudiantes()) {
                modEst.addRow(new Object[]{
                        est.getIdEstudiante(),
                        est.getNombreCompleto(),
                        est.getCorreo(),
                        est.getEstadoEstudiante(),
                        est.getFechaFinSuspension() != null ? est.getFechaFinSuspension() : "—"
                });
            }
        };
        recargadores[2] = recargarEst;

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        panelBotones.setOpaque(false);
        JButton btnLevantarSancion = crearBotonPrincipal("Levantar Suspensión (Reactivar)");
        btnLevantarSancion.setPreferredSize(new Dimension(250, 38));
        JButton btnSuspender = crearBotonSecundario("Aplicar Suspensión (2 días)");
        btnSuspender.setPreferredSize(new Dimension(230, 38));

        panelBotones.add(btnSuspender);
        panelBotones.add(btnLevantarSancion);

        btnLevantarSancion.addActionListener(e -> {
            int f = tablaEst.getSelectedRow();
            if (f < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un estudiante de la tabla.");
                return;
            }
            String idEst = String.valueOf(modEst.getValueAt(f, 0));
            mostrarDialogoResultado(this, "Gestión de Penalizaciones",
                    catalogoController.gestionarPenalizacionEstudiante(idEst, false));
            recargarEst.run();
        });

        btnSuspender.addActionListener(e -> {
            int f = tablaEst.getSelectedRow();
            if (f < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un estudiante de la tabla.");
                return;
            }
            String idEst = String.valueOf(modEst.getValueAt(f, 0));
            mostrarDialogoResultado(this, "Gestión de Penalizaciones",
                    catalogoController.gestionarPenalizacionEstudiante(idEst, true));
            recargarEst.run();
        });

        JPanel panelTabla = new JPanel(new BorderLayout(0, 6));
        panelTabla.setOpaque(false);
        panelTabla.setBorder(crearBordeSeccion("Control de Sanciones y Suspensiones de Estudiantes (Regla: 2 días sin multa monetaria)"));
        panelTabla.add(crearScrollTablaEstilizado(tablaEst), BorderLayout.CENTER);

        panel.add(panelTabla, BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);
        return panel;
    }

    // =========================================================================
    // PANEL DE FONDO SEMI-TRANSPARENTE GLOBAL (Design-Doc 4.2: paintComponent)
    // =========================================================================

    private class PanelFondoDashboard extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int w = getWidth();
            int h = getHeight();

            GradientPaint gp = new GradientPaint(0, 0, new Color(248, 250, 253), 0, h, new Color(235, 240, 248));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.16f));
            if (imagenFondoUniversidad != null) {
                g2d.drawImage(imagenFondoUniversidad, 0, 0, w, h, this);
            } else {
                g2d.setColor(COLOR_AZUL_INSTITUCIONAL);
                g2d.setStroke(new BasicStroke(2.5f));
                for (int x = 60; x < w - 60; x += 90) {
                    g2d.drawRect(x, h / 4, 65, h / 2);
                }
            }
            g2d.dispose();
        }
    }

    // =========================================================================
    // ICONOS DESCRIPTIVOS PARA LAS TARJETAS DEL DASHBOARD
    // =========================================================================

    private enum TipoIconoTarjeta {
        PRESTAMO, DEVOLUCION, CATALOGO, QR, CUBICULO, ESTUDIANTE
    }

    private static class IconoTarjetaVectorial implements Icon {
        private final TipoIconoTarjeta tipo;
        private final int ancho;
        private final int alto;

        public IconoTarjetaVectorial(TipoIconoTarjeta tipo, int ancho, int alto) {
            this.tipo = tipo;
            this.ancho = ancho;
            this.alto = alto;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(115, 22, 34, 22));
            g2.fillOval(x, y, ancho, alto);

            g2.setColor(COLOR_GUINDA_UNMSM);
            g2.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int cx = x + ancho / 2;
            int cy = y + alto / 2;

            switch (tipo) {
                case PRESTAMO:
                    g2.drawRoundRect(cx - 16, cy - 10, 15, 22, 3, 3);
                    g2.drawRoundRect(cx + 1, cy - 10, 15, 22, 3, 3);
                    g2.setColor(COLOR_AZUL_INSTITUCIONAL);
                    g2.drawLine(cx, cy - 18, cx, cy - 6);
                    g2.drawLine(cx, cy - 18, cx - 5, cy - 13);
                    g2.drawLine(cx, cy - 18, cx + 5, cy - 13);
                    break;
                case DEVOLUCION:
                    g2.drawRoundRect(cx - 14, cy - 12, 28, 24, 4, 4);
                    g2.drawLine(cx - 8, cy - 4, cx + 8, cy - 4);
                    g2.setColor(new Color(22, 120, 65));
                    g2.drawLine(cx - 6, cy + 3, cx - 1, cy + 8);
                    g2.drawLine(cx - 1, cy + 8, cx + 8, cy - 1);
                    break;
                case CATALOGO:
                    g2.drawOval(cx - 14, cy - 14, 20, 20);
                    g2.setColor(COLOR_AZUL_INSTITUCIONAL);
                    g2.drawLine(cx + 3, cy + 3, cx + 14, cy + 14);
                    g2.drawLine(cx - 9, cy - 4, cx + 1, cy - 4);
                    break;
                case QR:
                    g2.drawRect(cx - 15, cy - 15, 11, 11);
                    g2.drawRect(cx + 4, cy - 15, 11, 11);
                    g2.drawRect(cx - 15, cy + 4, 11, 11);
                    g2.setColor(COLOR_AZUL_INSTITUCIONAL);
                    g2.fillRect(cx + 4, cy + 4, 5, 5);
                    g2.fillRect(cx + 10, cy + 10, 5, 5);
                    break;
                case CUBICULO:
                    g2.drawRoundRect(cx - 14, cy - 8, 28, 16, 6, 6);
                    g2.setColor(COLOR_AZUL_INSTITUCIONAL);
                    g2.drawOval(cx - 11, cy - 17, 7, 7);
                    g2.drawOval(cx + 4, cy - 17, 7, 7);
                    g2.drawOval(cx - 11, cy + 10, 7, 7);
                    g2.drawOval(cx + 4, cy + 10, 7, 7);
                    break;
                case ESTUDIANTE:
                    g2.drawOval(cx - 7, cy - 14, 14, 14);
                    g2.setColor(COLOR_AZUL_INSTITUCIONAL);
                    g2.drawArc(cx - 15, cy + 2, 30, 18, 0, 180);
                    break;
            }
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return ancho;
        }

        @Override
        public int getIconHeight() {
            return alto;
        }
    }

    private ImageIcon crearEscudoUnmsmFallback(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(145, 28, 42));
        g2.fillOval(2, 2, size - 4, size - 4);
        g2.setColor(COLOR_DORADO_UNMSM);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawOval(2, 2, size - 4, size - 4);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.drawString("SM", size / 2 - 11, size / 2 + 5);
        g2.dispose();
        return new ImageIcon(img);
    }

    // =========================================================================
    // COMPONENTES AUXILIARES DE UI/UX (Reglas estrictas de transparencia y estilo)
    // =========================================================================

    private TitledBorder crearBordeSeccion(String titulo) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_BORDE_SUTIL, 1, true),
                " " + titulo + " ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                FUENTE_SECCION,
                COLOR_AZUL_INSTITUCIONAL
        );
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(FUENTE_ETIQUETA);
        label.setForeground(COLOR_TEXTO_OSCURO);
        return label;
    }

    private JTextField crearCampoTexto(String valorInicial, int columnas) {
        JTextField campo = new JTextField(valorInicial, columnas);
        campo.setFont(FUENTE_NORMAL);
        campo.setForeground(COLOR_TEXTO_OSCURO);
        campo.setBackground(Color.WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE_SUTIL, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
        return campo;
    }

    private JButton crearBotonPrincipal(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(FUENTE_BOTON);
        boton.setPreferredSize(new Dimension(205, 38));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    private JButton crearBotonSecundario(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(FUENTE_NORMAL);
        boton.setPreferredSize(new Dimension(190, 38));
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
        tabla.setBackground(Color.WHITE);
        tabla.setForeground(COLOR_TEXTO_OSCURO);
        tabla.setGridColor(new Color(230, 234, 240));
        tabla.setRowHeight(30);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setSelectionBackground(new Color(224, 236, 252));
        tabla.setSelectionForeground(COLOR_TEXTO_OSCURO);
        tabla.setFillsViewportHeight(true);

        JTableHeader header = tabla.getTableHeader();
        header.setFont(FUENTE_HEADER_TABLA);
        header.setBackground(COLOR_ENCABEZADO_TABLA);
        header.setForeground(COLOR_TEXTO_OSCURO);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        headerRenderer.setBackground(COLOR_ENCABEZADO_TABLA);
        headerRenderer.setForeground(COLOR_TEXTO_OSCURO);
        headerRenderer.setFont(FUENTE_HEADER_TABLA);
        headerRenderer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 1, COLOR_BORDE_SUTIL),
                new EmptyBorder(6, 8, 6, 8)
        ));

        TableColumnModel columnModel = tabla.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setHeaderRenderer(headerRenderer);
            if (i < anchosColumnas.length) {
                columnModel.getColumn(i).setPreferredWidth(anchosColumnas[i]);
            }
        }
        return tabla;
    }

    private JScrollPane crearScrollTablaEstilizado(JTable tabla) {
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_SUTIL, 1));
        return scroll;
    }

    /**
     * Configura el diseño visual, alineación, proporciones, encabezado institucional y
     * renderizado zebra/estados de la tabla "Préstamos Activos" en el Módulo de Devolución.
     */
    private void estilizarTablaPrestamos(JTable tabla, JScrollPane scrollPane) {
        final Color COLOR_HEADER_FONDO = new Color(0x6E, 0x14, 0x1A); // Guinda institucional #6E141A
        final Color COLOR_HEADER_DIVISOR = new Color(0x88, 0x26, 0x2E);
        final Color COLOR_FILA_PAR = new Color(0xFF, 0xFF, 0xFF);     // Blanco #FFFFFF
        final Color COLOR_FILA_IMPAR = new Color(0xF8, 0xFA, 0xFC);   // Gris muy suave #F8FAFC
        final Color COLOR_LINEA_DIVISORIA = new Color(0xE2, 0xE8, 0xF0); // #E2E8F0
        final Color COLOR_SELECCION_FONDO = new Color(0xDB, 0xEA, 0xFE); // Azul suave #DBEAFE
        final Color COLOR_SELECCION_TEXTO = new Color(0x0F, 0x17, 0x2A); // Texto oscuro #0F172A
        final Color COLOR_TEXTO_BASE = new Color(0x1E, 0x29, 0x3B);
        final Color COLOR_ESTADO_ACTIVO = new Color(0x15, 0x80, 0x3D);   // Verde #15803D
        final Color COLOR_FECHA_VENCIDA = new Color(0xB9, 0x1C, 0x1C);   // Rojo #B91C1C
        final Color COLOR_BORDE_SCROLL = new Color(0xCB, 0xD5, 0xE1);    // #CBD5E1

        final Font FUENTE_HEADER = new Font("Segoe UI", Font.BOLD, 13);
        final Font FUENTE_CELDA_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
        final Font FUENTE_CELDA_NEGRITA = new Font("Segoe UI", Font.BOLD, 13);

        // 1. Comportamiento general y grilla de la tabla
        tabla.setFont(FUENTE_CELDA_REGULAR);
        tabla.setRowHeight(32);
        tabla.setBackground(COLOR_FILA_PAR);
        tabla.setForeground(COLOR_TEXTO_BASE);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setSelectionBackground(COLOR_SELECCION_FONDO);
        tabla.setSelectionForeground(COLOR_SELECCION_TEXTO);
        tabla.setShowHorizontalLines(true);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(COLOR_LINEA_DIVISORIA);
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setFillsViewportHeight(true);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // 2. Estilo del encabezado (JTableHeader: 34px, negrita 13px, guinda #6E141A, texto blanco, sin bordes 3D)
        JTableHeader header = tabla.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 34));
        header.setBackground(COLOR_HEADER_FONDO);
        header.setForeground(Color.WHITE);
        header.setFont(FUENTE_HEADER);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                lbl.setOpaque(true);
                lbl.setBackground(COLOR_HEADER_FONDO);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(FUENTE_HEADER);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 1, COLOR_HEADER_DIVISOR),
                        new EmptyBorder(0, 10, 0, 10)
                ));
                return lbl;
            }
        };
        header.setDefaultRenderer(headerRenderer);

        // 3. Renderer personalizado para celdas (zebra striping, padding 10px, estado verde y fechas vencidas)
        final java.text.SimpleDateFormat formatoFechaHora = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Object valorFormateado = value;
                if (value instanceof java.util.Date) {
                    valorFormateado = formatoFechaHora.format((java.util.Date) value);
                }

                JLabel celda = (JLabel) super.getTableCellRendererComponent(
                        table, valorFormateado, isSelected, hasFocus, row, column);

                celda.setOpaque(true);
                celda.setHorizontalAlignment(SwingConstants.CENTER);
                celda.setBorder(new EmptyBorder(0, 10, 0, 10));
                celda.setFont(FUENTE_CELDA_REGULAR);

                // Fondo zebra striping o selección
                if (isSelected) {
                    celda.setBackground(COLOR_SELECCION_FONDO);
                    celda.setForeground(COLOR_SELECCION_TEXTO);
                } else {
                    celda.setBackground(row % 2 == 0 ? COLOR_FILA_PAR : COLOR_FILA_IMPAR);
                    celda.setForeground(COLOR_TEXTO_BASE);
                }

                // Columna 4: "Fecha Límite (7 días)" -> resaltar en rojo (#B91C1C) si está vencida
                if (column == 4 && value instanceof java.util.Date) {
                    java.util.Date fechaLimite = (java.util.Date) value;
                    if (fechaLimite.before(new java.util.Date())) {
                        celda.setForeground(COLOR_FECHA_VENCIDA);
                        celda.setFont(FUENTE_CELDA_NEGRITA);
                    }
                }

                // Columna 6: "Estado" -> renderizar "ACTIVO" en negrita verde (#15803D)
                if (column == 6 && value != null) {
                    String textoEstado = String.valueOf(value).trim().toUpperCase();
                    if ("ACTIVO".equals(textoEstado)) {
                        celda.setForeground(COLOR_ESTADO_ACTIVO);
                        celda.setFont(FUENTE_CELDA_NEGRITA);
                    } else if ("VENCIDO".equals(textoEstado)) {
                        celda.setForeground(COLOR_FECHA_VENCIDA);
                        celda.setFont(FUENTE_CELDA_NEGRITA);
                    }
                }

                return celda;
            }
        };

        // 4. Anchos mínimos, preferidos y máximos proporcionales por columna
        // Columnas: 0:N.° Préstamo, 1:Estudiante, 2:Código Ejemplar, 3:Fecha Préstamo, 4:Fecha Límite, 5:Canal, 6:Estado
        int[] anchosMinimos =    {95,  110, 120, 165, 165, 125, 95};
        int[] anchosPreferidos = {100, 125, 135, 195, 195, 145, 110};
        int[] anchosMaximos =    {120, 180, 190, 280, 280, 210, 150};

        TableColumnModel columnModel = tabla.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            javax.swing.table.TableColumn col = columnModel.getColumn(i);
            col.setHeaderRenderer(headerRenderer);
            col.setCellRenderer(cellRenderer);
            if (i < anchosPreferidos.length) {
                col.setMinWidth(anchosMinimos[i]);
                col.setPreferredWidth(anchosPreferidos[i]);
                col.setMaxWidth(anchosMaximos[i]);
            }
        }

        // 5. Configuración limpia del contenedor JScrollPane
        if (scrollPane != null) {
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(true);
            scrollPane.getViewport().setBackground(COLOR_FILA_PAR);
            scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_SCROLL, 1));
        }
    }

    private GridBagConstraints crearGbcBase() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private void mostrarDialogoResultado(Component parent, String titulo, String mensajeOriginal) {
        if (mensajeOriginal == null || mensajeOriginal.trim().isEmpty()) {
            return;
        }

        int tipoIcono = JOptionPane.INFORMATION_MESSAGE;
        String textoMayus = mensajeOriginal.toUpperCase();

        if (textoMayus.contains("[ERROR") || textoMayus.contains("ERROR AL") || textoMayus.contains("FALLO")) {
            tipoIcono = JOptionPane.ERROR_MESSAGE;
        } else if (textoMayus.contains("[VALIDACIÓN") || textoMayus.contains("[RECHAZADO")
                || textoMayus.contains("SUSPENDIDO") || textoMayus.contains("SANCIÓN")
                || textoMayus.contains("NO EXISTE") || textoMayus.contains("NO ESTÁ DISPONIBLE")
                || textoMayus.contains("LÍMITE")) {
            tipoIcono = JOptionPane.WARNING_MESSAGE;
        }

        String mensajeLimpio = mensajeOriginal
                .replaceAll("\\[[^\\]]+\\]\\s*", "")
                .trim();

        JOptionPane.showMessageDialog(parent, mensajeLimpio, titulo, tipoIcono);
    }
}
