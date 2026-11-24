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

@Entity
@Table(name = "alojamiento")
public class Alojamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alojamiento")
    private Integer idAlojamiento;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_anfitrion", nullable = false)
    private Usuario anfitrion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_politica", nullable = false)
    private PoliticaCancelacion politica;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @Column(nullable = false, length = 80)
    private String ciudad;

    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(name = "tipo_alojamiento", nullable = false, length = 40)
    private String tipoAlojamiento;

    @Column(nullable = false)
    private Integer capacidad;

    @Column(name = "precio_noche", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioNoche;

    @Column(name = "pct_tarifa_limpieza", nullable = false, precision = 5, scale = 2)
    private BigDecimal pctTarifaLimpieza = BigDecimal.ZERO;

    @Column(name = "pct_descuento", nullable = false, precision = 5, scale = 2)
    private BigDecimal pctDescuento = BigDecimal.ZERO;

    @Column(nullable = false, length = 20)
    private String estado = "ACTIVA";

    public Integer getIdAlojamiento() {
        return idAlojamiento;
    }

    public void setIdAlojamiento(Integer idAlojamiento) {
        this.idAlojamiento = idAlojamiento;
    }

    public Usuario getAnfitrion() {
        return anfitrion;
    }

    public void setAnfitrion(Usuario anfitrion) {
        this.anfitrion = anfitrion;
    }

    public PoliticaCancelacion getPolitica() {
        return politica;
    }

    public void setPolitica(PoliticaCancelacion politica) {
        this.politica = politica;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTipoAlojamiento() {
        return tipoAlojamiento;
    }

    public void setTipoAlojamiento(String tipoAlojamiento) {
        this.tipoAlojamiento = tipoAlojamiento;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public BigDecimal getPrecioNoche() {
        return precioNoche;
    }

    public void setPrecioNoche(BigDecimal precioNoche) {
        this.precioNoche = precioNoche;
    }

    public BigDecimal getPctTarifaLimpieza() {
        return pctTarifaLimpieza;
    }

    public void setPctTarifaLimpieza(BigDecimal pctTarifaLimpieza) {
        this.pctTarifaLimpieza = pctTarifaLimpieza;
    }

    public BigDecimal getPctDescuento() {
        return pctDescuento;
    }

    public void setPctDescuento(BigDecimal pctDescuento) {
        this.pctDescuento = pctDescuento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}