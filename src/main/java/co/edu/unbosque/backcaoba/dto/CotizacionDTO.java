package co.edu.unbosque.backcaoba.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CotizacionDTO(
        Integer idAlojamiento,
        String tituloAlojamiento,
        LocalDate fechaEntrada,
        LocalDate fechaSalida,
        Integer numeroHuespedes,
        Integer numeroNoches,
        BigDecimal precioNoche,
        BigDecimal precioBaseTotal,
        BigDecimal pctTarifaLimpieza,
        BigDecimal montoLimpieza,
        BigDecimal pctDescuento,
        BigDecimal montoDescuento,
        List<ServicioAdicionalDTO> serviciosAdicionales,
        BigDecimal montoServAdicionales,
        BigDecimal pctTarifaServicio,
        BigDecimal montoTarifaServicio,
        BigDecimal total) {
}