package co.edu.unbosque.backcaoba.service;

import co.edu.unbosque.backcaoba.dto.CotizacionDTO;
import co.edu.unbosque.backcaoba.dto.CotizacionRequestDTO;
import co.edu.unbosque.backcaoba.dto.ServicioAdicionalDTO;
import co.edu.unbosque.backcaoba.model.Alojamiento;
import co.edu.unbosque.backcaoba.model.ServicioAdicional;
import co.edu.unbosque.backcaoba.model.TarifaServicio;
import co.edu.unbosque.backcaoba.model.Usuario;
import co.edu.unbosque.backcaoba.repository.ServicioAdicionalRepository;
import co.edu.unbosque.backcaoba.repository.TarifaServicioRepository;
import co.edu.unbosque.backcaoba.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CotizacionService {

    private static final BigDecimal CIEN = new BigDecimal("100");

    private final AlojamientoLecturaService alojamientoLecturaService;
    private final ServicioAdicionalRepository servicioAdicionalRepository;
    private final TarifaServicioRepository tarifaServicioRepository;
    private final UsuarioRepository usuarioRepository;

    public CotizacionService(
            AlojamientoLecturaService alojamientoLecturaService,
            ServicioAdicionalRepository servicioAdicionalRepository,
            TarifaServicioRepository tarifaServicioRepository,
            UsuarioRepository usuarioRepository) {
        this.alojamientoLecturaService = alojamientoLecturaService;
        this.servicioAdicionalRepository = servicioAdicionalRepository;
        this.tarifaServicioRepository = tarifaServicioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public CotizacionDTO cotizar(String correoUsuario, CotizacionRequestDTO solicitud) {
        validarRolHuesped(correoUsuario);
        Alojamiento alojamiento = alojamientoLecturaService.buscarAlojamiento(solicitud.idAlojamiento());
        int huespedes = solicitud.numeroHuespedes() == null ? 1 : solicitud.numeroHuespedes();
        return calcularParaAlojamiento(
                alojamiento,
                solicitud.fechaEntrada(),
                solicitud.fechaSalida(),
                huespedes,
                solicitud.idsServiciosAdicionales());
    }

    public CotizacionDTO calcularParaAlojamiento(
            Alojamiento alojamiento,
            java.time.LocalDate fechaEntrada,
            java.time.LocalDate fechaSalida,
            int numeroHuespedes,
            List<Integer> idsServiciosAdicionales) {

        alojamientoLecturaService.validarFechasEstancia(fechaEntrada, fechaSalida);
        validarCapacidadYPrecio(alojamiento, numeroHuespedes);
        alojamientoLecturaService.verificarDisponibilidadObligatoria(alojamiento, fechaEntrada, fechaSalida);

        long nochesLong = ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
        int numeroNoches = Math.toIntExact(nochesLong);

        List<ServicioAdicional> serviciosSeleccionados = resolverServiciosAdicionales(
                alojamiento.getIdAlojamiento(),
                idsServiciosAdicionales);

        BigDecimal precioNoche = redondear(alojamiento.getPrecioNoche());
        BigDecimal precioBaseTotal = redondear(precioNoche.multiply(BigDecimal.valueOf(numeroNoches)));

        BigDecimal pctLimpieza = valorONullCero(alojamiento.getPctTarifaLimpieza());
        BigDecimal montoLimpieza = redondear(precioBaseTotal.multiply(pctLimpieza).divide(CIEN, 6, RoundingMode.HALF_UP));

        BigDecimal pctDescuento = valorONullCero(alojamiento.getPctDescuento());
        BigDecimal montoDescuento = redondear(precioBaseTotal.multiply(pctDescuento).divide(CIEN, 6, RoundingMode.HALF_UP));

        BigDecimal montoServAdicionales = BigDecimal.ZERO;
        List<ServicioAdicionalDTO> serviciosDTO = new ArrayList<>();
        for (ServicioAdicional servicio : serviciosSeleccionados) {
            BigDecimal costo = redondear(servicio.getCosto());
            montoServAdicionales = montoServAdicionales.add(costo);
            serviciosDTO.add(new ServicioAdicionalDTO(
                    servicio.getIdServicioAdicional(),
                    servicio.getNombre(),
                    costo));
        }
        montoServAdicionales = redondear(montoServAdicionales);

        BigDecimal subtotal = precioBaseTotal
                .add(montoLimpieza)
                .add(montoServAdicionales)
                .subtract(montoDescuento);
        if (subtotal.compareTo(BigDecimal.ZERO) < 0) {
            subtotal = BigDecimal.ZERO;
        }

        BigDecimal pctTarifaServicio = tarifaServicioRepository
                .findTopByOrderByVigenteDesdeDescIdTarifaDesc()
                .map(TarifaServicio::getPorcentaje)
                .orElse(BigDecimal.ZERO);

        BigDecimal montoTarifaServicio = redondear(
                subtotal.multiply(pctTarifaServicio).divide(CIEN, 6, RoundingMode.HALF_UP));

        BigDecimal total = redondear(subtotal.add(montoTarifaServicio));

        return new CotizacionDTO(
                alojamiento.getIdAlojamiento(),
                alojamiento.getTitulo(),
                fechaEntrada,
                fechaSalida,
                numeroHuespedes,
                numeroNoches,
                precioNoche,
                precioBaseTotal,
                pctLimpieza,
                montoLimpieza,
                pctDescuento,
                montoDescuento,
                serviciosDTO,
                montoServAdicionales,
                pctTarifaServicio,
                montoTarifaServicio,
                total);
    }

    public List<ServicioAdicional> resolverServiciosAdicionales(
            Integer idAlojamiento,
            List<Integer> idsServiciosAdicionales) {
        if (idsServiciosAdicionales == null || idsServiciosAdicionales.isEmpty()) {
            return List.of();
        }
        Set<Integer> idsUnicos = new LinkedHashSet<>(idsServiciosAdicionales);
        List<ServicioAdicional> encontrados = servicioAdicionalRepository
                .findByIdServicioAdicionalInAndAlojamientoIdAlojamiento(new ArrayList<>(idsUnicos), idAlojamiento);
        if (encontrados.size() != idsUnicos.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Uno o más servicios adicionales no existen o no pertenecen a este alojamiento.");
        }
        return encontrados;
    }

    private void validarCapacidadYPrecio(Alojamiento alojamiento, int numeroHuespedes) {
        if (alojamiento.getPrecioNoche() == null || alojamiento.getPrecioNoche().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El alojamiento tiene un precio por noche inválido.");
        }
        if (alojamiento.getCapacidad() == null || alojamiento.getCapacidad() <= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El alojamiento tiene una capacidad inválida.");
        }
        if (numeroHuespedes < 1 || numeroHuespedes > alojamiento.getCapacidad()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El número de huéspedes debe estar entre 1 y la capacidad máxima del alojamiento ("
                            + alojamiento.getCapacidad() + ").");
        }
    }

    private void validarRolHuesped(String correoUsuario) {
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
        String rol = usuario.getRol().getNombre().trim().toUpperCase(Locale.ROOT);
        if (!"HUESPED".equals(rol)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo los usuarios con rol HUESPED pueden solicitar cotizaciones.");
        }
    }

    private BigDecimal valorONullCero(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

    private BigDecimal redondear(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }
}