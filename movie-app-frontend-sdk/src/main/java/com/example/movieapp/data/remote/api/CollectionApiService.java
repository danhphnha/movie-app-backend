package com.example.movieapp.data.remote.api;

import com.example.movieapp.core.network.ApiResponse;
import com.example.movieapp.data.remote.request.CreateCollectionRequest;
import com.example.movieapp.data.remote.response.CollectionDto;
import com.example.movieapp.data.remote.response.MovieDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface CollectionApiService {

    @GET("api/getCollectionsByUser")
    Call<ApiResponse<List<CollectionDto>>> getMyCollections();

    @GET("api/getFilmsByCollectionId")
    Call<ApiResponse<List<MovieDto>>> getFilmsByCollectionId(@Query("collectionId") String collectionId);

    @POST("api/addCollection")
    Call<ApiResponse<CollectionDto>> createCollection(@Body CreateCollectionRequest request);

    @POST("api/addFilmToCollection")
    Call<ApiResponse<String>> addFilmToCollection(
            @Query("collectionId") String collectionId,
            @Body MovieDto movie
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
}
