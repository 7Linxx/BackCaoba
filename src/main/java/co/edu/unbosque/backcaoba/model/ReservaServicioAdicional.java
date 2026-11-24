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
@Table(name = "reserva_servicio_adicional")
public class ReservaServicioAdicional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva_servicio")
    private Integer idReservaServicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva", nullable = false)
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_servicio_adicional", nullable = false)
    private ServicioAdicional servicioAdicional;

    @Column(name = "nombre_servicio", nullable = false, length = 80)
    private String nombreServicio;

    @Column(name = "costo_aplicado", nullable = false, precision = 12, scale = 2)
    private BigDecimal costoAplicado;

    public Integer getIdReservaServicio() {
        return idReservaServicio;
    }

    public void setIdReservaServicio(Integer idReservaServicio) {
        this.idReservaServicio = idReservaServicio;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public ServicioAdicional getServicioAdicional() {
        return servicioAdicional;
    }

    public void setServicioAdicional(ServicioAdicional servicioAdicional) {
        this.servicioAdicional = servicioAdicional;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    public BigDecimal getCostoAplicado() {
        return costoAplicado;
    }

    public void setCostoAplicado(BigDecimal costoAplicado) {
        this.costoAplicado = costoAplicado;
    }
}