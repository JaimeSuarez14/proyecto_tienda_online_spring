package com.tienda.modelo;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaProducto categoria;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(precision = 5, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private boolean disponible = true;

    @Column(nullable = false)
    private boolean promocion = false;

    private String imagenUrl;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String unidadMedida; // kg, litros, unidades, etc.

    @Column(nullable = true)
    private String genero;

    @Column(nullable = true)
    private String talla;

    @Column(nullable = true)
    private String tela;

    @OneToMany(mappedBy = "producto")
    private List<Carrito> carritos;

    @OneToMany(mappedBy = "producto")
    private List<DetalleVenta> detallesVenta;

    public enum CategoriaProducto {
        HIGIENE_PERSONAL("Higiene y cuidado personal"),
        LIMPIEZA_HOGAR("Limpieza del hogar"),
        SNACKS_GOLOSINAS("Snacks y golosinas"),
        BEBES_MATERNIDAD("Bebés y maternidad"),
        ELECTRONICA("Electrónica y accesorios"),
        ROPA_ACCESORIOS("Ropa y accesorios"),
        BELLEZA("Belleza"),
        COCINA_HOGAR("Cocina y hogar"),
        BEBIDAS("Bebidas"),
        ABARROTES("Abarrotes básicos"),
        TECNOLOGIA("Tecnología básica"),
        MANUALIDADES("Manualidades y arte");

        private final String descripcion;

        CategoriaProducto(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    // Constructores
    public Producto() {
    }

    public Producto(String nombre, String descripcion, CategoriaProducto categoria, BigDecimal precio,
            BigDecimal descuento, int stock, boolean disponible, boolean promocion,
            String imagenUrl, String marca, String unidadMedida, String genero, String talla, String tela) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precio = precio;
        this.descuento = descuento;
        this.stock = stock;
        this.disponible = disponible;
        this.promocion = promocion;
        this.imagenUrl = imagenUrl;
        this.marca = marca;
        this.unidadMedida = unidadMedida;
        this.genero = genero;
        this.talla = talla;
        this.tela = tela;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public CategoriaProducto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaProducto categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public boolean getPromocion() {
        return promocion;
    }

    public void setPromocion(boolean promocion) {
        this.promocion = promocion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getTela() {
        return tela;
    }

    public void setTela(String tela) {
        this.tela = tela;
    }

    public List<Carrito> getCarritos() {
        return carritos;
    }

    public void setCarritos(List<Carrito> carritos) {
        this.carritos = carritos;
    }

    public List<DetalleVenta> getDetallesVenta() {
        return detallesVenta;
    }

    public void setDetallesVenta(List<DetalleVenta> detallesVenta) {
        this.detallesVenta = detallesVenta;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", categoria=" + categoria +
                ", precio=" + precio +
                ", descuento=" + descuento +
                ", stock=" + stock +
                ", disponible=" + disponible +
                ", promocion=" + promocion +
                ", imagenUrl='" + imagenUrl + '\'' +
                ", marca='" + marca + '\'' +
                ", unidadMedida='" + unidadMedida + '\'' +
                ", genero='" + genero + '\'' +
                ", talla='" + talla + '\'' +
                ", tela='" + tela + '\'' +
                '}';
    }
}