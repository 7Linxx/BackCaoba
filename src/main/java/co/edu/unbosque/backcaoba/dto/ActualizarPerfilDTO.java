package co.edu.unbosque.backcaoba.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ActualizarPerfilDTO(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Size(max = 100) String apellido,
        @Pattern(regexp = "^$|[0-9+() -]{7,20}$", message = "Teléfono inválido") String telefono) {
}
