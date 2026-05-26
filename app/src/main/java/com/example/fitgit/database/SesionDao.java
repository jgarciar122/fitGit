package com.example.fitgit.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.fitgit.model.PuntoGrafica;
import com.example.fitgit.model.SerieRegistro;
import com.example.fitgit.model.Sesion;
import com.example.fitgit.model.SesionConDetalle;

import java.util.List;

@Dao
public interface SesionDao {

    @Insert
    long insertarSesion(Sesion sesion);

    @Insert
    void insertarSeries(List<SerieRegistro> series);

    @Query("SELECT * FROM sesiones " +
            "INNER JOIN series_registro ON sesiones.id = series_registro.sesionId " +
            "WHERE series_registro.ejercicioId = :ejercicioId AND sesiones.userId = :userId " +
            "ORDER BY sesiones.fecha DESC")
    LiveData<List<Sesion>> obtenerSesionesPorEjercicio(String ejercicioId, String userId);

    @Query("SELECT * FROM series_registro WHERE sesionId = :sesionId AND ejercicioId = :ejercicioId")
    LiveData<List<SerieRegistro>> obtenerSeriesDeSesion(int sesionId, String ejercicioId);

    @Query("SELECT series_registro.* FROM series_registro " +
            "INNER JOIN sesiones ON series_registro.sesionId = sesiones.id " +
            "WHERE series_registro.ejercicioId = :ejercicioId AND sesiones.userId = :userId " +
            "ORDER BY sesiones.fecha DESC LIMIT 10")
    List<SerieRegistro> obtenerUltimasSeries(String ejercicioId, String userId);

    @Query("SELECT * FROM sesiones WHERE userId = :userId ORDER BY fecha DESC")
    LiveData<List<Sesion>> obtenerHistorial(String userId);

    @Query("SELECT * FROM series_registro WHERE sesionId = :sesionId")
    LiveData<List<SerieRegistro>> obtenerSeriesDeSesionCompleta(int sesionId);

    @Query("SELECT * FROM sesiones WHERE userId = :userId AND fecha >= :desdeTimestamp ORDER BY fecha DESC")
    LiveData<List<Sesion>> obtenerSesionesDesde(String userId, long desdeTimestamp);

    @Query("SELECT sesiones.fecha, MAX(series_registro.kg) as kg FROM series_registro " +
            "INNER JOIN sesiones ON series_registro.sesionId = sesiones.id " +
            "WHERE series_registro.ejercicioId = :ejercicioId AND sesiones.userId = :userId " +
            "GROUP BY sesiones.id " +
            "ORDER BY sesiones.fecha ASC")
    LiveData<List<PuntoGrafica>> obtenerEvolucionEjercicio(String ejercicioId, String userId);

    @Query("SELECT sesiones.id as sesionId, sesiones.fecha, sesiones.rutinaId, " +
            "rutinas.nombre as nombreRutina, " +
            "series_registro.ejercicioId, tabla_ejercicios.nombre as nombreEjercicio, " +
            "series_registro.kg, series_registro.repeticiones " +
            "FROM sesiones " +
            "INNER JOIN rutinas ON sesiones.rutinaId = rutinas.id " +
            "INNER JOIN series_registro ON sesiones.id = series_registro.sesionId " +
            "INNER JOIN tabla_ejercicios ON series_registro.ejercicioId = tabla_ejercicios.id " +
            "WHERE sesiones.userId = :userId " +
            "ORDER BY sesiones.fecha DESC")
    LiveData<List<SesionConDetalle>> obtenerHistorialCompleto(String userId);

    @Query("SELECT * FROM sesiones WHERE rutinaId = :rutinaId AND userId = :userId " +
            "AND fecha >= :inicioDia AND fecha <= :finDia LIMIT 1")
    Sesion obtenerSesionDeHoy(int rutinaId, String userId, long inicioDia, long finDia);

    @Query("DELETE FROM series_registro WHERE sesionId = :sesionId")
    void eliminarSeriesDeSesion(int sesionId);

    @Query("DELETE FROM sesiones WHERE id = :sesionId")
    void eliminarSesion(int sesionId);

    @Query("DELETE FROM series_registro WHERE sesionId = :sesionId AND ejercicioId = :ejercicioId")
    void eliminarEjercicioDeSesion(int sesionId, String ejercicioId);
}