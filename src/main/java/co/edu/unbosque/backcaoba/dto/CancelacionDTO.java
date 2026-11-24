package co.edu.unbosque.backcaoba.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CancelacionDTO(
        Integer idCancelacion,
        Integer idReserva,
        Integer idUsuario,
        String nombreUsuario,
        LocalDateTime fechaCancelacion,
        String motivo,
        BigDecimal montoDevolucion,
        String estadoReserva) {
}