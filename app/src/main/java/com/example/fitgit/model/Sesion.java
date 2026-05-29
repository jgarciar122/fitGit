package com.example.fitgit.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sesiones")
public class    Sesion {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int rutinaId;
    public String userId;
    public long fecha;

    public Sesion(int rutinaId, String userId) {
        this.rutinaId = rutinaId;
        this.userId = userId;
        this.fecha = System.currentTimeMillis();
    }
}