package com.example.fitgit.model;

import androidx.room.ColumnInfo;

public class SesionConDetalle {
    @ColumnInfo(name = "sesionId")
    public int sesionId;

    @ColumnInfo(name = "fecha")
    public long fecha;

    @ColumnInfo(name = "rutinaId")
    public int rutinaId;

    @ColumnInfo(name = "nombreRutina")
    public String nombreRutina;

    @ColumnInfo(name = "ejercicioId")
    public String ejercicioId;

    @ColumnInfo(name = "nombreEjercicio")
    public String nombreEjercicio;

    @ColumnInfo(name = "kg")
    public float kg;

    @ColumnInfo(name = "repeticiones")
    public int repeticiones;
}