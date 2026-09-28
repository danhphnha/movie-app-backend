package com.example.movieapp.data.remote.api;

import com.example.movieapp.core.network.ApiResponse;
import com.example.movieapp.data.remote.request.InitPaymentRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PaymentApiService {

    @POST("api/pay")
    Call<ApiResponse<Map<String, String>>> createPaymentUrl(@Body InitPaymentRequest request);
}
