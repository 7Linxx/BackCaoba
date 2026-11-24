package co.edu.unbosque.backcaoba.dto;

public class ConteoEstadoDTO {

    private String estado;
    private Long cantidad;
    private Double porcentaje;

    public ConteoEstadoDTO(){
    	
    }

    public ConteoEstadoDTO(String estado, Long cantidad, Double porcentaje) {
        this.estado = estado;
        this.cantidad = cantidad;
        this.porcentaje = porcentaje;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(Double porcentaje) {
        this.porcentaje = porcentaje;
    }
}