package co.edu.unbosque.backcaoba.service;

import co.edu.unbosque.backcaoba.dto.ActualizarPerfilDTO;
import co.edu.unbosque.backcaoba.dto.RegistroDTO;
import co.edu.unbosque.backcaoba.dto.UsuarioDTO;
import co.edu.unbosque.backcaoba.model.Rol;
import co.edu.unbosque.backcaoba.model.Usuario;
import co.edu.unbosque.backcaoba.repository.RolRepository;
import co.edu.unbosque.backcaoba.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
            PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    private ResponseStatusException error(HttpStatus status, String message) {
        return new ResponseStatusException(status, message);
    }

    private String normalizar(String value) {
        return Normalizer.normalize(value == null ? "" : value.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toUpperCase(Locale.ROOT);
    }

    private Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> error(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
    }

    private Rol buscarRol(String nombre) {
        String buscado = normalizar(nombre);
        return rolRepository.findAll().stream()
                .filter(rol -> normalizar(rol.getNombre()).equals(buscado))
                .findFirst().orElseThrow(() -> error(HttpStatus.INTERNAL_SERVER_ERROR,
                        "El rol solicitado no existe en la base de datos: " + nombre));
    }

    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPerfil(String correo) {
        Usuario usuario = buscarPorCorreo(correo);
        return modelMapper.map(usuario, UsuarioDTO.class);
    }

    @Transactional
    public UsuarioDTO actualizarPerfil(String correo, ActualizarPerfilDTO datos) {
        Usuario usuario = buscarPorCorreo(correo);
        usuario.setNombre(datos.nombre().trim());
        usuario.setApellido(datos.apellido().trim());
        usuario.setTelefono(datos.telefono() == null || datos.telefono().isBlank()
                ? null
                : datos.telefono().trim());
        usuarioRepository.save(usuario);
        return modelMapper.map(usuario, UsuarioDTO.class);
    }

    @Transactional
    public void cambiarEstadoCuenta(String correo, String estado) {
        Usuario usuario = buscarPorCorreo(correo);
        if ("RESTRINGIDA".equalsIgnoreCase(usuario.getEstadoCuenta())) {
            throw error(HttpStatus.FORBIDDEN,
                    "Una cuenta restringida no puede reactivarse desde el perfil.");
        }
        usuario.setEstadoCuenta(estado);
        usuarioRepository.save(usuario);
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
    public UsuarioDTO crearAdministrador(RegistroDTO datos) {
        if (!Boolean.TRUE.equals(datos.getAceptaTratamientoDatos())) {
            throw error(HttpStatus.BAD_REQUEST, "Debe aceptar el tratamiento de datos personales.");
        }
        String correo = datos.getCorreo().trim().toLowerCase(Locale.ROOT);
        if (usuarioRepository.existsByCorreo(correo)) {
            throw error(HttpStatus.CONFLICT, "El correo ya está registrado.");
        }
        if (datos.getContrasena() == null
                || !datos.getContrasena().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            throw error(HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener mínimo 8 caracteres, una mayúscula, una minúscula y un número.");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(datos.getNombre().trim());
        usuario.setApellido(datos.getApellido().trim());
        usuario.setCorreo(correo);
        usuario.setTelefono(datos.getTelefono() == null || datos.getTelefono().isBlank()
                ? null
                : datos.getTelefono().trim());
        usuario.setContrasenaHash(passwordEncoder.encode(datos.getContrasena()));
        usuario.setRol(buscarRol("ADMINISTRADOR"));
        usuario.setFechaAceptacionDatos(LocalDateTime.now());
        usuarioRepository.save(usuario);
        return modelMapper.map(usuario, UsuarioDTO.class);
    }

    @Transactional
    public UsuarioDTO asignarRol(Integer id, String nombreRol, String correoAdministrador) {
        String rolNormalizado = normalizar(nombreRol);
        if (!List.of("HUESPED", "ANFITRION", "ADMINISTRADOR").contains(rolNormalizado)) {
            throw error(HttpStatus.BAD_REQUEST, "Rol no permitido.");
        }
        Usuario objetivo = usuarioRepository.findById(id)
                .orElseThrow(() -> error(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
        if (objetivo.getCorreo().equalsIgnoreCase(correoAdministrador)) {
            throw error(HttpStatus.CONFLICT,
                    "No puedes modificar tu propio rol desde esta pantalla.");
        }
        objetivo.setRol(buscarRol(rolNormalizado));
        usuarioRepository.save(objetivo);
        return modelMapper.map(objetivo, UsuarioDTO.class);
    }

    @Transactional
    public UsuarioDTO cambiarEstadoUsuario(Integer id, String nuevoEstado, String correoAdministrador) {
        String estado = normalizar(nuevoEstado);
        if (!List.of("ACTIVA", "INACTIVA", "RESTRINGIDA").contains(estado)) {
            throw error(HttpStatus.BAD_REQUEST, "Estado de cuenta no permitido.");
        }
        Usuario objetivo = usuarioRepository.findById(id)
                .orElseThrow(() -> error(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
        if (objetivo.getCorreo().equalsIgnoreCase(correoAdministrador)) {
            throw error(HttpStatus.CONFLICT,
                    "No puedes cambiar el estado de tu propia cuenta desde esta pantalla.");
        }
        objetivo.setEstadoCuenta(estado);
        usuarioRepository.save(objetivo);
        return modelMapper.map(objetivo, UsuarioDTO.class);
    }

    @Transactional
    public UsuarioDTO restringirUsuario(Integer id, String motivo, String correoAdministrador) {
        Usuario objetivo = usuarioRepository.findById(id)
                .orElseThrow(() -> error(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
        if (objetivo.getCorreo().equalsIgnoreCase(correoAdministrador)) {
            throw error(HttpStatus.CONFLICT, "No puedes restringir tu propia cuenta.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw error(HttpStatus.BAD_REQUEST, "Debes indicar el motivo de la restricción.");
        }
        objetivo.setEstadoCuenta("RESTRINGIDA");
        objetivo.setMotivoRestriccion(motivo.trim());
        usuarioRepository.save(objetivo);
        return modelMapper.map(objetivo, UsuarioDTO.class);
    }
}
