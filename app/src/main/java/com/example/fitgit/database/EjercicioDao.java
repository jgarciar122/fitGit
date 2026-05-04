package com.example.fitgit.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.fitgit.model.Ejercicio;
import com.example.fitgit.model.Rutina;

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

    @Delete
    void eliminar(Rutina rutina);
}