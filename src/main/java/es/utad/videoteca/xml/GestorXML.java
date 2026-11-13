package es.utad.videoteca.xml;

import es.utad.videoteca.modelo.Pelicula;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Lee y escribe ficheros XML de películas usando DOM, la API nativa de Java.
 * <p>
 * DOM carga el XML entero en memoria como un árbol de nodos. Es la forma más sencilla
 * de trabajar con ficheros pequeños como los de una videoteca.
 * </p>
 * <p>
 * Estructura que se lee y se escribe:
 * </p>
 * <pre>
 * &lt;peliculas&gt;
 *     &lt;pelicula&gt;
 *         &lt;titulo&gt;, &lt;director&gt;, &lt;genero&gt;, &lt;anio&gt;, &lt;duracion&gt;,
 *         &lt;formato&gt;, &lt;estado&gt;, &lt;sinopsis&gt;
 *     &lt;/pelicula&gt;
 * &lt;/peliculas&gt;
 * </pre>
 *
 * @author Diego
 * @version 1.0
 */
public class GestorXML {

    /** Etiqueta raíz del documento. */
    private static final String RAIZ = "peliculas";
    /** Etiqueta de cada película. */
    private static final String PELICULA = "pelicula";
    /** Etiqueta del título. */
    private static final String TITULO = "titulo";
    /** Etiqueta del director. */
    private static final String DIRECTOR = "director";
    /** Etiqueta del género. */
    private static final String GENERO = "genero";
    /** Etiqueta del año. */
    private static final String ANIO = "anio";
    /** Etiqueta de la duración. */
    private static final String DURACION = "duracion";
    /** Etiqueta del formato. */
    private static final String FORMATO = "formato";
    /** Etiqueta del estado. */
    private static final String ESTADO = "estado";
    /** Etiqueta de la sinopsis. */
    private static final String SINOPSIS = "sinopsis";

    /**
     * Crea un gestor de XML. No necesita ninguna configuración previa.
     */
    public GestorXML() {
    }

    /**
     * Lee un fichero XML con DOM y devuelve las películas que contiene.
     *
     * @param rutaXml ruta del fichero XML que se quiere leer
     * @return lista con una película por cada elemento {@code <pelicula>} encontrado
     * @throws FileNotFoundException        si el fichero no existe
     * @throws IOException                  si hay un error al leer el fichero
     * @throws SAXException                 si el XML está mal formado
     * @throws ParserConfigurationException si no se puede crear el parser DOM
     * @throws NumberFormatException        si {@code <anio>} o {@code <duracion>} no contienen un número
     */
    public List<Pelicula> leerPeliculas(String rutaXml)
            throws IOException, SAXException, ParserConfigurationException {

        File archivo = new File(rutaXml);
        if (!archivo.exists()) {
            throw new FileNotFoundException("No existe el fichero XML: " + archivo.getAbsolutePath());
        }

        // 1) Factoría -> 2) DocumentBuilder -> 3) Document (el XML entero cargado como un árbol)
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document documento = builder.parse(archivo);
        documento.getDocumentElement().normalize();

        // 4) Buscar todos los nodos <pelicula> y recorrerlos
        NodeList nodosPelicula = documento.getElementsByTagName(PELICULA);
        List<Pelicula> peliculas = new ArrayList<>();

        for (int i = 0; i < nodosPelicula.getLength(); i++) {
            Element elemento = (Element) nodosPelicula.item(i);

            Pelicula pelicula = new Pelicula(
                    leerTexto(elemento, TITULO),
                    leerTexto(elemento, DIRECTOR),
                    leerTexto(elemento, GENERO),
                    leerEntero(elemento, ANIO),
                    leerEntero(elemento, DURACION),
                    leerTexto(elemento, FORMATO),
                    leerTexto(elemento, ESTADO),
                    leerTexto(elemento, SINOPSIS));

            peliculas.add(pelicula);
        }
        return peliculas;
    }

