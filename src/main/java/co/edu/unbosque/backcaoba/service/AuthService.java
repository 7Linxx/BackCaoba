package co.edu.unbosque.backcaoba.service;

import co.edu.unbosque.backcaoba.dto.AuthResponseDTO;
import co.edu.unbosque.backcaoba.dto.LoginDTO;
import co.edu.unbosque.backcaoba.dto.RegistroDTO;
import co.edu.unbosque.backcaoba.dto.RestablecerContrasenaDTO;
import co.edu.unbosque.backcaoba.dto.SolicitudRecuperacionDTO;
import co.edu.unbosque.backcaoba.dto.UsuarioDTO;
import co.edu.unbosque.backcaoba.dto.ValidarTokenDTO;
import co.edu.unbosque.backcaoba.model.Rol;
import co.edu.unbosque.backcaoba.model.TokenRecuperacion;
import co.edu.unbosque.backcaoba.model.Usuario;
import co.edu.unbosque.backcaoba.repository.RolRepository;
import co.edu.unbosque.backcaoba.repository.TokenRecuperacionRepository;
import co.edu.unbosque.backcaoba.repository.UsuarioRepository;
import co.edu.unbosque.backcaoba.security.JwtUtil;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    private static final long SEGUNDOS_ENTRE_SOLICITUDES = 60;
    private static final int MAX_INTENTOS = 5;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final TokenRecuperacionRepository tokenRecuperacionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CorreoService correoService;
    private final ModelMapper modelMapper;
    private final long minutosRecuperacion;
    private final SecureRandom aleatorio = new SecureRandom();
    private final ConcurrentMap<Long, Integer> intentosFallidos = new ConcurrentHashMap<>();

    public AuthService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
            TokenRecuperacionRepository tokenRecuperacionRepository, PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil, CorreoService correoService, ModelMapper modelMapper,
            @Value("${recovery.expiration-minutes}") long minutosRecuperacion) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.tokenRecuperacionRepository = tokenRecuperacionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.correoService = correoService;
        this.modelMapper = modelMapper;
        this.minutosRecuperacion = minutosRecuperacion;
    }

    private ResponseStatusException error(HttpStatus status, String message) {
        return new ResponseStatusException(status, message);
    }

    private String normalizar(String value) {
        return Normalizer.normalize(value == null ? "" : value.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toUpperCase(Locale.ROOT);
    }

    private String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("No fue posible procesar el token", ex);
        }
    }

    private AuthResponseDTO crearRespuestaAuth(Usuario usuario) {
        String token = jwtUtil.generarToken(usuario.getCorreo(), usuario.getRol().getNombre());
        String nombreCompleto = (usuario.getNombre() + " " + usuario.getApellido()).trim();
        return new AuthResponseDTO(token, "Bearer", usuario.getIdUsuario(), nombreCompleto,
                usuario.getRol().getNombre());
    }

    private Rol obtenerRol(String nombre) {
        String buscado = normalizar(nombre);
        return rolRepository.findAll().stream()
                .filter(rol -> normalizar(rol.getNombre()).equals(buscado))
                .findFirst().orElseThrow(() -> error(HttpStatus.INTERNAL_SERVER_ERROR,
                        "El rol solicitado no existe en la base de datos: " + nombre));
    }

    public void validarContrasenaSegura(String contrasena) {
        if (contrasena == null || !contrasena.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            throw error(HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener mínimo 8 caracteres, una mayúscula, una minúscula y un número.");
        }
    }

    @Transactional
    public UsuarioDTO registrar(RegistroDTO datos) {
        if (!Boolean.TRUE.equals(datos.getAceptaTratamientoDatos())) {
            throw error(HttpStatus.BAD_REQUEST, "Debe aceptar el tratamiento de datos personales.");
        }
        String rol = normalizar(datos.getRol());
        if (!"HUESPED".equals(rol) && !"ANFITRION".equals(rol)) {
            throw error(HttpStatus.BAD_REQUEST, "Solo puede registrarse como HUÉSPED o ANFITRIÓN.");
        }
        String correo = datos.getCorreo().trim().toLowerCase(Locale.ROOT);
        if (usuarioRepository.existsByCorreo(correo)) {
            throw error(HttpStatus.CONFLICT, "El correo ya está registrado.");
        }
        validarContrasenaSegura(datos.getContrasena());
        Usuario usuario = new Usuario();
        usuario.setNombre(datos.getNombre().trim());
        usuario.setApellido(datos.getApellido().trim());
        usuario.setCorreo(correo);
        usuario.setTelefono(datos.getTelefono() == null || datos.getTelefono().isBlank()
                ? null
                : datos.getTelefono().trim());
        usuario.setContrasenaHash(passwordEncoder.encode(datos.getContrasena()));
        usuario.setRol(obtenerRol(rol));
        usuario.setFechaAceptacionDatos(LocalDateTime.now());
        usuarioRepository.save(usuario);
        return modelMapper.map(usuario, UsuarioDTO.class);
    }

    public AuthResponseDTO iniciarSesion(LoginDTO datos) {
        Usuario usuario = usuarioRepository.findByCorreo(datos.getCorreo().trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> error(HttpStatus.UNAUTHORIZED, "correo o contraseña incorrectos"));
        if (!passwordEncoder.matches(datos.getContrasena(), usuario.getContrasenaHash())) {
            throw error(HttpStatus.UNAUTHORIZED, "correo o contraseña incorrectos");
        }
        if (!"ACTIVA".equalsIgnoreCase(usuario.getEstadoCuenta())) {
            throw error(HttpStatus.FORBIDDEN, "La cuenta está desactivada o restringida.");
        }
        return crearRespuestaAuth(usuario);
    }

    @Transactional
    public AuthResponseDTO reactivarCuenta(LoginDTO datos) {
        Usuario usuario = usuarioRepository.findByCorreo(datos.getCorreo().trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> error(HttpStatus.UNAUTHORIZED, "correo o contraseña incorrectos"));
        if (!passwordEncoder.matches(datos.getContrasena(), usuario.getContrasenaHash())) {
            throw error(HttpStatus.UNAUTHORIZED, "correo o contraseña incorrectos");
        }
        if (!"INACTIVA".equalsIgnoreCase(usuario.getEstadoCuenta())) {
            throw error(HttpStatus.CONFLICT,
                    "La cuenta no está desactivada o no puede reactivarse desde este proceso.");
        }
        usuario.setEstadoCuenta("ACTIVA");
        usuarioRepository.save(usuario);
        return crearRespuestaAuth(usuario);
    }

    private String generarCodigoRecuperacion() {
        return String.format("%06d", aleatorio.nextInt(1_000_000));
    }

    private String generarSal() {
        byte[] bytes = new byte[16];
        aleatorio.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashCodigo(String sal, String codigo) {
        return sal + "$" + hash(sal + ":" + codigo);
    }

    private boolean codigoCoincide(String guardado, String codigo) {
        int separador = guardado.indexOf('$');
        if (separador < 0) {
            return false;
        }
        String esperado = hashCodigo(guardado.substring(0, separador), codigo);
        return MessageDigest.isEqual(esperado.getBytes(StandardCharsets.UTF_8),
                guardado.getBytes(StandardCharsets.UTF_8));
    }

    @Transactional
    public void solicitarRecuperacion(SolicitudRecuperacionDTO datos) {
        Usuario usuario = usuarioRepository.findByCorreo(datos.getCorreo().trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> error(HttpStatus.NOT_FOUND,
                        "No existe una cuenta registrada con ese correo."));
        if ("RESTRINGIDA".equalsIgnoreCase(usuario.getEstadoCuenta())) {
            throw error(HttpStatus.FORBIDDEN,
                    "La cuenta está restringida. Comunícate con el administrador.");
        }
        if (tokenRecuperacionRepository.existsByUsuarioAndFechaCreacionAfter(
                usuario, LocalDateTime.now().minusSeconds(SEGUNDOS_ENTRE_SOLICITUDES))) {
            throw error(HttpStatus.TOO_MANY_REQUESTS,
                    "Ya enviamos un código hace poco. Espera un minuto antes de solicitar otro.");
        }
        tokenRecuperacionRepository.invalidarPendientes(usuario);
        String codigo = generarCodigoRecuperacion();
        TokenRecuperacion token = new TokenRecuperacion();
        token.setUsuario(usuario);
        token.setTokenHash(hashCodigo(generarSal(), codigo));
        token.setFechaExpiracion(LocalDateTime.now().plusMinutes(minutosRecuperacion));
        tokenRecuperacionRepository.save(token);
        correoService.enviarRecuperacion(usuario.getCorreo(), usuario.getNombre(), codigo, minutosRecuperacion);
    }

    private TokenRecuperacion obtenerTokenVigente(String correo, String codigo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> error(HttpStatus.BAD_REQUEST, "El código no es válido."));
        TokenRecuperacion token = tokenRecuperacionRepository.findFirstByUsuarioOrderByIdDesc(usuario)
                .orElseThrow(() -> error(HttpStatus.BAD_REQUEST, "El código no es válido. Solicita uno nuevo."));
        if (token.isUsado()) {
            throw error(HttpStatus.BAD_REQUEST,
                    "El código ya fue utilizado o fue reemplazado por uno más reciente. Solicita uno nuevo.");
        }
        if (!token.getFechaExpiracion().isAfter(LocalDateTime.now())) {
            throw error(HttpStatus.BAD_REQUEST, "El código venció. Solicita uno nuevo.");
        }
        if (!codigoCoincide(token.getTokenHash(), codigo.trim())) {
            Long tokenId = token.getId();
            int fallidos = intentosFallidos.getOrDefault(tokenId, 0) + 1;
            intentosFallidos.put(tokenId, fallidos);
            if (fallidos >= MAX_INTENTOS) {
                intentosFallidos.remove(tokenId);
                token.setUsado(true);
                tokenRecuperacionRepository.save(token);
                throw error(HttpStatus.BAD_REQUEST,
                        "Superaste el número máximo de intentos. Solicita un nuevo código.");
            }
            int restantes = MAX_INTENTOS - fallidos;
            throw error(HttpStatus.BAD_REQUEST, "El código no es válido. Te quedan " + restantes
                    + (restantes == 1 ? " intento." : " intentos."));
        }
        return token;
    }

    @Transactional(noRollbackFor = ResponseStatusException.class)
    public void validarTokenRecuperacion(ValidarTokenDTO datos) {
        obtenerTokenVigente(datos.correo(), datos.token());
    }

    @Transactional(noRollbackFor = ResponseStatusException.class)
    public void restablecerContrasena(RestablecerContrasenaDTO datos) {
        validarContrasenaSegura(datos.getNuevaContrasena());
        if (!datos.getNuevaContrasena().equals(datos.getConfirmacionContrasena())) {
            throw error(HttpStatus.BAD_REQUEST, "Las contraseñas no coinciden.");
        }
        TokenRecuperacion token = obtenerTokenVigente(datos.getCorreo(), datos.getToken());
        Usuario usuario = token.getUsuario();
        if (passwordEncoder.matches(datos.getNuevaContrasena(), usuario.getContrasenaHash())) {
            throw error(HttpStatus.BAD_REQUEST,
                    "La nueva contraseña debe ser diferente a la actual.");
        }
        usuario.setContrasenaHash(passwordEncoder.encode(datos.getNuevaContrasena()));
        usuarioRepository.save(usuario);
        token.setUsado(true);
        tokenRecuperacionRepository.save(token);
        intentosFallidos.remove(token.getId());
    }
}
