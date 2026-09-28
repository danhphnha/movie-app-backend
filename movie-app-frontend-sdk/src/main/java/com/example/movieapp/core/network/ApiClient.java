package com.example.movieapp.core.network;

import com.example.movieapp.data.remote.api.CollectionApiService;
import com.example.movieapp.data.remote.api.PaymentApiService;
import com.example.movieapp.data.remote.api.ReviewApiService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Production-ready Retrofit API Client configuration
 */
public final class ApiClient {

    // Base URL configuration:
    // - Android Emulator: "http://10.0.2.2:8080/"
    // - Local Network: "http://192.168.1.X:8080/"
    // - Cloud Production (Render): "https://backendmobile-lqh7.onrender.com/"
    public static final String BASE_URL = "http://10.0.2.2:8080/";

    private static Retrofit retrofitInstance = null;

    private ApiClient() {}

    private static synchronized Retrofit getRetrofit() {
        if (retrofitInstance == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor())
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();

            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitInstance;
    }

    public static CollectionApiService getCollectionApi() {
        return getRetrofit().create(CollectionApiService.class);
    }

    public static ReviewApiService getReviewApi() {
        return getRetrofit().create(ReviewApiService.class);
    }

    public static PaymentApiService getPaymentApi() {
        return getRetrofit().create(PaymentApiService.class);
    }
}
