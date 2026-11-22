package co.edu.unbosque.backcaoba.controller;

import co.edu.unbosque.backcaoba.dto.ActualizarPerfilDTO;
import co.edu.unbosque.backcaoba.dto.EstadoCuentaResponseDTO;
import co.edu.unbosque.backcaoba.dto.UsuarioDTO;
import co.edu.unbosque.backcaoba.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/perfil")
    public UsuarioDTO obtenerPerfil(Authentication authentication) {
        return usuarioService.obtenerPerfil(authentication.getName());
    }

    @PutMapping("/perfil")
    public UsuarioDTO actualizarPerfil(
            Authentication authentication,
            @Valid @RequestBody ActualizarPerfilDTO datos) {
        return usuarioService.actualizarPerfil(authentication.getName(), datos);
    }

    @PatchMapping("/cuenta")
    public EstadoCuentaResponseDTO cambiarEstadoCuenta(
            Authentication authentication,
            @RequestParam boolean activa) {
        String estado = activa ? "ACTIVA" : "INACTIVA";
        usuarioService.cambiarEstadoCuenta(authentication.getName(), estado);
        return new EstadoCuentaResponseDTO(estado);
    }
}
