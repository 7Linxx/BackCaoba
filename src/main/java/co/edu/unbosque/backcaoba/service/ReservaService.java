package co.edu.unbosque.backcaoba.service;

import co.edu.unbosque.backcaoba.dto.CancelacionDTO;
import co.edu.unbosque.backcaoba.dto.CancelacionRequestDTO;
import co.edu.unbosque.backcaoba.dto.CotizacionDTO;
import co.edu.unbosque.backcaoba.dto.PoliticaCancelacionDTO;
import co.edu.unbosque.backcaoba.dto.ReservaDTO;
import co.edu.unbosque.backcaoba.dto.ReservaRequestDTO;
import co.edu.unbosque.backcaoba.dto.ReservaServicioAdicionalDTO;
import co.edu.unbosque.backcaoba.model.Alojamiento;
import co.edu.unbosque.backcaoba.model.Cancelacion;
import co.edu.unbosque.backcaoba.model.PoliticaCancelacion;
import co.edu.unbosque.backcaoba.model.Reserva;
import co.edu.unbosque.backcaoba.model.ReservaServicioAdicional;
import co.edu.unbosque.backcaoba.model.ServicioAdicional;
import co.edu.unbosque.backcaoba.model.Usuario;
import co.edu.unbosque.backcaoba.repository.AlojamientoRepository;
import co.edu.unbosque.backcaoba.repository.CancelacionRepository;
import co.edu.unbosque.backcaoba.repository.ReservaRepository;
import co.edu.unbosque.backcaoba.repository.ReservaServicioAdicionalRepository;
import co.edu.unbosque.backcaoba.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReservaService {

    private static final String ROL_HUESPED = "HUESPED";
    private static final String ROL_ANFITRION = "ANFITRION";
    private static final String ESTADO_CONFIRMADA = "CONFIRMADA";
    private static final String ESTADO_CANCELADA = "CANCELADA";
    private static final BigDecimal CIEN = new BigDecimal("100");
    private static final DateTimeFormatter FORMATO_COMPROBANTE = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final ReservaRepository reservaRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final ReservaServicioAdicionalRepository reservaServicioAdicionalRepository;
    private final CancelacionRepository cancelacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final CotizacionService cotizacionService;

    public ReservaService(
            ReservaRepository reservaRepository,
            AlojamientoRepository alojamientoRepository,
            ReservaServicioAdicionalRepository reservaServicioAdicionalRepository,
            CancelacionRepository cancelacionRepository,
            UsuarioRepository usuarioRepository,
            CotizacionService cotizacionService) {
        this.reservaRepository = reservaRepository;
        this.alojamientoRepository = alojamientoRepository;
        this.reservaServicioAdicionalRepository = reservaServicioAdicionalRepository;
        this.cancelacionRepository = cancelacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.cotizacionService = cotizacionService;
    }

    @Transactional
    public ReservaDTO crearReserva(String correoUsuario, ReservaRequestDTO solicitud) {
        Usuario huesped = buscarUsuarioConRol(correoUsuario, ROL_HUESPED,
                "Solo los usuarios con rol HUESPED pueden crear reservas.");

        String clave = solicitud.claveIdempotencia().trim();

        
        Optional<Reserva> existente = reservaRepository.findByClaveIdempotencia(clave);
        if (existente.isPresent()) {
            return responderIdempotente(existente.get(), huesped);
        }

        
        Alojamiento alojamiento = alojamientoRepository.findByIdParaActualizar(solicitud.idAlojamiento())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Alojamiento no encontrado."));

       
        Optional<Reserva> existenteBajoBloqueo = reservaRepository.findByClaveIdempotencia(clave);
        if (existenteBajoBloqueo.isPresent()) {
            return responderIdempotente(existenteBajoBloqueo.get(), huesped);
        }

        
        CotizacionDTO cotizacion = cotizacionService.calcularParaAlojamiento(
                alojamiento,
                solicitud.fechaEntrada(),
                solicitud.fechaSalida(),
                solicitud.numeroHuespedes(),
                solicitud.idsServiciosAdicionales());

        List<ServicioAdicional> serviciosSeleccionados = cotizacionService.resolverServiciosAdicionales(
                alojamiento.getIdAlojamiento(),
                solicitud.idsServiciosAdicionales());

        PoliticaCancelacion politica = alojamiento.getPolitica();
        LocalDateTime ahora = LocalDateTime.now();

        Reserva reserva = new Reserva();
        reserva.setHuesped(huesped);
        reserva.setAlojamiento(alojamiento);
        reserva.setPolitica(politica);
        reserva.setPoliticaNombre(politica.getNombre());
        reserva.setPoliticaDiasLimite(politica.getDiasLimiteReembolso());
        reserva.setPoliticaPctEnPlazo(politica.getPctReembolsoEnPlazo());
        reserva.setPoliticaPctFueraPlazo(politica.getPctReembolsoFueraPlazo());
        reserva.setClaveIdempotencia(clave);
        reserva.setCodigoComprobante(generarCodigoComprobante(ahora));
        reserva.setFechaEntrada(cotizacion.fechaEntrada());
        reserva.setFechaSalida(cotizacion.fechaSalida());
        reserva.setNumeroHuespedes(cotizacion.numeroHuespedes());
        reserva.setNumeroNoches(cotizacion.numeroNoches());
        reserva.setPrecioNocheAplicado(cotizacion.precioNoche());
        reserva.setPrecioBaseTotal(cotizacion.precioBaseTotal());
        reserva.setMontoLimpieza(cotizacion.montoLimpieza());
        reserva.setMontoDescuento(cotizacion.montoDescuento());
        reserva.setMontoServAdicionales(cotizacion.montoServAdicionales());
        reserva.setPctTarifaServicio(cotizacion.pctTarifaServicio());
        reserva.setMontoTarifaServicio(cotizacion.montoTarifaServicio());
        reserva.setTotal(cotizacion.total());
        reserva.setEstado(ESTADO_CONFIRMADA);
        reserva.setFechaSolicitud(ahora);
        reserva.setFechaConfirmacion(ahora);

        Reserva guardada = reservaRepository.saveAndFlush(reserva);

        for (ServicioAdicional servicio : serviciosSeleccionados) {
            ReservaServicioAdicional item = new ReservaServicioAdicional();
            item.setReserva(guardada);
            item.setServicioAdicional(servicio);
            item.setNombreServicio(servicio.getNombre());
            item.setCostoAplicado(servicio.getCosto().setScale(2, RoundingMode.HALF_UP));
            reservaServicioAdicionalRepository.save(item);
        }

        return mapearReserva(guardada);
    }

    @Transactional(readOnly = true)
    public List<ReservaDTO> listarHistorialHuesped(String correoUsuario) {
        Usuario huesped = buscarUsuarioConRol(correoUsuario, ROL_HUESPED,
                "Solo los usuarios con rol HUESPED pueden consultar su historial de reservas.");
        return reservaRepository.findByHuespedIdUsuarioOrderByFechaSolicitudDesc(huesped.getIdUsuario())
                .stream()
                .map(this::mapearReserva)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservaDTO> listarReservasRecibidasAnfitrion(String correoUsuario) {
        Usuario anfitrion = buscarUsuarioConRol(correoUsuario, ROL_ANFITRION,
                "Solo los usuarios con rol ANFITRION pueden consultar las reservas recibidas.");
        return reservaRepository.findByAlojamientoAnfitrionIdUsuarioOrderByFechaSolicitudDesc(anfitrion.getIdUsuario())
                .stream()
                .map(this::mapearReserva)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservaDTO obtenerDetalle(String correoUsuario, Integer idReserva) {
        Usuario usuario = buscarUsuario(correoUsuario);
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada."));

        String rol = normalizarRol(usuario);
        boolean esHuespedPropietario = ROL_HUESPED.equals(rol)
                && reserva.getHuesped().getIdUsuario().equals(usuario.getIdUsuario());
        boolean esAnfitrionPropietario = ROL_ANFITRION.equals(rol)
                && reserva.getAlojamiento().getAnfitrion().getIdUsuario().equals(usuario.getIdUsuario());

        if (!esHuespedPropietario && !esAnfitrionPropietario) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tienes permisos para consultar el detalle de esta reserva.");
        }
        return mapearReserva(reserva);
    }

    @Transactional
    public CancelacionDTO cancelarReserva(
            String correoUsuario,
            Integer idReserva,
            CancelacionRequestDTO solicitud) {
        Usuario huesped = buscarUsuarioConRol(correoUsuario, ROL_HUESPED,
                "Solo el huésped propietario puede cancelar una reserva.");

        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada."));

        if (!reserva.getHuesped().getIdUsuario().equals(huesped.getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No puedes cancelar una reserva que pertenece a otro huésped.");
        }

        if (!ESTADO_CONFIRMADA.equalsIgnoreCase(reserva.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Solo se pueden cancelar reservas en estado CONFIRMADA (estado actual: "
                            + reserva.getEstado() + ").");
        }

        LocalDate hoy = LocalDate.now();
        if (!hoy.isBefore(reserva.getFechaEntrada())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No es posible cancelar una reserva cuya fecha de entrada ya llegó o pasó.");
        }

        long diasAnticipacion = ChronoUnit.DAYS.between(hoy, reserva.getFechaEntrada());
        BigDecimal pctDevolucion = diasAnticipacion >= reserva.getPoliticaDiasLimite()
                ? reserva.getPoliticaPctEnPlazo()
                : reserva.getPoliticaPctFueraPlazo();

        BigDecimal montoDevolucion = reserva.getTotal()
                .multiply(pctDevolucion)
                .divide(CIEN, 2, RoundingMode.HALF_UP);

        reserva.setEstado(ESTADO_CANCELADA);
        reservaRepository.save(reserva);

        Cancelacion cancelacion = new Cancelacion();
        cancelacion.setReserva(reserva);
        cancelacion.setUsuario(huesped);
        cancelacion.setFechaCancelacion(LocalDateTime.now());
        cancelacion.setMotivo(solicitud.motivo().trim());
        cancelacion.setMontoDevolucion(montoDevolucion);

        Cancelacion guardada = cancelacionRepository.save(cancelacion);

        return mapearCancelacion(guardada, reserva.getEstado());
    }

    private ReservaDTO responderIdempotente(Reserva reserva, Usuario huesped) {
        if (!reserva.getHuesped().getIdUsuario().equals(huesped.getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La clave de idempotencia ya fue utilizada por otra solicitud.");
        }
        return mapearReserva(reserva);
    }

    private Usuario buscarUsuario(String correoUsuario) {
        return usuarioRepository.findByCorreo(correoUsuario.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
    }

    private Usuario buscarUsuarioConRol(String correoUsuario, String rolEsperado, String mensajeError) {
        Usuario usuario = buscarUsuario(correoUsuario);
        if (!rolEsperado.equals(normalizarRol(usuario))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, mensajeError);
        }
        return usuario;
    }

    private String normalizarRol(Usuario usuario) {
        return usuario.getRol().getNombre().trim().toUpperCase(Locale.ROOT);
    }

    private String generarCodigoComprobante(LocalDateTime ahora) {
        String sufijo = UUID.randomUUID().toString().replace("-", "")
                .substring(0, 8).toUpperCase(Locale.ROOT);
        return "CAOBA-" + ahora.format(FORMATO_COMPROBANTE) + "-" + sufijo;
    }

    private ReservaDTO mapearReserva(Reserva r) {
        PoliticaCancelacionDTO politicaDTO = new PoliticaCancelacionDTO(
                r.getPolitica().getIdPolitica(),
                r.getPoliticaNombre(),
                r.getPolitica().getDescripcion(),
                r.getPoliticaDiasLimite(),
                r.getPoliticaPctEnPlazo(),
                r.getPoliticaPctFueraPlazo());

        List<ReservaServicioAdicionalDTO> servicios = reservaServicioAdicionalRepository
                .findByReservaIdReserva(r.getIdReserva())
                .stream()
                .map(s -> new ReservaServicioAdicionalDTO(
                        s.getServicioAdicional().getIdServicioAdicional(),
                        s.getNombreServicio(),
                        s.getCostoAplicado()))
                .toList();

        CancelacionDTO cancelacionDTO = cancelacionRepository.findByReservaIdReserva(r.getIdReserva())
                .map(c -> mapearCancelacion(c, r.getEstado()))
                .orElse(null);

        String nombreHuesped = r.getHuesped().getNombre() + " " + r.getHuesped().getApellido();

        return new ReservaDTO(
                r.getIdReserva(),
                r.getCodigoComprobante(),
                r.getFechaEntrada(),
                r.getFechaSalida(),
                r.getNumeroHuespedes(),
                r.getNumeroNoches(),
                r.getPrecioNocheAplicado(),
                r.getPrecioBaseTotal(),
                r.getMontoLimpieza(),
                r.getMontoDescuento(),
                r.getMontoServAdicionales(),
                r.getPctTarifaServicio(),
                r.getMontoTarifaServicio(),
                r.getTotal(),
                r.getEstado(),
                r.getFechaSolicitud(),
                r.getFechaConfirmacion(),
                r.getHuesped().getIdUsuario(),
                nombreHuesped,
                r.getHuesped().getCorreo(),
                r.getAlojamiento().getIdAlojamiento(),
                r.getAlojamiento().getTitulo(),
                r.getAlojamiento().getCiudad(),
                politicaDTO,
                servicios,
                cancelacionDTO);
    }

    private CancelacionDTO mapearCancelacion(Cancelacion c, String estadoReserva) {
        String nombreUsuario = c.getUsuario().getNombre() + " " + c.getUsuario().getApellido();
        return new CancelacionDTO(
                c.getIdCancelacion(),
                c.getReserva().getIdReserva(),
                c.getUsuario().getIdUsuario(),
                nombreUsuario,
                c.getFechaCancelacion(),
                c.getMotivo(),
                c.getMontoDevolucion(),
                estadoReserva);
    }
}