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

public class RepositorioSesion {
    private SesionDao dao;

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
}