package co.edu.unbosque.backcaoba.controller;

import co.edu.unbosque.backcaoba.dto.CambioEstadoDTO;
import co.edu.unbosque.backcaoba.dto.RegistroDTO;
import co.edu.unbosque.backcaoba.dto.RolDTO;
import co.edu.unbosque.backcaoba.dto.UsuarioDTO;
import co.edu.unbosque.backcaoba.security.UsuarioPrincipal;
import co.edu.unbosque.backcaoba.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @PostMapping("/crearadministrador")
    public ResponseEntity<UsuarioDTO> crearAdministrador(@Valid @RequestBody RegistroDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crearAdministrador(dto));
    }

    @PatchMapping("/restringir/{id}")
    public ResponseEntity<UsuarioDTO> restringirUsuario(@AuthenticationPrincipal UsuarioPrincipal principal,
            @PathVariable Integer id, @Valid @RequestBody CambioEstadoDTO dto) {
        return ResponseEntity.ok(usuarioService.restringirUsuario(principal.getIdUsuario(), id, dto));
    }

    @PatchMapping("/rol/{id}")
    public ResponseEntity<UsuarioDTO> asignarRol(@AuthenticationPrincipal UsuarioPrincipal principal,
            @PathVariable Integer id, @Valid @RequestBody RolDTO dto) {
        return ResponseEntity.ok(usuarioService.asignarRol(principal.getIdUsuario(), id, dto));
    }

    @PatchMapping("/estado/{id}")
    public ResponseEntity<UsuarioDTO> cambiarEstado(@AuthenticationPrincipal UsuarioPrincipal principal,
            @PathVariable Integer id, @Valid @RequestBody CambioEstadoDTO dto) {
        return ResponseEntity.ok(usuarioService.cambiarEstadoUsuario(principal.getIdUsuario(), id, dto));
    }
}
