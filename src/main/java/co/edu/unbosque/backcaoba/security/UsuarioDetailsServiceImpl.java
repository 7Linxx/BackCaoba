package co.edu.unbosque.backcaoba.security;

import co.edu.unbosque.backcaoba.model.Usuario;
import co.edu.unbosque.backcaoba.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Locale;

@Service
public class UsuarioDetailsServiceImpl implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado."));
        String autoridad = "ROLE_" + usuario.getRol().getNombre().trim().toUpperCase(Locale.ROOT);
        boolean activa = "ACTIVA".equalsIgnoreCase(usuario.getEstadoCuenta());
        return User.withUsername(usuario.getCorreo())
                .password(usuario.getContrasenaHash())
                .authorities(autoridad)
                .disabled(!activa)
                .build();
    }
}
