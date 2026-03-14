package com.overseas.portal.dto;

import com.overseas.portal.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDto {
    private Long id;

    @NotNull(message = "University ID is required")
    private Long universityId;

    private String universityName;
    private String universityCountry;
    private Long courseId;
    private String courseName;
    private Long studentId;
    private String studentName;
    private ApplicationStatus status;
    private Integer intakeYear;
    private String intakeSemester;
    private String personalStatement;
    private String notes;
    private String adminNotes;
    private String offerLetterUrl;
    private LocalDateTime submittedAt;
    private LocalDateTime decisionAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
