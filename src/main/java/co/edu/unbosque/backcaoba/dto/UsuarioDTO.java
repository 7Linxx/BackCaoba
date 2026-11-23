package co.edu.unbosque.backcaoba.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class UsuarioDTO {

    private Integer idUsuario;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio.")
    @Size(max = 100)
    private String apellido;

    private String correo;

    @Pattern(regexp = "^$|[0-9+() -]{7,20}$", message = "Teléfono inválido")
    private String telefono;
    private String estadoCuenta;
    private String motivoRestriccion;
    private LocalDateTime fechaAceptacionDatos;
    private LocalDateTime fechaRegistro;
    private String rol;

    public UsuarioDTO() {
    }

    public UsuarioDTO(Integer idUsuario, String nombre, String apellido, String correo, String telefono,
            String estadoCuenta, String motivoRestriccion, LocalDateTime fechaAceptacionDatos,
            LocalDateTime fechaRegistro, String rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;
        this.estadoCuenta = estadoCuenta;
        this.motivoRestriccion = motivoRestriccion;
        this.fechaAceptacionDatos = fechaAceptacionDatos;
        this.fechaRegistro = fechaRegistro;
        this.rol = rol;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEstadoCuenta() {
        return estadoCuenta;
    }

    public void setEstadoCuenta(String estadoCuenta) {
        this.estadoCuenta = estadoCuenta;
    }

    public String getMotivoRestriccion() {
        return motivoRestriccion;
    }

    public void setMotivoRestriccion(String motivoRestriccion) {
        this.motivoRestriccion = motivoRestriccion;
    }

    public LocalDateTime getFechaAceptacionDatos() {
        return fechaAceptacionDatos;
    }

    public void setFechaAceptacionDatos(LocalDateTime fechaAceptacionDatos) {
        this.fechaAceptacionDatos = fechaAceptacionDatos;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