    /**
     * Escribe una lista de películas en un fichero XML con DOM, respetando la estructura definida
     * y con el contenido indentado para que sea fácil de leer.
     * <p>
     * Si el fichero ya existe, se sobrescribe.
     * </p>
     *
     * @param peliculas películas que se van a guardar
     * @param rutaXml   ruta del fichero XML de salida
     * @return número de elementos {@code <pelicula>} grabados en el fichero
     * @throws ParserConfigurationException si no se puede crear el documento DOM
     * @throws TransformerException         si falla la conversión del árbol DOM a texto XML
     * @throws IOException                  si no se puede escribir el fichero
     */
    public int escribirPeliculas(List<Pelicula> peliculas, String rutaXml)
            throws ParserConfigurationException, TransformerException, IOException {

        // 1) Crear un documento vacío
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document documento = builder.newDocument();

        // 2) Crear el nodo raíz <peliculas> y colgarlo del documento
        Element raiz = documento.createElement(RAIZ);
        documento.appendChild(raiz);

        // 3) Por cada película, crear <pelicula> con sus hijos en el orden exacto de la estructura
        int grabadas = 0;
        for (Pelicula p : peliculas) {
            Element elementoPelicula = documento.createElement(PELICULA);
            raiz.appendChild(elementoPelicula);

            anadirHijo(documento, elementoPelicula, TITULO, p.getTitulo());
            anadirHijo(documento, elementoPelicula, DIRECTOR, p.getDirector());
            anadirHijo(documento, elementoPelicula, GENERO, p.getGenero());
            anadirHijo(documento, elementoPelicula, ANIO, enteroATexto(p.getAnio()));
            anadirHijo(documento, elementoPelicula, DURACION, enteroATexto(p.getDuracion()));
            anadirHijo(documento, elementoPelicula, FORMATO, p.getFormato());
            anadirHijo(documento, elementoPelicula, ESTADO, p.getEstado());
            anadirHijo(documento, elementoPelicula, SINOPSIS, p.getSinopsis());
            grabadas++;
        }

        // 4) Transformer: convierte el árbol DOM en texto XML con indentación
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        // La cabecera la escribimos nosotros para que quede igual que en el enunciado:
        // el Transformer de Java añade standalone="no" o pega la cabecera a <peliculas> en la misma línea
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        // 5) Escribir en el fichero. try-with-resources cierra el fichero aunque haya un error
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(rutaXml), StandardCharsets.UTF_8)) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.newLine();
            transformer.transform(new DOMSource(documento), new StreamResult(writer));
        }
        return grabadas;
    }

    /**
     * Devuelve el texto de la primera etiqueta hija con ese nombre.
     *
     * @param padre    elemento {@code <pelicula>} del que se lee
     * @param etiqueta nombre de la etiqueta hija (por ejemplo, "titulo")
     * @return texto sin espacios al principio ni al final, o {@code null} si la etiqueta no existe o está vacía
     */
    private String leerTexto(Element padre, String etiqueta) {
        NodeList nodos = padre.getElementsByTagName(etiqueta);
        if (nodos.getLength() == 0) {
            return null;
        }
        String texto = nodos.item(0).getTextContent().trim();
        return texto.isEmpty() ? null : texto;
    }

    /**
     * Devuelve el valor numérico de la primera etiqueta hija con ese nombre.
     *
     * @param padre    elemento {@code <pelicula>} del que se lee
     * @param etiqueta nombre de la etiqueta hija (por ejemplo, "anio")
     * @return el número, o {@code null} si la etiqueta no existe o está vacía
     * @throws NumberFormatException si el texto de la etiqueta no es un número entero
     */
    private Integer leerEntero(Element padre, String etiqueta) {
        String texto = leerTexto(padre, etiqueta);
        if (texto == null) {
            return null;
        }
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("La etiqueta <" + etiqueta + "> no contiene un número válido: " + texto);
        }
    }

    /**
     * Crea una etiqueta hija con un texto dentro y la añade al elemento padre.
     * Si el valor es {@code null}, la etiqueta se crea vacía para mantener la estructura.
     *
     * @param documento documento DOM en el que se crean los nodos
     * @param padre     elemento al que se añade la etiqueta
     * @param etiqueta  nombre de la nueva etiqueta
     * @param valor     texto que irá dentro de la etiqueta
     */
    private void anadirHijo(Document documento, Element padre, String etiqueta, String valor) {
        Element hijo = documento.createElement(etiqueta);
        hijo.appendChild(documento.createTextNode(valor == null ? "" : valor));
        padre.appendChild(hijo);
    }

    /**
     * Convierte un entero a texto para escribirlo en el XML.
     *
     * @param valor número a convertir, puede ser {@code null}
     * @return el número como texto, o {@code null} si el valor es {@code null}
     */
    private String enteroATexto(Integer valor) {
        return valor == null ? null : String.valueOf(valor);
    }
}
