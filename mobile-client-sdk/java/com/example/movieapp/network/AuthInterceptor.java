package com.example.movieapp.network;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            try {
                Task<GetTokenResult> task = currentUser.getIdToken(false);
                GetTokenResult tokenResult = Tasks.await(task);
                String token = tokenResult.getToken();

                if (token != null && !token.trim().isEmpty()) {
                    Request authenticatedRequest = originalRequest.newBuilder()
                            .header("Authorization", "Bearer " + token)
                            .build();
                    return chain.proceed(authenticatedRequest);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        return chain.proceed(originalRequest);
    }
}
