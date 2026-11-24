package co.edu.unbosque.backcaoba.dto;

import java.util.List;

public class TablaReporteDTO {

    private String titulo;
    private String[] columnas;
    private List<String[]> filas;

    public TablaReporteDTO() {
    	
    }

    public TablaReporteDTO(String titulo, String[] columnas, List<String[]> filas) {
        this.titulo = titulo;
        this.columnas = columnas;
        this.filas = filas;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String[] getColumnas() {
        return columnas;
    }

    public void setColumnas(String[] columnas) {
        this.columnas = columnas;
    }

    public List<String[]> getFilas() {
        return filas;
    }

    public void setFilas(List<String[]> filas) {
        this.filas = filas;
    }
}