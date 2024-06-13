package com.alura.literalura.principal;

import com.alura.literalura.model.*;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;
import com.alura.literalura.service.ConsumoAPI;
import com.alura.literalura.service.ConvierteDatos;

import java.util.Comparator;
import java.util.Optional;
import java.util.Scanner;
import java.util.List;

public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/";
    private static final Scanner teclado = new Scanner(System.in);
    private final ConvierteDatos conversor = new ConvierteDatos();
    private final ConsumoAPI consulta = new ConsumoAPI();
    private int opcionUsuario = -1;
    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private List<Autor> autores;
    private List<Libro> libros;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void consultaPrincipal(){
        do {
            mostrarMenu();
            try {
                opcionUsuario = Integer.parseInt(teclado.nextLine());
                procesarOpcion(opcionUsuario);
            } catch (NumberFormatException e) {
                System.out.println("Opción no válida. Por favor ingrese un número.");
            }
        } while (opcionUsuario != 0);
    }

    private void mostrarMenu(){
        System.out.println("""
                Seleccione una opción valida a continuación:             
                1- Buscar libro por título
                2- Listar libros registrados
                3- Listar autores registrados
                4- Listar autores vivos en un determinado año
                5- Listar libros por idioma
                0- Salir
                """);
    }

    private void procesarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> buscarLibroWeb();
            case 2 -> mostrarLibrosBuscados();
            case 3 -> mostrarAutoresBuscados();
            case 4 -> mostrarAutoresPorAnio();
            case 5 -> listarLibrosPorIdioma();
            case 0 -> System.out.println("Finalizando el programa");
            default -> System.out.println("Opción no válida");
        }
    }

    private void buscarLibroWeb(){
        System.out.println("Ingrese el nombre del libro que desea buscar");
        String libroUsuario = teclado.nextLine();
        String busqueda = "?search=" + libroUsuario.replace(" ","+");

        try {
            String json = consulta.obtenerDatos(URL_BASE + busqueda);
            Datos datos = conversor.obtenerDatos(json, Datos.class);
            DatosLibros datosLibro = datos.resultados().get(0);
            Libro libro = new Libro(datosLibro);
            Autor autor = new Autor().obtenerPrimerAutor(datosLibro);
            System.out.println(libro);
            guardarLibroConAutor(libro, autor);
        } catch (Exception e) {
            System.out.println("Ocurrió un error al buscar el libro: " + e.getMessage());
        }
    }

    private void guardarLibroConAutor(Libro libro, Autor autor){
        Optional<Autor> autorBuscado = autorRepository.findByNombreContains(autor.getNombre());

        if (autorBuscado.isPresent()) {
            System.out.println("El autor ya existe");
            libro.setAutor(autorBuscado.get());
        } else {
            System.out.println("Nuevo autor");
            autorRepository.save(autor);
            libro.setAutor(autor);
        }

        try {
            libroRepository.save(libro);
        } catch (Exception e) {
            System.out.println("Ocurrió un error al guardar el libro: " + e.getMessage());
        }
    }

    private void mostrarLibrosBuscados() {
        libros = libroRepository.findAll();
        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros registrados.");
        } else {
            imprimeLibrosOrdenadosPorNombre(libros);
        }
    }

    private void mostrarAutoresBuscados() {
        autores = autorRepository.findAll();
        if (autores.isEmpty()) {
            System.out.println("No se encontraron autores registrados.");
        } else {
            imprimeAutoresOrdenadosPorNombre(autores);
        }
    }

    private void mostrarAutoresPorAnio(){
        System.out.println("De qué año deseas ver autores");
        try {
            int anio = Integer.parseInt(teclado.nextLine());
            autores = autorRepository.findByFechaDeNacimientoLessThanEqualAndFechaDeMuerteGreaterThanEqual(anio, anio);
            if (autores.isEmpty()) {
                System.out.println("No se encontraron autores vivos en ese año.");
            } else {
                imprimeAutoresOrdenadosPorNombre(autores);
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor ingrese un año válido.");
        }
    }

    private void listarLibrosPorIdioma(){
        muestraMenuIdiomas();
        String idioma = teclado.nextLine().trim();
        if (idioma.length() < 2) {
            System.out.println("Ingrese un idioma válido.");
            return;
        }
        String claveIdioma = idioma.substring(0, 2);
        libros = libroRepository.findByIdiomasContains(claveIdioma);

        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros en ese idioma.");
        } else {
            imprimeLibrosOrdenadosPorNombre(libros);
        }
    }

    private void muestraMenuIdiomas(){
        System.out.println("""
                Ingrese el idioma para buscar los libros:
                es- Español
                en- Inglés
                pt- Portugués
                fr- Francés 
                """);
    }

    private void imprimeAutoresOrdenadosPorNombre(List<Autor> autores){
        autores.stream()
                .sorted(Comparator.comparing(Autor::getNombre))
                .forEach(System.out::println);
    }

    private void imprimeLibrosOrdenadosPorNombre(List<Libro> libros) {
        libros.stream()
                .sorted(Comparator.comparing(Libro::getNombreAutor))
                .forEach(System.out::println);
    }
}
