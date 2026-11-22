package co.edu.unbosque.backcaoba.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class SolicitudRecuperacionDTO {

    @Email
    @NotBlank
    private String correo;

    public SolicitudRecuperacionDTO() {
    }

    public SolicitudRecuperacionDTO(String correo) {
        this.correo = correo;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
