package es.utad.videoteca;

import es.utad.videoteca.modelo.Pelicula;
import es.utad.videoteca.servicio.VideotecaService;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Punto de entrada de la aplicación. Prueba el flujo completo de la práctica paso a paso:
 * <ol>
 *     <li>Crear la base de datos y la tabla, e insertar 5 películas.</li>
 *     <li>Generar un XML de prueba con 3 películas.</li>
 *     <li>Método A: importar el XML a PostgreSQL (quedan 8 películas).</li>
 *     <li>Método B: exportar las 8 películas de PostgreSQL a un nuevo XML.</li>
 * </ol>
 * <p>
 * Todas las excepciones se capturan aquí, en un único sitio, para mostrar al usuario
 * un mensaje claro según el tipo de error.
 * </p>
 *
 * @author Diego
 * @version 1.0
 */
public class Main {

    /** Fichero XML de prueba que se genera en el paso 2 y se importa en el paso 3. */
    private static final String XML_IMPORTACION = "nuevas_peliculas.xml";

    /** Fichero XML de salida por defecto del paso 4. */
    private static final String XML_EXPORTACION = "videoteca_exportada.xml";

    /**
     * Constructor privado: esta clase solo contiene el método main y no se instancia.
     */
    private Main() {
    }

    /**
     * Ejecuta las pruebas de la práctica.
     *
     * @param args opcional: el primer argumento es el nombre del XML de exportación;
     *             si no se indica, se usa {@value #XML_EXPORTACION}
     */
    public static void main(String[] args) {
        String rutaExportacion = args.length > 0 ? args[0] : XML_EXPORTACION;
        VideotecaService videoteca = new VideotecaService();

        try {
            mostrarPaso("PASO 1: Crear la base de datos y la tabla, e insertar las 5 películas iniciales");
            videoteca.inicializarBaseDeDatos(DatosPrueba.peliculasIniciales());

            mostrarPaso("PASO 2: Generar por código el XML de prueba con 3 películas");
            videoteca.generarXmlDePrueba(DatosPrueba.peliculasParaXml(), XML_IMPORTACION);

            mostrarPaso("PASO 3: Método A - Importar el XML a PostgreSQL");
            videoteca.importarDesdeXml(XML_IMPORTACION);
            System.out.println("Películas que hay ahora en la base de datos: " + videoteca.contarPeliculas());
            for (Pelicula pelicula : videoteca.listarPeliculas()) {
                System.out.println("   " + pelicula);
            }

            mostrarPaso("PASO 4: Método B - Exportar la base de datos a XML");
            videoteca.exportarAXml(rutaExportacion);

            System.out.println();
            System.out.println("Programa terminado correctamente.");

        } catch (SQLException e) {
            System.err.println("Error de base de datos: " + e.getMessage());
            System.err.println("Comprueba que PostgreSQL está arrancado y que el usuario y la contraseña de ConexionBD son correctos.");
        } catch (SAXException e) {
            System.err.println("El fichero XML está mal formado: " + e.getMessage());
        } catch (ParserConfigurationException | TransformerException e) {
            System.err.println("Error al procesar el XML: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error al leer o escribir el fichero: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Dato numérico incorrecto en el XML: " + e.getMessage());
        }
    }

    /**
     * Muestra por consola el título de un paso, para que la salida sea fácil de seguir.
     *
     * @param titulo texto del paso
     */
    private static void mostrarPaso(String titulo) {
        System.out.println();
        System.out.println("==================================================================");
        System.out.println(titulo);
        System.out.println("==================================================================");
    }
}
