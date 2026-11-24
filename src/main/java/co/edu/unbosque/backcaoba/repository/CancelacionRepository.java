package co.edu.unbosque.backcaoba.repository;

import co.edu.unbosque.backcaoba.model.Cancelacion;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CancelacionRepository extends JpaRepository<Cancelacion, Integer> {
    Optional<Cancelacion> findByReservaIdReserva(Integer idReserva);
}