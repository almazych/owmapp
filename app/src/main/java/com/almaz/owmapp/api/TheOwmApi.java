package com.almaz.owmapp.api;

import com.almaz.owmapp.data.Data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TheOwmApi {

    @GET("forecast" )
    Call<Data> getData(@Query("q") String cityName, @Query("APPID") String appId, @Query("units") String metric, @Query("lang") String lang);

    @GET("forecast" )
    Call<Data> getDataFromGeo(@Query("lat") String lat, @Query("lon") String lon, @Query("APPID") String appId, @Query("units") String metric, @Query("lang") String lang);
}
