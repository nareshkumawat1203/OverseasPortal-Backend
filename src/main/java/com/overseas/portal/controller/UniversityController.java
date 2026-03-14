package com.overseas.portal.controller;

import com.overseas.portal.dto.UniversityDto;
import com.overseas.portal.repository.CourseRepository;
import com.overseas.portal.service.UniversityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/universities")
@RequiredArgsConstructor
@Tag(name = "Universities", description = "University directory and search")
public class UniversityController {

    private final UniversityService universityService;
    private final CourseRepository courseRepository;

    @GetMapping
    @Operation(summary = "Search universities (public)")
    public ResponseEntity<Page<UniversityDto>> search(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 12, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(universityService.searchUniversities(country, keyword, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get university details (public)")
    public ResponseEntity<UniversityDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(universityService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create a university (admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UniversityDto> create(@Valid @RequestBody UniversityDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(universityService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a university (admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UniversityDto> update(
            @PathVariable Long id,
            @RequestBody UniversityDto dto) {
        return ResponseEntity.ok(universityService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft-delete a university (admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        universityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
