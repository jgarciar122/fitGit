package com.example.fitgit.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "rutina_ejercicio_cross_ref",
        primaryKeys = {"rutinaId", "ejercicioId"})
public class RutinaEjercicioCrossRef {
    public int rutinaId;
    @NonNull
    public String ejercicioId; // Usamos String porque el ID de la API suele ser texto
}