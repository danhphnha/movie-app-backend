package com.example.movieapp.data.remote.api;

import com.example.movieapp.core.network.ApiResponse;
import com.example.movieapp.data.remote.request.CreateReviewRequest;
import com.example.movieapp.data.remote.request.ReplyReviewRequest;
import com.example.movieapp.data.remote.request.UpdateReplyRequest;
import com.example.movieapp.data.remote.response.CommentDto;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.*;

public interface ReviewApiService {

    @GET("api/reviews/{slug}")
    Call<ApiResponse<List<CommentDto>>> getReviewsBySlug(@Path("slug") String slug);

    @GET("api/reviews/{slug}/average")
    Call<ApiResponse<Map<String, Object>>> getAverageRating(@Path("slug") String slug);

    @POST("api/reviews")
    Call<ApiResponse<CommentDto>> submitReview(@Body CreateReviewRequest request);

    @GET("api/reviews/my-review/{slug}")
    Call<ApiResponse<CommentDto>> getMyReview(@Path("slug") String slug);

    @DELETE("api/reviews/{reviewId}")
    Call<ApiResponse<Void>> deleteReview(@Path("reviewId") String reviewId);

    @POST("api/reviews/reply")
    Call<ApiResponse<CommentDto>> replyToComment(@Body ReplyReviewRequest request);

    @GET("api/reviews/{commentId}/replies")
    Call<ApiResponse<List<CommentDto>>> getRepliesForComment(@Path("commentId") String commentId);

    @PUT("api/reviews/{reviewId}/reply")
    Call<ApiResponse<Void>> updateMyReply(
            @Path("reviewId") String reviewId,
            @Body UpdateReplyRequest request
    );
}
