package co.edu.unbosque.backcaoba.repository;

import co.edu.unbosque.backcaoba.model.ServicioAdicional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicioAdicionalRepository extends JpaRepository<ServicioAdicional, Integer> {

    List<ServicioAdicional> findByAlojamientoIdAlojamientoOrderByIdServicioAdicionalAsc(Integer idAlojamiento);

    List<ServicioAdicional> findByIdServicioAdicionalInAndAlojamientoIdAlojamiento(
            List<Integer> ids,
            Integer idAlojamiento);
}