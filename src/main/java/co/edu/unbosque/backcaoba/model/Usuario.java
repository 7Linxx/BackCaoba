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
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(nullable = false, length = 100)
    private String apellido;
    @Column(nullable = false, unique = true, length = 150)
    private String correo;
    @Column(name = "contrasena_hash", nullable = false, length = 255)
    private String contrasenaHash;
    @Column(length = 20)
    private String telefono;
    @Column(name = "estado_cuenta", nullable = false, length = 20)
    private String estadoCuenta = "ACTIVA";
    @Column(name = "motivo_restriccion")
    private String motivoRestriccion;
    @Column(name = "fecha_aceptacion_datos", nullable = false)
    private LocalDateTime fechaAceptacionDatos;
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol r) {
        rol = r;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String v) {
        nombre = v;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String v) {
        apellido = v;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String v) {
        correo = v;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(String v) {
        contrasenaHash = v;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String v) {
        telefono = v;
    }

    public String getEstadoCuenta() {
        return estadoCuenta;
    }

    public void setEstadoCuenta(String v) {
        estadoCuenta = v;
    }

    public String getMotivoRestriccion() {
        return motivoRestriccion;
    }

    public void setMotivoRestriccion(String motivoRestriccion) {
        this.motivoRestriccion = motivoRestriccion;
    }

    public LocalDateTime getFechaAceptacionDatos() {
        return fechaAceptacionDatos;
    }

    public void setFechaAceptacionDatos(LocalDateTime v) {
        fechaAceptacionDatos = v;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
}
