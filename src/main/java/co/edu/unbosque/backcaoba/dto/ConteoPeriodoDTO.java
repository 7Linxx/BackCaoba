package co.edu.unbosque.backcaoba.dto;

public class ConteoPeriodoDTO {

    /** Periodo en formato ISO: "2026-11-15" (día) o "2026-11" (mes). */
    private String periodo;
    /** Texto para mostrar en la gráfica: "15/11" o "nov. 2026". */
    private String etiqueta;
    private Long cantidad;

    public ConteoPeriodoDTO() {
    }

    public ConteoPeriodoDTO(String periodo, String etiqueta, Long cantidad) {
        this.periodo = periodo;
        this.etiqueta = etiqueta;
        this.cantidad = cantidad;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }
}
