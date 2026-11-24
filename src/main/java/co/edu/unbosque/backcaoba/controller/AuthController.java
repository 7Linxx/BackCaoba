package co.edu.unbosque.backcaoba.controller;

import co.edu.unbosque.backcaoba.dto.AuthResponseDTO;
import co.edu.unbosque.backcaoba.dto.LoginDTO;
import co.edu.unbosque.backcaoba.dto.RegistroDTO;
import co.edu.unbosque.backcaoba.dto.RestablecerContrasenaDTO;
import co.edu.unbosque.backcaoba.dto.SolicitudRecuperacionDTO;
import co.edu.unbosque.backcaoba.dto.UsuarioDTO;
import co.edu.unbosque.backcaoba.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> registrar(@Valid @RequestBody RegistroDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        return ResponseEntity.ok(authService.iniciarSesion(dto));
    }

    @PostMapping("/recuperacion")
    public ResponseEntity<String> solicitarRecuperacion(@Valid @RequestBody SolicitudRecuperacionDTO dto) {
        authService.solicitarRecuperacion(dto);
        return ResponseEntity.ok(
                "Te enviamos un código de verificación a tu correo. Revisa también la carpeta de spam.");
    }

    @PostMapping("/restablecer")
    public ResponseEntity<String> restablecer(@Valid @RequestBody RestablecerContrasenaDTO dto) {
        authService.restablecerContrasena(dto);
        return ResponseEntity.ok("Tu contraseña fue actualizada. Ya puedes iniciar sesión.");
    }

    // Extra (no está en el diagrama)
    @PostMapping("/reactivar")
    public ResponseEntity<AuthResponseDTO> reactivar(@Valid @RequestBody LoginDTO dto) {
        return ResponseEntity.ok(authService.reactivarCuenta(dto));
    }

    // Extra (no está en el diagrama): solo se usan correo y token del DTO
    @PostMapping("/restablecer/validar")
    public ResponseEntity<String> validarToken(@RequestBody RestablecerContrasenaDTO dto) {
        authService.validarTokenRecuperacion(dto);
        return ResponseEntity.ok("El código es válido.");
    }
}
