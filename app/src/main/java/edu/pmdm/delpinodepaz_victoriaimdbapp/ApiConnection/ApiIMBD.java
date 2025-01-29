package edu.pmdm.delpinodepaz_victoriaimdbapp.ApiConnection;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.*;

import edu.pmdm.delpinodepaz_victoriaimdbapp.Movies.Movie;


public class ApiIMBD {
    private static String headerKey= "x-rapidapi-key";
    private static String headerHost= "x-rapidapi-host";
    private static String apiKey= "586f49b130msh99155cbad4559f5p12735bjsn219e7f19e09e";
    private static String headerValue="imdb-com.p.rapidapi.com";
    private static Movie movie;
    private static List<Movie> moviesList;

    public static void getTop10Movie(){
        Thread thread=new Thread(()->{
            try {
                moviesList= new ArrayList<>();
                String URLstring= ("https://imdb-com.p.rapidapi.com/title/get-top-meter?topMeterTitlesType=ALL");
                URL url=new URL(URLstring);

                HttpURLConnection conection= (HttpURLConnection) url.openConnection();
                conection.setRequestMethod("GET");
                conection.setRequestProperty(headerKey,apiKey);
                conection.setRequestProperty(headerHost,headerValue );
                conection.setConnectTimeout(5000);
                conection.setReadTimeout(5000);

                int responseCode=conection.getResponseCode();
                BufferedReader bReader= new BufferedReader(new InputStreamReader(conection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while((inputLine=bReader.readLine())!=null){
                    response.append(inputLine);
                }
                bReader.close();
                String json=response.toString();
                JSONObject jsonObject= new JSONObject(json);
                JSONObject jsonObjectData= jsonObject.getJSONObject("data");

                JSONObject jsonObjectFilms= jsonObjectData.getJSONObject("topMeterTitles");
                JSONArray filmsArray=jsonObjectFilms.getJSONArray("edges");
                for (int i = 0; i < 10; i++) {
                    movie=new Movie();
                    JSONObject filmObject = filmsArray.getJSONObject(i); // Obtener cada objeto JSON dentro del array
                    JSONObject movieObject=filmObject.getJSONObject("node");
                    String movieID= movieObject.getString("id");
                    Log.d("Victoria__id "+i,movieID);
                    movie.setId(movieID);

                    JSONObject titleTextObject = movieObject.getJSONObject("titleText");
                    String titleText = titleTextObject.getString("text");
                    Log.d("Victoria__title "+i,titleText);
                    movie.setTitle(titleText);

                    JSONObject releaseDateObject = movieObject.getJSONObject("releaseDate");
                    int month = releaseDateObject.getInt("month");
                    int day = releaseDateObject.getInt("day");
                    int year = releaseDateObject.getInt("year");
                    String releaseDate= day+"/"+month+"/"+year;
                    Log.d("Victoria__releaseDate "+i,releaseDate);
                    movie.setReleaseDate(releaseDate);

                    JSONObject typeObject = movieObject.getJSONObject("titleType");
                    String type = typeObject.getString("id");
                    Log.d("Victoria__type "+i,type);

                    JSONObject imgObject = movieObject.getJSONObject("primaryImage");
                    String imgURL = imgObject.getString("url");
                    Log.d("Victoria__url "+i,imgURL);
                    movie.setPhoto(imgURL);

                    JSONObject rankingObject = movieObject.getJSONObject("meterRanking");
                    int ranking = rankingObject.getInt("currentRank");
                    Log.d("Victoria__rank"+i,ranking+"");
                    movie.setRating(ranking+"");

                    Log.d("Victoria__","*****************************************************************");
                    moviesList.add(movie);
                    getMovieOverview(movieID);

                }

                Log.d("Victoria__","*****************************************************************");

            }catch (Exception e){
                Log.d("Victoria__2",e.getMessage());
            }
        });
        thread.start();
    }

    public static void getMovieOverview(String movieID){
        Thread thread=new Thread(()->{
            try {
                String URLstring= ("https://imdb-com.p.rapidapi.com/title/get-overview?tconst="+movieID);
                URL url=new URL(URLstring);

                HttpURLConnection conection= (HttpURLConnection) url.openConnection();
                conection.setRequestMethod("GET");
                conection.setRequestProperty(headerKey,apiKey);
                conection.setRequestProperty(headerHost,headerValue );
                conection.setConnectTimeout(5000);
                conection.setReadTimeout(5000);

                int responseCode=conection.getResponseCode();
                BufferedReader bReader= new BufferedReader(new InputStreamReader(conection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while((inputLine=bReader.readLine())!=null){
                    response.append(inputLine);
                }
                bReader.close();
                if(response!=null) {
                String json=response.toString();
                JSONObject jsonObject= new JSONObject(json);//plot
                Log.d("Victoria__Overview", jsonObject.toString());
                }

            }catch (Exception e){
                Log.d("Victoria__2",e.getMessage());
            }
        });
        thread.start();
    }
}
