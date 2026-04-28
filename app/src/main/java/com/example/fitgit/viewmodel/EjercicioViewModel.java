package com.example.fitgit.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.fitgit.model.Ejercicio;
import com.example.fitgit.repositorio.RepositorioEjercicio;

import java.util.List;

public class EjercicioViewModel extends AndroidViewModel { // 1. Cambiado a AndroidViewModel
    private RepositorioEjercicio repositorio;
    private final MutableLiveData<String> filtroMusculo = new MutableLiveData<>("todos");
    private final LiveData<List<Ejercicio>> ejercicios;

    public EjercicioViewModel(@NonNull Application application) { // 2. Recibe Application
        super(application);
        repositorio = new RepositorioEjercicio(application);

        // 3. Lógica reactiva: cuando el filtro cambia, pedimos los datos al repo
        ejercicios = Transformations.switchMap(filtroMusculo, musculo -> {
            if (musculo.equalsIgnoreCase("todos")) {
                return repositorio.obtenerEjercicios();
            } else {
                return repositorio.obtenerEjerciciosFiltrados(musculo);
            }
        });
    }

    public LiveData<List<Ejercicio>> getEjercicios() {
        return ejercicios;
    }

    /**
     * Al cambiar el valor de 'filtroMusculo', el switchMap de arriba
     * detecta el cambio y actualiza la lista de ejercicios automáticamente.
     */
    public void filtrar(String musculo) {
        filtroMusculo.setValue(musculo);
    }
}