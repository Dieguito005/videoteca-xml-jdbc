package es.utad.videoteca.servicio;

import es.utad.videoteca.bd.ConexionBD;
import es.utad.videoteca.bd.PeliculaDAO;
import es.utad.videoteca.modelo.Pelicula;
import es.utad.videoteca.xml.GestorXML;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Lógica de la videoteca: une la parte de base de datos ({@link PeliculaDAO})
 * con la parte de ficheros XML ({@link GestorXML}).
 * <p>
 * Aquí están los dos métodos que pide el enunciado:
 * </p>
 * <ul>
 *     <li><b>Método A</b>: {@link #importarDesdeXml(String)} (XML &rarr; base de datos).</li>
 *     <li><b>Método B</b>: {@link #exportarAXml(String)} (base de datos &rarr; XML).</li>
 * </ul>
 * <p>
 * Las excepciones no se capturan aquí: se lanzan hacia arriba para que el {@code Main}
 * decida qué mensaje mostrar al usuario.
 * </p>
 *
 * @author Diego
 * @version 1.0
 */
public class VideotecaService {

    /** Acceso a la tabla peliculas. */
    private final PeliculaDAO peliculaDAO;

    /** Lectura y escritura de ficheros XML. */
    private final GestorXML gestorXML;

    /**
     * Crea el servicio con su DAO y su gestor de XML.
     */
    public VideotecaService() {
        this.peliculaDAO = new PeliculaDAO();
        this.gestorXML = new GestorXML();
    }

    /**
     * Prepara la base de datos para las pruebas: crea la base de datos si no existe,
     * crea la tabla {@code peliculas} (dejándola vacía) e inserta las películas iniciales.
     *
     * @param peliculasIniciales películas de prueba que se insertan en la tabla
     * @return número de películas insertadas
     * @throws SQLException si falla cualquier operación con la base de datos
     */
    public int inicializarBaseDeDatos(List<Pelicula> peliculasIniciales) throws SQLException {
        boolean creada = ConexionBD.crearBaseDeDatosSiNoExiste();
        System.out.println(creada
                ? "Base de datos '" + ConexionBD.NOMBRE_BD + "' creada."
                : "La base de datos '" + ConexionBD.NOMBRE_BD + "' ya existía.");

        peliculaDAO.crearYVaciarTabla();
        System.out.println("Tabla 'peliculas' creada y vacía (el id vuelve a empezar en 1).");

        int insertadas = peliculaDAO.insertar(peliculasIniciales);
        System.out.println("Películas iniciales insertadas en la tabla: " + insertadas);
        return insertadas;
    }

    /**
     * Genera por código el fichero XML de prueba que después se importará.
     *
     * @param peliculas películas que se escriben en el XML
     * @param rutaXml   ruta del fichero XML que se crea
     * @return número de elementos {@code <pelicula>} grabados en el fichero
     * @throws ParserConfigurationException si no se puede crear el documento DOM
     * @throws TransformerException         si falla la generación del XML
     * @throws IOException                  si no se puede escribir el fichero
     */
    public int generarXmlDePrueba(List<Pelicula> peliculas, String rutaXml)
            throws ParserConfigurationException, TransformerException, IOException {
        int grabadas = gestorXML.escribirPeliculas(peliculas, rutaXml);
        System.out.println("XML de prueba '" + rutaXml + "' generado con " + grabadas + " películas.");
        return grabadas;
    }

    /**
     * <b>Método A.</b> Lee y parsea un fichero XML con DOM e inserta cada {@code <pelicula>}
     * en la tabla {@code peliculas} usando JDBC.
     * <p>
     * Primero se lee el XML entero y después se inserta. Así, si el XML tiene un error,
     * no se llega a insertar nada en la base de datos.
     * </p>
     *
     * @param rutaXml ruta del fichero XML que se quiere importar
     * @return número de películas insertadas en la tabla
     * @throws IOException                  si el fichero no existe o no se puede leer
     * @throws SAXException                 si el XML está mal formado
     * @throws ParserConfigurationException si no se puede crear el parser DOM
     * @throws SQLException                 si falla la inserción en la base de datos
     */
    public int importarDesdeXml(String rutaXml)
            throws IOException, SAXException, ParserConfigurationException, SQLException {

        List<Pelicula> peliculas = gestorXML.leerPeliculas(rutaXml);
        System.out.println("Elementos <pelicula> leídos de '" + rutaXml + "': " + peliculas.size());

        int insertadas = peliculaDAO.insertar(peliculas);
        System.out.println("Total de películas insertadas en la tabla 'peliculas': " + insertadas);
        return insertadas;
    }

    /**
     * <b>Método B.</b> Lee todas las películas de la tabla {@code peliculas} con JDBC
     * y las vuelca en el fichero XML que se le indica, respetando la estructura definida.
     *
     * @param rutaXml ruta o nombre del fichero XML de salida (por ejemplo, "videoteca_exportada.xml")
     * @return número de películas grabadas en el fichero
     * @throws SQLException                 si falla la consulta a la base de datos
     * @throws ParserConfigurationException si no se puede crear el documento DOM
     * @throws TransformerException         si falla la generación del XML
     * @throws IOException                  si no se puede escribir el fichero
     */
    public int exportarAXml(String rutaXml)
            throws SQLException, ParserConfigurationException, TransformerException, IOException {

        List<Pelicula> peliculas = peliculaDAO.obtenerTodas();
        System.out.println("Películas leídas de la tabla 'peliculas': " + peliculas.size());

        int grabadas = gestorXML.escribirPeliculas(peliculas, rutaXml);
        System.out.println("Total de películas grabadas en el archivo '" + rutaXml + "': " + grabadas);
        return grabadas;
    }

    /**
     * Cuenta las películas que hay ahora mismo en la base de datos.
     *
     * @return número de filas de la tabla {@code peliculas}
     * @throws SQLException si falla la consulta
     */
    public int contarPeliculas() throws SQLException {
        return peliculaDAO.contar();
    }

    /**
     * Devuelve todas las películas de la base de datos, ordenadas por id.
     *
     * @return lista de películas
     * @throws SQLException si falla la consulta
     */
    public List<Pelicula> listarPeliculas() throws SQLException {
        return peliculaDAO.obtenerTodas();
    }
}
