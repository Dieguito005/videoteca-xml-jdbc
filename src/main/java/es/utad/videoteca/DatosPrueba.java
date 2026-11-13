package es.utad.videoteca;

import es.utad.videoteca.modelo.Pelicula;

import java.util.List;

/**
 * Datos de prueba de la práctica.
 * <p>
 * Están separados del {@code Main} y de la lógica para que se puedan cambiar
 * sin tocar el resto del código.
 * </p>
 *
 * @author Diego
 * @version 1.0
 */
public final class DatosPrueba {

    /**
     * Constructor privado: es una clase de utilidad con métodos estáticos y no se instancia.
     */
    private DatosPrueba() {
    }

    /**
     * Devuelve las 5 películas iniciales que se insertan en la base de datos.
     *
     * @return lista inmutable con 5 películas
     */
    public static List<Pelicula> peliculasIniciales() {
        return List.of(
                new Pelicula("El Padrino", "Francis Ford Coppola", "Drama", 1972, 175,
                        "Blu-ray", "Edición coleccionista",
                        "El patriarca de una familia mafiosa transfiere el control de su imperio a su hijo."),
                new Pelicula("La lista de Schindler", "Steven Spielberg", "Drama histórico", 1993, 195,
                        "DVD", "Nuevo",
                        "Un empresario alemán salva la vida de más de mil refugiados judíos durante el Holocausto."),
                new Pelicula("Pulp Fiction", "Quentin Tarantino", "Crimen", 1994, 154,
                        "Blu-ray", "Usado",
                        "Las vidas de dos sicarios, un boxeador y una pareja de atracadores se entrelazan "
                                + "en cuatro historias de violencia y redención."),
                new Pelicula("El caballero oscuro", "Christopher Nolan", "Acción", 2008, 152,
                        "4K", "Nuevo",
                        "Batman se enfrenta al Joker, una mente criminal que busca sumir a Gotham City en la anarquía."),
                new Pelicula("Origen", "Christopher Nolan", "Ciencia ficción", 2010, 148,
                        "Blu-ray", "Nuevo",
                        "Un ladrón capaz de infiltrarse en los sueños recibe la misión inversa de implantar "
                                + "una idea en la mente de un objetivo.")
        );
    }

    /**
     * Devuelve las 3 películas que se escriben en el XML de importación.
     * <p>
     * Incluye una segunda edición de "El Padrino" en 4K para demostrar que la misma película
     * puede estar varias veces en la videoteca con distinto formato.
     * </p>
     *
     * @return lista inmutable con 3 películas
     */
    public static List<Pelicula> peliculasParaXml() {
        return List.of(
                new Pelicula("Interstellar", "Christopher Nolan", "Ciencia ficción", 2014, 169,
                        "4K", "Nuevo",
                        "Un grupo de astronautas viaja a través de un agujero de gusano en busca "
                                + "de un nuevo hogar para la humanidad."),
                new Pelicula("Matrix", "Lana Wachowski y Lilly Wachowski", "Ciencia ficción", 1999, 136,
                        "DVD", "Usado",
                        "Un hacker descubre que el mundo en el que vive es una simulación creada por máquinas "
                                + "y se une a la rebelión contra ellas."),
                new Pelicula("El Padrino", "Francis Ford Coppola", "Drama", 1972, 175,
                        "4K", "Nuevo",
                        "El patriarca de una familia mafiosa transfiere el control de su imperio a su hijo.")
        );
    }
}
