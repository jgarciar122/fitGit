package com.example.fitgit.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fitgit.adapter.AdaptadorEntrenamiento;
import com.example.fitgit.adapter.AdaptadorSerie;
import com.example.fitgit.databinding.ActivityEntrenamientoBinding;
import com.example.fitgit.model.SerieRegistro;
import com.example.fitgit.model.Sesion;
import com.example.fitgit.repository.RepositorioSesion;
import com.example.fitgit.viewmodel.RutinaViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class EntrenamientoActivity extends AppCompatActivity {

    private ActivityEntrenamientoBinding binding;
    private AdaptadorEntrenamiento adaptador;
    private RutinaViewModel viewModel;
    private int rutinaId;
    private String nombreRutina;
    private String userId;

    // Cronómetro
    private Handler handler = new Handler(Looper.getMainLooper());
    private int segundos = 0;
    private boolean cronometroActivo = false;

    private Runnable cronometroRunnable = new Runnable() {
        @Override
        public void run() {
            segundos++;
            int min = segundos / 60;
            int seg = segundos % 60;
            binding.tvCronometro.setText(String.format("%02d:%02d", min, seg));
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEntrenamientoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        rutinaId = getIntent().getIntExtra("rutina_id", -1);
        nombreRutina = getIntent().getStringExtra("rutina_nombre");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Toolbar
        setSupportActionBar(binding.toolbarEntrenamiento);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        binding.toolbarEntrenamiento.setNavigationOnClickListener(v -> onBackPressed());
        binding.tvNombreRutinaEntrenamiento.setText(nombreRutina);

        // RecyclerView
        adaptador = new AdaptadorEntrenamiento();
        binding.rvEjerciciosEntrenamiento.setLayoutManager(new LinearLayoutManager(this));
        binding.rvEjerciciosEntrenamiento.setAdapter(adaptador);

        // Cargar ejercicios de la rutina
        viewModel = new ViewModelProvider(this).get(RutinaViewModel.class);
        viewModel.obtenerEjerciciosDeRutina(rutinaId).observe(this, ejercicios -> {
            if (ejercicios != null && !ejercicios.isEmpty()) {
                adaptador.setEjercicios(ejercicios);
                // Arrancar cronómetro al cargar
                if (!cronometroActivo) {
                    cronometroActivo = true;
                    handler.post(cronometroRunnable);
                }
            }
        });

        binding.btnFinalizarEntrenamiento.setOnClickListener(v -> mostrarDialogoFinalizar());
    }

    private void mostrarDialogoFinalizar() {
        new MaterialAlertDialogBuilder(this, com.example.fitgit.R.style.DialogRedondeado)
                .setTitle("Finalizar entrenamiento")
                .setMessage("¿Seguro que quieres finalizar? Se guardarán todas las series registradas.")
                .setPositiveButton("Finalizar", (dialog, which) -> guardarEntrenamiento())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void guardarEntrenamiento() {
        Map<String, List<AdaptadorSerie.FilaSerie>> seriesPorEjercicio =
                adaptador.getSeriesPorEjercicio();

        if (seriesPorEjercicio.isEmpty()) {
            Toast.makeText(this, "No hay series que guardar", Toast.LENGTH_SHORT).show();
            return;
        }

        RepositorioSesion repositorio = new RepositorioSesion(getApplication());

        Executors.newSingleThreadExecutor().execute(() -> {
            // Crear sesión
            Sesion sesion = new Sesion(rutinaId, userId);
            long sesionId = repositorio.insertarSesionSincrono(sesion);

            // Crear series para cada ejercicio
            List<SerieRegistro> todasLasSeries = new ArrayList<>();
            for (Map.Entry<String, List<AdaptadorSerie.FilaSerie>> entry : seriesPorEjercicio.entrySet()) {
                String ejercicioId = entry.getKey();
                for (AdaptadorSerie.FilaSerie fila : entry.getValue()) {
                    if (fila.kg > 0 || fila.reps > 0) {
                        todasLasSeries.add(new SerieRegistro(
                                (int) sesionId, ejercicioId, fila.kg, fila.reps
                        ));
                    }
                }
            }

            if (!todasLasSeries.isEmpty()) {
                repositorio.insertarSeries(todasLasSeries, userId);
            }

            runOnUiThread(() -> {
                int duracion = segundos / 60;
                Toast.makeText(this,
                        "¡Entrenamiento guardado! Duración: " + duracion + " min",
                        Toast.LENGTH_LONG).show();
                finish();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(cronometroRunnable);
    }

    @Override
    public void onBackPressed() {
        new MaterialAlertDialogBuilder(this, com.example.fitgit.R.style.DialogRedondeado)
                .setTitle("¿Salir del entrenamiento?")
                .setMessage("Si sales ahora perderás las series no guardadas.")
                .setPositiveButton("Salir", (dialog, which) -> {
                    handler.removeCallbacks(cronometroRunnable);
                    super.onBackPressed();
                })
                .setNegativeButton("Continuar", null)
                .show();
    }
}