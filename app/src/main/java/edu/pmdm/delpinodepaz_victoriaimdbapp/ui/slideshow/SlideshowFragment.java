package edu.pmdm.delpinodepaz_victoriaimdbapp.ui.slideshow;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import edu.pmdm.delpinodepaz_victoriaimdbapp.ApiConnection.ApiTMDB;
import edu.pmdm.delpinodepaz_victoriaimdbapp.Movies.Genre;
import edu.pmdm.delpinodepaz_victoriaimdbapp.SearchResultActivity;
import edu.pmdm.delpinodepaz_victoriaimdbapp.databinding.FragmentSlideshowBinding;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    private List<Genre> genreObjectList;
    private List<String> genreList;
    private Spinner genreSpinner;
    private Button btnSearch;
    private Genre selectedGenre;
    private EditText txtYear;
    private String year;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnSearch = binding.btnSearch;
        genreSpinner = binding.spinnerGenre;
        txtYear = binding.eTxtYear;

        // Obtener la lista de géneros desde la API
        genreObjectList = ApiTMDB.getGenre();
        genreList = new ArrayList<>();

        // Agregar un elemento por defecto como primer elemento de la lista
        genreList.add(0, "Selecciona un género");
        for (Genre genre : genreObjectList) {
            Log.d("TMDB_", genre.getGenreName());
            genreList.add(genre.getGenreName());
        }

        // Configurar el adaptador para el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                genreList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(adapter);

        // Configurar el listener para el Spinner
        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                    selectedGenre = null;
                } else {
                    String selectedGenreName = genreList.get(position);
                    for (Genre g : genreObjectList) {
                        if (g.getGenreName().equals(selectedGenreName)) {
                            selectedGenre = g;
                            break;
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Configurar el botón para iniciar la actividad de búsqueda
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedGenre != null) {
                    try {
                        year = txtYear.getText().toString().trim();
                        int yearInt = Integer.parseInt(year);
                        int currentYear = java.time.Year.now().getValue();

                        if (yearInt >= 1888 && yearInt <= currentYear) {
                            // Crear el Intent para iniciar SearchResultActivity
                            Intent intent = new Intent(requireContext(), SearchResultActivity.class);
                            intent.putExtra("GENRE_ID", selectedGenre.getId());
                            intent.putExtra("YEAR", year);
                            startActivity(intent);
                        } else {
                            Toast.makeText(requireContext(), "El año debe estar entre 1888 y " + currentYear, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "Ocurrió un error al procesar el año.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Por favor, selecciona un género válido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}