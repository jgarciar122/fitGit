package com.example.fitgit.api;

import androidx.lifecycle.MutableLiveData;

import com.example.fitgit.model.Ejercicio;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepositorioEjercicio {
    private ServicioEjercicios servicio;
    private MutableLiveData<List<Ejercicio>> datosEjercicios;

    public RepositorioEjercicio() {
        servicio = ClienteRetrofit.getInstancia();
        datosEjercicios = new MutableLiveData<>();
    }

    public MutableLiveData<List<Ejercicio>> obtenerEjercicios() {
        servicio.obtenerTodosLosEjercicios().enqueue(new Callback<List<Ejercicio>>() {
            @Override
            public void onResponse(Call<List<Ejercicio>> call, Response<List<Ejercicio>> response) {
                if (response.isSuccessful()) {
                    // Aquí es donde en el futuro guardaremos en Room antes de mostrar
                    datosEjercicios.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Ejercicio>> call, Throwable t) {
                // Si falla la red, aquí cargaríamos desde Room (Offline)
                datosEjercicios.setValue(null);
            }
        });
        return datosEjercicios;
    }
}