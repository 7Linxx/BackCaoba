package co.edu.unbosque.backcaoba.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_huesped", nullable = false)
    private Usuario huesped;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_alojamiento", nullable = false)
    private Alojamiento alojamiento;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_politica", nullable = false)
    private PoliticaCancelacion politica;

    @Column(name = "politica_nombre", nullable = false, length = 60)
    private String politicaNombre;

    @Column(name = "politica_dias_limite", nullable = false)
    private Integer politicaDiasLimite;

    @Column(name = "politica_pct_en_plazo", nullable = false, precision = 5, scale = 2)
    private BigDecimal politicaPctEnPlazo;

    @Column(name = "politica_pct_fuera_plazo", nullable = false, precision = 5, scale = 2)
    private BigDecimal politicaPctFueraPlazo;

    @Column(name = "clave_idempotencia", nullable = false, unique = true, length = 64)
    private String claveIdempotencia;

    @Column(name = "codigo_comprobante", nullable = false, unique = true, length = 30)
    private String codigoComprobante;

    @Column(name = "fecha_entrada", nullable = false)
    private LocalDate fechaEntrada;

    @Column(name = "fecha_salida", nullable = false)
    private LocalDate fechaSalida;

    @Column(name = "numero_huespedes", nullable = false)
    private Integer numeroHuespedes;

    @Column(name = "numero_noches", nullable = false)
    private Integer numeroNoches;

    @Column(name = "precio_noche_aplicado", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioNocheAplicado;

    @Column(name = "precio_base_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioBaseTotal;

    @Column(name = "monto_limpieza", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoLimpieza;

    @Column(name = "monto_descuento", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoDescuento;

    @Column(name = "monto_serv_adicionales", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoServAdicionales;

    @Column(name = "pct_tarifa_servicio", nullable = false, precision = 5, scale = 2)
    private BigDecimal pctTarifaServicio;

    @Column(name = "monto_tarifa_servicio", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoTarifaServicio;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, length = 15)
    private String estado = "CONFIRMADA";

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud = LocalDateTime.now();

    @Column(name = "fecha_confirmacion", nullable = false)
    private LocalDateTime fechaConfirmacion = LocalDateTime.now();

    public Integer getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
    }

    public Usuario getHuesped() {
        return huesped;
    }

    public void setHuesped(Usuario huesped) {
        this.huesped = huesped;
    }

    public Alojamiento getAlojamiento() {
        return alojamiento;
    }

    public void setAlojamiento(Alojamiento alojamiento) {
        this.alojamiento = alojamiento;
    }

    public PoliticaCancelacion getPolitica() {
        return politica;
    }

    public void setPolitica(PoliticaCancelacion politica) {
        this.politica = politica;
    }

    public String getPoliticaNombre() {
        return politicaNombre;
    }

    public void setPoliticaNombre(String politicaNombre) {
        this.politicaNombre = politicaNombre;
    }

    public Integer getPoliticaDiasLimite() {
        return politicaDiasLimite;
    }

    public void setPoliticaDiasLimite(Integer politicaDiasLimite) {
        this.politicaDiasLimite = politicaDiasLimite;
    }

    public BigDecimal getPoliticaPctEnPlazo() {
        return politicaPctEnPlazo;
    }

    public void setPoliticaPctEnPlazo(BigDecimal politicaPctEnPlazo) {
        this.politicaPctEnPlazo = politicaPctEnPlazo;
    }

    public BigDecimal getPoliticaPctFueraPlazo() {
        return politicaPctFueraPlazo;
    }

    public void setPoliticaPctFueraPlazo(BigDecimal politicaPctFueraPlazo) {
        this.politicaPctFueraPlazo = politicaPctFueraPlazo;
    }

    public String getClaveIdempotencia() {
        return claveIdempotencia;
    }

    public void setClaveIdempotencia(String claveIdempotencia) {
        this.claveIdempotencia = claveIdempotencia;
    }

    public String getCodigoComprobante() {
        return codigoComprobante;
    }

    public void setCodigoComprobante(String codigoComprobante) {
        this.codigoComprobante = codigoComprobante;
    }

    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(LocalDate fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public Integer getNumeroHuespedes() {
        return numeroHuespedes;
    }

    public void setNumeroHuespedes(Integer numeroHuespedes) {
        this.numeroHuespedes = numeroHuespedes;
    }

    public Integer getNumeroNoches() {
        return numeroNoches;
    }

    public void setNumeroNoches(Integer numeroNoches) {
        this.numeroNoches = numeroNoches;
    }

    public BigDecimal getPrecioNocheAplicado() {
        return precioNocheAplicado;
    }

    public void setPrecioNocheAplicado(BigDecimal precioNocheAplicado) {
        this.precioNocheAplicado = precioNocheAplicado;
    }

    public BigDecimal getPrecioBaseTotal() {
        return precioBaseTotal;
    }

    public void setPrecioBaseTotal(BigDecimal precioBaseTotal) {
        this.precioBaseTotal = precioBaseTotal;
    }

    public BigDecimal getMontoLimpieza() {
        return montoLimpieza;
    }

    public void setMontoLimpieza(BigDecimal montoLimpieza) {
        this.montoLimpieza = montoLimpieza;
    }

    public BigDecimal getMontoDescuento() {
        return montoDescuento;
    }

    public void setMontoDescuento(BigDecimal montoDescuento) {
        this.montoDescuento = montoDescuento;
    }

    public BigDecimal getMontoServAdicionales() {
        return montoServAdicionales;
    }

    public void setMontoServAdicionales(BigDecimal montoServAdicionales) {
        this.montoServAdicionales = montoServAdicionales;
    }

    public BigDecimal getPctTarifaServicio() {
        return pctTarifaServicio;
    }

    public void setPctTarifaServicio(BigDecimal pctTarifaServicio) {
        this.pctTarifaServicio = pctTarifaServicio;
    }

    public BigDecimal getMontoTarifaServicio() {
        return montoTarifaServicio;
    }

    public void setMontoTarifaServicio(BigDecimal montoTarifaServicio) {
        this.montoTarifaServicio = montoTarifaServicio;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDateTime getFechaConfirmacion() {
        return fechaConfirmacion;
    }

    public void setFechaConfirmacion(LocalDateTime fechaConfirmacion) {
        this.fechaConfirmacion = fechaConfirmacion;
    }
}