package com.example.fitgit;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fitgit.adapter.AdaptadorEjercicios;
import com.example.fitgit.model.Ejercicio;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navegacionInferior;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets barrasSistema = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(barrasSistema.left, barrasSistema.top, barrasSistema.right, barrasSistema.bottom);
            return insets;
        });

        // Toolbar
        toolbar = findViewById(R.id.toolbar_principal);
        setSupportActionBar(toolbar);

        // Navegación Inferior
        navegacionInferior = findViewById(R.id.navegacion_inferior);

        // Escuchador
        navegacionInferior.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_ejercicios) {
                toolbar.setTitle("Buscador de Ejercicios");
                return true;
            } else if (id == R.id.nav_rutinas) {
                toolbar.setTitle("Mis Rutinas");
                return true;
            } else if (id == R.id.nav_progreso) {
                toolbar.setTitle("Mi Progreso");
                return true;
            }
            return false;
        });


        RecyclerView rvEjercicios = findViewById(R.id.rvEjercicios);
        rvEjercicios.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

    //Lista de prueba
        List<Ejercicio> listaPrueba = new ArrayList<>();

        listaPrueba.add(new Ejercicio("1", "Sentadilla con Barra", "Piernas", "Cuádriceps", "Barra", "",
                Arrays.asList(
                        "Coloca la barra sobre tus hombros (trapecios).",
                        "Mantén los pies a la anchura de los hombros y la espalda recta.",
                        "Baja la cadera como si te sentaras en una silla, manteniendo el peso en los talones.",
                        "Sube de forma controlada hasta la posición inicial."
                )));

        listaPrueba.add(new Ejercicio("2", "Press de Banca", "Pecho", "Pectorales", "Mancuernas", "",
                Arrays.asList(
                        "Túmbate en el banco con los pies apoyados en el suelo.",
                        "Sujeta las mancuernas sobre el pecho con los brazos extendidos.",
                        "Baja las pesas lentamente hacia los lados del pecho.",
                        "Empuja de nuevo hacia arriba sin bloquear los codos."
                )));

        listaPrueba.add(new Ejercicio("3", "Dominadas", "Espalda", "Dorsales", "Peso corporal", "",
                Arrays.asList(
                        "Cuélgate de la barra con las palmas hacia fuera.",
                        "Tira de tu cuerpo hacia arriba llevando el pecho hacia la barra.",
                        "Sube hasta que la barbilla supere la barra.",
                        "Baja lentamente de forma controlada."
                )));

        listaPrueba.add(new Ejercicio("4", "Curl de Bíceps", "Brazos", "Bíceps", "Mancuernas", "",
                Arrays.asList(
                        "Ponte de pie con una mancuerna en cada mano.",
                        "Mantén los codos pegados al cuerpo.",
                        "Flexiona los brazos llevando las pesas hacia los hombros.",
                        "Baja lentamente hasta estirar los brazos por completo."
                )));
        //Adaptador
        AdaptadorEjercicios adaptador = new AdaptadorEjercicios(listaPrueba);
        rvEjercicios.setAdapter(adaptador);
    }
}