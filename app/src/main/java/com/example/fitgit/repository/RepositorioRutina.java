package com.example.fitgit.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.fitgit.database.AppDatabase;
import com.example.fitgit.database.RutinaDao;
import com.example.fitgit.model.Rutina;
import com.example.fitgit.model.RutinaConConteo;
import com.example.fitgit.model.RutinaEjercicioCrossRef;
import com.example.fitgit.model.Ejercicio;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RepositorioRutina {
    private RutinaDao dao;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public RepositorioRutina(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.rutinaDao();
    }

    public LiveData<List<RutinaConConteo>> obtenerRutinasConConteo(String userId) {
        return dao.obtenerRutinasConConteo(userId);
    }

    public LiveData<List<Ejercicio>> obtenerEjerciciosDeRutina(int rutinaId) {
        return dao.obtenerEjerciciosDeRutina(rutinaId);
    }

    public void insertarRutina(String nombre, String userId) {
        Rutina nuevaRutina = new Rutina(nombre, "Mi rutina personalizada", userId);
        executor.execute(() -> dao.insertarRutina(nuevaRutina));
    }

    public void eliminarRutina(Rutina rutina) {
        executor.execute(() -> dao.eliminarRutina(rutina));
    }

    public void eliminarEjercicioDeRutina(RutinaEjercicioCrossRef ref) {
        executor.execute(() -> dao.eliminarEjercicioDeRutina(ref));
    }
}