package co.edu.unbosque.backcaoba.repository;

import co.edu.unbosque.backcaoba.model.TokenRecuperacion;
import co.edu.unbosque.backcaoba.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacion, Long> {
    Optional<TokenRecuperacion> findByTokenHash(String hash);

    Optional<TokenRecuperacion> findFirstByUsuarioOrderByIdDesc(Usuario usuario);

    boolean existsByUsuarioAndFechaCreacionAfter(Usuario usuario, LocalDateTime desde);

    @Modifying
    @Query("update TokenRecuperacion t set t.usado = true where t.usuario = :usuario and t.usado = false")
    int invalidarPendientes(@Param("usuario") Usuario usuario);
}
