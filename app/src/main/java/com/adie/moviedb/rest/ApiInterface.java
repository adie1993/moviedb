package com.adie.moviedb.rest;


import com.adie.moviedb.model.CreditsResponse;
import com.adie.moviedb.model.DetailMovie;
import com.adie.moviedb.model.DetailTV;
import com.adie.moviedb.model.InTheaterResponse;
import com.adie.moviedb.model.MovieResponse;
import com.adie.moviedb.model.TVResponse;
import com.adie.moviedb.model.VideoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiInterface {
    // interface of required endpoints API
    //TODO EndPoints API here
    @Headers({"Content-Type:application/json"})
    @GET("movie/now_playing")
    Call<InTheaterResponse> getTheater(@Query("api_key") String apiKey);

    @GET("movie/popular")
    Call<MovieResponse> getPopular(@Query("api_key") String apiKey);

    @GET("tv/popular")
    Call<TVResponse> getPopularTV(@Query("api_key") String apiKey);

    @GET("movie/{id}")
    Call<DetailMovie> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("tv/{id}")
    Call<DetailTV> getTVDetails(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<VideoResponse> getTrailer(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("tv/{id}/videos")
    Call<VideoResponse> getTVTrailer(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("movie/{id}/credits")
    Call<CreditsResponse> getCredits(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("tv/{id}/credits")
    Call<CreditsResponse> getTVCredits(@Path("id") int id, @Query("api_key") String apiKey);
}
