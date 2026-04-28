package com.example.fitgit.api;

import com.example.fitgit.model.Ejercicio;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface ServicioEjercicios {

    // Los headers necesarios para ExerciseDB en RapidAPI [cite: 42]
    @Headers({
            "X-RapidAPI-Key: TU_API_KEY_AQUÍ",
            "X-RapidAPI-Host: exercisedb.p.rapidapi.com"
    })
    @GET("exercises?limit=20") // Traer 20 ejercicios generales
    Call<List<Ejercicio>> obtenerTodosLosEjercicios();

    @Headers({
            "X-RapidAPI-Key: TU_API_KEY_AQUÍ",
            "X-RapidAPI-Host: exercisedb.p.rapidapi.com"
    })
    @GET("exercises/bodyPart/{parte}") // Filtrar por parte del cuerpo
    Call<List<Ejercicio>> obtenerEjerciciosPorParte(@Path("parte") String parte);
}