package com.overseas.portal.controller;

import com.overseas.portal.entity.Document;
import com.overseas.portal.entity.User;
import com.overseas.portal.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Tag(name = "File Upload", description = "Document upload and management")
@SecurityRequirement(name = "bearerAuth")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    @Operation(summary = "Upload a document")
    public ResponseEntity<Document> upload(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            @RequestParam(value = "applicationId", required = false) Long applicationId) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Document saved = fileStorageService.storeDocument(user.getId(), file, documentType, applicationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/my")
    @Operation(summary = "List all documents uploaded by the authenticated user")
    public ResponseEntity<List<Document>> getMyDocuments(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(fileStorageService.getUserDocuments(user.getId()));
    }

    @GetMapping("/application/{applicationId}")
    @Operation(summary = "List documents attached to a specific application")
    public ResponseEntity<List<Document>> getApplicationDocuments(@PathVariable Long applicationId) {
        return ResponseEntity.ok(fileStorageService.getApplicationDocuments(applicationId));
    }

    @DeleteMapping("/{documentId}")
    @Operation(summary = "Delete a document")
    public ResponseEntity<Map<String, String>> delete(
            @PathVariable Long documentId,
            @AuthenticationPrincipal User user) {
        fileStorageService.deleteDocument(documentId, user.getId());
        return ResponseEntity.ok(Map.of("message", "Document deleted successfully"));
    }
}
