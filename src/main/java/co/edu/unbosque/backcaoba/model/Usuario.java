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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    public Usuario() {
    }

    public Usuario(Integer idUsuario, String nombre, String apellido, String correo, String contrasenaHash,
            String telefono, String estadoCuenta, String motivoRestriccion, LocalDateTime fechaAceptacionDatos,
            LocalDateTime fechaRegistro, Rol rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasenaHash = contrasenaHash;
        this.telefono = telefono;
        this.estadoCuenta = estadoCuenta;
        this.motivoRestriccion = motivoRestriccion;
        this.fechaAceptacionDatos = fechaAceptacionDatos;
        this.fechaRegistro = fechaRegistro;
        this.rol = rol;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEstadoCuenta() {
        return estadoCuenta;
    }

    public void setEstadoCuenta(String estadoCuenta) {
        this.estadoCuenta = estadoCuenta;
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

    public void setFechaAceptacionDatos(LocalDateTime fechaAceptacionDatos) {
        this.fechaAceptacionDatos = fechaAceptacionDatos;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
