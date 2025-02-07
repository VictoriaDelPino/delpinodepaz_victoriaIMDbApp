package edu.pmdm.delpinodepaz_victoriaimdbapp.ui.home;

import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import edu.pmdm.delpinodepaz_victoriaimdbapp.ApiConnection.ApiIMBD;
import edu.pmdm.delpinodepaz_victoriaimdbapp.Database.DBManager;
import edu.pmdm.delpinodepaz_victoriaimdbapp.MovieActivity;
import edu.pmdm.delpinodepaz_victoriaimdbapp.Movies.Movie;
import edu.pmdm.delpinodepaz_victoriaimdbapp.MyItemRecycleViewAdapter;
import edu.pmdm.delpinodepaz_victoriaimdbapp.R;
import edu.pmdm.delpinodepaz_victoriaimdbapp.databinding.FragmentHomeBinding;

//Fragmento que muestra el top 10 de películas.
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MyItemRecycleViewAdapter adapter;
    private List<Movie> movieList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        // Inicializa la base de datos con el contexto actual
        DBManager.init(getContext());

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Configura el RecyclerView con una lista de películas
        setupRecyclerView();

        return root;
    }

    private void setupRecyclerView() {
        // Crea lista de películas de prueba
        movieList=new ArrayList<>();
        movieList.add(new Movie("a","b","https://r-charts.com/es/miscelanea/procesamiento-imagenes-magick_files/figure-html/importar-imagen-r.png","d","e","f"));
        //Llama a la API para cargar el top 10 de películas
        //movieList = ApiIMBD.getTop10Movie();

        // Configura RecyclerView con GridLayoutManager para 2 columnas
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Inicializa el adaptador del RecyclerView y define los eventos de click
        adapter = new MyItemRecycleViewAdapter(movieList, getContext(), new MyItemRecycleViewAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(Movie movie) {
                Toast.makeText(getContext(), "Click en: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
                // Inicia una nueva actividad para mostrar detalles de la película
                Intent intent = new Intent(getActivity(), MovieActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(Movie movie) {
                // Obtiene el usuario actual autenticado en Firebase
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


                if (currentUser != null) {
                    String userEmail = currentUser.getEmail();
                    // Intenta guardar la película en la lista de favoritos del usuario
                    try {
                        DBManager.setUserFavorite(userEmail, movie);
                        Toast.makeText(
                                getContext(),
                                movie.getTitle() + " "+getString(R.string.save_as_favorite),
                                Toast.LENGTH_SHORT
                        ).show();

                    } catch (Exception e) {
                        // Muestra un mensaje de error si ocurre un problema con la base de datos
                        Toast.makeText(
                                getContext(),
                                getString(R.string.error_saving_favorites),
                                Toast.LENGTH_SHORT
                        ).show();
                        Log.e("Error", "Error en DB", e);
                    }
                } else {
                    Toast.makeText(
                            getContext(),
                            getString(R.string.start_session_to_save_as_favorite),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
        // Asigna el adaptador al RecyclerView
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

