package com.ricknotes.weatherapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {
    String BASE_URL = "https://api.openweathermap.org/data/2.5/";

    @GET("onecall")
    Call<Results> get(@Query("lat")double lat, @Query("lon")double lon,@Query("units") String unit,
                      @Query("appid")String apiKey);

    @GET("weather")
    Call<Results> get(@Query("q")String city, @Query("appid")String apiKey);

}
