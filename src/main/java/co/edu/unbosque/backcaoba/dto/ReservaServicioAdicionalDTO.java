package co.edu.unbosque.backcaoba.dto;

import java.math.BigDecimal;

public record ReservaServicioAdicionalDTO(
        Integer idServicioAdicional,
        String nombre,
        BigDecimal costoAplicado) {
}