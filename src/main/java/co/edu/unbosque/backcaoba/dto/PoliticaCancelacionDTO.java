package co.edu.unbosque.backcaoba.dto;

import java.math.BigDecimal;

public record PoliticaCancelacionDTO(
        Integer idPolitica,
        String nombre,
        String descripcion,
        Integer diasLimiteReembolso,
        BigDecimal pctReembolsoEnPlazo,
        BigDecimal pctReembolsoFueraPlazo) {
}