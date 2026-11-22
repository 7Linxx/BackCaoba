package co.edu.unbosque.backcaoba.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RestablecerContrasenaDTO {

    @NotBlank(message = "El correo es obligatorio.")
    @Email
    private String correo;

    @NotBlank(message = "Ingresa el código que llegó a tu correo.")
    private String token;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "Mínimo 8 caracteres, una mayúscula, una minúscula y un número")
    private String nuevaContrasena;

    @NotBlank(message = "Confirma la nueva contraseña.")
    private String confirmacionContrasena;

    public RestablecerContrasenaDTO() {
    }

    public RestablecerContrasenaDTO(String correo, String token, String nuevaContrasena,
            String confirmacionContrasena) {
        this.correo = correo;
        this.token = token;
        this.nuevaContrasena = nuevaContrasena;
        this.confirmacionContrasena = confirmacionContrasena;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNuevaContrasena() {
        return nuevaContrasena;
    }

    public void setNuevaContrasena(String nuevaContrasena) {
        this.nuevaContrasena = nuevaContrasena;
    }

    public String getConfirmacionContrasena() {
        return confirmacionContrasena;
    }

    public void setConfirmacionContrasena(String confirmacionContrasena) {
        this.confirmacionContrasena = confirmacionContrasena;
    }
}
