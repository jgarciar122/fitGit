package com.example.fitgit.model;

import java.util.List;

public class EjercicioConSeries {
    public String ejercicioId;
    public String nombreEjercicio;
    public List<SerieRegistro> series;

    public EjercicioConSeries(String ejercicioId, String nombreEjercicio, List<SerieRegistro> series) {
        this.ejercicioId = ejercicioId;
        this.nombreEjercicio = nombreEjercicio;
        this.series = series;
    }
}