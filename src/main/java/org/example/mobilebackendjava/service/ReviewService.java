package org.example.mobilebackendjava.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import lombok.extern.slf4j.Slf4j;
import org.example.mobilebackendjava.dto.CreateReviewRequest;
import org.example.mobilebackendjava.dto.ReplyRequest;
import org.example.mobilebackendjava.dto.UpdateReplyRequest;
import org.example.mobilebackendjava.exception.AppException;
import org.example.mobilebackendjava.exception.ForbiddenException;
import org.example.mobilebackendjava.exception.ResourceNotFoundException;
import org.example.mobilebackendjava.model.Comment;
import org.example.mobilebackendjava.security.UserPrincipal;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class ReviewService {

    private static final String COLLECTION_NAME = "reviews";
    private static final String USERS_COLLECTION_NAME = "users";
    private final Firestore db;

    public ReviewService(Firestore db) {
        this.db = db;
    }

    public List<Comment> getReviewsBySlug(String slug) {
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("slug", slug)
                    .whereEqualTo("hidden", false)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            List<Comment> reviews = new ArrayList<>();
            for (DocumentSnapshot doc : documents) {
                Comment comment = doc.toObject(Comment.class);
                if (comment != null) {
                    comment.setId(doc.getId());
                    reviews.add(comment);
                }
            }
            reviews.sort((a, b) -> {
                if (a.getTimestamp() == null || b.getTimestamp() == null) return 0;
                return b.getTimestamp().compareTo(a.getTimestamp());
            });
            return reviews;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error fetching reviews for slug: {}", slug, e);
            throw new AppException("Failed to retrieve reviews", e);
        }
    }

    public Map<String, Object> getAverageRating(String slug) {
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("slug", slug)
                    .whereEqualTo("hidden", false)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            Map<String, Object> result = new HashMap<>();
            result.put("average", 0.0);
            result.put("count", 0);

            if (documents.isEmpty()) {
                return result;
            }

            double total = 0;
            int count = 0;
            for (DocumentSnapshot doc : documents) {
                Object ratingObj = doc.get("rating");
                if (ratingObj instanceof Number) {
                    double rating = ((Number) ratingObj).doubleValue();
                    if (rating > 0) {
                        total += rating;
                        count++;
                    }
                }
            }

            if (count > 0) {
                double average = total / count;
                result.put("average", Math.round(average * 10.0) / 10.0);
                result.put("count", count);
            }
            return result;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error calculating average rating for slug: {}", slug, e);
            throw new AppException("Failed to calculate average rating", e);
        }
    }

    public Comment submitReview(UserPrincipal user, CreateReviewRequest req) {
        try {
            String username = resolveUsername(user.getUid(), user.getName());

            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("userId", user.getUid())
                    .whereEqualTo("slug", req.getSlug())
                    .limit(1)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            Date now = new Date();

            if (!documents.isEmpty()) {
                String existingId = documents.get(0).getId();
                Map<String, Object> updates = new HashMap<>();
                updates.put("comment", req.getComment());
                updates.put("rating", req.getRating());
                updates.put("timestamp", now);
                updates.put("username", username);
                if (req.getMovieTitle() != null) {
                    updates.put("movieTitle", req.getMovieTitle());
                }

                db.collection(COLLECTION_NAME).document(existingId).update(updates).get();

                Comment comment = new Comment();
                comment.setId(existingId);
                comment.setUserId(user.getUid());
                comment.setUsername(username);
                comment.setSlug(req.getSlug());
                comment.setComment(req.getComment());
                comment.setRating(req.getRating());
                comment.setMovieTitle(req.getMovieTitle());
                comment.setTimestamp(now);
                log.info("Updated review {} for slug {} by user {}", existingId, req.getSlug(), user.getUid());
                return comment;
            } else {
                Comment comment = new Comment();
                comment.setUserId(user.getUid());
                comment.setUsername(username);
                comment.setSlug(req.getSlug());
                comment.setComment(req.getComment());
                comment.setRating(req.getRating());
                comment.setMovieTitle(req.getMovieTitle());
                comment.setTimestamp(now);
                comment.setHidden(false);

                ApiFuture<DocumentReference> result = db.collection(COLLECTION_NAME).add(comment);
                String id = result.get().getId();
                comment.setId(id);
                log.info("Created review {} for slug {} by user {}", id, req.getSlug(), user.getUid());
                return comment;
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error submitting review", e);
            throw new AppException("Failed to submit review", e);
        }
    }

    public Comment getUserReview(String userId, String slug) {
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("slug", slug)
                    .limit(1)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            if (documents.isEmpty()) {
                throw new ResourceNotFoundException("No review found for user and slug");
            }

            Comment comment = documents.get(0).toObject(Comment.class);
            if (comment != null) {
                comment.setId(documents.get(0).getId());
            }
            return comment;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error fetching user review", e);
            throw new AppException("Failed to get user review", e);
        }
    }

    public void deleteReview(String reviewId, String currentUserId, boolean isAdmin) {
        try {
            DocumentSnapshot doc = db.collection(COLLECTION_NAME).document(reviewId).get().get();
            if (!doc.exists()) {
                throw new ResourceNotFoundException("Review not found: " + reviewId);
            }

            String ownerId = doc.getString("userId");
            if (!isAdmin && (ownerId == null || !ownerId.equals(currentUserId))) {
                throw new ForbiddenException("You do not have permission to delete this review");
            }

            db.collection(COLLECTION_NAME).document(reviewId).delete().get();
            log.info("Deleted review ID: {} by user: {}", reviewId, currentUserId);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error deleting review: {}", reviewId, e);
            throw new AppException("Failed to delete review", e);
        }
    }

    public Comment addReply(UserPrincipal user, ReplyRequest req) {
        try {
            String username = resolveUsername(user.getUid(), user.getName());

            Comment reply = new Comment();
            reply.setParentId(req.getParentId());
            reply.setUserId(user.getUid());
            reply.setUsername(username);
            reply.setComment(req.getComment());
            reply.setSlug(req.getSlug());
            reply.setMovieTitle(req.getMovieTitle());
            reply.setTimestamp(new Date());
            reply.setHidden(false);

            ApiFuture<DocumentReference> result = db.collection(COLLECTION_NAME).add(reply);
            String id = result.get().getId();
            reply.setId(id);
            log.info("Added reply ID: {} for parentId: {} by user: {}", id, req.getParentId(), user.getUid());
            return reply;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error adding reply", e);
            throw new AppException("Failed to add reply", e);
        }
    }

    public List<Comment> getReplies(String commentId) {
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("parentId", commentId)
                    .whereEqualTo("hidden", false)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            List<Comment> replies = new ArrayList<>();
            for (DocumentSnapshot doc : documents) {
                Comment comment = doc.toObject(Comment.class);
                if (comment != null) {
                    comment.setId(doc.getId());
                    replies.add(comment);
                }
            }
            replies.sort((a, b) -> {
                if (a.getTimestamp() == null || b.getTimestamp() == null) return 0;
                return b.getTimestamp().compareTo(a.getTimestamp());
            });
            return replies;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error fetching replies for comment: {}", commentId, e);
            throw new AppException("Failed to retrieve replies", e);
        }
    }

    public void updateReply(String reviewId, String userId, UpdateReplyRequest req) {
        try {
            DocumentSnapshot doc = db.collection(COLLECTION_NAME).document(reviewId).get().get();
            if (!doc.exists()) {
                throw new ResourceNotFoundException("Reply not found: " + reviewId);
            }

            String ownerId = doc.getString("userId");
            if (ownerId == null || !ownerId.equals(userId)) {
                throw new ForbiddenException("You can only edit your own comment");
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("comment", req.getComment());
            updates.put("timestamp", new Date());

            db.collection(COLLECTION_NAME).document(reviewId).update(updates).get();
            log.info("Updated reply ID: {} by user: {}", reviewId, userId);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error updating reply: {}", reviewId, e);
            throw new AppException("Failed to update reply", e);
        }
    }

    public void setCommentVisibility(String reviewId, boolean hidden) {
        try {
            DocumentSnapshot doc = db.collection(COLLECTION_NAME).document(reviewId).get().get();
            if (!doc.exists()) {
                throw new ResourceNotFoundException("Review not found: " + reviewId);
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("hidden", hidden);
            db.collection(COLLECTION_NAME).document(reviewId).update(updates).get();
            log.info("Set comment ID {} hidden state to {}", reviewId, hidden);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error updating comment visibility", e);
            throw new AppException("Failed to update comment visibility", e);
        }
    }

    public List<Comment> getAllCommentsForAdmin() {
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            List<Comment> comments = new ArrayList<>();
            for (DocumentSnapshot doc : documents) {
                Comment comment = doc.toObject(Comment.class);
                if (comment != null) {
                    comment.setId(doc.getId());
                    comments.add(comment);
                }
            }
            comments.sort((a, b) -> {
                if (a.getTimestamp() == null || b.getTimestamp() == null) return 0;
                return b.getTimestamp().compareTo(a.getTimestamp());
            });
            return comments;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error fetching all comments for admin", e);
            throw new AppException("Failed to retrieve comments", e);
        }
    }

    private String resolveUsername(String userId, String tokenName) {
        if (tokenName != null && !tokenName.trim().isEmpty()) {
            return tokenName;
        }
        try {
            DocumentSnapshot userDoc = db.collection(USERS_COLLECTION_NAME).document(userId).get().get();
            if (userDoc.exists() && userDoc.getString("name") != null) {
                return userDoc.getString("name");
            }
        } catch (Exception e) {
            log.warn("Could not fetch username from users collection for userId {}", userId);
        }
        return "Người dùng";
    }
}
