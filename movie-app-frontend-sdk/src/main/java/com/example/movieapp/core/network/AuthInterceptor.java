package com.example.movieapp.core.network;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor that automatically injects Firebase ID Token into Authorization Header
 */
public class AuthInterceptor implements Interceptor {

    private static final String TAG = "AuthInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            try {
                // Synchronously await Firebase Token in OkHttp background thread
                Task<GetTokenResult> task = currentUser.getIdToken(false);
                GetTokenResult tokenResult = Tasks.await(task);
                String token = tokenResult.getToken();

                if (token != null && !token.trim().isEmpty()) {
                    Request authenticatedRequest = originalRequest.newBuilder()
                            .header("Authorization", "Bearer " + token)
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json")
                            .build();
                    return chain.proceed(authenticatedRequest);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to retrieve Firebase ID Token", e);
            }
        }

        return chain.proceed(originalRequest);
    }
}
