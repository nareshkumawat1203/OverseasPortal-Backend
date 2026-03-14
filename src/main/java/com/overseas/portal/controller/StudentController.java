package com.overseas.portal.controller;

import com.overseas.portal.dto.StudentDto;
import com.overseas.portal.entity.User;
import com.overseas.portal.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@Tag(name = "Student", description = "Student profile management")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/profile")
    @Operation(summary = "Get the authenticated student's profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentDto> getMyProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(studentService.getProfile(user.getId()));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update the authenticated student's profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentDto> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody StudentDto dto) {
        return ResponseEntity.ok(studentService.updateProfile(user.getId(), dto));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Map<String, String>> changePassword(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> body) {
        studentService.changePassword(user.getId(), body.get("currentPassword"), body.get("newPassword"));
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
}
