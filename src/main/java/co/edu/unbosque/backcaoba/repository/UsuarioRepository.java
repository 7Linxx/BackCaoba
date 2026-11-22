package co.edu.unbosque.backcaoba.repository;

import co.edu.unbosque.backcaoba.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);
}
