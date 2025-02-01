package edu.pmdm.delpinodepaz_victoriaimdbapp.ApiConnection;

import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.*;
import edu.pmdm.delpinodepaz_victoriaimdbapp.Movies.Movie;
import java.util.concurrent.CountDownLatch;

public class ApiIMBD {
    private static final String HEADER_KEY = "x-rapidapi-key";
    private static final String HEADER_HOST = "x-rapidapi-host";
    private static final String API_KEY = "5f871d3eeemsh5a94169685bb269p1e3fd8jsn031f2b0f5978";
    private static final String HEADER_VALUE = "imdb-com.p.rapidapi.com";
    private static final int TIMEOUT = 5000;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private static final List<Movie> moviesList = new ArrayList<>();

    public interface MovieOverviewCallback {
        void onOverviewReceived(String overview);
    }

    public static ArrayList<Movie> getTop10Movie() {
        CountDownLatch countDownLatch= new CountDownLatch(1);
        executorService.execute(() -> {

            try {
                String URLstring = "https://imdb-com.p.rapidapi.com/title/get-top-meter?topMeterTitlesType=ALL";
                URL url = new URL(URLstring);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty(HEADER_KEY, API_KEY);
                connection.setRequestProperty(HEADER_HOST, HEADER_VALUE);
                connection.setConnectTimeout(TIMEOUT);
                connection.setReadTimeout(TIMEOUT);

                if (connection.getResponseCode() != 200) {
                    Log.d("Victoria__Error", "API Response Code: " + connection.getResponseCode());
                    return;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONArray filmsArray = new JSONObject(response.toString())
                        .getJSONObject("data")
                        .getJSONObject("topMeterTitles")
                        .getJSONArray("edges");

                List<Movie> tempMoviesList = new ArrayList<>();
                for (int i = 0; i < Math.min(filmsArray.length(), 10); i++) {
                    JSONObject movieObject = filmsArray.getJSONObject(i).getJSONObject("node");
                    Movie movie = new Movie();
                    movie.setId(movieObject.getString("id"));
                    movie.setTitle(movieObject.getJSONObject("titleText").getString("text"));
                    movie.setPhoto(movieObject.getJSONObject("primaryImage").getString("url"));

                    JSONObject releaseDateObject = movieObject.optJSONObject("releaseDate");
                    if (releaseDateObject != null) {
                        String releaseDate = releaseDateObject.optInt("day", 1) + "/" +
                                releaseDateObject.optInt("month", 1) + "/" +
                                releaseDateObject.optInt("year", 2000);
                        movie.setReleaseDate(releaseDate);
                    }

                    movie.setRating(String.valueOf(movieObject.getJSONObject("meterRanking").optInt("currentRank", 0)));
                    tempMoviesList.add(movie);
                }

                moviesList.clear();
                moviesList.addAll(tempMoviesList);
                CountDownLatch countDown= new CountDownLatch(moviesList.size());

                for (int i = 0; i < moviesList.size(); i++) {
                    Movie movie = moviesList.get(i);
                    int finalI = i;
                    getMovieOverview(movie.getId(), overview -> {
                        movie.setDescription(overview);
                        Log.d("Victoria__Overview", "Descripción recibida: " + overview);
                        Log.d("Victoria__movie_" + finalI, movie.toString());
                        countDown.countDown();
                    });
                }
                try {
                    countDown.await();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

            } catch (Exception e) {
                Log.d("Victoria__Error", "Error: " + e.getMessage());
            }

            for (int i = 0; i < moviesList.size(); i++) {
                Movie movie = moviesList.get(i);
                Log.d("Victoria__movie___" + i, movie.toString());

            }
            countDownLatch.countDown();

        });
        try{
            countDownLatch.await();
        }catch (InterruptedException ei){
            ei.printStackTrace();
        }
        return (ArrayList<Movie>) moviesList;
    }

    public static void getMovieOverview(String movieID, MovieOverviewCallback callback) {
        executorService.submit(() -> {
            try {
                String URLstring = "https://imdb-com.p.rapidapi.com/title/get-overview?tconst=" + movieID;
                URL url = new URL(URLstring);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty(HEADER_KEY, API_KEY);
                connection.setRequestProperty(HEADER_HOST, HEADER_VALUE);
                connection.setConnectTimeout(TIMEOUT);
                connection.setReadTimeout(TIMEOUT);

                int responseCode = connection.getResponseCode();
                if (responseCode == 429) {
                    Thread.sleep(3000);
                    getMovieOverview(movieID, callback);
                    return;
                }
                if (responseCode != 200) {
                    Log.d("Victoria__Error", "API Response Code: " + responseCode);
                    callback.onOverviewReceived("");
                    return;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                String plot = jsonObject.getJSONObject("data")
                        .getJSONObject("title")
                        .getJSONObject("plot")
                        .getJSONObject("plotText")
                        .getString("plainText");

                callback.onOverviewReceived(plot);

            } catch (Exception e) {
                Log.d("Victoria__Error", "Error: " + e.getMessage());
                callback.onOverviewReceived("");
            }
        });
    }
}