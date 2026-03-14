package com.overseas.portal.controller;

import com.overseas.portal.entity.ServiceCategory;
import com.overseas.portal.entity.ServiceListing;
import com.overseas.portal.entity.User;
import com.overseas.portal.exception.ResourceNotFoundException;
import com.overseas.portal.exception.UnauthorizedException;
import com.overseas.portal.repository.ProviderProfileRepository;
import com.overseas.portal.repository.ServiceCategoryRepository;
import com.overseas.portal.repository.ServiceListingRepository;
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

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@Tag(name = "Services", description = "Service listings and categories")
public class ServiceController {

    private final ServiceListingRepository serviceListingRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final ProviderProfileRepository providerProfileRepository;

    // ── Public ────────────────────────────────────────────────────────────────

    @GetMapping("/categories")
    @Operation(summary = "List all active service categories (public)")
    public ResponseEntity<List<ServiceCategory>> getCategories() {
        return ResponseEntity.ok(serviceCategoryRepository.findByActiveTrue());
    }

    @GetMapping
    @Operation(summary = "Search service listings (public)")
    public ResponseEntity<Page<ServiceListing>> searchServices(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(
            serviceListingRepository.searchServices(categoryId, keyword, minPrice, maxPrice, pageable)
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a service listing by ID (public)")
    public ResponseEntity<ServiceListing> getById(@PathVariable Long id) {
        ServiceListing service = serviceListingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Service", id));
        return ResponseEntity.ok(service);
    }

    // ── Provider-only ─────────────────────────────────────────────────────────

    @GetMapping("/my-listings")
    @Operation(summary = "Get authenticated provider's listings")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<List<ServiceListing>> getMyListings(@AuthenticationPrincipal User user) {
        var profile = providerProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));
        return ResponseEntity.ok(serviceListingRepository.findByProviderIdAndActiveTrue(profile.getId()));
    }

    @PostMapping
    @Operation(summary = "Create a service listing")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ServiceListing> create(
            @AuthenticationPrincipal User user,
            @RequestBody ServiceListing listing) {
        var profile = providerProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));
        listing.setProvider(profile);
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceListingRepository.save(listing));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a service listing")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ServiceListing> update(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestBody ServiceListing updated) {
        ServiceListing existing = serviceListingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Service", id));

        if (!existing.getProvider().getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You do not own this listing");
        }

        if (updated.getTitle() != null) existing.setTitle(updated.getTitle());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        if (updated.getPrice() != null) existing.setPrice(updated.getPrice());
        if (updated.getFeatures() != null) existing.setFeatures(updated.getFeatures());
        if (updated.getDurationDays() != null) existing.setDurationDays(updated.getDurationDays());

        return ResponseEntity.ok(serviceListingRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate a service listing")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        ServiceListing listing = serviceListingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Service", id));

        if (!listing.getProvider().getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You do not own this listing");
        }

        listing.setActive(false);
        serviceListingRepository.save(listing);
        return ResponseEntity.noContent().build();
    }
}
