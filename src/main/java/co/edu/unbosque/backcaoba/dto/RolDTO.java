package co.edu.unbosque.backcaoba.dto;

import jakarta.validation.constraints.NotBlank;

public class RolDTO {

    private Integer idRol;

    @NotBlank(message = "El rol es obligatorio.")
    private String nombre;

    public RolDTO() {
    }

    public RolDTO(Integer idRol, String nombre) {
        this.idRol = idRol;
        this.nombre = nombre;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
