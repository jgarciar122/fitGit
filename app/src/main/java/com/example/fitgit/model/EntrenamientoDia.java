package com.example.fitgit.model;

import java.util.List;

public class EntrenamientoDia {
    public long fecha;
    public String nombreRutina;
    public List<EjercicioConSeries> ejercicios;

    public EntrenamientoDia(long fecha, String nombreRutina, List<EjercicioConSeries> ejercicios) {
        this.fecha = fecha;
        this.nombreRutina = nombreRutina;
        this.ejercicios = ejercicios;
    }
}