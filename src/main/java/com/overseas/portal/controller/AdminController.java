package com.overseas.portal.controller;

import com.overseas.portal.dto.ProviderDto;
import com.overseas.portal.entity.User;
import com.overseas.portal.service.AdminService;
import com.overseas.portal.service.ProviderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin-only management endpoints")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final ProviderService providerService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get platform-wide dashboard statistics")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/users")
    @Operation(summary = "List all registered users")
    public ResponseEntity<Page<User>> getAllUsers(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllUsers(pageable));
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Get a user by ID")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getUserById(userId));
    }

    @PatchMapping("/users/{userId}/toggle-active")
    @Operation(summary = "Enable or disable a user account")
    public ResponseEntity<User> toggleUserActive(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.toggleUserActive(userId));
    }

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "Soft-delete a user account")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/providers/{profileId}/verify")
    @Operation(summary = "Verify or unverify a provider profile")
    public ResponseEntity<ProviderDto> verifyProvider(
            @PathVariable Long profileId,
            @RequestBody Map<String, Boolean> body) {
        boolean verified = Boolean.TRUE.equals(body.get("verified"));
        return ResponseEntity.ok(providerService.verifyProvider(profileId, verified));
    }
}
