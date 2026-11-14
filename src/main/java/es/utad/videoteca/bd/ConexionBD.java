package es.utad.videoteca.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Centraliza los datos de conexión a PostgreSQL y la creación de la base de datos de la videoteca.
 * <p>
 * Tener la URL, el usuario y la contraseña en un único sitio evita repetirlos en cada clase:
 * si cambian, solo hay que tocar este fichero.
 * </p>
 *
 * @author Diego
 * @version 1.0
 */
public final class ConexionBD {

    /** Servidor y puerto de PostgreSQL (el puerto por defecto es 5433). */
    private static final String SERVIDOR = "jdbc:postgresql://localhost:5433/";

    /** Base de datos que existe siempre en cualquier instalación de PostgreSQL. */
    private static final String BD_POR_DEFECTO = "postgres";

    /** Nombre de la base de datos de la práctica. */
    public static final String NOMBRE_BD = "videoteca";

    /** Usuario de PostgreSQL. */
    private static final String USUARIO = "postgres";

    /** Contraseña de PostgreSQL. CAMBIA ESTE VALOR por la contraseña de tu instalación. */
    private static final String CONTRASENA = "postgres";

    /**
     * Constructor privado: es una clase de utilidad con métodos estáticos y no se instancia.
     */
    private ConexionBD() {
    }

    /**
     * Abre una conexión con la base de datos de la videoteca.
     * <p>
     * Quien llama a este método es responsable de cerrar la conexión.
     * En este proyecto se hace siempre con try-with-resources.
     * </p>
     *
     * @return conexión abierta con la base de datos {@value #NOMBRE_BD}
     * @throws SQLException si PostgreSQL no está arrancado o el usuario/contraseña no son correctos
     */
    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(SERVIDOR + NOMBRE_BD, USUARIO, CONTRASENA);
    }

    /**
     * Crea la base de datos {@value #NOMBRE_BD} si todavía no existe.
     * <p>
     * Como la base de datos aún puede no existir, nos conectamos primero a la base de datos
     * {@code postgres}, que viene con cualquier instalación, y desde ahí consultamos el
     * catálogo {@code pg_database}.
     * </p>
     *
     * @return {@code true} si se ha creado ahora, {@code false} si ya existía
     * @throws SQLException si falla la conexión o la creación
     */
    public static boolean crearBaseDeDatosSiNoExiste() throws SQLException {
        String sqlExiste = "SELECT 1 FROM pg_database WHERE datname = ?";

        try (Connection con = DriverManager.getConnection(SERVIDOR + BD_POR_DEFECTO, USUARIO, CONTRASENA);
             PreparedStatement psExiste = con.prepareStatement(sqlExiste)) {

            psExiste.setString(1, NOMBRE_BD);
            try (ResultSet rs = psExiste.executeQuery()) {
                if (rs.next()) {
                    return false; // ya existe, no hay que hacer nada
                }
            }

            // Los "?" solo sirven para VALORES, no para nombres de tablas o bases de datos.
            // Por eso el nombre va concatenado. Es seguro porque es una constante nuestra,
            // no un texto que escriba el usuario.
            try (PreparedStatement psCrear = con.prepareStatement("CREATE DATABASE " + NOMBRE_BD)) {
                psCrear.executeUpdate();
            }
            return true;
        }
    }
}
