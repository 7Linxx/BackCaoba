package co.edu.unbosque.backcaoba.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ValidarTokenDTO(
        @NotBlank(message = "El correo es obligatorio.") @Email String correo,
        @NotBlank(message = "Ingresa el código que llegó a tu correo.") String token) {
}
