package com.alura.literalura.model;

import jakarta.persistence.*;

import java.util.List;


@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(unique = true)
    private String titulo;

    @ManyToOne()
    private Autor autor;

    private String nombreAutor;
    private String idiomas;
    private Double numeroDeDescargas;

    public Libro() {
    }

    public Libro(DatosLibros datosLibros) {
        this.titulo = datosLibros.titulo();
        this.nombreAutor = obtenerPrimerAutor(datosLibros).getNombre();
        this.idiomas = obtenerPrimerIdioma(datosLibros);
        this.numeroDeDescargas = datosLibros.numeroDeDescargas();
    }

    // seters and getters


    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getNombreAutor() {
        return nombreAutor;
    }

    public void setNombreAutor(String nombreAutor) {
        this.nombreAutor = nombreAutor;
    }

    public String getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(String idiomas){
        this.idiomas = idiomas;
    }

    public Double getNumeroDeDescargas() {
        return numeroDeDescargas;
    }

    public void setNumeroDeDescargas(Double numeroDeDescargas) {
        this.numeroDeDescargas = numeroDeDescargas;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    //metodos
    public Autor obtenerPrimerAutor(DatosLibros datosLibro){
        DatosAutor datosAutor = datosLibro.autor().get(0);
        return new Autor(datosAutor);
    }

    public String obtenerPrimerIdioma(DatosLibros datosLibros){
        String idioma = datosLibros.idiomas().toString();
        return idioma;
    }

    @Override
    public String toString(){
        return
                "Titulo = " + titulo + "\'" +
                        ", Autor =" + nombreAutor +
                        ", Idiomas =" + idiomas+
                        " , Numero de descargas= " + numeroDeDescargas
                ;
    }
}