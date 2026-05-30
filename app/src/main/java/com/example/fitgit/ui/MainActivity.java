package com.example.fitgit.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import androidx.navigation.NavOptions;

import com.example.fitgit.R;
import com.example.fitgit.database.AppDatabase;
import com.example.fitgit.databinding.ActivityMainBinding;
import com.example.fitgit.model.Rutina;
import com.example.fitgit.model.RutinaEjercicioCrossRef;
import com.example.fitgit.model.Sesion;
import com.example.fitgit.model.SerieRegistro;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarMain);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        // setupWithNavController registra el onDestinationChangedListener que actualiza
        // la selección visual del bottom nav — lo necesitamos
        NavigationUI.setupWithNavController(binding.navegacionInferior, navController);

        // Reemplazamos el onItemSelectedListener para garantizar que siempre se hace
        // popUpTo al inicio antes de navegar, eliminando Perfil u otros destinos que
        // pudieran estar apilados fuera del bottom nav
        binding.navegacionInferior.setOnItemSelectedListener(item -> {
            navController.navigate(item.getItemId(), null, new NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setPopUpTo(navController.getGraph().getStartDestinationId(), false)
                    .build());
            return true;
        });

        binding.navegacionInferior.setOnItemReselectedListener(item -> {
            if (navController.getCurrentDestination() != null
                    && navController.getCurrentDestination().getId() != item.getItemId()) {
                // Estamos en un destino fuera del bottom nav (ej: Perfil)
                navController.navigate(item.getItemId(), null, new NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(navController.getGraph().getStartDestinationId(), false)
                        .build());
            } else {
                // Reselección real: volvemos a la raíz del tab actual
                navController.popBackStack(item.getItemId(), false);
            }
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            binding.tvToolbarTitulo.setText(destination.getLabel());
        });

        binding.btnPerfil.setOnClickListener(v ->
                navController.navigate(R.id.nav_perfil, null, new NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build())
        );

        sincronizarDesdeFirestore();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    private void sincronizarDesdeFirestore() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario == null) return;

        String userId = usuario.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        AppDatabase roomDb = AppDatabase.getDatabase(this);

        db.collection("usuarios").document(userId)
                .collection("rutinas")
                .get()
                .addOnSuccessListener(rutinasSnapshot -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        for (com.google.firebase.firestore.DocumentSnapshot rutinaDoc : rutinasSnapshot.getDocuments()) {
                            try {
                                int rutinaId = Integer.parseInt(rutinaDoc.getId());
                                String nombre = rutinaDoc.getString("nombre");
                                String descripcion = rutinaDoc.getString("descripcion");
                                Long fecha = rutinaDoc.getLong("fechaCreacion");

                                if (roomDb.rutinaDao().obtenerRutinaPorId(rutinaId) == null) {
                                    Rutina rutina = new Rutina(nombre, descripcion, userId);
                                    rutina.setId(rutinaId);
                                    rutina.setFechaCreacion(fecha != null ? fecha : 0);
                                    roomDb.rutinaDao().insertarRutinaConId(rutina);
                                }

                                rutinaDoc.getReference().collection("ejercicios")
                                        .get()
                                        .addOnSuccessListener(ejerciciosSnapshot -> {
                                            Executors.newSingleThreadExecutor().execute(() -> {
                                                for (com.google.firebase.firestore.DocumentSnapshot ejDoc : ejerciciosSnapshot.getDocuments()) {
                                                    RutinaEjercicioCrossRef ref = new RutinaEjercicioCrossRef();
                                                    ref.rutinaId = rutinaId;
                                                    ref.ejercicioId = ejDoc.getId();
                                                    roomDb.rutinaDao().añadirEjercicioARutina(ref);
                                                }
                                            });
                                        });
                            } catch (Exception e) {
                                Log.e("SYNC", "Error sincronizando rutina: " + e.getMessage());
                            }
                        }
                    });
                });

        db.collection("usuarios").document(userId)
                .collection("sesiones")
                .get()
                .addOnSuccessListener(sesionesSnapshot -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        for (com.google.firebase.firestore.DocumentSnapshot sesionDoc : sesionesSnapshot.getDocuments()) {
                            try {
                                int sesionId = Integer.parseInt(sesionDoc.getId());
                                Long rutinaId = sesionDoc.getLong("rutinaId");
                                Long fecha = sesionDoc.getLong("fecha");

                                if (roomDb.sesionDao().obtenerSesionPorId(sesionId) == null) {
                                    Sesion sesion = new Sesion(
                                            rutinaId != null ? rutinaId.intValue() : 0,
                                            userId
                                    );
                                    sesion.id = sesionId;
                                    sesion.fecha = fecha != null ? fecha : 0;
                                    roomDb.sesionDao().insertarSesionConId(sesion);
                                }

                                sesionDoc.getReference().collection("series")
                                        .get()
                                        .addOnSuccessListener(seriesSnapshot -> {
                                            Executors.newSingleThreadExecutor().execute(() -> {
                                                for (com.google.firebase.firestore.DocumentSnapshot serieDoc : seriesSnapshot.getDocuments()) {
                                                    try {
                                                        int serieId = Integer.parseInt(serieDoc.getId());
                                                        String ejercicioId = serieDoc.getString("ejercicioId");
                                                        Double kg = serieDoc.getDouble("kg");
                                                        Long reps = serieDoc.getLong("repeticiones");

                                                        if (roomDb.sesionDao().obtenerSeriePorId(serieId) == null) {
                                                            SerieRegistro serie = new SerieRegistro(
                                                                    sesionId,
                                                                    ejercicioId,
                                                                    kg != null ? kg.floatValue() : 0,
                                                                    reps != null ? reps.intValue() : 0
                                                            );
                                                            serie.id = serieId;
                                                            roomDb.sesionDao().insertarSerieConId(serie);
                                                        }
                                                    } catch (Exception e) {
                                                        Log.e("SYNC", "Error sincronizando serie: " + e.getMessage());
                                                    }
                                                }
                                            });
                                        });
                            } catch (Exception e) {
                                Log.e("SYNC", "Error sincronizando sesión: " + e.getMessage());
                            }
                        }
                    });
                });
    }
}
