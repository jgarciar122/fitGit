package com.example.fitgit.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.fitgit.model.PuntoGrafica;
import com.example.fitgit.model.SerieRegistro;
import com.example.fitgit.model.Sesion;
import com.example.fitgit.repository.RepositorioSesion;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Calendar;
import java.util.List;

public class SesionViewModel extends AndroidViewModel {
    private RepositorioSesion repositorio;
    private String userId;

    public SesionViewModel(@NonNull Application application) {
        super(application);
        repositorio = new RepositorioSesion(application);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public LiveData<List<Sesion>> obtenerHistorial() {
        return repositorio.obtenerHistorial(userId);
    }

    public LiveData<List<SerieRegistro>> obtenerSeriesDeSesion(int sesionId) {
        return repositorio.obtenerSeriesDeSesion(sesionId);
    }

    public LiveData<List<Sesion>> obtenerSesionesEstaSemana() {
        // Timestamp del lunes de esta semana a las 00:00
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return repositorio.obtenerSesionesDesde(userId, cal.getTimeInMillis());
    }

    public LiveData<List<PuntoGrafica>> obtenerEvolucionEjercicio(String ejercicioId) {
        return repositorio.obtenerEvolucionEjercicio(ejercicioId, userId);
    }
}