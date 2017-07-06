package com.adie.moviedb.rest;


import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {
    public static final String BASE_IMAGE = "https://image.tmdb.org/t/p/h632";
    public static final String BASE_IMAGE_DET = "https://image.tmdb.org/t/p/w342";
    public static final String API_KEY = "082331bb036bcb2175e25063993b591e";
    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit==null) {


            //HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
           // loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // TODO bridge for retrofit API request with GSON converter
            OkHttpClient.Builder client = new OkHttpClient.Builder();

           // client.addInterceptor(loggingInterceptor);
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();
        }
        return retrofit;
    }
}
