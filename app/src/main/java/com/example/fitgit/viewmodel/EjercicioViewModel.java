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
    }

    public LiveData<List<Ejercicio>> getEjercicios() {
        if (ejercicios == null) {
            ejercicios = repositorio.obtenerEjercicios();
        }
        return ejercicios;
    }
}