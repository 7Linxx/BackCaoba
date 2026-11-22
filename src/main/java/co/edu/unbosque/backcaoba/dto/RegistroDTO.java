package co.edu.unbosque.backcaoba.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegistroDTO {

    @NotBlank
    @Size(max = 100)
    private String nombre;

    @NotBlank
    @Size(max = 100)
    private String apellido;

    @Email
    @NotBlank
    @Size(max = 150)
    private String correo;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "Mínimo 8 caracteres, una mayúscula, una minúscula y un número")
    private String contrasena;

    @Pattern(regexp = "^$|[0-9+() -]{7,20}$", message = "Teléfono inválido")
    private String telefono;

    @NotBlank
    private String rol;

    @AssertTrue(message = "Debes aceptar el tratamiento de datos personales.")
    private Boolean aceptaTratamientoDatos;

    public RegistroDTO() {
    }

    public RegistroDTO(String nombre, String apellido, String correo, String contrasena, String telefono,
            String rol, Boolean aceptaTratamientoDatos) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasena = contrasena;
        this.telefono = telefono;
        this.rol = rol;
        this.aceptaTratamientoDatos = aceptaTratamientoDatos;
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

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Boolean getAceptaTratamientoDatos() {
        return aceptaTratamientoDatos;
    }

    public void setAceptaTratamientoDatos(Boolean aceptaTratamientoDatos) {
        this.aceptaTratamientoDatos = aceptaTratamientoDatos;
    }
}
