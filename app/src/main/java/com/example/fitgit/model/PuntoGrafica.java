package com.example.fitgit.model;

import androidx.room.ColumnInfo;

public class PuntoGrafica {
    @ColumnInfo(name = "fecha")
    public long fecha;

    @ColumnInfo(name = "kg")
    public float kg;
}