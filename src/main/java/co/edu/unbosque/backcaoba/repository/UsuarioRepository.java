package co.edu.unbosque.backcaoba.repository;

import co.edu.unbosque.backcaoba.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByCorreo(String correo);
    boolean existsByCorreo(String correo);

    long countByRolNombre(String nombreRol);

    // Reportes: trae solo las fechas de registro, no los usuarios completos
    @Query("select u.fechaRegistro from Usuario u where u.fechaRegistro >= :desde")
    List<LocalDateTime> findFechasRegistroDesde(@Param("desde") LocalDateTime desde);
}
