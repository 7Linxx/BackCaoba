package co.edu.unbosque.backcaoba.repository;

import co.edu.unbosque.backcaoba.model.Alojamiento;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlojamientoRepository extends JpaRepository<Alojamiento, Integer> {

    List<Alojamiento> findByEstadoOrderByIdAlojamientoAsc(String estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Alojamiento a WHERE a.idAlojamiento = :id")
    Optional<Alojamiento> findByIdParaActualizar(@Param("id") Integer id);
}