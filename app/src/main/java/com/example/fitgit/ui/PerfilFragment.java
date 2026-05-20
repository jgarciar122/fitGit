package com.example.fitgit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fitgit.databinding.FragmentPerfilBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if (usuario != null) {
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

        binding.btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
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