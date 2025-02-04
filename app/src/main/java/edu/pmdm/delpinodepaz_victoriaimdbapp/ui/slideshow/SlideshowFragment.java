package edu.pmdm.delpinodepaz_victoriaimdbapp.ui.slideshow;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import edu.pmdm.delpinodepaz_victoriaimdbapp.databinding.FragmentSlideshowBinding;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    private List<Genre> genreObjectList;
    private List <String> genreList;
    private Spinner genreSpinner;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.eTxtYear;
        //slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Asignar el Spinner del XML
        genreSpinner = binding.spinnerGenre;

        // Obtener la lista de géneros desde la API
        genreObjectList = ApiTMDB.getGenre();
        genreList=new ArrayList<>();


        // Agregar un elemento por defecto como primer elemento de la lista
        genreList.add(0, "Selecciona un género");
        for (int i =0;i<genreObjectList.size();i++){
            String genre=genreObjectList.get(i).getGenreName();
            //por que no entra aqui y luego no saca la lista?
            Log.d("TMDB_",genre);
            genreList.add(genre);
        }
        // Configurar el adaptador para el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                genreList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        genreSpinner.setAdapter(adapter);

        // Configurar el listener para evitar que el usuario seleccione el primer elemento
        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Si selecciona "Selecciona un género", no hacer nada
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY); // Opcional: ponerlo en gris
                } else {
                    // Mostrar la opción seleccionada
                    String selectedGenre = genreList.get(position);
                    Toast.makeText(requireContext(), "Seleccionaste: " + selectedGenre, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}