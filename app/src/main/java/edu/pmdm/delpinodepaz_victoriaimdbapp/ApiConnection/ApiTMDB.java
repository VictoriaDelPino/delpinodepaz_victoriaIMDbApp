package edu.pmdm.delpinodepaz_victoriaimdbapp.ApiConnection;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.pmdm.delpinodepaz_victoriaimdbapp.Movies.Genre;

public class ApiTMDB {
    private static final String API_URL = "https://api.themoviedb.org/3/genre/movie/list?language=es";
    private static final String API_KEY = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJjNDg2M2FmMGIxNTA3MzVjYjMyYjQwOTNiY2E0YTBiZCIsIm5iZiI6MTczODQzNjcxOC40NzMsInN1YiI6IjY3OWU3MDZlYTFlMzNjNDA4YTI2MWNhYyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.LqxvWm0e_oI1DS7NAM1djVEWHw89rD_p7TXhbE8FSI0"; // Reemplázalo con tu token válido
    private static final ExecutorService executorServiceTMDB = Executors.newFixedThreadPool(5);
    private static final List<Genre> genreList = new ArrayList<>();

    public static ArrayList<Genre> getGenre() {
        CountDownLatch countDownLatchTMDB= new CountDownLatch(1);
        executorServiceTMDB.execute(() -> {

            HttpURLConnection connection = null;
            try {
                URL url = new URL(API_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Authorization", API_KEY);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                Log.d("TMDB", "API Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Convertir la respuesta a un JSONObject
                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray genresArray = jsonObject.getJSONArray("genres");

                    Genre genre;
                    // Iterar sobre los géneros y extraer los nombres
                    for (int i = 0; i < genresArray.length(); i++) {
                        genre=new Genre();
                        JSONObject genreObject = genresArray.getJSONObject(i);
                        int genreId=genreObject.getInt("id");
                        String genreName = genreObject.getString("name");
                        genre.setId(genreId);
                        genre.setGenreName(genreName);
                        genreList.add(genre);
                        Log.d("TMDB", "Género: " + genreName);
                    }
                } else {
                    Log.e("TMDB", "Error en la API: Código " + responseCode);
                }

            } catch (Exception e) {
                Log.e("TMDB", "Error en la API: " + e.getMessage(), e);

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            countDownLatchTMDB.countDown();
        });
        try{
            countDownLatchTMDB.await();
        }catch (InterruptedException ei){
            ei.printStackTrace();
        }
        return (ArrayList<Genre>) genreList;
    }


}

