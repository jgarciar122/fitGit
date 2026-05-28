package com.example.fitgit.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.fitgit.model.Ejercicio;
import com.example.fitgit.model.Rutina;
import com.example.fitgit.model.RutinaConConteo;
import com.example.fitgit.model.RutinaEjercicioCrossRef;
import com.example.fitgit.repository.RepositorioRutina;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

public class RutinaViewModel extends AndroidViewModel {
    private RepositorioRutina repositorio;
    private String userId;

    public RutinaViewModel(@NonNull Application application) {
        super(application);
        repositorio = new RepositorioRutina(application);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public LiveData<List<RutinaConConteo>> obtenerRutinasConConteo() {
        return repositorio.obtenerRutinasConConteo(userId);
    }

    public LiveData<List<Ejercicio>> obtenerEjerciciosDeRutina(int rutinaId) {
        return repositorio.obtenerEjerciciosDeRutina(rutinaId);
    }

    public void insertarRutina(String nombre) {
        repositorio.insertarRutina(nombre, userId);
    }

    public void eliminarRutina(RutinaConConteo rutina) {
        Rutina r = new Rutina(rutina.nombre, "", userId);
        r.setId(rutina.id);
        repositorio.eliminarRutina(r);
    }

    public void eliminarEjercicioDeRutina(int rutinaId, Ejercicio ejercicio) {
        RutinaEjercicioCrossRef ref = new RutinaEjercicioCrossRef();
        ref.rutinaId = rutinaId;
        ref.ejercicioId = ejercicio.getId();
        repositorio.eliminarEjercicioDeRutina(ref, userId);
    }
}