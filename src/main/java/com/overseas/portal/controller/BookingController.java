package com.overseas.portal.controller;

import com.overseas.portal.dto.BookingDto;
import com.overseas.portal.entity.User;
import com.overseas.portal.enums.BookingStatus;
import com.overseas.portal.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Service booking management")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/my")
    @Operation(summary = "Get the authenticated student's bookings")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Page<BookingDto>> getMyBookings(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) BookingStatus status,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(bookingService.getMyBookings(user.getId(), status, pageable));
    }

    @GetMapping("/provider")
    @Operation(summary = "Get all bookings for the authenticated provider")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<Page<BookingDto>> getProviderBookings(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(bookingService.getProviderBookings(user.getId(), pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single booking by ID")
    public ResponseEntity<BookingDto> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bookingService.getById(id, user.getId()));
    }

    @PostMapping
    @Operation(summary = "Create a new booking")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<BookingDto> create(
            @AuthenticationPrincipal User user,
            @RequestBody BookingDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.create(user.getId(), dto));
    }

    @PatchMapping("/{id}/confirm")
    @Operation(summary = "Provider confirms a pending booking")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<BookingDto> confirm(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bookingService.confirm(id, user.getId()));
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Provider marks a booking as complete")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<BookingDto> complete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bookingService.complete(id, user.getId()));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel a booking (student or provider)")
    public ResponseEntity<BookingDto> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(bookingService.cancel(id, user.getId(), body.get("reason")));
    }
}
