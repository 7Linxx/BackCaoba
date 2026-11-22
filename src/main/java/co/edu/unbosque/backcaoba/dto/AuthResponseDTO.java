package co.edu.unbosque.backcaoba.dto;

public class AuthResponseDTO {

    private String token;
    private String tipo;
    private Integer idUsuario;
    private String nombreCompleto;
    private String rol;

    public AuthResponseDTO() {
    }

    public AuthResponseDTO(String token, String tipo, Integer idUsuario, String nombreCompleto, String rol) {
        this.token = token;
        this.tipo = tipo;
        this.idUsuario = idUsuario;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
