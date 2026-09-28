package org.example.mobilebackendjava.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import lombok.extern.slf4j.Slf4j;
import org.example.mobilebackendjava.exception.AppException;
import org.example.mobilebackendjava.exception.ForbiddenException;
import org.example.mobilebackendjava.exception.ResourceNotFoundException;
import org.example.mobilebackendjava.model.CollectionFilm;
import org.example.mobilebackendjava.model.Movie;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class CollectionFilmService {
    private static final String COLLECTIONS_PATH = "collections";
    private static final String LIST_FILM_PATH = "list_film";
    private final Firestore db;

    public CollectionFilmService(Firestore db) {
        this.db = db;
    }

    public List<CollectionFilm> getAllCollections() {
        try {
            CollectionReference collectionRef = db.collection(COLLECTIONS_PATH);
            ApiFuture<QuerySnapshot> querySnapshot = collectionRef.get();

            List<CollectionFilm> result = new ArrayList<>();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                CollectionFilm collection = document.toObject(CollectionFilm.class);
                if (collection != null) {
                    collection.setId(document.getId());
                    result.add(collection);
                }
            }
            return result;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error fetching all collections", e);
            throw new AppException("Failed to retrieve collections: " + e.getMessage(), e);
        }
    }

    public List<CollectionFilm> getCollectionsByUserId(String userId) {
        try {
            CollectionReference collectionReference = db.collection(COLLECTIONS_PATH);
            Query query = collectionReference.whereEqualTo("userId", userId);
            ApiFuture<QuerySnapshot> querySnapshot = query.get();

            List<CollectionFilm> result = new ArrayList<>();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                CollectionFilm collectionFilm = document.toObject(CollectionFilm.class);
                if (collectionFilm != null) {
                    collectionFilm.setId(document.getId());
                    result.add(collectionFilm);
                }
            }
            return result;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error fetching collections for user: {}", userId, e);
            throw new AppException("Failed to retrieve collections for user", e);
        }
    }

    public List<Movie> getFilmsByCollectionId(String collectionId, String userId) {
        verifyCollectionOwnership(collectionId, userId);

        CollectionReference filmsRef = db.collection(COLLECTIONS_PATH)
                .document(collectionId)
                .collection(LIST_FILM_PATH);

        try {
            ApiFuture<QuerySnapshot> querySnapshot = filmsRef.get();
            List<Movie> result = new ArrayList<>();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                Movie film = document.toObject(Movie.class);
                if (film != null) {
                    result.add(film);
                }
            }
            return result;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error fetching films in collection: {}", collectionId, e);
            throw new AppException("Failed to retrieve films from collection", e);
        }
    }

    public void addFilmToCollection(String collectionId, String userId, Movie film) {
        verifyCollectionOwnership(collectionId, userId);

        CollectionReference filmsRef = db.collection(COLLECTIONS_PATH)
                .document(collectionId)
                .collection(LIST_FILM_PATH);

        try {
            Query query = filmsRef.whereEqualTo("slug", film.getSlug()).limit(1);
            if (!query.get().get().isEmpty()) {
                throw new AppException("Phim đã tồn tại trong bộ sưu tập.");
            }

            filmsRef.add(film).get();
            log.info("Added film {} to collection {} for user {}", film.getSlug(), collectionId, userId);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error adding film to collection: {}", collectionId, e);
            throw new AppException("Failed to add film to collection", e);
        }
    }

    public boolean isFilmInCollection(String collectionId, String userId, String slug) {
        verifyCollectionOwnership(collectionId, userId);

        CollectionReference filmsRef = db.collection(COLLECTIONS_PATH)
                .document(collectionId)
                .collection(LIST_FILM_PATH);

        try {
            Query query = filmsRef.whereEqualTo("slug", slug).limit(1);
            return !query.get().get().isEmpty();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error checking film in collection", e);
            throw new AppException("Failed to check film in collection", e);
        }
    }

    public boolean isFilmInAnyCollectionOfUser(String userId, String slug) {
        try {
            List<CollectionFilm> userCollections = getCollectionsByUserId(userId);
            for (CollectionFilm col : userCollections) {
                CollectionReference filmsRef = db.collection(COLLECTIONS_PATH)
                        .document(col.getId())
                        .collection(LIST_FILM_PATH);

                Query query = filmsRef.whereEqualTo("slug", slug).limit(1);
                if (!query.get().get().isEmpty()) {
                    return true;
                }
            }
            return false;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error checking film in user collections", e);
            throw new AppException("Failed to check film in user collections", e);
        }
    }

    public boolean isCollectionExists(String collectionName, String userId) {
        try {
            CollectionReference collectionRef = db.collection(COLLECTIONS_PATH);
            Query query = collectionRef
                    .whereEqualTo("collection_name", collectionName)
                    .whereEqualTo("userId", userId)
                    .limit(1);

            return !query.get().get().isEmpty();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error checking collection existence", e);
            throw new AppException("Failed to check collection existence", e);
        }
    }

    public CollectionFilm addCollection(String collectionName, String userId) {
        if (isCollectionExists(collectionName, userId)) {
            throw new AppException("Bộ sưu tập đã tồn tại!");
        }

        CollectionFilm collectionFilm = new CollectionFilm();
        collectionFilm.setCollection_name(collectionName);
        collectionFilm.setUserId(userId);

        try {
            ApiFuture<DocumentReference> future = db.collection(COLLECTIONS_PATH).add(collectionFilm);
            String newId = future.get().getId();
            collectionFilm.setId(newId);
            log.info("Created collection {} for user {}", newId, userId);
            return collectionFilm;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error creating collection", e);
            throw new AppException("Failed to create collection", e);
        }
    }

    public void deleteCollection(String collectionId, String userId) {
        verifyCollectionOwnership(collectionId, userId);

        try {
            CollectionReference filmsRef = db.collection(COLLECTIONS_PATH)
                    .document(collectionId)
                    .collection(LIST_FILM_PATH);

            List<QueryDocumentSnapshot> filmDocs = filmsRef.get().get().getDocuments();
            for (QueryDocumentSnapshot doc : filmDocs) {
                filmsRef.document(doc.getId()).delete();
            }

            db.collection(COLLECTIONS_PATH).document(collectionId).delete().get();
            log.info("Deleted collection {} and its items for user {}", collectionId, userId);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error deleting collection: {}", collectionId, e);
            throw new AppException("Failed to delete collection", e);
        }
    }

    public void deleteFilmFromCollection(String collectionId, String userId, String slug) {
        verifyCollectionOwnership(collectionId, userId);

        try {
            CollectionReference filmsRef = db.collection(COLLECTIONS_PATH)
                    .document(collectionId)
                    .collection(LIST_FILM_PATH);

            Query query = filmsRef.whereEqualTo("slug", slug);
            List<QueryDocumentSnapshot> docs = query.get().get().getDocuments();

            if (docs.isEmpty()) {
                throw new ResourceNotFoundException("Không tìm thấy phim trong bộ sưu tập.");
            }

            for (QueryDocumentSnapshot doc : docs) {
                filmsRef.document(doc.getId()).delete();
            }
            log.info("Deleted film {} from collection {}", slug, collectionId);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error deleting film from collection: {}", collectionId, e);
            throw new AppException("Failed to delete film from collection", e);
        }
    }

    private void verifyCollectionOwnership(String collectionId, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return;
        }
        try {
            DocumentSnapshot doc = db.collection(COLLECTIONS_PATH).document(collectionId).get().get();
            if (!doc.exists()) {
                throw new ResourceNotFoundException("Collection not found: " + collectionId);
            }
            String ownerId = doc.getString("userId");
            if (ownerId != null && !ownerId.equals(userId)) {
                throw new ForbiddenException("You do not have access to this collection");
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new AppException("Error checking collection ownership", e);
        }
    }
}
