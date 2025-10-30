package com.loyalstring.LatestApis;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    //   private static final String BASE_URL = "https://testing.loyalstring.co.in/api/";
    private  static  String BASE_URL="https://rrgold.loyalstring.co.in/api/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)  // Increase connection timeout
                    .readTimeout(60, TimeUnit.SECONDS)     // Increase read timeout
                    .writeTimeout(60, TimeUnit.SECONDS)    // Increase write timeout
                    .retryOnConnectionFailure(true)        // Enable retry on failure
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)                 // Attach OkHttpClient
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}