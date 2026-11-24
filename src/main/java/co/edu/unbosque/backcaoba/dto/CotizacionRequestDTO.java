package co.edu.unbosque.backcaoba.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record CotizacionRequestDTO(
        @NotNull(message = "El identificador del alojamiento es obligatorio.") Integer idAlojamiento,
        @NotNull(message = "La fecha de entrada es obligatoria.") LocalDate fechaEntrada,
        @NotNull(message = "La fecha de salida es obligatoria.") LocalDate fechaSalida,
        @Min(value = 1, message = "Debe indicar al menos 1 huésped.") Integer numeroHuespedes,
        List<Integer> idsServiciosAdicionales) {
}