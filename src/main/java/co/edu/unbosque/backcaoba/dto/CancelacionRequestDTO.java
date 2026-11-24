package co.edu.unbosque.backcaoba.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelacionRequestDTO(
        @NotBlank(message = "Debe indicar el motivo de la cancelación.")
        @Size(max = 500, message = "El motivo no puede superar los 500 caracteres.")
        String motivo) {
}