package co.edu.unbosque.backcaoba.dto;

import java.util.List;

public record AlojamientoDisponibilidadDTO(
        Integer idAlojamiento,
        String titulo,
        String estado,
        Boolean disponibleEnRango,
        String motivoNoDisponible,
        List<DisponibilidadRangoDTO> ventanasDisponibles,
        List<DisponibilidadRangoDTO> periodosOcupados) {
}