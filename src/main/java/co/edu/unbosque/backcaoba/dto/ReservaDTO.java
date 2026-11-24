package co.edu.unbosque.backcaoba.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ReservaDTO(
        Integer idReserva,
        String codigoComprobante,
        LocalDate fechaEntrada,
        LocalDate fechaSalida,
        Integer numeroHuespedes,
        Integer numeroNoches,
        BigDecimal precioNocheAplicado,
        BigDecimal precioBaseTotal,
        BigDecimal montoLimpieza,
        BigDecimal montoDescuento,
        BigDecimal montoServAdicionales,
        BigDecimal pctTarifaServicio,
        BigDecimal montoTarifaServicio,
        BigDecimal total,
        String estado,
        LocalDateTime fechaSolicitud,
        LocalDateTime fechaConfirmacion,
        Integer idHuesped,
        String nombreHuesped,
        String correoHuesped,
        Integer idAlojamiento,
        String tituloAlojamiento,
        String ciudadAlojamiento,
        PoliticaCancelacionDTO politicaCancelacion,
        List<ReservaServicioAdicionalDTO> serviciosAdicionales,
        CancelacionDTO cancelacion) {
}