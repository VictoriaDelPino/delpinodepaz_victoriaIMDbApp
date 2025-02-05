package edu.pmdm.delpinodepaz_victoriaimdbapp.ui.gallery;

import static edu.pmdm.delpinodepaz_victoriaimdbapp.Database.DBManager.deleteUserFavorite;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.pmdm.delpinodepaz_victoriaimdbapp.Database.DBManager;
import edu.pmdm.delpinodepaz_victoriaimdbapp.FavoritesFragment;
import edu.pmdm.delpinodepaz_victoriaimdbapp.MovieActivity;
import edu.pmdm.delpinodepaz_victoriaimdbapp.Movies.Movie;
import edu.pmdm.delpinodepaz_victoriaimdbapp.MyItemRecycleViewAdapter;
import edu.pmdm.delpinodepaz_victoriaimdbapp.R;
import edu.pmdm.delpinodepaz_victoriaimdbapp.databinding.FragmentGalleryBinding;

public class GalleryFragment extends Fragment {

    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;
    private MyItemRecycleViewAdapter adapter;
    private List<Movie> movieList;
    private FragmentGalleryBinding binding;
    private String userEmail;
    private Button btnShare;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        userEmail = currentUser.getEmail();
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        btnShare = binding.btnShare;
        View root = binding.getRoot();
        try {
            movieList = DBManager.getUserFavorites(userEmail);
        } catch (Exception e) {
            Log.e("Database_", "Error en DB", e);
        }

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestBluetoothPermission();
            }
        });

        setupRecyclerView();
        return root;
    }

    private void requestBluetoothPermission() {
        // Para Android 12+ se usa el permiso BLUETOOTH_CONNECT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSION);
                return;
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH}, REQUEST_BLUETOOTH_PERMISSION);
                return;
            }
        }
        // Si ya se tienen los permisos, se muestra el JSON en el fragmento FavoriteFragment
        showMovieListJson();
        Toast.makeText(getContext(), "Permiso de Bluetooth concedido", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showMovieListJson();
                Toast.makeText(getContext(), "Permiso de Bluetooth concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Permiso de Bluetooth denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showMovieListJson() {
        if (movieList == null || movieList.isEmpty()) {
            Toast.makeText(getContext(), "No hay películas para mostrar", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONArray jsonArray = new JSONArray();
        try {
            for (Movie movie : movieList) {
                JSONObject movieObj = new JSONObject();
                movieObj.put("id", movie.getId());
                movieObj.put("photo", movie.getPhoto());
                movieObj.put("title", movie.getTitle());
                movieObj.put("description", movie.getDescription());
                movieObj.put("releaseDate", movie.getReleaseDate());
                movieObj.put("rating", movie.getRating());
                jsonArray.put(movieObj);
            }
        } catch (JSONException e) {
            Log.e("JSON_", "Error al convertir la lista de películas a JSON", e);
            Toast.makeText(getContext(), "Error al generar JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        String jsonMovies = jsonArray.toString();
        FavoritesFragment favoritesFragment = FavoritesFragment.newInstance(jsonMovies);
        favoritesFragment.show(getParentFragmentManager(), "FavoritesDialog");
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
