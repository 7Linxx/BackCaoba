package co.edu.unbosque.backcaoba.controller;

import co.edu.unbosque.backcaoba.dto.UsuarioDTO;
import co.edu.unbosque.backcaoba.security.UsuarioPrincipal;
import co.edu.unbosque.backcaoba.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/perfil")
    public ResponseEntity<UsuarioDTO> obtenerPerfil(@AuthenticationPrincipal UsuarioPrincipal principal) {
        return ResponseEntity.ok(usuarioService.obtenerPerfil(principal.getIdUsuario()));
    }

    @PutMapping("/perfil")
    public ResponseEntity<UsuarioDTO> actualizarPerfil(@AuthenticationPrincipal UsuarioPrincipal principal,
            @Valid @RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarPerfil(principal.getIdUsuario(), dto));
    }

    @PatchMapping("/cuenta")
    public ResponseEntity<UsuarioDTO> cambiarEstadoCuenta(@AuthenticationPrincipal UsuarioPrincipal principal,
            @RequestParam Boolean activa) {
        return ResponseEntity.ok(usuarioService.cambiarEstadoCuenta(principal.getIdUsuario(), activa));
    }
}
