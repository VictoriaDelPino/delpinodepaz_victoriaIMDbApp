package edu.pmdm.delpinodepaz_victoriaimdbapp.ApiConnection;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;

public class ApiIMBD {
    private static String headerKey= "x-rapidapi-key";
    private static String headerHost= "x-rapidapi-host";
    private static String apiKey= "586f49b130msh99155cbad4559f5p12735bjsn219e7f19e09e";
    private static String headerValue="imdb-com.p.rapidapi.com";

    public static void getTop10Movie(){
        Thread thread=new Thread(()->{
            try {
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

                Log.d("Victoria__1",filmsArray.toString());

            }catch (Exception e){
                Log.d("Victoria__2",e.getMessage());
            }
        });
        thread.start();
    }
}
