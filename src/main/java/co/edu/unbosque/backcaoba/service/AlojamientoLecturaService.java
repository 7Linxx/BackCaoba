package co.edu.unbosque.backcaoba.service;

import co.edu.unbosque.backcaoba.dto.AlojamientoDisponibilidadDTO;
import co.edu.unbosque.backcaoba.dto.AlojamientoResumenDTO;
import co.edu.unbosque.backcaoba.dto.DisponibilidadRangoDTO;
import co.edu.unbosque.backcaoba.dto.PoliticaCancelacionDTO;
import co.edu.unbosque.backcaoba.dto.ServicioAdicionalDTO;
import co.edu.unbosque.backcaoba.model.Alojamiento;
import co.edu.unbosque.backcaoba.model.Disponibilidad;
import co.edu.unbosque.backcaoba.model.PoliticaCancelacion;
import co.edu.unbosque.backcaoba.model.Reserva;
import co.edu.unbosque.backcaoba.model.ServicioAdicional;
import co.edu.unbosque.backcaoba.repository.AlojamientoRepository;
import co.edu.unbosque.backcaoba.repository.DisponibilidadRepository;
import co.edu.unbosque.backcaoba.repository.ReservaRepository;
import co.edu.unbosque.backcaoba.repository.ServicioAdicionalRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AlojamientoLecturaService {

    public static final String ESTADO_ACTIVA = "ACTIVA";
    public static final String ESTADO_CONFIRMADA = "CONFIRMADA";
    public static final String TIPO_DISPONIBLE = "DISPONIBLE";
    public static final String TIPO_BLOQUEADO = "BLOQUEADO";

    private final AlojamientoRepository alojamientoRepository;
    private final ServicioAdicionalRepository servicioAdicionalRepository;
    private final DisponibilidadRepository disponibilidadRepository;
    private final ReservaRepository reservaRepository;

    public AlojamientoLecturaService(
            AlojamientoRepository alojamientoRepository,
            ServicioAdicionalRepository servicioAdicionalRepository,
            DisponibilidadRepository disponibilidadRepository,
            ReservaRepository reservaRepository) {
        this.alojamientoRepository = alojamientoRepository;
        this.servicioAdicionalRepository = servicioAdicionalRepository;
        this.disponibilidadRepository = disponibilidadRepository;
        this.reservaRepository = reservaRepository;
    }

    @Transactional(readOnly = true)
    public List<AlojamientoResumenDTO> listarActivos() {
        return alojamientoRepository.findByEstadoOrderByIdAlojamientoAsc(ESTADO_ACTIVA)
                .stream()
                .map(this::mapearResumen)
                .toList();
    }

    @Transactional(readOnly = true)
    public AlojamientoResumenDTO obtenerDetalle(Integer idAlojamiento) {
        Alojamiento alojamiento = buscarAlojamiento(idAlojamiento);
        return mapearResumen(alojamiento);
    }

    @Transactional(readOnly = true)
    public AlojamientoDisponibilidadDTO consultarDisponibilidad(
            Integer idAlojamiento,
            LocalDate fechaEntrada,
            LocalDate fechaSalida) {
        Alojamiento alojamiento = buscarAlojamiento(idAlojamiento);

        List<Disponibilidad> registros = disponibilidadRepository
                .findByAlojamientoIdAlojamientoOrderByFechaInicioAsc(idAlojamiento);

        List<DisponibilidadRangoDTO> ventanasDisponibles = registros.stream()
                .filter(d -> TIPO_DISPONIBLE.equalsIgnoreCase(d.getTipo()))
                .map(d -> new DisponibilidadRangoDTO(d.getFechaInicio(), d.getFechaFin(), TIPO_DISPONIBLE))
                .toList();

        List<DisponibilidadRangoDTO> periodosOcupados = new ArrayList<>();
        registros.stream()
                .filter(d -> TIPO_BLOQUEADO.equalsIgnoreCase(d.getTipo()))
                .map(d -> new DisponibilidadRangoDTO(d.getFechaInicio(), d.getFechaFin(), TIPO_BLOQUEADO))
                .forEach(periodosOcupados::add);

        List<Reserva> confirmadas = reservaRepository
                .findByAlojamientoIdAlojamientoAndEstadoOrderByFechaEntradaAsc(idAlojamiento, ESTADO_CONFIRMADA);
        confirmadas.stream()
                .map(r -> new DisponibilidadRangoDTO(r.getFechaEntrada(), r.getFechaSalida(), "RESERVADO"))
                .forEach(periodosOcupados::add);

        Boolean disponibleEnRango = null;
        String motivo = null;

        if (fechaEntrada != null || fechaSalida != null) {
            if (fechaEntrada == null || fechaSalida == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Debe proporcionar tanto fechaEntrada como fechaSalida.");
            }
            validarFechasEstancia(fechaEntrada, fechaSalida);
            motivo = evaluarMotivoIndisponibilidad(alojamiento, fechaEntrada, fechaSalida);
            disponibleEnRango = (motivo == null);
        }

        return new AlojamientoDisponibilidadDTO(
                alojamiento.getIdAlojamiento(),
                alojamiento.getTitulo(),
                alojamiento.getEstado(),
                disponibleEnRango,
                motivo,
                ventanasDisponibles,
                periodosOcupados);
    }

    public void validarFechasEstancia(LocalDate fechaEntrada, LocalDate fechaSalida) {
        if (fechaEntrada == null || fechaSalida == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Las fechas de entrada y salida son obligatorias.");
        }
        LocalDate hoy = LocalDate.now();
        if (!fechaEntrada.isAfter(hoy)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha de entrada debe ser estrictamente futura (posterior a hoy).");
        }
        if (!fechaSalida.isAfter(fechaEntrada)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha de salida debe ser posterior a la fecha de entrada.");
        }
    }

    public String evaluarMotivoIndisponibilidad(
            Alojamiento alojamiento,
            LocalDate fechaEntrada,
            LocalDate fechaSalida) {
        if (!ESTADO_ACTIVA.equalsIgnoreCase(alojamiento.getEstado())) {
            return "El alojamiento no está activo para recibir reservas (estado actual: "
                    + alojamiento.getEstado() + ").";
        }
        Integer idAlojamiento = alojamiento.getIdAlojamiento();
        boolean tieneVentanas = disponibilidadRepository
                .existsByAlojamientoIdAlojamientoAndTipo(idAlojamiento, TIPO_DISPONIBLE);
        if (tieneVentanas && !disponibilidadRepository
                .existeVentanaDisponibleQueCubraRango(idAlojamiento, fechaEntrada, fechaSalida)) {
            return "Las fechas seleccionadas están fuera del periodo habilitado por el anfitrión.";
        }
        if (disponibilidadRepository.existeBloqueoEnRango(idAlojamiento, fechaEntrada, fechaSalida)) {
            return "El alojamiento tiene fechas bloqueadas por el anfitrión en el periodo seleccionado.";
        }
        if (reservaRepository.existeSolapamientoConfirmado(idAlojamiento, fechaEntrada, fechaSalida)) {
            return "El alojamiento ya cuenta con una reserva confirmada que se solapa con las fechas seleccionadas.";
        }
        return null;
    }

    public void verificarDisponibilidadObligatoria(
            Alojamiento alojamiento,
            LocalDate fechaEntrada,
            LocalDate fechaSalida) {
        String motivo = evaluarMotivoIndisponibilidad(alojamiento, fechaEntrada, fechaSalida);
        if (motivo != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, motivo);
        }
    }

    public Alojamiento buscarAlojamiento(Integer idAlojamiento) {
        return alojamientoRepository.findById(idAlojamiento)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Alojamiento no encontrado."));
    }

    private AlojamientoResumenDTO mapearResumen(Alojamiento a) {
        PoliticaCancelacion p = a.getPolitica();
        PoliticaCancelacionDTO politicaDTO = new PoliticaCancelacionDTO(
                p.getIdPolitica(),
                p.getNombre(),
                p.getDescripcion(),
                p.getDiasLimiteReembolso(),
                p.getPctReembolsoEnPlazo(),
                p.getPctReembolsoFueraPlazo());

        List<ServicioAdicionalDTO> servicios = servicioAdicionalRepository
                .findByAlojamientoIdAlojamientoOrderByIdServicioAdicionalAsc(a.getIdAlojamiento())
                .stream()
                .map(this::mapearServicio)
                .toList();

        String nombreAnfitrion = a.getAnfitrion().getNombre() + " " + a.getAnfitrion().getApellido();

        return new AlojamientoResumenDTO(
                a.getIdAlojamiento(),
                a.getTitulo(),
                a.getDescripcion(),
                a.getCiudad(),
                a.getDireccion(),
                a.getTipoAlojamiento(),
                a.getCapacidad(),
                a.getPrecioNoche(),
                a.getPctTarifaLimpieza(),
                a.getPctDescuento(),
                a.getEstado(),
                a.getAnfitrion().getIdUsuario(),
                nombreAnfitrion,
                politicaDTO,
                servicios);
    }

    private ServicioAdicionalDTO mapearServicio(ServicioAdicional s) {
        return new ServicioAdicionalDTO(s.getIdServicioAdicional(), s.getNombre(), s.getCosto());
    }
}