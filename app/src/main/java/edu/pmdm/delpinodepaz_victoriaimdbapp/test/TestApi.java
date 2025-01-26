package edu.pmdm.delpinodepaz_victoriaimdbapp.test;

import android.util.Log;

import java.net.*;
import java.io.*;

public class TestApi {
    private static String headerKey= "x-rapidapi-key";
    private static String headerHost= "x-rapidapi-host";
    private static String apiKey= "586f49b130msh99155cbad4559f5p12735bjsn219e7f19e09e";

    public static void test(){
        Thread thread=new Thread(()->{
            try {
                String URLstring= ("https://imdb-com.p.rapidapi.com/title/get-top-meter?topMeterTitlesType=ALL");
                URL url=new URL(URLstring);

                HttpURLConnection conection= (HttpURLConnection) url.openConnection();
                conection.setRequestMethod("GET");
                conection.setConnectTimeout(5000);
                conection.setReadTimeout(5000);

                int responseCode=conection.getResponseCode();
                Log.d("Victoria",responseCode+"");
            }catch (Exception e){
                Log.d("Victoria",e.getMessage());
            }
        });
        thread.start();

    }

}
