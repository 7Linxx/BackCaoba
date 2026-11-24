package co.edu.unbosque.backcaoba.dto;

import java.math.BigDecimal;
import java.util.List;

public record AlojamientoResumenDTO(
        Integer idAlojamiento,
        String titulo,
        String descripcion,
        String ciudad,
        String direccion,
        String tipoAlojamiento,
        Integer capacidad,
        BigDecimal precioNoche,
        BigDecimal pctTarifaLimpieza,
        BigDecimal pctDescuento,
        String estado,
        Integer idAnfitrion,
        String nombreAnfitrion,
        PoliticaCancelacionDTO politicaCancelacion,
        List<ServicioAdicionalDTO> serviciosAdicionales) {
}