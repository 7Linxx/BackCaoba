package co.edu.unbosque.backcaoba.controller;

import co.edu.unbosque.backcaoba.dto.CambioEstadoDTO;
import co.edu.unbosque.backcaoba.dto.CambioRolDTO;
import co.edu.unbosque.backcaoba.dto.RegistroDTO;
import co.edu.unbosque.backcaoba.dto.UsuarioDTO;
import co.edu.unbosque.backcaoba.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/usuarios")
public class AdminController {
    private final UsuarioService usuarioService;

    public AdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    @PostMapping("/administradores")
    public ResponseEntity<UsuarioDTO> crearAdministrador(@Valid @RequestBody RegistroDTO datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crearAdministrador(datos));
    }

    @PatchMapping("/{id}/restringir")
    public UsuarioDTO restringirUsuario(
            @PathVariable Integer id,
            @Valid @RequestBody CambioEstadoDTO cambio,
            Authentication authentication) {
        return usuarioService.restringirUsuario(id, cambio.getMotivo(), authentication.getName());
    }

    @PatchMapping("/{id}/rol")
    public UsuarioDTO asignarRol(
            @PathVariable Integer id,
            @Valid @RequestBody CambioRolDTO cambio,
            Authentication authentication) {
        return usuarioService.asignarRol(id, cambio.rol(), authentication.getName());
    }

    @PatchMapping("/{id}/estado")
    public UsuarioDTO cambiarEstado(
            @PathVariable Integer id,
            @Valid @RequestBody CambioEstadoDTO cambio,
            Authentication authentication) {
        return usuarioService.cambiarEstadoUsuario(id, cambio.getEstado(), authentication.getName());
    }
}
