package com.example.fitgit.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fitgit.adapter.AdaptadorEjercicios;
import com.example.fitgit.database.AppDatabase;
import com.example.fitgit.databinding.FragmentDetalleRutinaBinding;
import com.example.fitgit.model.Ejercicio;
import com.example.fitgit.model.RutinaEjercicioCrossRef;

import java.util.concurrent.Executors;

public class DetalleRutinaFragment extends Fragment {
    private int rutinaId;
    private String nombreRutina;
    private FragmentDetalleRutinaBinding binding;

    public static DetalleRutinaFragment newInstance(int id, String nombre) {
        DetalleRutinaFragment fragment = new DetalleRutinaFragment();
        Bundle args = new Bundle();
        args.putInt("rutina_id", id);
        args.putString("rutina_nombre", nombre);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetalleRutinaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            rutinaId = getArguments().getInt("rutina_id");
            nombreRutina = getArguments().getString("rutina_nombre");
        }

        AppDatabase db = AppDatabase.getDatabase(requireContext());

        AdaptadorEjercicios adaptador = new AdaptadorEjercicios();
        adaptador.setEsModoQuitar(true);

        binding.rvEjerciciosDetalle.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvEjerciciosDetalle.setAdapter(adaptador);
        binding.tvTituloDetalle.setText("RUTINA: " + nombreRutina);

        adaptador.setOnEjercicioClickListener(ejercicio -> {
            eliminarEjercicioDeEstaRutina(db, ejercicio);
        });

        db.rutinaDao().obtenerEjerciciosDeRutina(rutinaId).observe(getViewLifecycleOwner(), ejercicios -> {
            if (ejercicios != null) {
                adaptador.setEjercicios(ejercicios);
            }
        });
    }

    private void eliminarEjercicioDeEstaRutina(AppDatabase db, Ejercicio ejercicio) {
        Executors.newSingleThreadExecutor().execute(() -> {

            RutinaEjercicioCrossRef ref = new RutinaEjercicioCrossRef();
            ref.rutinaId = this.rutinaId;
            ref.ejercicioId = ejercicio.getId();

            db.rutinaDao().eliminarEjercicioDeRutina(ref);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), ejercicio.getNombre() + " eliminado", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}