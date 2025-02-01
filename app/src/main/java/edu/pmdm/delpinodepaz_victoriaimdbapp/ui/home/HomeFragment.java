package edu.pmdm.delpinodepaz_victoriaimdbapp.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.pmdm.delpinodepaz_victoriaimdbapp.ApiConnection.ApiIMBD;
import edu.pmdm.delpinodepaz_victoriaimdbapp.Movies.Movie;
import edu.pmdm.delpinodepaz_victoriaimdbapp.MyItemRecycleViewAdapter;
import edu.pmdm.delpinodepaz_victoriaimdbapp.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MyItemRecycleViewAdapter adapter;
    private List<Movie> movieList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Configurar RecyclerView
        setupRecyclerView();

        return root;
    }

    private void setupRecyclerView() {
        // Crear lista de películas de prueba
        movieList = ApiIMBD.getTop10Movie();
        Log.d("Victoria__recyclerview", String.valueOf(movieList.size()));
      /*  movieList.add(new Movie("Inception", "1", "https://image-url.com/inception.jpg","Desc","Sci-Fi","8.8"));
        movieList.add(new Movie("Interstellar", "2", "https://image-url.com/interstellar.jpg","Desc","Sci-Fi","8.6"));
        movieList.add(new Movie("The Dark Knight", "3", "https://image-url.com/darkknight.jpg","Desc","Action","9.0"));
        movieList.add(new Movie("Tenet", "4", "https://image-url.com/tenet.jpg","Desc","Thriller","7.5"));
*/
        // Configurar RecyclerView con GridLayoutManager para 2 columnas
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new MyItemRecycleViewAdapter(movieList, getContext(), new MyItemRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie movie) {
                Toast.makeText(getContext(), "Clic en: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(Movie movie) {
                Toast.makeText(getContext(), "Manteniendo: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

