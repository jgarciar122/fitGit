package com.example.fitgit.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.fitgit.R;
import com.example.fitgit.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Cargar el fragment de Ejercicios por defecto al abrir
        if (savedInstanceState == null) {
            reemplazarFragment(new EjerciciosFragment());
        }

        configurarNavegacion();

        binding.btnCerrarSesionTemp.setOnClickListener(v -> cerrarSesion());
    }

    private void configurarNavegacion() {
        binding.navegacionInferior.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_ejercicios) {
                reemplazarFragment(new EjerciciosFragment());
                return true;
            } else if (id == R.id.nav_rutinas) {
                reemplazarFragment(new RutinasFragment());
                return true;
            } else if (id == R.id.nav_progreso) {
                reemplazarFragment(new ProgresoFragment());
                return true;
            }
            return false;
        });
    }

    private void reemplazarFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
    }

    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
        GoogleSignIn.getClient(this, gso).signOut();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}