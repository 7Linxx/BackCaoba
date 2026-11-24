package co.edu.unbosque.backcaoba.controller;

import co.edu.unbosque.backcaoba.dto.CotizacionDTO;
import co.edu.unbosque.backcaoba.dto.CotizacionRequestDTO;
import co.edu.unbosque.backcaoba.service.CotizacionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cotizaciones")
public class CotizacionController {

    private final CotizacionService cotizacionService;

    public CotizacionController(CotizacionService cotizacionService) {
        this.cotizacionService = cotizacionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('HUESPED')")
    public CotizacionDTO cotizar(
            Authentication authentication,
            @Valid @RequestBody CotizacionRequestDTO solicitud) {
        return cotizacionService.cotizar(authentication.getName(), solicitud);
    }
}