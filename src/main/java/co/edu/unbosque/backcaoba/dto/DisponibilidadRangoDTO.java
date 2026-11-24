package co.edu.unbosque.backcaoba.dto;

import java.time.LocalDate;

public record DisponibilidadRangoDTO(
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String tipo) {
}