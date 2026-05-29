package com.example.fitgit.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.example.fitgit.model.EjercicioConSeries;
import com.example.fitgit.model.EntrenamientoDia;
import com.example.fitgit.model.PuntoGrafica;
import com.example.fitgit.model.SerieRegistro;
import com.example.fitgit.model.Sesion;
import com.example.fitgit.model.SesionConDetalle;
import com.example.fitgit.repository.RepositorioSesion;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

public class SesionViewModel extends AndroidViewModel {
    private RepositorioSesion repositorio;
    private String userId;

    public SesionViewModel(@NonNull Application application) {
        super(application);
        repositorio = new RepositorioSesion(application);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }

    public LiveData<List<Sesion>> obtenerHistorial() {
        return repositorio.obtenerHistorial(userId);
    }

    public LiveData<List<SerieRegistro>> obtenerSeriesDeSesion(int sesionId) {
        return repositorio.obtenerSeriesDeSesion(sesionId);
    }

    public LiveData<List<Sesion>> obtenerSesionesEstaSemana() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return repositorio.obtenerSesionesDesde(userId, cal.getTimeInMillis());
    }

    public LiveData<List<Sesion>> obtenerSesionesSemana(long inicio, long fin) {
        return repositorio.obtenerSesionesSemana(userId, inicio, fin);
    }

    public LiveData<List<PuntoGrafica>> obtenerEvolucionEjercicio(String ejercicioId) {
        return repositorio.obtenerEvolucionEjercicio(ejercicioId, userId);
    }

    public LiveData<List<EntrenamientoDia>> obtenerHistorialAgrupado() {
        return Transformations.map(
                repositorio.obtenerHistorialCompleto(userId),
                this::agruparPorSesion
        );
    }

    public void eliminarSesionCompleta(int sesionId) {
        repositorio.eliminarSesionCompleta(sesionId, userId);
    }

    public void eliminarEjercicioDeSesion(int sesionId, String ejercicioId) {
        repositorio.eliminarEjercicioDeSesion(sesionId, ejercicioId, userId);
    }

    private List<EntrenamientoDia> agruparPorSesion(List<SesionConDetalle> filas) {
        LinkedHashMap<Integer, EntrenamientoDia> mapa = new LinkedHashMap<>();

        if (filas == null) return new ArrayList<>();

        for (SesionConDetalle fila : filas) {
            if (!mapa.containsKey(fila.sesionId)) {
                mapa.put(fila.sesionId, new EntrenamientoDia(
                        fila.fecha, fila.nombreRutina, new ArrayList<>()
                ));
            }

            EntrenamientoDia dia = mapa.get(fila.sesionId);

            EjercicioConSeries ejercicioExistente = null;
            for (EjercicioConSeries e : dia.ejercicios) {
                if (e.ejercicioId.equals(fila.ejercicioId)) {
                    ejercicioExistente = e;
                    break;
                }
            }

            if (ejercicioExistente == null) {
                ejercicioExistente = new EjercicioConSeries(
                        fila.ejercicioId, fila.nombreEjercicio, new ArrayList<>()
                );
                dia.ejercicios.add(ejercicioExistente);
            }

            ejercicioExistente.series.add(
                    new SerieRegistro(fila.sesionId, fila.ejercicioId, fila.kg, fila.repeticiones)
            );
        }

        return new ArrayList<>(mapa.values());
    }
}