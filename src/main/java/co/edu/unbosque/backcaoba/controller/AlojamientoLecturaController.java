package co.edu.unbosque.backcaoba.controller;

import co.edu.unbosque.backcaoba.dto.AlojamientoDisponibilidadDTO;
import co.edu.unbosque.backcaoba.dto.AlojamientoResumenDTO;
import co.edu.unbosque.backcaoba.service.AlojamientoLecturaService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alojamientos")
public class AlojamientoLecturaController {

    private final AlojamientoLecturaService alojamientoLecturaService;

    public AlojamientoLecturaController(AlojamientoLecturaService alojamientoLecturaService) {
        this.alojamientoLecturaService = alojamientoLecturaService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HUESPED', 'ANFITRION', 'ADMINISTRADOR')")
    public List<AlojamientoResumenDTO> listarActivos() {
        return alojamientoLecturaService.listarActivos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HUESPED', 'ANFITRION', 'ADMINISTRADOR')")
    public AlojamientoResumenDTO obtenerDetalle(@PathVariable Integer id) {
        return alojamientoLecturaService.obtenerDetalle(id);
    }

    @GetMapping("/{id}/disponibilidad")
    @PreAuthorize("hasAnyRole('HUESPED', 'ANFITRION', 'ADMINISTRADOR')")
    public AlojamientoDisponibilidadDTO consultarDisponibilidad(
            @PathVariable Integer id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEntrada,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida) {
        return alojamientoLecturaService.consultarDisponibilidad(id, fechaEntrada, fechaSalida);
    }
}