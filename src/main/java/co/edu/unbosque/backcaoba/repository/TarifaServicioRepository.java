package co.edu.unbosque.backcaoba.repository;

import co.edu.unbosque.backcaoba.model.TarifaServicio;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarifaServicioRepository extends JpaRepository<TarifaServicio, Integer> {
    Optional<TarifaServicio> findTopByOrderByVigenteDesdeDescIdTarifaDesc();
}