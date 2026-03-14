package com.overseas.portal.service;

import com.overseas.portal.entity.Document;
import com.overseas.portal.entity.User;
import com.overseas.portal.exception.ResourceNotFoundException;
import com.overseas.portal.repository.DocumentRepository;
import com.overseas.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Transactional
    public Document storeDocument(Long userId, MultipartFile file, String documentType, Long applicationId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null
            ? file.getOriginalFilename() : "file");
        String extension = getExtension(originalFilename);
        String storedFilename = UUID.randomUUID() + "." + extension;

        try {
            Path uploadPath = Paths.get(uploadDir, "users", userId.toString()).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            Path targetLocation = uploadPath.resolve(storedFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/uploads/users/" + userId + "/" + storedFilename;

            Document document = Document.builder()
                .user(user)
                .documentType(documentType)
                .fileName(originalFilename)
                .fileUrl(fileUrl)
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .build();

            if (applicationId != null) {
                document.setApplication(
                    documentRepository.findById(applicationId)
                        .map(d -> d.getApplication())
                        .orElse(null)
                );
            }

            Document saved = documentRepository.save(document);
            log.info("Stored file {} for user {}", storedFilename, userId);
            return saved;

        } catch (IOException e) {
            log.error("Failed to store file: {}", e.getMessage());
            throw new RuntimeException("Failed to store file: " + originalFilename, e);
        }
    }

    public List<Document> getUserDocuments(Long userId) {
        return documentRepository.findByUserId(userId);
    }

    public List<Document> getApplicationDocuments(Long applicationId) {
        return documentRepository.findByApplicationId(applicationId);
    }

    @Transactional
    public void deleteDocument(Long documentId, Long userId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new ResourceNotFoundException("Document", documentId));

        if (!document.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Document", documentId);
        }

        try {
            Path filePath = Paths.get(uploadDir).resolve(document.getFileUrl().replaceFirst("^/uploads/", ""));
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("Could not delete physical file: {}", e.getMessage());
        }

        documentRepository.delete(document);
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex >= 0 ? filename.substring(dotIndex + 1).toLowerCase() : "bin";
    }
}
