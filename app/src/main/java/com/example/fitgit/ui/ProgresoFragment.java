package com.example.fitgit.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fitgit.adapter.AdaptadorHistorial;
import com.example.fitgit.databinding.FragmentProgresoBinding;
import com.example.fitgit.viewmodel.SesionViewModel;

import java.util.Calendar;

public class ProgresoFragment extends Fragment {

    private FragmentProgresoBinding binding;
    private SesionViewModel viewModel;
    private AdaptadorHistorial adaptador;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProgresoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SesionViewModel.class);

        // Configurar RecyclerView del historial
        adaptador = new AdaptadorHistorial();
        binding.rvHistorial.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvHistorial.setAdapter(adaptador);

        // Observar historial agrupado
        viewModel.obtenerHistorialAgrupado().observe(getViewLifecycleOwner(), entrenamientos -> {
            if (entrenamientos == null || entrenamientos.isEmpty()) {
                binding.tvSinHistorial.setVisibility(View.VISIBLE);
                binding.rvHistorial.setVisibility(View.GONE);
            } else {
                binding.tvSinHistorial.setVisibility(View.GONE);
                binding.rvHistorial.setVisibility(View.VISIBLE);
                adaptador.setEntrenamientos(entrenamientos);
            }
        });

        // Observar resumen semanal
        viewModel.obtenerSesionesEstaSemana().observe(getViewLifecycleOwner(),
                sesiones -> actualizarResumenSemanal(sesiones));
    }

    private void actualizarResumenSemanal(java.util.List<com.example.fitgit.model.Sesion> sesiones) {
        android.widget.TextView[] circulos = {
                binding.circuloLunes, binding.circuloMartes, binding.circuloMiercoles,
                binding.circuloJueves, binding.circuloViernes, binding.circuloSabado,
                binding.circuloDomingo
        };

        // Resetear todos a ⬜
        for (android.widget.TextView c : circulos) {
            c.setText("⬜");
        }

        if (sesiones == null || sesiones.isEmpty()) {
            binding.tvResumenSemana.setText("0 días entrenados esta semana");
            return;
        }

        int diasEntrenados = 0;
        java.util.Set<Integer> diasMarcados = new java.util.HashSet<>();

        for (com.example.fitgit.model.Sesion sesion : sesiones) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(sesion.fecha);
            int diaSemana = cal.get(Calendar.DAY_OF_WEEK);
            int indice = (diaSemana == Calendar.SUNDAY) ? 6 : diaSemana - 2;

            if (indice >= 0 && indice <= 6 && !diasMarcados.contains(indice)) {
                diasMarcados.add(indice);
                diasEntrenados++;
                circulos[indice].setText("✅");
            }
        }

        binding.tvResumenSemana.setText(diasEntrenados + " día" +
                (diasEntrenados != 1 ? "s" : "") + " entrenado" +
                (diasEntrenados != 1 ? "s" : "") + " esta semana");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}