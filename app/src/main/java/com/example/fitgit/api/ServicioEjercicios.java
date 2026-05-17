package com.example.fitgit.api;

import com.example.fitgit.model.Ejercicio;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface ServicioEjercicios {


    @Headers({
            "X-RapidAPI-Key: 84a7879aafmshd2ebff39f76e114p1e4397jsn321495fa09a5",
            "X-RapidAPI-Host: exercisedb.p.rapidapi.com"
    })
    @GET("exercises?limit=20")
    Call<List<Ejercicio>> obtenerTodosLosEjercicios();

    @Headers({
            "X-RapidAPI-Key: 84a7879aafmshd2ebff39f76e114p1e4397jsn321495fa09a5",
            "X-RapidAPI-Host: exercisedb.p.rapidapi.com"
    })
    @GET("exercises/bodyPart/{bodyPart}?limit=20")
    Call<List<Ejercicio>> obtenerEjerciciosPorMusculo(@Path("bodyPart") String musculo);
}