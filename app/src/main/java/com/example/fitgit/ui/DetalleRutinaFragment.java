package com.example.fitgit.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fitgit.adapter.AdaptadorEjercicios;
import com.example.fitgit.database.AppDatabase;
import com.example.fitgit.databinding.FragmentDetalleRutinaBinding; // Asegúrate de que este nombre coincida con tu XML

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
        // 1. Es fundamental inflar el binding aquí
        binding = FragmentDetalleRutinaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            rutinaId = getArguments().getInt("rutina_id");
            nombreRutina = getArguments().getString("rutina_nombre");

            // Opcional: Podrías cambiar el título de una Toolbar aquí con nombreRutina
        }

        AppDatabase db = AppDatabase.getDatabase(requireContext());

        // 2. Configurar el RecyclerView con un LayoutManager
        AdaptadorEjercicios adaptador = new AdaptadorEjercicios();
        binding.rvEjerciciosDetalle.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvEjerciciosDetalle.setAdapter(adaptador);
        binding.tvTituloDetalle.setText("RUTINA: " + nombreRutina);

        // 3. Observar los datos
        db.rutinaDao().obtenerEjerciciosDeRutina(rutinaId).observe(getViewLifecycleOwner(), ejercicios -> {
            if (ejercicios != null) {
                adaptador.setEjercicios(ejercicios);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 4. Limpiar el binding para evitar fugas de memoria
        binding = null;
    }
}