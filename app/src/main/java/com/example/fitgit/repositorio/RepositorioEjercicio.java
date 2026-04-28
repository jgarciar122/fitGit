package com.example.fitgit.repositorio;

import androidx.lifecycle.MutableLiveData;

import com.example.fitgit.api.ClienteRetrofit;
import com.example.fitgit.api.ServicioEjercicios;
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
                if (response.isSuccessful() && response.body() != null) {
                    // --- AÑADE ESTO PARA VER EL TEXTO REAL ---
                    retrofit2.Response<List<Ejercicio>> rawResponse = response;
                    android.util.Log.d("API_RAW_JSON", "Cuerpo completo: " + new com.google.gson.Gson().toJson(response.body().get(0)));
                    // -----------------------------------------

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

    public void filtrarPorMusculo(String musculo) {
        // Si el usuario elige "Todos", cargamos la lista normal
        if (musculo.equalsIgnoreCase("todos")) {
            obtenerEjercicios();
            return;
        }

        servicio.obtenerEjerciciosPorMusculo(musculo.toLowerCase()).enqueue(new Callback<List<Ejercicio>>() {
            @Override
            public void onResponse(Call<List<Ejercicio>> call, Response<List<Ejercicio>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    datosEjercicios.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Ejercicio>> call, Throwable t) {
                // Manejar error
            }
        });
    }
}