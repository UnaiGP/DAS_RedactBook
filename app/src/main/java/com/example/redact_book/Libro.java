package com.example.redact_book;

public class Libro {
    private final String titulo;
    private final int paginas;
    private final int valoracion;

    public Libro(String titulo, int paginas, int valoracion) {
        this.titulo = titulo;
        this.paginas = paginas;
        this.valoracion = valoracion;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getPaginas() {
        return paginas;
    }

    public int getValoracion() {
        return valoracion;
    }
}
