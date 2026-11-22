package co.edu.unbosque.backcaoba.controller;

import co.edu.unbosque.backcaoba.dto.AuthResponseDTO;
import co.edu.unbosque.backcaoba.dto.LoginDTO;
import co.edu.unbosque.backcaoba.dto.MensajeDTO;
import co.edu.unbosque.backcaoba.dto.RegistroDTO;
import co.edu.unbosque.backcaoba.dto.RestablecerContrasenaDTO;
import co.edu.unbosque.backcaoba.dto.SolicitudRecuperacionDTO;
import co.edu.unbosque.backcaoba.dto.UsuarioDTO;
import co.edu.unbosque.backcaoba.dto.ValidarTokenDTO;
import co.edu.unbosque.backcaoba.dto.ValidacionTokenResponseDTO;
import co.edu.unbosque.backcaoba.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> registrar(@Valid @RequestBody RegistroDTO datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(datos));
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody LoginDTO datos) {
        return authService.iniciarSesion(datos);
    }

    @PostMapping("/reactivar")
    public AuthResponseDTO reactivar(@Valid @RequestBody LoginDTO datos) {
        return authService.reactivarCuenta(datos);
    }

    @PostMapping("/recuperacion")
    public MensajeDTO solicitarRecuperacion(
            @Valid @RequestBody SolicitudRecuperacionDTO datos) {
        authService.solicitarRecuperacion(datos);
        return new MensajeDTO(
                "Te enviamos un código de verificación a tu correo. Revisa también la carpeta de spam.");
    }

    @PostMapping("/restablecer/validar")
    public ValidacionTokenResponseDTO validarToken(@Valid @RequestBody ValidarTokenDTO datos) {
        authService.validarTokenRecuperacion(datos);
        return new ValidacionTokenResponseDTO(true);
    }

    @PostMapping("/restablecer")
    public MensajeDTO restablecer(@Valid @RequestBody RestablecerContrasenaDTO datos) {
        authService.restablecerContrasena(datos);
        return new MensajeDTO("Tu contraseña fue actualizada. Ya puedes iniciar sesión.");
    }
}
