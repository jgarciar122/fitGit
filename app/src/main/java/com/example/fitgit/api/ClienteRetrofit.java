package com.example.fitgit.api;

import com.example.fitgit.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClienteRetrofit {
    private static final String BASE_URL = "https://exercisedb.p.rapidapi.com/";
    private static Retrofit retrofit = null;

    public static ServicioEjercicios getInstancia() {
        if (retrofit == null) {
            // añado las cabeceras de la api en el interceptor para no tenerlas en cada endpoint
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request request = chain.request().newBuilder()
                                .addHeader("X-RapidAPI-Key", BuildConfig.RAPIDAPI_KEY)
                                .addHeader("X-RapidAPI-Host", "exercisedb.p.rapidapi.com")
                                .build();
                        return chain.proceed(request);
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ServicioEjercicios.class);
    }
}
