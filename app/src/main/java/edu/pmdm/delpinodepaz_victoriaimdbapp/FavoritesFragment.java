package edu.pmdm.delpinodepaz_victoriaimdbapp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class FavoritesFragment extends DialogFragment {
    private static final String ARG_JSON = "json_arg";
    private String jsonContent;

    public static FavoritesFragment newInstance(String jsonContent) {
        FavoritesFragment fragment = new FavoritesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_JSON, jsonContent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false); // Evita que se cierre tocando fuera o con el botón atrás
        if (getArguments() != null) {
            jsonContent = getArguments().getString(ARG_JSON);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        // Asignar texto al TextView
        TextView jsonMoviesFav = view.findViewById(R.id.txtMoviesList);
        if (jsonContent != null) {
            jsonMoviesFav.setText(jsonContent);
        } else {
            jsonMoviesFav.setText("No hay datos disponibles.");
        }

        // Configurar botón de cierre
        Button btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> dismiss());

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Favoritos");
        dialog.setCanceledOnTouchOutside(false); // Evita cierre al tocar fuera
        return dialog;
    }
}
