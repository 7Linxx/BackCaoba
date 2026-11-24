package co.edu.unbosque.backcaoba.repository;

import co.edu.unbosque.backcaoba.model.TokenRecuperacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacion, Integer> {

    Optional<TokenRecuperacion> findByTokenHash(String tokenHash);

    List<TokenRecuperacion> findByUsuarioIdUsuarioAndUsadoFalse(Integer idUsuario);

    // Extra (no está en el diagrama): limita a una solicitud de código por minuto
    boolean existsByUsuarioIdUsuarioAndFechaCreacionAfter(Integer idUsuario, LocalDateTime desde);
}
