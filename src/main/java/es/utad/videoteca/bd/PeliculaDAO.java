package es.utad.videoteca.bd;

import es.utad.videoteca.modelo.Pelicula;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) de la tabla {@code peliculas}.
 * <p>
 * Es la única clase que contiene SQL. Sigue siempre el patrón JDBC:
 * {@code Connection} &rarr; {@code PreparedStatement} &rarr; {@code ResultSet} (solo en consultas),
 * cerrando los tres recursos automáticamente con try-with-resources.
 * </p>
 *
 * @author Diego
 * @version 1.0
 */
public class PeliculaDAO {

    /** Crea la tabla solo si no existe. */
    private static final String SQL_CREAR_TABLA = """
            CREATE TABLE IF NOT EXISTS peliculas (
                id SERIAL PRIMARY KEY,
                titulo VARCHAR(200) NOT NULL,
                director VARCHAR(100),
                genero VARCHAR(50),
                anio INTEGER,
                duracion INTEGER, -- en minutos
                sinopsis TEXT,
                formato VARCHAR(50), -- DVD, Blu-ray, 4K, etc.
                estado VARCHAR(30), -- Nuevo, Usado, Edición coleccionista, etc.
                fecha_adquisicion DATE DEFAULT CURRENT_DATE
            )""";

    /** Borra todas las filas y reinicia el contador del id para que vuelva a empezar en 1. */
    private static final String SQL_VACIAR_TABLA = "TRUNCATE TABLE peliculas RESTART IDENTITY";

    /** Inserta una película. Cada "?" es un parámetro que se rellena con un setXxx(). */
    private static final String SQL_INSERTAR = """
            INSERT INTO peliculas (titulo, director, genero, anio, duracion, sinopsis, formato, estado)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)""";

    /** Obtiene todas las películas ordenadas por id. */
    private static final String SQL_SELECCIONAR_TODAS = """
            SELECT id, titulo, director, genero, anio, duracion, sinopsis, formato, estado, fecha_adquisicion
            FROM peliculas
            ORDER BY id""";

    /** Cuenta las filas de la tabla. */
    private static final String SQL_CONTAR = "SELECT COUNT(*) FROM peliculas";

    /**
     * Crea el DAO. La conexión no se guarda aquí: cada método abre la suya y la cierra al terminar.
     */
    public PeliculaDAO() {
    }

    /**
     * Crea la tabla {@code peliculas} si no existe y la deja vacía, con el id empezando de nuevo en 1.
     * <p>
     * Así el programa se puede ejecutar tantas veces como se quiera sin ir duplicando las películas.
     * </p>
     *
     * @throws SQLException si falla la conexión o alguna de las sentencias
     */
    public void crearYVaciarTabla() throws SQLException {
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement psCrear = con.prepareStatement(SQL_CREAR_TABLA);
             PreparedStatement psVaciar = con.prepareStatement(SQL_VACIAR_TABLA)) {

            psCrear.executeUpdate();
            psVaciar.executeUpdate();
        }
    }

    /**
     * Inserta una lista de películas en la tabla {@code peliculas}.
     * <p>
     * Se prepara la sentencia una sola vez y se reutiliza para cada película, cambiando solo los
     * parámetros. Además, todo se hace dentro de una transacción: o se insertan todas, o ninguna.
     * </p>
     *
     * @param peliculas películas a insertar
     * @return número de filas insertadas en la tabla
     * @throws SQLException si falla la conexión o alguna inserción (en ese caso se deshace todo)
     */
    public int insertar(List<Pelicula> peliculas) throws SQLException {
        int insertadas = 0;

        try (Connection con = ConexionBD.obtenerConexion()) {
            con.setAutoCommit(false); // empezamos una transacción

            try (PreparedStatement ps = con.prepareStatement(SQL_INSERTAR)) {
                for (Pelicula p : peliculas) {
                    ps.setString(1, p.getTitulo());
                    ps.setString(2, p.getDirector());
                    ps.setString(3, p.getGenero());
                    ponerEnteroONulo(ps, 4, p.getAnio());
                    ponerEnteroONulo(ps, 5, p.getDuracion());
                    ps.setString(6, p.getSinopsis());
                    ps.setString(7, p.getFormato());
                    ps.setString(8, p.getEstado());

                    insertadas += ps.executeUpdate(); // devuelve 1 por cada fila insertada
                }
                con.commit(); // todo ha ido bien: se guardan los cambios
            } catch (SQLException e) {
                con.rollback(); // algo ha fallado: no se guarda ninguna película
                throw e;        // y se avisa a quien llamó
            }
        }
        return insertadas;
    }

    /**
     * Obtiene todas las películas de la tabla, ordenadas por id.
     *
     * @return lista con todas las películas (vacía si la tabla no tiene filas)
     * @throws SQLException si falla la conexión o la consulta
     */
    public List<Pelicula> obtenerTodas() throws SQLException {
        List<Pelicula> peliculas = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(SQL_SELECCIONAR_TODAS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) { // avanza fila a fila mientras queden filas
                Pelicula p = new Pelicula();
                p.setId(rs.getInt("id"));
                p.setTitulo(rs.getString("titulo"));
                p.setDirector(rs.getString("director"));
                p.setGenero(rs.getString("genero"));
                // getObject(..., Integer.class) devuelve null si la columna es NULL; getInt() devolvería 0
                p.setAnio(rs.getObject("anio", Integer.class));
                p.setDuracion(rs.getObject("duracion", Integer.class));
                p.setSinopsis(rs.getString("sinopsis"));
                p.setFormato(rs.getString("formato"));
                p.setEstado(rs.getString("estado"));
                p.setFechaAdquisicion(rs.getObject("fecha_adquisicion", LocalDate.class));
                peliculas.add(p);
            }
        }
        return peliculas;
    }

    /**
     * Cuenta cuántas películas hay en la tabla.
     *
     * @return número de filas de la tabla {@code peliculas}
     * @throws SQLException si falla la conexión o la consulta
     */
    public int contar() throws SQLException {
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(SQL_CONTAR);
             ResultSet rs = ps.executeQuery()) {

            rs.next(); // COUNT(*) siempre devuelve exactamente una fila
            return rs.getInt(1);
        }
    }

    /**
     * Asigna un entero a un parámetro, o NULL si el valor es {@code null}.
     * <p>
     * Hace falta porque {@code setInt()} recibe un {@code int} primitivo y no admite {@code null}.
     * </p>
     *
     * @param ps       sentencia preparada
     * @param posicion posición del "?" (empieza en 1)
     * @param valor    valor a asignar, puede ser {@code null}
     * @throws SQLException si la posición no es válida
     */
    private void ponerEnteroONulo(PreparedStatement ps, int posicion, Integer valor) throws SQLException {
        if (valor == null) {
            ps.setNull(posicion, Types.INTEGER);
        } else {
            ps.setInt(posicion, valor);
        }
    }
}
