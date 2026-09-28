package com.example.movieapp.data.repository;

import com.example.movieapp.core.network.ApiClient;
import com.example.movieapp.core.network.ApiResponse;
import com.example.movieapp.data.remote.api.CollectionApiService;
import com.example.movieapp.data.remote.request.CreateCollectionRequest;
import com.example.movieapp.data.remote.response.CollectionDto;
import com.example.movieapp.data.remote.response.MovieDto;

import java.util.List;

import retrofit2.Callback;

/**
 * Repository pattern for Collection operations
 */
public class CollectionRepository {

    private final CollectionApiService apiService;

    public CollectionRepository() {
        this.apiService = ApiClient.getCollectionApi();
    }

    public void getMyCollections(Callback<ApiResponse<List<CollectionDto>>> callback) {
        apiService.getMyCollections().enqueue(callback);
    }

    public void getFilmsByCollectionId(String collectionId, Callback<ApiResponse<List<MovieDto>>> callback) {
        apiService.getFilmsByCollectionId(collectionId).enqueue(callback);
    }

    public void createCollection(String collectionName, Callback<ApiResponse<CollectionDto>> callback) {
        apiService.createCollection(new CreateCollectionRequest(collectionName)).enqueue(callback);
    }

    public void addFilmToCollection(String collectionId, MovieDto movie, Callback<ApiResponse<String>> callback) {
        apiService.addFilmToCollection(collectionId, movie).enqueue(callback);
    }

    public void checkFilmInCollection(String collectionId, String slug, Callback<ApiResponse<Boolean>> callback) {
        apiService.checkFilmInCollection(collectionId, slug).enqueue(callback);
    }

    public void checkFilmInUserCollections(String slug, Callback<ApiResponse<Boolean>> callback) {
        apiService.checkFilmInUserCollections(slug).enqueue(callback);
    }

    public void deleteCollection(String collectionId, Callback<ApiResponse<String>> callback) {
        apiService.deleteCollection(collectionId).enqueue(callback);
    }

    public void deleteFilmFromCollection(String collectionId, String slug, Callback<ApiResponse<String>> callback) {
        apiService.deleteFilmFromCollection(collectionId, slug).enqueue(callback);
    }
}
