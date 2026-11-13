package es.utad.videoteca.modelo;

import java.time.LocalDate;

/**
 * Representa una película de la videoteca, es decir, una edición concreta que tiene el coleccionista.
 * <p>
 * Cada objeto equivale a una fila de la tabla {@code peliculas} y a un elemento {@code <pelicula>}
 * del XML. La misma película puede aparecer varias veces con distinto formato o estado
 * (DVD, Blu-ray, 4K, edición coleccionista...), por eso no hay ninguna restricción de título único.
 * </p>
 * <p>
 * El {@code id} y la {@code fechaAdquisicion} los genera la base de datos y no forman parte
 * de la estructura del XML.
 * </p>
 *
 * @author Diego
 * @version 1.0
 */
public class Pelicula {

    /** Identificador generado por PostgreSQL (columna SERIAL). Vale {@code null} hasta que se guarda. */
    private Integer id;

    /** Título de la película. Es obligatorio (NOT NULL en la tabla). */
    private String titulo;

    /** Director o directores. */
    private String director;

    /** Género cinematográfico. */
    private String genero;

    /** Año de estreno. Se usa {@link Integer} para poder representar un valor nulo. */
    private Integer anio;

    /** Duración en minutos. Se usa {@link Integer} para poder representar un valor nulo. */
    private Integer duracion;

    /** Formato de la edición: DVD, Blu-ray, 4K, etc. */
    private String formato;

    /** Estado de la edición: Nuevo, Usado, Edición coleccionista, etc. */
    private String estado;

    /** Resumen del argumento. */
    private String sinopsis;

    /** Fecha en la que se añadió a la videoteca (por defecto, el día de la inserción). */
    private LocalDate fechaAdquisicion;

    /**
     * Constructor vacío. Se usa al leer de la base de datos, rellenando después con los setters.
     */
    public Pelicula() {
    }

    /**
     * Crea una película con los mismos campos que tiene el XML.
     * El id y la fecha de adquisición los asigna la base de datos al insertar.
     *
     * @param titulo   título de la película
     * @param director director o directores
     * @param genero   género cinematográfico
     * @param anio     año de estreno
     * @param duracion duración en minutos
     * @param formato  formato de la edición (DVD, Blu-ray, 4K...)
     * @param estado   estado de la edición (Nuevo, Usado...)
     * @param sinopsis resumen del argumento
     */
    public Pelicula(String titulo, String director, String genero, Integer anio, Integer duracion,
                    String formato, String estado, String sinopsis) {
        this.titulo = titulo;
        this.director = director;
        this.genero = genero;
        this.anio = anio;
        this.duracion = duracion;
        this.formato = formato;
        this.estado = estado;
        this.sinopsis = sinopsis;
    }

    /**
     * Devuelve el identificador.
     *
     * @return id generado por la base de datos, o {@code null} si aún no se ha guardado
     */
    public Integer getId() {
        return id;
    }

    /**
     * Cambia el identificador.
     *
     * @param id nuevo identificador
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Devuelve el título.
     *
     * @return título de la película
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Cambia el título.
     *
     * @param titulo nuevo título
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Devuelve el director.
     *
     * @return director o directores
     */
    public String getDirector() {
        return director;
    }

    /**
     * Cambia el director.
     *
     * @param director nuevo director
     */
    public void setDirector(String director) {
        this.director = director;
    }

    /**
     * Devuelve el género.
     *
     * @return género cinematográfico
     */
    public String getGenero() {
        return genero;
    }

    /**
     * Cambia el género.
     *
     * @param genero nuevo género
     */
    public void setGenero(String genero) {
        this.genero = genero;
    }

    /**
     * Devuelve el año de estreno.
     *
     * @return año de estreno, o {@code null} si no se conoce
     */
    public Integer getAnio() {
        return anio;
    }

    /**
     * Cambia el año de estreno.
     *
     * @param anio nuevo año de estreno
     */
    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    /**
     * Devuelve la duración.
     *
     * @return duración en minutos, o {@code null} si no se conoce
     */
    public Integer getDuracion() {
        return duracion;
    }

    /**
     * Cambia la duración.
     *
     * @param duracion nueva duración en minutos
     */
    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }

    /**
     * Devuelve el formato.
     *
     * @return formato de la edición
     */
    public String getFormato() {
        return formato;
    }

    /**
     * Cambia el formato.
     *
     * @param formato nuevo formato
     */
    public void setFormato(String formato) {
        this.formato = formato;
    }

    /**
     * Devuelve el estado.
     *
     * @return estado de la edición
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Cambia el estado.
     *
     * @param estado nuevo estado
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Devuelve la sinopsis.
     *
     * @return resumen del argumento
     */
    public String getSinopsis() {
        return sinopsis;
    }

    /**
     * Cambia la sinopsis.
     *
     * @param sinopsis nueva sinopsis
     */
    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    /**
     * Devuelve la fecha de adquisición.
     *
     * @return fecha en la que se añadió a la videoteca
     */
    public LocalDate getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    /**
     * Cambia la fecha de adquisición.
     *
     * @param fechaAdquisicion nueva fecha de adquisición
     */
    public void setFechaAdquisicion(LocalDate fechaAdquisicion) {
        this.fechaAdquisicion = fechaAdquisicion;
    }

    /**
     * Devuelve una línea legible con los datos principales, pensada para mostrarla por consola.
     *
     * @return texto con id, título, año, formato y estado
     */
    @Override
    public String toString() {
        return "[" + id + "] " + titulo + " (" + anio + ") - " + formato + " - " + estado;
    }
}
