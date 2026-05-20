package com.example.fitgit.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.fitgit.model.Ejercicio;
import com.example.fitgit.repository.RepositorioEjercicio;

import java.util.ArrayList;
import java.util.List;

public class EjercicioViewModel extends AndroidViewModel {

    private RepositorioEjercicio repositorio;
    private final MutableLiveData<String> filtroMusculo = new MutableLiveData<>("todos");
    private final MutableLiveData<String> busquedaTexto = new MutableLiveData<>("");
    private final MediatorLiveData<List<Ejercicio>> ejerciciosFiltrados = new MediatorLiveData<>();
    private final LiveData<List<Ejercicio>> dbSource;

    public EjercicioViewModel(@NonNull Application application) {
        super(application);
        repositorio = new RepositorioEjercicio(application);

        dbSource = Transformations.switchMap(filtroMusculo, musculo -> {
            if (musculo.equalsIgnoreCase("todos")) {
                return repositorio.obtenerEjercicios();
            } else {
                return repositorio.obtenerEjerciciosFiltrados(musculo);
            }
        });

        ejerciciosFiltrados.addSource(dbSource, lista -> aplicarFiltros(lista, busquedaTexto.getValue()));
        ejerciciosFiltrados.addSource(busquedaTexto, query -> aplicarFiltros(dbSource.getValue(), query));
    }

    private void aplicarFiltros(List<Ejercicio> lista, String query) {
        if (lista == null) return;

        if (query == null || query.trim().isEmpty()) {
            ejerciciosFiltrados.setValue(lista);
            return;
        }

        List<Ejercicio> filtrada = new ArrayList<>();
        String queryLower = query.toLowerCase();
        for (Ejercicio e : lista) {
            if (e.getNombre() != null && e.getNombre().toLowerCase().contains(queryLower)) {
                filtrada.add(e);
            }
        }
        ejerciciosFiltrados.setValue(filtrada);
    }

    public LiveData<List<Ejercicio>> getEjercicios() {
        return ejerciciosFiltrados;
    }

    public void filtrar(String musculo) {
        filtroMusculo.setValue(musculo);
    }

    public void buscarPorNombre(String query) {
        busquedaTexto.setValue(query);
    }
}