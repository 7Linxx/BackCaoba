package co.edu.unbosque.backcaoba.security;

import co.edu.unbosque.backcaoba.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Usuario autenticado. Los controllers lo reciben con
 * {@code @AuthenticationPrincipal UsuarioPrincipal principal} para conocer el
 * id del usuario que hace la petición.
 */
public class UsuarioPrincipal implements UserDetails {

    private final Integer idUsuario;
    private final String correo;
    private final String contrasenaHash;
    private final String rol;
    private final boolean activa;

    public UsuarioPrincipal(Usuario usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.correo = usuario.getCorreo();
        this.contrasenaHash = usuario.getContrasenaHash();
        this.rol = usuario.getRol().getNombre().trim().toUpperCase(Locale.ROOT);
        this.activa = "ACTIVA".equalsIgnoreCase(usuario.getEstadoCuenta());
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getRol() {
        return rol;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol));
    }

    @Override
    public String getPassword() {
        return contrasenaHash;
    }

    @Override
    public String getUsername() {
        return correo;
    }

    @Override
    public boolean isEnabled() {
        return activa;
    }
}
