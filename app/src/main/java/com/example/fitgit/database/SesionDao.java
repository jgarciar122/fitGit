package com.example.fitgit.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.fitgit.model.PuntoGrafica;
import com.example.fitgit.model.SerieRegistro;
import com.example.fitgit.model.Sesion;

import java.util.List;

@Dao
public interface SesionDao {

    // Inserta una sesión y devuelve su ID generado (lo necesitamos para las series)
    @Insert
    long insertarSesion(Sesion sesion);

    @Insert
    void insertarSeries(List<SerieRegistro> series);

    // Obtener todas las sesiones de un ejercicio concreto para ver el historial
    @Query("SELECT * FROM sesiones " +
            "INNER JOIN series_registro ON sesiones.id = series_registro.sesionId " +
            "WHERE series_registro.ejercicioId = :ejercicioId AND sesiones.userId = :userId " +
            "ORDER BY sesiones.fecha DESC")
    LiveData<List<Sesion>> obtenerSesionesPorEjercicio(String ejercicioId, String userId);

    // Obtener las series de una sesión concreta
    @Query("SELECT * FROM series_registro WHERE sesionId = :sesionId AND ejercicioId = :ejercicioId")
    LiveData<List<SerieRegistro>> obtenerSeriesDeSesion(int sesionId, String ejercicioId);

    // Obtener la última sesión de un ejercicio (para prerellenar el Bottom Sheet)
    @Query("SELECT series_registro.* FROM series_registro " +
            "INNER JOIN sesiones ON series_registro.sesionId = sesiones.id " +
            "WHERE series_registro.ejercicioId = :ejercicioId AND sesiones.userId = :userId " +
            "ORDER BY sesiones.fecha DESC LIMIT 10")
    List<SerieRegistro> obtenerUltimasSeries(String ejercicioId, String userId);

    // Historial: todas las sesiones del usuario ordenadas por fecha
    @Query("SELECT * FROM sesiones WHERE userId = :userId ORDER BY fecha DESC")
    LiveData<List<Sesion>> obtenerHistorial(String userId);

    // Series de una sesión completa (para expandir en el historial)
    @Query("SELECT * FROM series_registro WHERE sesionId = :sesionId")
    LiveData<List<SerieRegistro>> obtenerSeriesDeSesionCompleta(int sesionId);

    // Resumen semanal: sesiones de los últimos 7 días
    @Query("SELECT * FROM sesiones WHERE userId = :userId AND fecha >= :desdeTimestamp ORDER BY fecha DESC")
    LiveData<List<Sesion>> obtenerSesionesDesde(String userId, long desdeTimestamp);

    // Máximo kg por ejercicio en cada sesión (para la gráfica)
    @Query("SELECT sesiones.fecha, MAX(series_registro.kg) as kg FROM series_registro " +
            "INNER JOIN sesiones ON series_registro.sesionId = sesiones.id " +
            "WHERE series_registro.ejercicioId = :ejercicioId AND sesiones.userId = :userId " +
            "ORDER BY sesiones.fecha ASC")
    LiveData<List<PuntoGrafica>> obtenerEvolucionEjercicio(String ejercicioId, String userId);
}