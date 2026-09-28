package com.example.movieapp.network;

import com.example.movieapp.network.model.*;
import org.example.mobilebackendjava.model.CollectionFilm;
import org.example.mobilebackendjava.model.Comment;
import org.example.mobilebackendjava.model.Movie;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // ==================== 1. BỘ SƯU TẬP (COLLECTIONS) ====================
    @GET("api/getCollectionsByUser")
    Call<ApiResponse<List<CollectionFilm>>> getMyCollections();

    @GET("api/getFilmsByCollectionId")
    Call<ApiResponse<List<Movie>>> getFilmsByCollectionId(@Query("collectionId") String collectionId);

    @POST("api/addCollection")
    Call<ApiResponse<CollectionFilm>> addCollection(@Body CollectionCreateRequest request);

    @POST("api/addFilmToCollection")
    Call<ApiResponse<String>> addFilmToCollection(
            @Query("collectionId") String collectionId,
            @Body Movie movie
    );

    @GET("api/checkFilmInCollection")
    Call<ApiResponse<Boolean>> checkFilmInCollection(
            @Query("collectionId") String collectionId,
            @Query("slug") String slug
    );

    @GET("api/checkFilmInUserCollections")
    Call<ApiResponse<Boolean>> checkFilmInUserCollections(@Query("slug") String slug);

    @DELETE("api/deleteCollection")
    Call<ApiResponse<String>> deleteCollection(@Query("collectionId") String collectionId);

    @DELETE("api/deleteFilmFromCollection")
    Call<ApiResponse<String>> deleteFilmFromCollection(
            @Query("collectionId") String collectionId,
            @Query("slug") String slug
    );


    // ==================== 2. ĐÁNH GIÁ & BÌNH LUẬN (REVIEWS) ====================
    @GET("api/reviews/{slug}")
    Call<ApiResponse<List<Comment>>> getReviews(@Path("slug") String slug);

    @GET("api/reviews/{slug}/average")
    Call<ApiResponse<Map<String, Object>>> getAverageRating(@Path("slug") String slug);

    @POST("api/reviews")
    Call<ApiResponse<Comment>> submitReview(@Body CreateReviewRequest request);

    @GET("api/reviews/my-review/{slug}")
    Call<ApiResponse<Comment>> getMyReview(@Path("slug") String slug);

    @DELETE("api/reviews/{reviewId}")
    Call<ApiResponse<Void>> deleteReview(@Path("reviewId") String reviewId);

    @POST("api/reviews/reply")
    Call<ApiResponse<Comment>> addReply(@Body ReplyRequest request);

    @GET("api/reviews/{commentId}/replies")
    Call<ApiResponse<List<Comment>>> getReplies(@Path("commentId") String commentId);

    @PUT("api/reviews/{reviewId}/reply")
    Call<ApiResponse<Void>> updateReply(
            @Path("reviewId") String reviewId,
            @Body UpdateReplyRequest request
    );


    // ==================== 3. THANH TOÁN VNPAY ====================
    @POST("api/pay")
    Call<ApiResponse<Map<String, String>>> createPayment(@Body PaymentInitRequest request);
}
