package com.example.fitgit.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fitgit.R;
import com.example.fitgit.adapter.AdaptadorHistorial;
import com.example.fitgit.databinding.FragmentProgresoBinding;
import com.example.fitgit.model.EntrenamientoDia;
import com.example.fitgit.model.EjercicioConSeries;
import com.example.fitgit.model.PuntoGrafica;
import com.example.fitgit.model.Sesion;
import com.example.fitgit.viewmodel.SesionViewModel;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProgresoFragment extends Fragment {

    private FragmentProgresoBinding binding;
    private SesionViewModel viewModel;
    private AdaptadorHistorial adaptador;
    private LinkedHashMap<String, String> ejerciciosDisponibles = new LinkedHashMap<>();
    private String ejercicioIdSeleccionado = null;

    // 0 = semana actual, -1 = anterior, etc
    private int offsetSemana = 0;
    private LiveData<List<Sesion>> liveDataSemanaActual = null;
    private androidx.lifecycle.Observer<List<Sesion>> observerSemana = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProgresoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SesionViewModel.class);

        adaptador = new AdaptadorHistorial();

        adaptador.setOnEliminarSesionListener(sesionId ->
                new MaterialAlertDialogBuilder(requireContext(), R.style.DialogRedondeado)
                        .setTitle("Eliminar sesión")
                        .setMessage("¿Seguro que quieres eliminar este entrenamiento completo?")
                        .setPositiveButton("Eliminar", (dialog, which) ->
                                viewModel.eliminarSesionCompleta(sesionId))
                        .setNegativeButton("Cancelar", null)
                        .show()
        );

        adaptador.setOnEliminarEjercicioListener((sesionId, ejercicioId) ->
                new MaterialAlertDialogBuilder(requireContext(), R.style.DialogRedondeado)
                        .setTitle("Eliminar ejercicio")
                        .setMessage("¿Seguro que quieres eliminar este ejercicio de la sesión?")
                        .setPositiveButton("Eliminar", (dialog, which) ->
                                viewModel.eliminarEjercicioDeSesion(sesionId, ejercicioId))
                        .setNegativeButton("Cancelar", null)
                        .show()
        );

        binding.rvHistorial.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvHistorial.setAdapter(adaptador);

        configurarGrafica();
        configurarNavegacionSemanas();
        cargarSemana();

        viewModel.obtenerHistorialAgrupado().observe(getViewLifecycleOwner(), entrenamientos -> {
            if (entrenamientos == null || entrenamientos.isEmpty()) {
                binding.tvSinHistorial.setVisibility(View.VISIBLE);
                binding.rvHistorial.setVisibility(View.GONE);
            } else {
                binding.tvSinHistorial.setVisibility(View.GONE);
                binding.rvHistorial.setVisibility(View.VISIBLE);
                adaptador.setEntrenamientos(entrenamientos);
                actualizarSelectorEjercicios(entrenamientos);
            }
        });
    }

    private void configurarNavegacionSemanas() {
        binding.btnSemanaAnterior.setOnClickListener(v -> {
            offsetSemana--;
            cargarSemana();
        });

        binding.btnSemanaSiguiente.setOnClickListener(v -> {
            if (offsetSemana < 0) {
                offsetSemana++;
                cargarSemana();
            }
        });
    }

    private void cargarSemana() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_YEAR, offsetSemana);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long inicioDia = cal.getTimeInMillis();

        cal.add(Calendar.DAY_OF_WEEK, 6);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        long finSemana = cal.getTimeInMillis();

        if (offsetSemana == 0) {
            binding.tvTituloSemana.setText("ESTA SEMANA");
        } else if (offsetSemana == -1) {
            binding.tvTituloSemana.setText("SEMANA PASADA");
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.forLanguageTag("es"));
            Calendar inicio = Calendar.getInstance();
            inicio.setTimeInMillis(inicioDia);
            binding.tvTituloSemana.setText(sdf.format(inicio.getTime()).toUpperCase());
        }

        binding.btnSemanaSiguiente.setAlpha(offsetSemana < 0 ? 1f : 0.3f);
        binding.btnSemanaSiguiente.setEnabled(offsetSemana < 0);

        if (liveDataSemanaActual != null && observerSemana != null) {
            liveDataSemanaActual.removeObserver(observerSemana);
        }
        observerSemana = sesiones -> actualizarResumenSemanal(sesiones);
        liveDataSemanaActual = viewModel.obtenerSesionesSemana(inicioDia, finSemana);
        liveDataSemanaActual.observe(getViewLifecycleOwner(), observerSemana);
    }

    private void configurarGrafica() {
        binding.graficaEvolucion.setTouchEnabled(true);
        binding.graficaEvolucion.setDragEnabled(true);
        binding.graficaEvolucion.setScaleEnabled(true);
        binding.graficaEvolucion.getDescription().setEnabled(false);
        binding.graficaEvolucion.getLegend().setEnabled(false);
        binding.graficaEvolucion.setNoDataText("Selecciona un ejercicio");
        binding.graficaEvolucion.setNoDataTextColor(Color.GRAY);

        XAxis xAxis = binding.graficaEvolucion.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.GRAY);
        xAxis.setDrawGridLines(false);

        binding.graficaEvolucion.getAxisRight().setEnabled(false);
        binding.graficaEvolucion.getAxisLeft().setTextColor(Color.GRAY);
        binding.graficaEvolucion.getAxisLeft().setDrawGridLines(true);
        binding.graficaEvolucion.getAxisLeft().setGridColor(Color.parseColor("#F0F0F0"));
    }

    private void actualizarSelectorEjercicios(List<EntrenamientoDia> entrenamientos) {
        ejerciciosDisponibles.clear();

        for (EntrenamientoDia entrenamiento : entrenamientos) {
            for (EjercicioConSeries ejercicio : entrenamiento.ejercicios) {
                if (!ejerciciosDisponibles.containsKey(ejercicio.ejercicioId)) {
                    ejerciciosDisponibles.put(ejercicio.ejercicioId, ejercicio.nombreEjercicio);
                }
            }
        }

        // si borraron el ejercicio del historial, limpio el selector
        if (ejercicioIdSeleccionado != null && !ejerciciosDisponibles.containsKey(ejercicioIdSeleccionado)) {
            ejercicioIdSeleccionado = null;
            binding.actvSelectorEjercicio.setText("", false);
            binding.graficaEvolucion.clear();
            binding.tvSinDatosGrafica.setVisibility(View.GONE);
            binding.tvMaximoKg.setVisibility(View.GONE);
        }

        List<String> nombres = new ArrayList<>(ejerciciosDisponibles.values());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                nombres
        );
        binding.actvSelectorEjercicio.setAdapter(adapter);

        binding.actvSelectorEjercicio.setOnItemClickListener((parent, v, position, id) -> {
            String nombreSeleccionado = nombres.get(position);
            String ejercicioId = null;
            for (Map.Entry<String, String> entry : ejerciciosDisponibles.entrySet()) {
                if (entry.getValue().equals(nombreSeleccionado)) {
                    ejercicioId = entry.getKey();
                    break;
                }
            }
            if (ejercicioId != null) {
                cargarGrafica(ejercicioId);
            }
        });
    }

    private void cargarGrafica(String ejercicioId) {
        ejercicioIdSeleccionado = ejercicioId;
        viewModel.obtenerEvolucionEjercicio(ejercicioId).observe(getViewLifecycleOwner(), puntos -> {
            if (puntos == null || puntos.isEmpty()) {
                binding.graficaEvolucion.clear();
                binding.tvSinDatosGrafica.setVisibility(View.VISIBLE);
                binding.tvMaximoKg.setVisibility(View.GONE);
                return;
            }

            binding.tvSinDatosGrafica.setVisibility(View.GONE);
            binding.tvMaximoKg.setVisibility(View.VISIBLE);

            List<Entry> entradas = new ArrayList<>();
            List<Long> fechas = new ArrayList<>();
            float maxKg = 0;

            for (int i = 0; i < puntos.size(); i++) {
                PuntoGrafica punto = puntos.get(i);
                entradas.add(new Entry(i, punto.kg));
                fechas.add(punto.fecha);
                if (punto.kg > maxKg) maxKg = punto.kg;
            }

            LineDataSet dataSet = new LineDataSet(entradas, "kg");
            dataSet.setColor(getResources().getColor(R.color.primary, null));
            dataSet.setCircleColor(getResources().getColor(R.color.primary, null));
            dataSet.setLineWidth(2.5f);
            dataSet.setCircleRadius(4f);
            dataSet.setDrawValues(false);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            dataSet.setDrawFilled(true);
            dataSet.setFillColor(getResources().getColor(R.color.primary, null));
            dataSet.setFillAlpha(30);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.forLanguageTag("es"));
            binding.graficaEvolucion.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    int index = (int) value;
                    if (index >= 0 && index < fechas.size()) {
                        return sdf.format(new Date(fechas.get(index)));
                    }
                    return "";
                }
            });

            binding.graficaEvolucion.setData(new LineData(dataSet));
            binding.graficaEvolucion.animateX(500);
            binding.graficaEvolucion.invalidate();

            binding.tvMaximoKg.setText("Máximo registrado: " + maxKg + " kg");
        });
    }

    private void actualizarResumenSemanal(List<Sesion> sesiones) {
        android.widget.TextView[] circulos = {
                binding.circuloLunes, binding.circuloMartes, binding.circuloMiercoles,
                binding.circuloJueves, binding.circuloViernes, binding.circuloSabado,
                binding.circuloDomingo
        };

        for (android.widget.TextView c : circulos) {
            c.setText("⬜");
        }

        if (sesiones == null || sesiones.isEmpty()) {
            binding.tvResumenSemana.setText("0 días entrenados esta semana");
            return;
        }

        int diasEntrenados = 0;
        java.util.Set<Integer> diasMarcados = new java.util.HashSet<>();

        for (Sesion sesion : sesiones) {
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