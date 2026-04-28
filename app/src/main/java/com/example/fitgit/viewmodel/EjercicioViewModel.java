package com.example.fitgit.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.fitgit.model.Ejercicio;
import com.example.fitgit.repositorio.RepositorioEjercicio;

import java.util.List;

public class EjercicioViewModel extends ViewModel {
    private RepositorioEjercicio repositorio;
    private LiveData<List<Ejercicio>> ejercicios;

    public EjercicioViewModel() {
        repositorio = new RepositorioEjercicio();
        // Obtenemos la referencia al LiveData del repositorio desde el inicio
        ejercicios = repositorio.obtenerEjercicios();
    }

    public LiveData<List<Ejercicio>> getEjercicios() {
        return ejercicios;
    }

    /**
     * Método para filtrar los ejercicios por grupo muscular.
     * Este método es llamado por el Spinner desde la MainActivity.
     */
    public void filtrar(String musculo) {
        repositorio.filtrarPorMusculo(musculo);
    }
}