package com.example.movieapp.data.repository;

import com.example.movieapp.core.network.ApiClient;
import com.example.movieapp.core.network.ApiResponse;
import com.example.movieapp.data.remote.api.PaymentApiService;
import com.example.movieapp.data.remote.request.InitPaymentRequest;

import java.util.Map;

import retrofit2.Callback;

/**
 * Repository pattern for Payment operations
 */
public class PaymentRepository {

    private final PaymentApiService apiService;

    public PaymentRepository() {
        this.apiService = ApiClient.getPaymentApi();
    }

    public void createPaymentUrl(int amount, String orderInfo, Callback<ApiResponse<Map<String, String>>> callback) {
        InitPaymentRequest request = new InitPaymentRequest(amount, orderInfo);
        apiService.createPaymentUrl(request).enqueue(callback);
    }
}
