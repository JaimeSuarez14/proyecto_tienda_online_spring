package com.tienda.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cupon")
public class Cupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombreClave;

    @Column(nullable = false)
    private int porcentajeDescuento;

    @Column(nullable = false)
    private boolean activo = true;

    @Column
    private LocalDateTime fechaExpiracion;

    @Column
    private String descripcion;

    // Constructores
    public Cupon() {
    }

    public Cupon(String nombreClave, int porcentajeDescuento, boolean activo) {
        this.nombreClave = nombreClave;
        this.porcentajeDescuento = porcentajeDescuento;
        this.activo = activo;
    }

    public Cupon(String nombreClave, int porcentajeDescuento, boolean activo, LocalDateTime fechaExpiracion) {
        this.nombreClave = nombreClave;
        this.porcentajeDescuento = porcentajeDescuento;
        this.activo = activo;
        this.fechaExpiracion = fechaExpiracion;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreClave() {
        return nombreClave;
    }

    public void setNombreClave(String nombreClave) {
        this.nombreClave = nombreClave;
    }

    public int getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public void setPorcentajeDescuento(int porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Cupon{" +
                "id=" + id +
                ", nombreClave='" + nombreClave + '\'' +
                ", porcentajeDescuento=" + porcentajeDescuento +
                ", activo=" + activo +
                ", fechaExpiracion=" + fechaExpiracion +
                '}';
    }
}
