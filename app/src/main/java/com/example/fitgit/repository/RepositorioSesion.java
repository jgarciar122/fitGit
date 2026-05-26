package com.example.fitgit.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.fitgit.database.AppDatabase;
import com.example.fitgit.database.SesionDao;
import com.example.fitgit.model.PuntoGrafica;
import com.example.fitgit.model.SerieRegistro;
import com.example.fitgit.model.Sesion;
import com.example.fitgit.model.SesionConDetalle;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RepositorioSesion {
    private SesionDao dao;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public RepositorioSesion(Application application) {
        dao = AppDatabase.getDatabase(application).sesionDao();
    }

    public LiveData<List<Sesion>> obtenerHistorial(String userId) {
        return dao.obtenerHistorial(userId);
    }

    public LiveData<List<SerieRegistro>> obtenerSeriesDeSesion(int sesionId) {
        return dao.obtenerSeriesDeSesionCompleta(sesionId);
    }

    public LiveData<List<Sesion>> obtenerSesionesDesde(String userId, long desdeTimestamp) {
        return dao.obtenerSesionesDesde(userId, desdeTimestamp);
    }

    public LiveData<List<PuntoGrafica>> obtenerEvolucionEjercicio(String ejercicioId, String userId) {
        return dao.obtenerEvolucionEjercicio(ejercicioId, userId);
    }

    public LiveData<List<SesionConDetalle>> obtenerHistorialCompleto(String userId) {
        return dao.obtenerHistorialCompleto(userId);
    }

    public void eliminarSesionCompleta(int sesionId) {
        executor.execute(() -> {
            dao.eliminarSeriesDeSesion(sesionId);
            dao.eliminarSesion(sesionId);
        });
    }

    public void eliminarEjercicioDeSesion(int sesionId, String ejercicioId) {
        executor.execute(() -> dao.eliminarEjercicioDeSesion(sesionId, ejercicioId));
    }
}