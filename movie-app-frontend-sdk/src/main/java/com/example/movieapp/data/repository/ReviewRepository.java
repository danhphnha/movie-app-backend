package com.example.movieapp.data.repository;

import com.example.movieapp.core.network.ApiClient;
import com.example.movieapp.core.network.ApiResponse;
import com.example.movieapp.data.remote.api.ReviewApiService;
import com.example.movieapp.data.remote.request.CreateReviewRequest;
import com.example.movieapp.data.remote.request.ReplyReviewRequest;
import com.example.movieapp.data.remote.request.UpdateReplyRequest;
import com.example.movieapp.data.remote.response.CommentDto;

import java.util.List;
import java.util.Map;

import retrofit2.Callback;

/**
 * Repository pattern for Review and Comment operations
 */
public class ReviewRepository {

    private final ReviewApiService apiService;

    public ReviewRepository() {
        this.apiService = ApiClient.getReviewApi();
    }

    public void getReviewsBySlug(String slug, Callback<ApiResponse<List<CommentDto>>> callback) {
        apiService.getReviewsBySlug(slug).enqueue(callback);
    }

    public void getAverageRating(String slug, Callback<ApiResponse<Map<String, Object>>> callback) {
        apiService.getAverageRating(slug).enqueue(callback);
    }

    public void submitReview(String slug, String movieTitle, double rating, String comment, Callback<ApiResponse<CommentDto>> callback) {
        CreateReviewRequest request = new CreateReviewRequest(slug, movieTitle, rating, comment);
        apiService.submitReview(request).enqueue(callback);
    }

    public void getMyReview(String slug, Callback<ApiResponse<CommentDto>> callback) {
        apiService.getMyReview(slug).enqueue(callback);
    }

    public void deleteReview(String reviewId, Callback<ApiResponse<Void>> callback) {
        apiService.deleteReview(reviewId).enqueue(callback);
    }

    public void replyToComment(String parentId, String comment, String slug, String movieTitle, Callback<ApiResponse<CommentDto>> callback) {
        ReplyReviewRequest request = new ReplyReviewRequest(parentId, comment, slug, movieTitle);
        apiService.replyToComment(request).enqueue(callback);
    }

    public void getRepliesForComment(String commentId, Callback<ApiResponse<List<CommentDto>>> callback) {
        apiService.getRepliesForComment(commentId).enqueue(callback);
    }

    public void updateMyReply(String reviewId, String newComment, Callback<ApiResponse<Void>> callback) {
        UpdateReplyRequest request = new UpdateReplyRequest(newComment);
        apiService.updateMyReply(reviewId, request).enqueue(callback);
    }
}
