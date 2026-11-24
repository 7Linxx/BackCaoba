package co.edu.unbosque.backcaoba.controller;

import co.edu.unbosque.backcaoba.dto.CancelacionDTO;
import co.edu.unbosque.backcaoba.dto.CancelacionRequestDTO;
import co.edu.unbosque.backcaoba.dto.ReservaDTO;
import co.edu.unbosque.backcaoba.dto.ReservaRequestDTO;
import co.edu.unbosque.backcaoba.service.ReservaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    @PreAuthorize("hasRole('HUESPED')")
    public ResponseEntity<ReservaDTO> crearReserva(
            Authentication authentication,
            @Valid @RequestBody ReservaRequestDTO solicitud) {
        ReservaDTO creada = reservaService.crearReserva(authentication.getName(), solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping("/mias")
    @PreAuthorize("hasRole('HUESPED')")
    public List<ReservaDTO> listarMisReservas(Authentication authentication) {
        return reservaService.listarHistorialHuesped(authentication.getName());
    }

    @GetMapping("/anfitrion")
    @PreAuthorize("hasRole('ANFITRION')")
    public List<ReservaDTO> listarReservasRecibidas(Authentication authentication) {
        return reservaService.listarReservasRecibidasAnfitrion(authentication.getName());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HUESPED', 'ANFITRION')")
    public ReservaDTO obtenerDetalle(
            Authentication authentication,
            @PathVariable Integer id) {
        return reservaService.obtenerDetalle(authentication.getName(), id);
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('HUESPED')")
    public CancelacionDTO cancelarReserva(
            Authentication authentication,
            @PathVariable Integer id,
            @Valid @RequestBody CancelacionRequestDTO solicitud) {
        return reservaService.cancelarReserva(authentication.getName(), id, solicitud);
    }
}