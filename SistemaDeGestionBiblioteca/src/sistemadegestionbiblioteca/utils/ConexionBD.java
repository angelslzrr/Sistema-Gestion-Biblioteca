package sistemadegestionbiblioteca.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase utilitaria para gestionar la conexión a la base de datos Oracle
 * alojada en Docker (servicio FREEPDB1) mediante JDBC puro.
 * 
 * @author Grupo 6
 */
public final class ConexionBD {

    /** URL de conexión JDBC para Oracle Database en contenedor Docker (FREEPDB1). */
    public static final String URL = "jdbc:oracle:thin:@localhost:1521/FREEPDB1";

    /** Usuario del esquema de la biblioteca en Oracle. */
    public static final String USER = "biblioteca_grupo6";

    /** Contraseña del usuario de la base de datos. */
    public static final String PASSWORD = "biblioteca123";

    /** Nombre de la clase del driver JDBC de Oracle. */
    public static final String DRIVER = "oracle.jdbc.OracleDriver";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("[ADVERTENCIA] No se encontró el driver JDBC de Oracle (" + DRIVER + ") en el classpath. "
                    + "Asegúrese de agregar ojdbc11.jar / ojdbc8.jar a las librerías del proyecto en NetBeans.");
        }
    }

    private ConexionBD() {
        // Constructor privado para evitar instanciación
    }

    /**
     * Obtiene una nueva conexión activa hacia Oracle Database.
     *
     * @return Instancia de {@link Connection} lista para operar.
     * @throws SQLException Si ocurre un error de red o autenticación con la BD.
     */
    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Confirma (commit) de forma segura una transacción activa.
     *
     * @param conn Conexión con autoCommit desactivado.
     * @throws SQLException Si falla el commit en la base de datos.
     */
    public static void commit(Connection conn) throws SQLException {
        if (conn != null && !conn.isClosed() && !conn.getAutoCommit()) {
            conn.commit();
        }
    }

    /**
     * Revierte (rollback) de forma segura una transacción activa sin propagar excepciones secundarias.
     *
     * @param conn Conexión con autoCommit desactivado.
     */
    public static void rollback(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed() && !conn.getAutoCommit()) {
                    conn.rollback();
                }
            } catch (SQLException e) {
                System.err.println("[ERROR] Error al ejecutar rollback: " + e.getMessage());
            }
        }
    }

    /**
     * Cierra de forma silenciosa y segura un {@link ResultSet}, un {@link Statement} y una {@link Connection}.
     *
     * @param rs   ResultSet a cerrar (puede ser null).
     * @param stmt Statement o PreparedStatement a cerrar (puede ser null).
     * @param conn Connection a cerrar (puede ser null).
     */
    public static void cerrarRecursos(ResultSet rs, Statement stmt, Connection conn) {
        cerrar(rs);
        cerrar(stmt);
        cerrar(conn);
    }

    /**
     * Cierra un {@link ResultSet} de forma segura.
     *
     * @param rs ResultSet a cerrar.
     */
    public static void cerrar(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("[ERROR] Error al cerrar ResultSet: " + e.getMessage());
            }
        }
    }

    /**
     * Cierra un {@link Statement} o {@link PreparedStatement} de forma segura.
     *
     * @param stmt Statement a cerrar.
     */
    public static void cerrar(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("[ERROR] Error al cerrar Statement: " + e.getMessage());
            }
        }
    }

    /**
     * Cierra una {@link Connection} restaurando previamente el modo autoCommit si estaba deshabilitado.
     *
     * @param conn Conexión a cerrar.
     */
    public static void cerrar(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    try {
                        if (!conn.getAutoCommit()) {
                            conn.setAutoCommit(true);
                        }
                    } catch (SQLException ignored) {
                    }
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("[ERROR] Error al cerrar Connection: " + e.getMessage());
            }
        }
    }
}
