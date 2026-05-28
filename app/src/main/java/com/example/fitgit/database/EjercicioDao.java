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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertarEjercicios(List<Ejercicio> ejercicios);

    @Query("SELECT * FROM tabla_ejercicios")
    LiveData<List<Ejercicio>> obtenerTodosLosEjercicios();

    @Query("SELECT * FROM tabla_ejercicios WHERE bodyPart = :musculo")
    LiveData<List<Ejercicio>> obtenerEjerciciosPorMusculo(String musculo);

    @Query("DELETE FROM tabla_ejercicios")
    void borrarTodo();

    @Query("SELECT * FROM tabla_ejercicios LIMIT 1")
    Ejercicio obtenerUnoSincrono();

    @Delete
    void eliminar(Rutina rutina);

    @Query("SELECT * FROM tabla_ejercicios WHERE id = :id LIMIT 1")
    Ejercicio obtenerEjercicioPorId(String id);

    @Query("UPDATE tabla_ejercicios SET nombre_es = :nombreEs, instrucciones_es = :instruccionesEs, traducido = 1 WHERE id = :id")
    void actualizarTraduccion(String id, String nombreEs, List<String> instruccionesEs);
}