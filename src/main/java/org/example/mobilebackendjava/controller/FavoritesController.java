package org.example.mobilebackendjava.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.mobilebackendjava.dto.ApiResponse;
import org.example.mobilebackendjava.dto.CollectionCreateRequest;
import org.example.mobilebackendjava.model.CollectionFilm;
import org.example.mobilebackendjava.model.Movie;
import org.example.mobilebackendjava.security.SecurityUtils;
import org.example.mobilebackendjava.service.CollectionFilmService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class FavoritesController {

    private final CollectionFilmService collectionFilmService;

    public FavoritesController(CollectionFilmService collectionFilmService) {
        this.collectionFilmService = collectionFilmService;
    }

    @GetMapping("/getAllCollections")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<CollectionFilm>> getAllCollections() {
        return ApiResponse.ok(collectionFilmService.getAllCollections());
    }

    // Get collections of the currently authenticated user
    @GetMapping("/getCollectionsByUser")
    public ApiResponse<List<CollectionFilm>> getMyCollections() {
        String currentUserId = SecurityUtils.getCurrentUserId();
        return ApiResponse.ok(collectionFilmService.getCollectionsByUserId(currentUserId));
    }

    @GetMapping("/getFilmsByCollectionId")
    public ApiResponse<List<Movie>> getFilmsByCollectionId(@RequestParam String collectionId) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        return ApiResponse.ok(collectionFilmService.getFilmsByCollectionId(collectionId, currentUserId));
    }

    @PostMapping("/addFilmToCollection")
    public ApiResponse<String> addFilmToCollection(
            @RequestParam String collectionId,
            @RequestBody Movie film) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        collectionFilmService.addFilmToCollection(collectionId, currentUserId, film);
        return ApiResponse.ok("Phim đã được thêm vào bộ sưu tập.", null);
    }

    @GetMapping("/checkFilmInCollection")
    public ApiResponse<Boolean> isFilmInCollection(
            @RequestParam String collectionId,
            @RequestParam String slug) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        boolean exists = collectionFilmService.isFilmInCollection(collectionId, currentUserId, slug);
        return ApiResponse.ok(exists);
    }

    @GetMapping("/checkFilmInUserCollections")
    public ApiResponse<Boolean> isFilmInUserCollections(@RequestParam String slug) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        boolean exists = collectionFilmService.isFilmInAnyCollectionOfUser(currentUserId, slug);
        return ApiResponse.ok(exists);
    }

    @PostMapping("/addCollection")
    public ApiResponse<CollectionFilm> addCollection(@Valid @RequestBody CollectionCreateRequest request) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        CollectionFilm created = collectionFilmService.addCollection(request.getCollectionName(), currentUserId);
        return ApiResponse.ok("Thêm bộ sưu tập thành công", created);
    }

    @DeleteMapping("/deleteCollection")
    public ApiResponse<String> deleteCollection(@RequestParam String collectionId) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        collectionFilmService.deleteCollection(collectionId, currentUserId);
        return ApiResponse.ok("Xóa bộ sưu tập thành công", null);
    }

    @DeleteMapping("/deleteFilmFromCollection")
    public ApiResponse<String> deleteFilmFromCollection(
            @RequestParam String collectionId,
            @RequestParam String slug) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        collectionFilmService.deleteFilmFromCollection(collectionId, currentUserId, slug);
        return ApiResponse.ok("Xóa phim khỏi bộ sưu tập thành công", null);
    }
}
