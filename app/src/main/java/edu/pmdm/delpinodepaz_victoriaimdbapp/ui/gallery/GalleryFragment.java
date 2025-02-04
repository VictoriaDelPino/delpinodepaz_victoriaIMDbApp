package edu.pmdm.delpinodepaz_victoriaimdbapp.ui.gallery;

import static edu.pmdm.delpinodepaz_victoriaimdbapp.Database.DBManager.deleteUserFavorite;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import edu.pmdm.delpinodepaz_victoriaimdbapp.Database.DBManager;
import edu.pmdm.delpinodepaz_victoriaimdbapp.MovieActivity;
import edu.pmdm.delpinodepaz_victoriaimdbapp.Movies.Movie;
import edu.pmdm.delpinodepaz_victoriaimdbapp.MyItemRecycleViewAdapter;
import edu.pmdm.delpinodepaz_victoriaimdbapp.databinding.FragmentGalleryBinding;

public class GalleryFragment extends Fragment {

    private MyItemRecycleViewAdapter adapter;
    private List<Movie> movieList;
    private FragmentGalleryBinding binding;
    private String userEmail;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        userEmail = currentUser.getEmail();
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        try {
            movieList=DBManager.getUserFavorites(userEmail);
        } catch (Exception e) {


            Log.e("Database_", "Error en DB", e);
        }

        //final TextView textView = binding.textGallery;

        setupRecyclerView();
        //galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    private void setupRecyclerView() {
        // Inicializar lista si es null
        if (movieList == null) {
            movieList = new ArrayList<>();
        }

        Log.d("Victoria__recyclerview", "Tamaño de movieList: " + movieList.size());

        RecyclerView recyclerView = binding.recyclerViewFav;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new MyItemRecycleViewAdapter(movieList, getContext(), new MyItemRecycleViewAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(Movie movie) {
                movie.setRating("");
                Toast.makeText(getContext(), "Clic en: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), MovieActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(Movie movie) {
                deleteUserFavorite(getContext(), userEmail, movie.getId());

                // Eliminar la película de la lista
                movieList.remove(movie);

                // Notificar al adaptador del cambio
                adapter.notifyDataSetChanged();

            }
        });

        recyclerView.setAdapter(adapter);
    }

        @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
