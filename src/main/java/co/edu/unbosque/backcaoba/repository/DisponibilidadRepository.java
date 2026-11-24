package co.edu.unbosque.backcaoba.repository;

import co.edu.unbosque.backcaoba.model.Disponibilidad;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Integer> {

    List<Disponibilidad> findByAlojamientoIdAlojamientoOrderByFechaInicioAsc(Integer idAlojamiento);

    boolean existsByAlojamientoIdAlojamientoAndTipo(Integer idAlojamiento, String tipo);

    @Query("""
            SELECT COUNT(d) > 0 FROM Disponibilidad d
            WHERE d.alojamiento.idAlojamiento = :idAlojamiento
              AND d.tipo = 'DISPONIBLE'
              AND d.fechaInicio <= :fechaEntrada
              AND d.fechaFin >= :fechaSalida
            """)
    boolean existeVentanaDisponibleQueCubraRango(
            @Param("idAlojamiento") Integer idAlojamiento,
            @Param("fechaEntrada") LocalDate fechaEntrada,
            @Param("fechaSalida") LocalDate fechaSalida);

    @Query("""
            SELECT COUNT(d) > 0 FROM Disponibilidad d
            WHERE d.alojamiento.idAlojamiento = :idAlojamiento
              AND d.tipo = 'BLOQUEADO'
              AND d.fechaInicio < :fechaSalida
              AND d.fechaFin > :fechaEntrada
            """)
    boolean existeBloqueoEnRango(
            @Param("idAlojamiento") Integer idAlojamiento,
            @Param("fechaEntrada") LocalDate fechaEntrada,
            @Param("fechaSalida") LocalDate fechaSalida);
}