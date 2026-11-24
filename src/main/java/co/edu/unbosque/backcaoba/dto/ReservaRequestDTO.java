package co.edu.unbosque.backcaoba.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record ReservaRequestDTO(
        @NotBlank(message = "La clave de idempotencia es obligatoria.")
        @Size(max = 64, message = "La clave de idempotencia no puede superar 64 caracteres.")
        String claveIdempotencia,
        @NotNull(message = "El identificador del alojamiento es obligatorio.") Integer idAlojamiento,
        @NotNull(message = "La fecha de entrada es obligatoria.") LocalDate fechaEntrada,
        @NotNull(message = "La fecha de salida es obligatoria.") LocalDate fechaSalida,
        @NotNull(message = "El número de huéspedes es obligatorio.")
        @Min(value = 1, message = "Debe indicar al menos 1 huésped.")
        Integer numeroHuespedes,
        List<Integer> idsServiciosAdicionales) {
}