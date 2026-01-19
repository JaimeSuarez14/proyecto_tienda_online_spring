// Paquete donde se ubicarán tus modelos
package com.tienda.modelo;

public class CategoriaDTO {
    private int id;
    private String nombre;
    private long count;

    public CategoriaDTO(int id, String nombre, long count) {
        this.id = id;
        this.nombre = nombre;
        this.count = count;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}