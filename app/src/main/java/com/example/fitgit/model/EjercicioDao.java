package com.example.fitgit.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EjercicioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) // Si ya existe, lo actualiza
    void insertarEjercicios(List<Ejercicio> ejercicios);

    @Query("SELECT * FROM tabla_ejercicios")
    LiveData<List<Ejercicio>> obtenerTodosLosEjercicios();

    @Query("SELECT * FROM tabla_ejercicios WHERE bodyPart = :musculo")
    LiveData<List<Ejercicio>> obtenerEjerciciosPorMusculo(String musculo);

    @Query("DELETE FROM tabla_ejercicios")
    void borrarTodo();
    // Añade este para la comprobación rápida
    @Query("SELECT * FROM tabla_ejercicios LIMIT 1")
    Ejercicio obtenerUnoSincrono();
}