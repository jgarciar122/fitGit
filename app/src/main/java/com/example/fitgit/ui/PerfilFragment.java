package com.example.fitgit.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fitgit.BuildConfig;
import com.example.fitgit.R;
import com.example.fitgit.databinding.FragmentPerfilBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.fitgit.FitGitApp;

import java.io.InputStream;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PerfilFragment extends Fragment {

    private static final String SUPABASE_URL = BuildConfig.SUPABASE_URL;
    private static final String SUPABASE_KEY = BuildConfig.SUPABASE_KEY;
    private FragmentPerfilBinding binding;
    private ActivityResultLauncher<Intent> selectorImagenLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectorImagenLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imagenUri = result.getData().getData();
                        if (imagenUri != null) {
                            subirFotoASupabase(imagenUri);
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cargarDatosUsuario();
        configurarDarkMode();
        configurarIdioma();

        binding.btnEditarNombre.setOnClickListener(v -> mostrarDialogoEditarNombre());
        binding.btnEditarFoto.setOnClickListener(v -> abrirGaleria());
        binding.btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
    }

    private void configurarDarkMode() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences(FitGitApp.PREFS_NAME, android.content.Context.MODE_PRIVATE);
        boolean darkMode = prefs.getBoolean(FitGitApp.KEY_DARK_MODE, false);
        binding.switchDarkMode.setChecked(darkMode);

        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(FitGitApp.KEY_DARK_MODE, isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });
    }

    private void configurarIdioma() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences(FitGitApp.PREFS_NAME, android.content.Context.MODE_PRIVATE);
        String idiomaActual = prefs.getString(FitGitApp.KEY_IDIOMA, "es");
        binding.switchIdioma.setChecked(idiomaActual.equals("en"));

        binding.switchIdioma.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String nuevoIdioma = isChecked ? "en" : "es";
            prefs.edit().putString(FitGitApp.KEY_IDIOMA, nuevoIdioma).apply();
            FitGitApp.aplicarIdioma(prefs);
        });
    }

    private void cargarDatosUsuario() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario == null) return;

        String nombre = usuario.getDisplayName();
        binding.tvNombrePerfil.setText(nombre != null ? nombre : "Sin nombre");
        binding.tvNombreDato.setText(nombre != null ? nombre : "Sin nombre");

        String email = usuario.getEmail();
        binding.tvEmailPerfil.setText(email != null ? email : "Sin email");
        binding.tvEmailDato.setText(email != null ? email : "Sin email");

        String proveedor = "Email";
        for (com.google.firebase.auth.UserInfo info : usuario.getProviderData()) {
            if (info.getProviderId().equals("google.com")) {
                proveedor = "Google";
                break;
            }
        }
        binding.tvProveedor.setText(proveedor);

        if (usuario.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(usuario.getPhotoUrl())
                    .placeholder(android.R.drawable.ic_menu_myplaces)
                    .into(binding.ivAvatar);
        }
    }

    private void mostrarDialogoEditarNombre() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario == null) return;

        EditText input = new EditText(requireContext());
        input.setText(usuario.getDisplayName());
        input.setSelection(input.getText().length());

        new MaterialAlertDialogBuilder(requireContext(), R.style.DialogRedondeado)
                .setTitle("Cambiar nombre")
                .setView(input)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nuevoNombre = input.getText().toString().trim();
                    if (!nuevoNombre.isEmpty()) {
                        actualizarNombre(nuevoNombre);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void actualizarNombre(String nuevoNombre) {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario == null) return;

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(nuevoNombre)
                .build();

        usuario.updateProfile(request).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                binding.tvNombrePerfil.setText(nuevoNombre);
                binding.tvNombreDato.setText(nuevoNombre);
                Toast.makeText(getContext(), "Nombre actualizado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Error al actualizar el nombre", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        selectorImagenLauncher.launch(intent);
    }

    private void subirFotoASupabase(Uri imagenUri) {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario == null) return;

        Toast.makeText(getContext(), R.string.perfil_subiendo_foto, Toast.LENGTH_SHORT).show();

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // redimensiono a 256x256 para que no pese mucho y cargue rapido en la app
                InputStream inputStream = requireContext().getContentResolver().openInputStream(imagenUri);
                android.graphics.Bitmap original = android.graphics.BitmapFactory.decodeStream(inputStream);
                inputStream.close();

                android.graphics.Bitmap redimensionado = android.graphics.Bitmap.createScaledBitmap(original, 256, 256, true);
                original.recycle();

                java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
                redimensionado.compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, buffer);
                redimensionado.recycle();
                byte[] bytes = buffer.toByteArray();

                String nombreArchivo = usuario.getEmail().replace("@", "_").replace(".", "_") + ".jpg";
                String uploadUrl = SUPABASE_URL + "/storage/v1/object/fotos-perfil/" + nombreArchivo;

                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(MediaType.parse("image/jpeg"), bytes);

                Request request = new Request.Builder()
                        .url(uploadUrl)
                        .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                        .addHeader("x-upsert", "true")
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String fotoUrl = SUPABASE_URL + "/storage/v1/object/public/fotos-perfil/" + nombreArchivo
                            + "?t=" + System.currentTimeMillis();
                    Uri fotoUri = Uri.parse(fotoUrl);
                    requireActivity().runOnUiThread(() -> actualizarFotoPerfil(fotoUri));
                } else {
                    String responseBody = response.body().string();
                    Log.e("SUPABASE", "Error " + response.code() + ": " + responseBody);
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Error al subir la foto: " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                Log.e("SUPABASE", "Excepción: " + e.getMessage());
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void actualizarFotoPerfil(Uri fotoUri) {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario == null) return;

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(fotoUri)
                .build();

        usuario.updateProfile(request).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Glide.with(this)
                        .load(fotoUri)
                        .placeholder(R.drawable.ic_perfil_placeholder)
                        .into(binding.ivAvatar);
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).actualizarAvatarToolbar(fotoUri);
                }
                Toast.makeText(getContext(), "Foto actualizada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Error al actualizar la foto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
        GoogleSignIn.getClient(requireContext(), gso).signOut();

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}