package edu.pmdm.delpinodepaz_victoriaimdbapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.pmdm.delpinodepaz_victoriaimdbapp.ApiConnection.ApiTMDB;
import edu.pmdm.delpinodepaz_victoriaimdbapp.Movies.Movie;

public class SearchResultActivity extends AppCompatActivity {

    private MyItemRecycleViewAdapter adapter;
    private List<Movie> movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_result);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtener datos del Intent
        Intent intent = getIntent();
        if (intent != null) {
            int genreId = intent.getIntExtra("GENRE_ID", -1);
            String year = intent.getStringExtra("YEAR");

            if (genreId != -1 && year != null) {
                Log.d("SearchResultActivity", "Recibido Genre ID: " + genreId + ", Año: " + year);

                // Obtener la lista de películas y evitar NullPointerException
                movieList = ApiTMDB.getSearchedList(year, String.valueOf(genreId));
                if (movieList == null) {
                    movieList = new ArrayList<>();
                    Log.e("SearchResultActivity", "API devolvió null, inicializando lista vacía");
                }

                Log.d("Victoria__recyclerview", "Tamaño de movieList: " + movieList.size());

            } else {
                Log.e("SearchResultActivity", "Datos inválidos recibidos");
                movieList = new ArrayList<>();
            }
        } else {
            Log.e("SearchResultActivity", "Intent nulo");
            movieList = new ArrayList<>();
        }

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        // Inicializar lista si es null (precaución adicional)
        if (movieList == null) {
            movieList = new ArrayList<>();
        }

        Log.d("Victoria__recyclerview", "Tamaño de movieList: " + movieList.size());

        // Referencia al RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycleViewSearchResult);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new MyItemRecycleViewAdapter(movieList, this, new MyItemRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie movie) {
                Toast.makeText(SearchResultActivity.this, "Clic en: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SearchResultActivity.this, MovieActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(Movie movie) {
                Toast.makeText(SearchResultActivity.this, "Manteniendo: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);
    }
}
