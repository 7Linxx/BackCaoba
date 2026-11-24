package co.edu.unbosque.backcaoba.dto;

public class ConteoRolDTO {

    private String rol;
    private Long cantidad;
    private Double porcentaje;

    public ConteoRolDTO() {
    }

    public ConteoRolDTO(String rol, Long cantidad, Double porcentaje) {
        this.rol = rol;
        this.cantidad = cantidad;
        this.porcentaje = porcentaje;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
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
