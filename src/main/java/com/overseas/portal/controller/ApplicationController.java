package com.overseas.portal.controller;

import com.overseas.portal.dto.ApplicationDto;
import com.overseas.portal.entity.User;
import com.overseas.portal.enums.ApplicationStatus;
import com.overseas.portal.service.ApplicationService;
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
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Applications", description = "Student university applications")
@SecurityRequirement(name = "bearerAuth")
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping("/my")
    @Operation(summary = "Get the authenticated student's applications")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Page<ApplicationDto>> getMyApplications(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(applicationService.getMyApplications(user.getId(), pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single application by ID")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApplicationDto> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.getById(id, user.getId()));
    }

    @PostMapping
    @Operation(summary = "Create a new application (DRAFT)")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApplicationDto> create(
            @AuthenticationPrincipal User user,
            @RequestBody ApplicationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(applicationService.create(user.getId(), dto));
    }

    @PatchMapping("/{id}/submit")
    @Operation(summary = "Submit a draft application")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApplicationDto> submit(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.submit(id, user.getId()));
    }

    @PatchMapping("/{id}/withdraw")
    @Operation(summary = "Withdraw an application")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApplicationDto> withdraw(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.withdraw(id, user.getId()));
    }

    // Admin-only: update status
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update application status (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApplicationDto> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        ApplicationStatus status = ApplicationStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(applicationService.updateStatus(id, status, body.get("adminNotes")));
    }

    @GetMapping
    @Operation(summary = "List all applications with optional filters (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ApplicationDto>> getAllApplications(
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) Long universityId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(applicationService.getAllApplications(status, universityId, pageable));
    }
}
