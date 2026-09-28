package org.example.mobilebackendjava.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.mobilebackendjava.dto.ApiResponse;
import org.example.mobilebackendjava.dto.CreateReviewRequest;
import org.example.mobilebackendjava.dto.ReplyRequest;
import org.example.mobilebackendjava.dto.UpdateReplyRequest;
import org.example.mobilebackendjava.model.Comment;
import org.example.mobilebackendjava.security.SecurityUtils;
import org.example.mobilebackendjava.security.UserPrincipal;
import org.example.mobilebackendjava.service.ReviewService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Public: Lấy danh sách đánh giá công khai theo slug
    @GetMapping("/{slug}")
    public ApiResponse<List<Comment>> getReviews(@PathVariable String slug) {
        return ApiResponse.ok(reviewService.getReviewsBySlug(slug));
    }

    // Public: Tính trung bình rating theo slug
    @GetMapping("/{slug}/average")
    public ApiResponse<Map<String, Object>> getAverageRating(@PathVariable String slug) {
        return ApiResponse.ok(reviewService.getAverageRating(slug));
    }

    // Authenticated: Gửi đánh giá mới hoặc cập nhật đánh giá của chính mình
    @PostMapping
    public ApiResponse<Comment> submitReview(@Valid @RequestBody CreateReviewRequest req) {
        UserPrincipal user = SecurityUtils.getCurrentUser();
        Comment result = reviewService.submitReview(user, req);
        return ApiResponse.ok("Đã gửi đánh giá thành công", result);
    }

    // Authenticated: Lấy đánh giá của chính mình cho 1 bộ phim
    @GetMapping("/my-review/{slug}")
    public ApiResponse<Comment> getMyReview(@PathVariable String slug) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        return ApiResponse.ok(reviewService.getUserReview(currentUserId, slug));
    }

    // Authenticated: Xóa đánh giá (chủ sở hữu hoặc Admin)
    @DeleteMapping("/{reviewId}")
    public ApiResponse<Void> deleteReview(@PathVariable String reviewId) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        boolean isAdmin = SecurityUtils.isCurrentUserAdmin();
        reviewService.deleteReview(reviewId, currentUserId, isAdmin);
        return ApiResponse.ok("Đã xóa đánh giá", null);
    }

    // Authenticated: Thêm phản hồi cho bình luận
    @PostMapping("/reply")
    public ApiResponse<Comment> addReply(@Valid @RequestBody ReplyRequest req) {
        UserPrincipal user = SecurityUtils.getCurrentUser();
        Comment reply = reviewService.addReply(user, req);
        return ApiResponse.ok("Đã thêm phản hồi", reply);
    }

    // Public: Lấy danh sách phản hồi cho bình luận
    @GetMapping("/{commentId}/replies")
    public ApiResponse<List<Comment>> getReplies(@PathVariable String commentId) {
        return ApiResponse.ok(reviewService.getReplies(commentId));
    }

    // Authenticated: Cập nhật phản hồi của chính mình
    @PutMapping("/{reviewId}/reply")
    public ApiResponse<Void> updateReply(
            @PathVariable String reviewId,
            @Valid @RequestBody UpdateReplyRequest req) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        reviewService.updateReply(reviewId, currentUserId, req);
        return ApiResponse.ok("Đã cập nhật phản hồi", null);
    }

    // Admin: Ẩn bình luận
    @PutMapping("/{reviewId}/hide")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> hideComment(@PathVariable String reviewId) {
        reviewService.setCommentVisibility(reviewId, true);
        return ApiResponse.ok("Đã ẩn bình luận", null);
    }

    // Admin: Hiện bình luận
    @PutMapping("/{reviewId}/show")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> showComment(@PathVariable String reviewId) {
        reviewService.setCommentVisibility(reviewId, false);
        return ApiResponse.ok("Đã hiện bình luận", null);
    }

    // Admin: Lấy tất cả bình luận để quản trị
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Comment>> getAllComments() {
        return ApiResponse.ok(reviewService.getAllCommentsForAdmin());
    }
}