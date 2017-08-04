package com.almaz.owmapp;

import android.app.Application;

import com.almaz.owmapp.api.TheOwmApi;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class App extends Application {

    private static TheOwmApi sTheOwmApi;
    private Retrofit retrofit;
    private OkHttpClient okHttpClient;

    @Override
    public void onCreate() {
        super.onCreate();

        Interceptor loggingInterceptor = new HttpLoggingInterceptor()
                .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/data/2.5/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        sTheOwmApi = retrofit.create(TheOwmApi.class);
    }

    public static TheOwmApi getApi() {
        return sTheOwmApi;
    }
}
