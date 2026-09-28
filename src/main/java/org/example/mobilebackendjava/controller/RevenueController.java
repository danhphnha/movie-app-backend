package org.example.mobilebackendjava.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.mobilebackendjava.dto.ApiResponse;
import org.example.mobilebackendjava.model.Payment;
import org.example.mobilebackendjava.service.RevenueService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/revenue")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class RevenueController {

    private final RevenueService revenueService;

    public RevenueController(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @GetMapping
    public ApiResponse<List<Payment>> getAllPayments() {
        return ApiResponse.ok(revenueService.getAllPayments());
    }

    @GetMapping("/total")
    public ApiResponse<Long> getTotalRevenue() {
        return ApiResponse.ok(revenueService.getTotalRevenue());
    }
}
