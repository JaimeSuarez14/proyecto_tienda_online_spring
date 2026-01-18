package com.tienda.modelo;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montoPagado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPago metodoPago;

    @Column(nullable = false)
    private LocalDateTime fechaVenta;

    @Column(nullable = false, unique = true)
    private String numeroBoleta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estado;

    @OneToMany(mappedBy = "pago", cascade = CascadeType.ALL)
    private List<DetalleVenta> detallesVenta;

    @Column(columnDefinition = "TEXT")
    private String detallesVentaJson; // Para almacenar los detalles como JSON

    public enum MetodoPago {
        tarjeta,
        efectivo,
        transferencia,
        yape,
        plin,
        paypal
    }

    public enum EstadoPago {
        pendiente,
        completado,
        rechazado
    }

    // Constructores
    public Pago() {
        this.fechaVenta = LocalDateTime.now();
        this.estado = EstadoPago.completado; // Por defecto al crear un pago exitoso
    }

    public Pago(Usuario usuario, BigDecimal montoPagado, MetodoPago metodoPago, String numeroBoleta,
            List<DetalleVenta> detallesVenta) {
        this.usuario = usuario;
        this.montoPagado = montoPagado;
        this.metodoPago = metodoPago;
        this.fechaVenta = LocalDateTime.now();
        this.numeroBoleta = numeroBoleta;
        this.estado = EstadoPago.completado;
        this.detallesVenta = detallesVenta;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public String getNumeroBoleta() {
        return numeroBoleta;
    }

    public void setNumeroBoleta(String numeroBoleta) {
        this.numeroBoleta = numeroBoleta;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }

    public List<DetalleVenta> getDetallesVenta() {
        return detallesVenta;
    }

    public void setDetallesVenta(List<DetalleVenta> detallesVenta) {
        this.detallesVenta = detallesVenta;
    }

    public String getDetallesVentaJson() {
        return detallesVentaJson;
    }

    public void setDetallesVentaJson(String detallesVentaJson) {
        this.detallesVentaJson = detallesVentaJson;
    }

    @Override
    public String toString() {
        return "Pago{" +
                "id=" + id +
                ", usuario=" + usuario.getId() +
                ", montoPagado=" + montoPagado +
                ", metodoPago=" + metodoPago +
                ", fechaVenta=" + fechaVenta +
                ", numeroBoleta='" + numeroBoleta + '\'' +
                ", estado=" + estado +
                '}';
    }
}
