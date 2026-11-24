package co.edu.unbosque.backcaoba.repository;

import co.edu.unbosque.backcaoba.model.Reserva;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    Optional<Reserva> findByClaveIdempotencia(String claveIdempotencia);

    List<Reserva> findByHuespedIdUsuarioOrderByFechaSolicitudDesc(Integer idHuesped);

    List<Reserva> findByAlojamientoAnfitrionIdUsuarioOrderByFechaSolicitudDesc(Integer idAnfitrion);
    long countByAlojamientoAnfitrionIdUsuario(Integer idAnfitrion);

    long countByAlojamientoAnfitrionIdUsuarioAndEstado(Integer idAnfitrion, String estado);

    List<Reserva> findByAlojamientoIdAlojamientoAndEstadoOrderByFechaEntradaAsc(
            Integer idAlojamiento,
            String estado);

    @Query("""
            SELECT COUNT(r) > 0 FROM Reserva r
            WHERE r.alojamiento.idAlojamiento = :idAlojamiento
              AND r.estado = 'CONFIRMADA'
              AND r.fechaEntrada < :fechaSalida
              AND r.fechaSalida > :fechaEntrada
            """)
    boolean existeSolapamientoConfirmado(
            @Param("idAlojamiento") Integer idAlojamiento,
            @Param("fechaEntrada") LocalDate fechaEntrada,
            @Param("fechaSalida") LocalDate fechaSalida);
}