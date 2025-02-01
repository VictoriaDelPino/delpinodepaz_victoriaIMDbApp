package edu.pmdm.delpinodepaz_victoriaimdbapp;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.pmdm.delpinodepaz_victoriaimdbapp.Movies.Movie;

public class MovieActivity extends AppCompatActivity {
    private TextView txtTitle;
    private TextView txtReleaseDate;
    private TextView txtDescription;
    private TextView txtRanking;
    private ImageView imgPhoto;
    private String title;
    private String releaseDate;
    private String description;
    private String ranking;
    private String urlPhoto;
    private final ExecutorService executorServiceMovie = Executors.newFixedThreadPool(4); // Para carga eficiente


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtTitle=findViewById(R.id.txtMovieTitle);
        txtDescription=findViewById(R.id.txtDescription);
        txtReleaseDate=findViewById(R.id.txtReleaseDate);
        txtRanking=findViewById(R.id.txtRating);
        imgPhoto=findViewById(R.id.imgMoviePhoto);

        Movie movie = getIntent().getParcelableExtra("movie");
        if (movie != null) {
            title=movie.getTitle();
            description= movie.getDescription();
            releaseDate= movie.getReleaseDate();
            ranking=movie.getRating();
            urlPhoto= movie.getPhoto();

            txtTitle.setText(title);
            txtDescription.setText(description);
            txtReleaseDate.setText(R.string.released_date+releaseDate);
            if(!ranking.equals("")) {
                txtRanking.setText(R.string.rating+ranking);
            }
            imgPhoto.setImageResource(R.drawable.ic_launcher_foreground);

            // Cargar la imagen en un hilo de fondo
            executorServiceMovie.execute(() -> {
                Bitmap bitmap = downloadImage(urlPhoto);
                if (bitmap != null) {
                    imgPhoto.post(() -> imgPhoto.setImageBitmap(bitmap)); // Asignar en la UI
                }
            });
        }

    }
    private Bitmap downloadImage(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}