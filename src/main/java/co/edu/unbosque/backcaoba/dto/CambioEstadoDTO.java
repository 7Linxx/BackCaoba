package co.edu.unbosque.backcaoba.dto;

import jakarta.validation.constraints.NotBlank;

public class CambioEstadoDTO {

    @NotBlank
    private String estado;
    private String motivo;

    public CambioEstadoDTO() {
    }

    public CambioEstadoDTO(String estado, String motivo) {
        this.estado = estado;
        this.motivo = motivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
