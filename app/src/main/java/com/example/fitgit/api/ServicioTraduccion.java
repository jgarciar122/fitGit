package com.example.fitgit.api;

import android.util.Log;

import com.example.fitgit.BuildConfig;
import com.example.fitgit.model.Ejercicio;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServicioTraduccion {

    private static final String API_KEY = BuildConfig.GOOGLE_TRANSLATE_KEY;
    private static ServicioTraduccion instancia;
    private OkHttpClient client = new OkHttpClient();
    private Executor executor = Executors.newSingleThreadExecutor();

    public interface TraduccionCallback {
        void onExito(String nombreEs, List<String> instruccionesEs);
        void onError(Exception e);
    }

    private ServicioTraduccion() {}

    public static ServicioTraduccion getInstance() {
        if (instancia == null) instancia = new ServicioTraduccion();
        return instancia;
    }

    public void traducirEjercicio(Ejercicio ejercicio, TraduccionCallback callback) {
        executor.execute(() -> {
            try {
                JSONObject body = new JSONObject();
                JSONArray textos = new JSONArray();
                textos.put(ejercicio.getNombre());
                if (ejercicio.getInstrucciones() != null) {
                    for (String instruccion : ejercicio.getInstrucciones()) {
                        textos.put(instruccion);
                    }
                }
                body.put("q", textos);
                body.put("target", "es");
                body.put("source", "en");
                body.put("format", "text");

                RequestBody requestBody = RequestBody.create(
                        MediaType.parse("application/json"),
                        body.toString()
                );

                Request request = new Request.Builder()
                        .url("https://translation.googleapis.com/language/translate/v2?key=" + API_KEY)
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                String responseStr = response.body().string();

                JSONObject jsonResponse = new JSONObject(responseStr);
                JSONArray translations = jsonResponse
                        .getJSONObject("data")
                        .getJSONArray("translations");

                String nombreEs = translations.getJSONObject(0).getString("translatedText");

                List<String> instruccionesEs = new ArrayList<>();
                for (int i = 1; i < translations.length(); i++) {
                    instruccionesEs.add(translations.getJSONObject(i).getString("translatedText"));
                }

                callback.onExito(nombreEs, instruccionesEs);

            } catch (Exception e) {
                Log.e("TRADUCCION", "Error: " + e.getMessage());
                callback.onError(e);
            }
        });
    }
}