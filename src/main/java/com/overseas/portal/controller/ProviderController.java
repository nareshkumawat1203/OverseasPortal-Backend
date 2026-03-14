package com.overseas.portal.controller;

import com.overseas.portal.dto.ProviderDto;
import com.overseas.portal.entity.User;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Provider", description = "Provider profile and directory")
public class ProviderController {

    private final ProviderService providerService;

    // ── Public endpoints ──────────────────────────────────────────────────────

    @GetMapping("/providers/public")
    @Operation(summary = "Browse and search providers (public)")
    public ResponseEntity<Page<ProviderDto>> searchProviders(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String providerType,
            @RequestParam(required = false) Boolean verified,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(providerService.searchProviders(country, providerType, verified, pageable));
    }

    @GetMapping("/providers/public/{profileId}")
    @Operation(summary = "Get a provider's public profile")
    public ResponseEntity<ProviderDto> getPublicProfile(@PathVariable Long profileId) {
        return ResponseEntity.ok(providerService.getPublicProfile(profileId));
    }

    // ── Authenticated provider endpoints ─────────────────────────────────────

    @GetMapping("/provider/profile")
    @Operation(summary = "Get the authenticated provider's profile")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ProviderDto> getMyProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(providerService.getProfile(user.getId()));
    }

    @PutMapping("/provider/profile")
    @Operation(summary = "Update the authenticated provider's profile")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ProviderDto> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody ProviderDto dto) {
        return ResponseEntity.ok(providerService.updateProfile(user.getId(), dto));
    }
}
