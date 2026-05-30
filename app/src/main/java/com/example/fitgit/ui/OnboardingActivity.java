package com.example.fitgit.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.example.fitgit.R;
import com.google.android.material.button.MaterialButton;

public class OnboardingActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "fitgit_prefs";
    private static final String KEY_ONBOARDING_DONE = "onboarding_done";

    private ViewPager2 viewPager;
    private MaterialButton btnSiguiente;
    private MaterialButton btnOmitir;
    private LinearLayout layoutIndicadores;
    private ImageView[] puntos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        btnSiguiente = findViewById(R.id.btnSiguiente);
        btnOmitir = findViewById(R.id.btnOmitir);
        layoutIndicadores = findViewById(R.id.layoutIndicadores);

        if (yaVisto(this)) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        viewPager.setAdapter(new OnboardingAdapter());

        configurarIndicadores(3);
        actualizarIndicador(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                actualizarIndicador(position);
                if (position == 2) {
                    btnSiguiente.setText("Empezar");
                    btnOmitir.setVisibility(View.INVISIBLE);
                } else {
                    btnSiguiente.setText("Siguiente");
                    btnOmitir.setVisibility(View.VISIBLE);
                }
            }
        });

        btnSiguiente.setOnClickListener(v -> {
            int siguiente = viewPager.getCurrentItem() + 1;
            if (siguiente < 3) {
                viewPager.setCurrentItem(siguiente);
            } else {
                finalizarOnboarding();
            }
        });

        btnOmitir.setOnClickListener(v -> finalizarOnboarding());
    }

    private void configurarIndicadores(int cantidad) {
        puntos = new ImageView[cantidad];
        int tamano = (int) (10 * getResources().getDisplayMetrics().density);
        int margen = (int) (6 * getResources().getDisplayMetrics().density);

        for (int i = 0; i < cantidad; i++) {
            puntos[i] = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(tamano, tamano);
            params.setMargins(margen, 0, margen, 0);
            puntos[i].setLayoutParams(params);
            puntos[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_circle_light_blue));
            puntos[i].setAlpha(0.3f);
            layoutIndicadores.addView(puntos[i]);
        }
    }

    private void actualizarIndicador(int posicion) {
        for (int i = 0; i < puntos.length; i++) {
            puntos[i].setAlpha(i == posicion ? 1f : 0.3f);
        }
    }

    private void finalizarOnboarding() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_ONBOARDING_DONE, true).apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public static boolean yaVisto(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_ONBOARDING_DONE, false);
    }
}
