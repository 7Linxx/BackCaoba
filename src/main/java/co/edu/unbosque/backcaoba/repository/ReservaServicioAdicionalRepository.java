package co.edu.unbosque.backcaoba.repository;

import co.edu.unbosque.backcaoba.model.ReservaServicioAdicional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaServicioAdicionalRepository
        extends JpaRepository<ReservaServicioAdicional, Integer> {

    List<ReservaServicioAdicional> findByReservaIdReserva(Integer idReserva);
}