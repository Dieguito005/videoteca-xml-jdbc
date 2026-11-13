# Videoteca: importar y exportar películas entre XML y PostgreSQL

Actividad práctica de las Unidades 1 y 2 de **Acceso a Datos** (2º DAM, U-tad).

Aplicación Java para que un coleccionista de cine gestione su videoteca en PostgreSQL. El programa crea por código la base de datos y la tabla `peliculas`, inserta 5 películas de prueba, genera un XML con 3 películas más, lo importa a la base de datos (**Método A**) y después vuelca toda la tabla a un nuevo XML (**Método B**).

**Vídeo demostrativo:** [ver en Loom](https://www.loom.com/share/daf876cf98a64eeda45e0b09cfb31b8d)

## Tecnologías

Java 21 o superior, Maven, PostgreSQL con su driver JDBC oficial (`Connection`, `PreparedStatement`, `ResultSet`) y DOM (`javax.xml`) para leer y escribir el XML.

## Estructura del proyecto

```
src/main/java/es/utad/videoteca/
├── Main.java                     Prueba el flujo completo en 4 pasos
├── DatosPrueba.java              Las 5 películas iniciales y las 3 del XML
├── modelo/Pelicula.java          Una película (una fila de la tabla / un <pelicula> del XML)
├── bd/ConexionBD.java            Datos de conexión y creación de la base de datos
├── bd/PeliculaDAO.java           Todo el SQL de la tabla peliculas
├── xml/GestorXML.java            Lectura y escritura del XML con DOM
└── servicio/VideotecaService.java  Método A (XML → BD) y Método B (BD → XML)
```

## Cómo ejecutarlo

1. Arrancar PostgreSQL con Docker: `docker compose up -d` (queda en `localhost:5433`, usuario y contraseña `postgres`). Si prefieres usar un PostgreSQL propio, cambia el puerto y la contraseña en `bd/ConexionBD.java`.
2. `ConexionBD.java` ya apunta al contenedor de Docker.
3. Abrir la carpeta en IntelliJ como proyecto Maven y ejecutar `Main`.

No hay que crear nada a mano: la base de datos `videoteca` y la tabla `peliculas` se crean solas. El programa se puede ejecutar todas las veces que se quiera, porque al empezar vacía la tabla y reinicia el `id`, así que nunca se duplican las películas de prueba.

Opcionalmente, se puede pasar como argumento el nombre del XML de exportación (por ejemplo, `mi_videoteca.xml`). Si no se indica, se usa `videoteca_exportada.xml`.

## Qué hace el `Main`

| Paso | Acción | Resultado |
|---|---|---|
| 1 | Crea la base de datos y la tabla, e inserta 5 películas | 5 películas en la BD |
| 2 | Genera por código `nuevas_peliculas.xml` | XML con 3 películas |
| 3 | **Método A**: importa el XML a PostgreSQL | 8 películas en la BD |
| 4 | **Método B**: exporta la tabla a `videoteca_exportada.xml` | XML con 8 películas |

En cada paso se imprime por consola cuántos elementos se han insertado en la tabla o grabado en el archivo. Los dos XML se generan en la carpeta raíz del proyecto.

## Decisiones de diseño

- **Varias ediciones de la misma película:** la tabla no tiene ninguna restricción de título único, porque un coleccionista puede tener, por ejemplo, *El Padrino* en Blu-ray y en 4K. El XML de prueba incluye esa segunda edición para demostrarlo.
- **JDBC:** todas las sentencias usan `PreparedStatement` con parámetros `?` (evita la inyección SQL) y todos los recursos se cierran con try-with-resources.
- **Transacción en las inserciones:** si falla una película, se hace `rollback` y no se inserta ninguna, para no dejar la importación a medias.
- **Importación en dos fases:** primero se lee el XML entero y después se inserta, así un XML con errores no llega a tocar la base de datos.
- **Excepciones:** las clases de datos lanzan sus excepciones hacia arriba y el `Main` las captura en un único sitio, mostrando un mensaje distinto según sea un error de base de datos, de XML o de fichero.
- **Documentación:** todas las clases y métodos están documentados con JavaDoc.

## Autor

Diego, 2º DAM, U-tad.
