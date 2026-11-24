package co.edu.unbosque.backcaoba.dto;

import java.math.BigDecimal;

public record ServicioAdicionalDTO(
        Integer idServicioAdicional,
        String nombre,
        BigDecimal costo) {
}