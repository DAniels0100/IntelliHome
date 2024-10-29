package com.example.intellihome.pojo;

import com.google.firebase.database.PropertyName;

public class Propiedad {
    private String nombre;
    private String ubicacion;
    private String amenidades;
    private int cantidadHabitaciones;
    private int cantidadPersonas;
    private String precio;

    public Propiedad() {}

    public Propiedad(String nombre, String ubicacion, String amenidades, String precio, int cantidadHabitaciones, int cantidadPersonas) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.amenidades = amenidades;
        this.cantidadHabitaciones = cantidadHabitaciones;
        this.cantidadPersonas = cantidadPersonas;
        this.precio = precio;
    }

    @PropertyName("nombrePropiedad")
    public String getNombre() {
        return nombre;
    }

    @PropertyName("nombrePropiedad")
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @PropertyName("ubicacion")
    public String getUbicacion() {
        return ubicacion;
    }

    @PropertyName("ubicacion")
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    @PropertyName("amenidadesCasa")
    public String getAmenidades() {
        return amenidades;
    }

    @PropertyName("amenidadesCasa")
    public void setAmenidades(String amenidades) {
        this.amenidades = amenidades;
    }

    public int getCantidadHabitaciones() {
        return cantidadHabitaciones;
    }

    public void setCantidadHabitaciones(int cantidadHabitaciones) {
        this.cantidadHabitaciones = cantidadHabitaciones;
    }

    public int getCantidadPersonas() {
        return cantidadPersonas;
    }

    public void setCantidadPersonas(int cantidadPersonas) {
        this.cantidadPersonas = cantidadPersonas;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }
}
