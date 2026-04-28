package com.example.fitgit.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClienteRetrofit {
    private static final String BASE_URL = "https://exercisedb.p.rapidapi.com/";
    private static Retrofit retrofit = null;

    public static ServicioEjercicios getInstancia() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ServicioEjercicios.class);
    }
}
