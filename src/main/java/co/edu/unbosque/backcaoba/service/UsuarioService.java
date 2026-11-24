package co.edu.unbosque.backcaoba.service;

import co.edu.unbosque.backcaoba.dto.CambioEstadoDTO;
import co.edu.unbosque.backcaoba.dto.RegistroDTO;
import co.edu.unbosque.backcaoba.dto.RolDTO;
import co.edu.unbosque.backcaoba.dto.UsuarioDTO;
import co.edu.unbosque.backcaoba.model.Rol;
import co.edu.unbosque.backcaoba.model.Usuario;
import co.edu.unbosque.backcaoba.repository.RolRepository;
import co.edu.unbosque.backcaoba.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class UsuarioService {

    private static final String PATRON_CONTRASENA = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
    private static final List<String> ROLES = List.of("HUESPED", "ANFITRION", "ADMINISTRADOR");
    private static final List<String> ESTADOS = List.of("ACTIVA", "INACTIVA", "RESTRINGIDA");

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPerfil(Integer idUsuario) {
        Usuario usuario = buscarUsuario(idUsuario);
        return modelMapper.map(usuario, UsuarioDTO.class);
    }

    @Transactional
    public UsuarioDTO actualizarPerfil(Integer idUsuario, UsuarioDTO dto) {
        Usuario usuario = buscarUsuario(idUsuario);
        // Solo se actualizan los datos personales; correo, rol y estado no se cambian desde el perfil
        usuario.setNombre(dto.getNombre().trim());
        usuario.setApellido(dto.getApellido().trim());
        usuario.setTelefono(dto.getTelefono() == null || dto.getTelefono().isBlank()
                ? null
                : dto.getTelefono().trim());
        usuarioRepository.save(usuario);
        return modelMapper.map(usuario, UsuarioDTO.class);
    }

    @Transactional
    public UsuarioDTO cambiarEstadoCuenta(Integer idUsuario, Boolean activa) {
        Usuario usuario = buscarUsuario(idUsuario);
        if ("RESTRINGIDA".equalsIgnoreCase(usuario.getEstadoCuenta())) {
            throw error(HttpStatus.FORBIDDEN, "Una cuenta restringida no puede reactivarse desde el perfil.");
        }
        usuario.setEstadoCuenta(Boolean.TRUE.equals(activa) ? "ACTIVA" : "INACTIVA");
        usuarioRepository.save(usuario);
        return modelMapper.map(usuario, UsuarioDTO.class);
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarUsuarios() {
        List<UsuarioDTO> dtoList = new ArrayList<>();
        for (Usuario u : usuarioRepository.findAll(Sort.by("idUsuario"))) {
            dtoList.add(modelMapper.map(u, UsuarioDTO.class));
        }
        return dtoList;
    }

    @Transactional
    public UsuarioDTO crearAdministrador(RegistroDTO dto) {
        if (!Boolean.TRUE.equals(dto.getAceptaTratamientoDatos())) {
            throw error(HttpStatus.BAD_REQUEST, "Debe aceptar el tratamiento de datos personales.");
        }
        String correo = dto.getCorreo().trim().toLowerCase(Locale.ROOT);
        if (usuarioRepository.existsByCorreo(correo)) {
            throw error(HttpStatus.CONFLICT, "El correo ya está registrado.");
        }
        if (dto.getContrasena() == null || !dto.getContrasena().matches(PATRON_CONTRASENA)) {
            throw error(HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener mínimo 8 caracteres, una mayúscula, una minúscula y un número.");
        }

        Usuario entity = modelMapper.map(dto, Usuario.class);
        entity.setIdUsuario(null);
        entity.setNombre(dto.getNombre().trim());
        entity.setApellido(dto.getApellido().trim());
        entity.setCorreo(correo);
        entity.setTelefono(dto.getTelefono() == null || dto.getTelefono().isBlank()
                ? null
                : dto.getTelefono().trim());
        entity.setContrasenaHash(passwordEncoder.encode(dto.getContrasena()));
        entity.setRol(buscarRol("ADMINISTRADOR"));
        entity.setFechaAceptacionDatos(LocalDateTime.now());
        usuarioRepository.save(entity);
        return modelMapper.map(entity, UsuarioDTO.class);
    }

    @Transactional
    public UsuarioDTO restringirUsuario(Integer idAdministrador, Integer idUsuario, CambioEstadoDTO dto) {
        Usuario objetivo = buscarUsuario(idUsuario);
        if (objetivo.getIdUsuario().equals(idAdministrador)) {
            throw error(HttpStatus.CONFLICT, "No puedes restringir tu propia cuenta.");
        }
        if (dto.getMotivo() == null || dto.getMotivo().isBlank()) {
            throw error(HttpStatus.BAD_REQUEST, "Debes indicar el motivo de la restricción.");
        }
        objetivo.setEstadoCuenta("RESTRINGIDA");
        objetivo.setMotivoRestriccion(dto.getMotivo().trim());
        usuarioRepository.save(objetivo);
        return modelMapper.map(objetivo, UsuarioDTO.class);
    }

    // Extra (no está en el diagrama): el administrador cambia el rol de un usuario
    @Transactional
    public UsuarioDTO asignarRol(Integer idAdministrador, Integer idUsuario, RolDTO dto) {
        String nombreRol = normalizar(dto.getNombre());
        if (!ROLES.contains(nombreRol)) {
            throw error(HttpStatus.BAD_REQUEST, "Rol no permitido.");
        }
        Usuario objetivo = buscarUsuario(idUsuario);
        if (objetivo.getIdUsuario().equals(idAdministrador)) {
            throw error(HttpStatus.CONFLICT, "No puedes modificar tu propio rol desde esta pantalla.");
        }
        objetivo.setRol(buscarRol(nombreRol));
        usuarioRepository.save(objetivo);
        return modelMapper.map(objetivo, UsuarioDTO.class);
    }

    // Extra (no está en el diagrama): el administrador activa o desactiva una cuenta
    @Transactional
    public UsuarioDTO cambiarEstadoUsuario(Integer idAdministrador, Integer idUsuario, CambioEstadoDTO dto) {
        String estado = normalizar(dto.getEstado());
        if (!ESTADOS.contains(estado)) {
            throw error(HttpStatus.BAD_REQUEST, "Estado de cuenta no permitido.");
        }
        Usuario objetivo = buscarUsuario(idUsuario);
        if (objetivo.getIdUsuario().equals(idAdministrador)) {
            throw error(HttpStatus.CONFLICT,
                    "No puedes cambiar el estado de tu propia cuenta desde esta pantalla.");
        }
        objetivo.setEstadoCuenta(estado);
        usuarioRepository.save(objetivo);
        return modelMapper.map(objetivo, UsuarioDTO.class);
    }

    private Usuario buscarUsuario(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> error(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
    }

    private Rol buscarRol(String nombre) {
        return rolRepository.findByNombre(normalizar(nombre))
                .orElseThrow(() -> error(HttpStatus.INTERNAL_SERVER_ERROR,
                        "El rol solicitado no existe en la base de datos: " + nombre));
    }

    private String normalizar(String valor) {
        return Normalizer.normalize(valor == null ? "" : valor.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toUpperCase(Locale.ROOT);
    }

    private ResponseStatusException error(HttpStatus status, String mensaje) {
        return new ResponseStatusException(status, mensaje);
    }
}
