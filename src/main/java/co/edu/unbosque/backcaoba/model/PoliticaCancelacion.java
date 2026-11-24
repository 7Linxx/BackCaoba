package co.edu.unbosque.backcaoba.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "politica_cancelacion")
public class PoliticaCancelacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_politica")
    private Integer idPolitica;

    @Column(nullable = false, unique = true, length = 60)
    private String nombre;

    @Column(nullable = false, length = 300)
    private String descripcion;

    @Column(name = "dias_limite_reembolso", nullable = false)
    private Integer diasLimiteReembolso;

    @Column(name = "pct_reembolso_en_plazo", nullable = false, precision = 5, scale = 2)
    private BigDecimal pctReembolsoEnPlazo;

    @Column(name = "pct_reembolso_fuera_plazo", nullable = false, precision = 5, scale = 2)
    private BigDecimal pctReembolsoFueraPlazo;

    public Integer getIdPolitica() {
        return idPolitica;
    }

    public void setIdPolitica(Integer idPolitica) {
        this.idPolitica = idPolitica;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getDiasLimiteReembolso() {
        return diasLimiteReembolso;
    }

    public void setDiasLimiteReembolso(Integer diasLimiteReembolso) {
        this.diasLimiteReembolso = diasLimiteReembolso;
    }

    public BigDecimal getPctReembolsoEnPlazo() {
        return pctReembolsoEnPlazo;
    }

    public void setPctReembolsoEnPlazo(BigDecimal pctReembolsoEnPlazo) {
        this.pctReembolsoEnPlazo = pctReembolsoEnPlazo;
    }

    public BigDecimal getPctReembolsoFueraPlazo() {
        return pctReembolsoFueraPlazo;
    }

    public void setPctReembolsoFueraPlazo(BigDecimal pctReembolsoFueraPlazo) {
        this.pctReembolsoFueraPlazo = pctReembolsoFueraPlazo;
    }
}