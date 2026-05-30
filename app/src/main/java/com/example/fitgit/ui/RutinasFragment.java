package com.example.fitgit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.example.fitgit.R;
import com.example.fitgit.adapter.AdaptadorRutinas;
import com.example.fitgit.databinding.FragmentRutinasBinding;
import com.example.fitgit.viewmodel.RutinaViewModel;

public class RutinasFragment extends Fragment {

    private FragmentRutinasBinding binding;
    private AdaptadorRutinas adaptador;
    private RutinaViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRutinasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(RutinaViewModel.class);

        adaptador = new AdaptadorRutinas();
        binding.rvRutinas.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
        binding.rvRutinas.setAdapter(adaptador);

        binding.fabAddRutina.setOnClickListener(v -> mostrarDialogoNuevaRutina());

        viewModel.obtenerRutinasConConteo().observe(getViewLifecycleOwner(), rutinas -> {
            if (rutinas != null) adaptador.setRutinas(rutinas);
        });

        adaptador.setOnRutinaClickListener(rutina -> {
            Bundle args = new Bundle();
            args.putInt("rutina_id", rutina.id);
            args.putString("rutina_nombre", rutina.nombre);
            Navigation.findNavController(view).navigate(R.id.action_rutinas_to_detalle, args);
        });

        adaptador.setOnEliminarRutinaListener(rutina -> {
            new MaterialAlertDialogBuilder(requireContext(), R.style.DialogEliminar)
                    .setTitle("Eliminar rutina")
                    .setMessage("¿Seguro que quieres eliminar \"" + rutina.nombre + "\"? Se perderán todos sus ejercicios.")
                    .setPositiveButton("Eliminar", (dialog, which) -> viewModel.eliminarRutina(rutina))
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        adaptador.setOnEmpezarRutinaListener(rutina -> {
            Intent intent = new Intent(requireContext(), EntrenamientoActivity.class);
            intent.putExtra("rutina_id", rutina.id);
            intent.putExtra("rutina_nombre", rutina.nombre);
            startActivity(intent);
        });
    }

    private void mostrarDialogoNuevaRutina() {
        final EditText input = new EditText(requireContext());
        input.setHint("Rutina pierna");

        new MaterialAlertDialogBuilder(requireContext(), R.style.DialogRedondeado)
                .setTitle("Nueva Rutina")
                .setMessage("¿Qué nombre le quieres poner a tu carpeta de ejercicios?")
                .setView(input)
                .setPositiveButton("Crear", (dialog, which) -> {
                    String nombre = input.getText().toString().trim();
                    if (!nombre.isEmpty()) {
                        viewModel.insertarRutina(nombre);
                        Toast.makeText(getContext(), "¡Carpeta '" + nombre + "' creada!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}