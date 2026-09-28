package com.example.movieapp.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // ⚠️ Đổi BASE_URL phù hợp với môi trường chạy:
    // Android Emulator: "http://10.0.2.2:8080/"
    // Máy thật cùng Wi-Fi: "http://<IP_MÁY_TÍNH>:8080/" (vd: "http://192.168.1.15:8080/")
    // Server Cloud Render: "https://backendmobile-lqh7.onrender.com/"
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor()) // Tự động inject Firebase ID Token
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
