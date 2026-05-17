package com.example.fitgit.repository;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;
import com.example.fitgit.api.ClienteRetrofit;
import com.example.fitgit.api.ServicioEjercicios;
import com.example.fitgit.database.AppDatabase;
import com.example.fitgit.database.EjercicioDao;
import com.example.fitgit.model.Ejercicio;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepositorioEjercicio {
    private ServicioEjercicios servicio;
    private EjercicioDao dao;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public RepositorioEjercicio(Application application) {
        servicio = ClienteRetrofit.getInstancia();
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.ejercicioDao();
    }

    public LiveData<List<Ejercicio>> obtenerEjercicios() {
        LiveData<List<Ejercicio>> datosLocales = dao.obtenerTodosLosEjercicios();

        executor.execute(() -> {
            if (dao.obtenerUnoSincrono() == null) {
                Log.d("REPO", "Base de datos vacía. Pidiendo datos a la API...");
                refrescarEjercicios();
            } else {
                Log.d("REPO", "Datos detectados en Room. Ahorrando llamada a la API.");
            }
        });

        return datosLocales;
    }

    private void refrescarEjercicios() {
        servicio.obtenerTodosLosEjercicios().enqueue(new Callback<List<Ejercicio>>() {
            @Override
            public void onResponse(Call<List<Ejercicio>> call, Response<List<Ejercicio>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> {
                        dao.borrarTodo();
                        dao.insertarEjercicios(response.body());
                    });
                }
            }
            @Override
            public void onFailure(Call<List<Ejercicio>> call, Throwable t) {
                Log.e("REPO", "Fallo de red al refrescar: " + t.getMessage());
            }
        });
    }

    public LiveData<List<Ejercicio>> obtenerEjerciciosFiltrados(String musculo) {
        if (musculo.equalsIgnoreCase("todos")) {
            return obtenerEjercicios();
        } else {
            executor.execute(() -> {
                if (dao.obtenerUnoSincrono() == null) {
                    refrescarEjerciciosPorMusculo(musculo);
                }
            });
            return dao.obtenerEjerciciosPorMusculo(musculo);
        }
    }

    private void refrescarEjerciciosPorMusculo(String musculo) {
        servicio.obtenerEjerciciosPorMusculo(musculo.toLowerCase()).enqueue(new Callback<List<Ejercicio>>() {
            @Override
            public void onResponse(Call<List<Ejercicio>> call, Response<List<Ejercicio>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> {
                        dao.insertarEjercicios(response.body());
                    });
                }
            }
            @Override
            public void onFailure(Call<List<Ejercicio>> call, Throwable t) {}
        });
    }
}