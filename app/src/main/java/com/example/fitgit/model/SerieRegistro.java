package com.example.fitgit.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "series_registro")
public class SerieRegistro {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int sesionId;
    public String ejercicioId;
    public float kg;
    public int repeticiones;

    public SerieRegistro(int sesionId, String ejercicioId, float kg, int repeticiones) {
        this.sesionId = sesionId;
        this.ejercicioId = ejercicioId;
        this.kg = kg;
        this.repeticiones = repeticiones;
    }
}