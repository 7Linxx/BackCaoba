package co.edu.unbosque.backcaoba.service;

import co.edu.unbosque.backcaoba.dto.AuthResponseDTO;
import co.edu.unbosque.backcaoba.dto.LoginDTO;
import co.edu.unbosque.backcaoba.dto.RegistroDTO;
import co.edu.unbosque.backcaoba.dto.RestablecerContrasenaDTO;
import co.edu.unbosque.backcaoba.dto.SolicitudRecuperacionDTO;
import co.edu.unbosque.backcaoba.dto.UsuarioDTO;
import co.edu.unbosque.backcaoba.model.Rol;
import co.edu.unbosque.backcaoba.model.TokenRecuperacion;
import co.edu.unbosque.backcaoba.model.Usuario;
import co.edu.unbosque.backcaoba.repository.RolRepository;
import co.edu.unbosque.backcaoba.repository.TokenRecuperacionRepository;
import co.edu.unbosque.backcaoba.repository.UsuarioRepository;
import co.edu.unbosque.backcaoba.security.JwtUtil;
import co.edu.unbosque.backcaoba.security.UsuarioPrincipal;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private static final long SEGUNDOS_ENTRE_SOLICITUDES = 60;
    private static final int MAX_INTENTOS = 5;
    private static final String PATRON_CONTRASENA = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private TokenRecuperacionRepository tokenRecuperacionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CorreoService correoService;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${recovery.expiration-minutes}")
    private long minutosRecuperacion;

    private final SecureRandom aleatorio = new SecureRandom();

    /** Intentos fallidos por código de recuperación (idToken -> intentos). */
    private final Map<Integer, Integer> intentosFallidos = new ConcurrentHashMap<>();

    @Transactional
    public UsuarioDTO registrar(RegistroDTO dto) {
        if (!Boolean.TRUE.equals(dto.getAceptaTratamientoDatos())) {
            throw error(HttpStatus.BAD_REQUEST, "Debe aceptar el tratamiento de datos personales.");
        }
        String nombreRol = normalizar(dto.getRol());
        if (!"HUESPED".equals(nombreRol) && !"ANFITRION".equals(nombreRol)) {
            throw error(HttpStatus.BAD_REQUEST, "Solo puede registrarse como HUÉSPED o ANFITRIÓN.");
        }
        String correo = normalizarCorreo(dto.getCorreo());
        if (usuarioRepository.existsByCorreo(correo)) {
            throw error(HttpStatus.CONFLICT, "El correo ya está registrado.");
        }
        validarContrasenaSegura(dto.getContrasena());

        Usuario entity = modelMapper.map(dto, Usuario.class);
        entity.setIdUsuario(null);
        entity.setNombre(dto.getNombre().trim());
        entity.setApellido(dto.getApellido().trim());
        entity.setCorreo(correo);
        entity.setTelefono(limpiarTelefono(dto.getTelefono()));
        entity.setContrasenaHash(passwordEncoder.encode(dto.getContrasena()));
        entity.setRol(buscarRol(nombreRol));
        entity.setFechaAceptacionDatos(LocalDateTime.now());
        usuarioRepository.save(entity);
        return modelMapper.map(entity, UsuarioDTO.class);
    }

    public AuthResponseDTO iniciarSesion(LoginDTO dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(normalizarCorreo(dto.getCorreo()), dto.getContrasena()));
            UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
            Usuario usuario = usuarioRepository.findById(principal.getIdUsuario())
                    .orElseThrow(() -> error(HttpStatus.UNAUTHORIZED, "correo o contraseña incorrectos"));
            return crearRespuestaAuth(usuario);
        } catch (DisabledException e) {
            throw error(HttpStatus.FORBIDDEN, "La cuenta está desactivada o restringida.");
        } catch (AuthenticationException e) {
            throw error(HttpStatus.UNAUTHORIZED, "correo o contraseña incorrectos");
        }
    }

    // Extra (no está en el diagrama): reactivar una cuenta desactivada por el propio usuario
    @Transactional
    public AuthResponseDTO reactivarCuenta(LoginDTO dto) {
        Usuario usuario = usuarioRepository.findByCorreo(normalizarCorreo(dto.getCorreo()))
                .orElseThrow(() -> error(HttpStatus.UNAUTHORIZED, "correo o contraseña incorrectos"));
        if (!passwordEncoder.matches(dto.getContrasena(), usuario.getContrasenaHash())) {
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

    @Transactional
    public void solicitarRecuperacion(SolicitudRecuperacionDTO dto) {
        Usuario usuario = usuarioRepository.findByCorreo(normalizarCorreo(dto.getCorreo()))
                .orElseThrow(() -> error(HttpStatus.NOT_FOUND, "No existe una cuenta registrada con ese correo."));
        if ("RESTRINGIDA".equalsIgnoreCase(usuario.getEstadoCuenta())) {
            throw error(HttpStatus.FORBIDDEN, "La cuenta está restringida. Comunícate con el administrador.");
        }
        if (tokenRecuperacionRepository.existsByUsuarioIdUsuarioAndFechaCreacionAfter(
                usuario.getIdUsuario(), LocalDateTime.now().minusSeconds(SEGUNDOS_ENTRE_SOLICITUDES))) {
            throw error(HttpStatus.TOO_MANY_REQUESTS,
                    "Ya enviamos un código hace poco. Espera un minuto antes de solicitar otro.");
        }

        // Los códigos anteriores que no se usaron dejan de servir
        List<TokenRecuperacion> pendientes = tokenRecuperacionRepository
                .findByUsuarioIdUsuarioAndUsadoFalse(usuario.getIdUsuario());
        for (TokenRecuperacion pendiente : pendientes) {
            pendiente.setUsado(true);
        }
        tokenRecuperacionRepository.saveAll(pendientes);

        String codigo = generarTokenRecuperacion(usuario);
        correoService.enviarCodigoRecuperacion(usuario.getCorreo(), codigo);
    }

    // Extra (no está en el diagrama): comprobar el código antes de pedir la nueva contraseña
    @Transactional(noRollbackFor = ResponseStatusException.class)
    public void validarTokenRecuperacion(RestablecerContrasenaDTO dto) {
        if (esVacio(dto.getCorreo()) || esVacio(dto.getToken())) {
            throw error(HttpStatus.BAD_REQUEST, "Ingresa el correo y el código que llegó a tu correo.");
        }
        obtenerTokenVigente(dto.getCorreo(), dto.getToken());
    }

    @Transactional(noRollbackFor = ResponseStatusException.class)
    public void restablecerContrasena(RestablecerContrasenaDTO dto) {
        validarContrasenaSegura(dto.getNuevaContrasena());
        if (!dto.getNuevaContrasena().equals(dto.getConfirmacionContrasena())) {
            throw error(HttpStatus.BAD_REQUEST, "Las contraseñas no coinciden.");
        }
        TokenRecuperacion token = obtenerTokenVigente(dto.getCorreo(), dto.getToken());
        Usuario usuario = token.getUsuario();
        if (passwordEncoder.matches(dto.getNuevaContrasena(), usuario.getContrasenaHash())) {
            throw error(HttpStatus.BAD_REQUEST, "La nueva contraseña debe ser diferente a la actual.");
        }
        usuario.setContrasenaHash(passwordEncoder.encode(dto.getNuevaContrasena()));
        usuarioRepository.save(usuario);
        token.setUsado(true);
        tokenRecuperacionRepository.save(token);
        intentosFallidos.remove(token.getIdToken());
    }

    private void validarContrasenaSegura(String contrasena) {
        if (contrasena == null || !contrasena.matches(PATRON_CONTRASENA)) {
            throw error(HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener mínimo 8 caracteres, una mayúscula, una minúscula y un número.");
        }
    }

    /**
     * Crea y guarda un código de recuperación para el usuario. En la base de
     * datos solo se guarda el hash del código; el código en claro se devuelve
     * para enviarlo por correo.
     */
    private String generarTokenRecuperacion(Usuario usuario) {
        String codigo = String.format("%06d", aleatorio.nextInt(1_000_000));
        TokenRecuperacion token = new TokenRecuperacion();
        token.setUsuario(usuario);
        token.setTokenHash(hashCodigo(generarSal(), codigo));
        token.setFechaCreacion(LocalDateTime.now());
        token.setFechaExpiracion(LocalDateTime.now().plusMinutes(minutosRecuperacion));
        token.setUsado(false);
        tokenRecuperacionRepository.save(token);
        return codigo;
    }

    private TokenRecuperacion obtenerTokenVigente(String correo, String codigo) {
        Usuario usuario = usuarioRepository.findByCorreo(normalizarCorreo(correo))
                .orElseThrow(() -> error(HttpStatus.BAD_REQUEST, "El código no es válido."));

        // Al pedir un código nuevo se invalidan los anteriores, así que solo queda uno pendiente
        TokenRecuperacion token = null;
        for (TokenRecuperacion pendiente : tokenRecuperacionRepository
                .findByUsuarioIdUsuarioAndUsadoFalse(usuario.getIdUsuario())) {
            if (token == null || pendiente.getIdToken() > token.getIdToken()) {
                token = pendiente;
            }
        }
        if (token == null) {
            throw error(HttpStatus.BAD_REQUEST,
                    "El código ya fue utilizado o fue reemplazado por uno más reciente. Solicita uno nuevo.");
        }
        if (!token.getFechaExpiracion().isAfter(LocalDateTime.now())) {
            throw error(HttpStatus.BAD_REQUEST, "El código venció. Solicita uno nuevo.");
        }
        if (!codigoCoincide(token.getTokenHash(), codigo.trim())) {
            int fallidos = intentosFallidos.getOrDefault(token.getIdToken(), 0) + 1;
            if (fallidos >= MAX_INTENTOS) {
                intentosFallidos.remove(token.getIdToken());
                token.setUsado(true);
                tokenRecuperacionRepository.save(token);
                throw error(HttpStatus.BAD_REQUEST,
                        "Superaste el número máximo de intentos. Solicita un nuevo código.");
            }
            intentosFallidos.put(token.getIdToken(), fallidos);
            int restantes = MAX_INTENTOS - fallidos;
            throw error(HttpStatus.BAD_REQUEST, "El código no es válido. Te quedan " + restantes
                    + (restantes == 1 ? " intento." : " intentos."));
        }
        return token;
    }

    private AuthResponseDTO crearRespuestaAuth(Usuario usuario) {
        String rol = usuario.getRol().getNombre();
        String token = jwtUtil.generarToken(usuario.getCorreo(), rol);
        String nombreCompleto = (usuario.getNombre() + " " + usuario.getApellido()).trim();
        return new AuthResponseDTO(token, "Bearer", usuario.getIdUsuario(), nombreCompleto, rol);
    }

    private Rol buscarRol(String nombre) {
        return rolRepository.findByNombre(normalizar(nombre))
                .orElseThrow(() -> error(HttpStatus.INTERNAL_SERVER_ERROR,
                        "El rol solicitado no existe en la base de datos: " + nombre));
    }

    private String generarSal() {
        byte[] bytes = new byte[16];
        aleatorio.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashCodigo(String sal, String codigo) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest((sal + ":" + codigo).getBytes(StandardCharsets.UTF_8));
            return sal + "$" + HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("No fue posible procesar el código", ex);
        }
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

    private String normalizar(String valor) {
        return Normalizer.normalize(valor == null ? "" : valor.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toUpperCase(Locale.ROOT);
    }

    private String normalizarCorreo(String correo) {
        return correo == null ? "" : correo.trim().toLowerCase(Locale.ROOT);
    }

    private String limpiarTelefono(String telefono) {
        return esVacio(telefono) ? null : telefono.trim();
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.isBlank();
    }

    private ResponseStatusException error(HttpStatus status, String mensaje) {
        return new ResponseStatusException(status, mensaje);
    }
}
