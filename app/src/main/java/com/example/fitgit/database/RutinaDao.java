package com.example.fitgit.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.fitgit.model.Ejercicio;
import com.example.fitgit.model.Rutina;
import com.example.fitgit.model.RutinaEjercicioCrossRef;

import java.util.List;

@Dao
public interface RutinaDao {
    @Insert
    void insertarRutina(Rutina rutina);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void añadirEjercicioARutina(RutinaEjercicioCrossRef crossRef);

    @Query("SELECT * FROM rutinas ORDER BY fechaCreacion DESC")
    LiveData<List<Rutina>> obtenerTodasLasRutinas();

    @Query("SELECT * FROM tabla_ejercicios INNER JOIN rutina_ejercicio_cross_ref ON " +
            "tabla_ejercicios.id = rutina_ejercicio_cross_ref.ejercicioId " +
            "WHERE rutina_ejercicio_cross_ref.rutinaId = :rutinaId")
    LiveData<List<Ejercicio>> obtenerEjerciciosDeRutina(int rutinaId);

    @Delete
    void eliminarEjercicioDeRutina(RutinaEjercicioCrossRef crossRef);
}