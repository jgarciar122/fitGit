package com.example.fitgit.api;

import com.example.fitgit.model.Ejercicio;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ServicioEjercicios {

    @GET("exercises?limit=20")
    Call<List<Ejercicio>> obtenerTodosLosEjercicios();

    @GET("exercises/bodyPart/{bodyPart}?limit=20")
    Call<List<Ejercicio>> obtenerEjerciciosPorMusculo(@Path("bodyPart") String musculo);
}
