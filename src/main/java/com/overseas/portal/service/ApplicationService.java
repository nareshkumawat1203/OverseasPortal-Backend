package com.overseas.portal.service;

import com.overseas.portal.dto.ApplicationDto;
import com.overseas.portal.entity.*;
import com.overseas.portal.enums.ApplicationStatus;
import com.overseas.portal.exception.ResourceNotFoundException;
import com.overseas.portal.exception.UnauthorizedException;
import com.overseas.portal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final StudentApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;
    private final CourseRepository courseRepository;
    private final NotificationService notificationService;

    public Page<ApplicationDto> getMyApplications(Long studentId, Pageable pageable) {
        return applicationRepository.findByStudentId(studentId, pageable).map(this::toDto);
    }

    public ApplicationDto getById(Long id, Long requestingUserId) {
        StudentApplication app = applicationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Application", id));

        if (!app.getStudent().getId().equals(requestingUserId)) {
            throw new UnauthorizedException("You can only view your own applications");
        }
        return toDto(app);
    }

    @Transactional
    public ApplicationDto create(Long studentId, ApplicationDto dto) {
        User student = userRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("User", studentId));
        University university = universityRepository.findById(dto.getUniversityId())
            .orElseThrow(() -> new ResourceNotFoundException("University", dto.getUniversityId()));

        Course course = null;
        if (dto.getCourseId() != null) {
            course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", dto.getCourseId()));
        }

        StudentApplication app = StudentApplication.builder()
            .student(student)
            .university(university)
            .course(course)
            .intakeYear(dto.getIntakeYear())
            .intakeSemester(dto.getIntakeSemester())
            .personalStatement(dto.getPersonalStatement())
            .notes(dto.getNotes())
            .status(ApplicationStatus.DRAFT)
            .build();

        StudentApplication saved = applicationRepository.save(app);

        notificationService.sendNotification(studentId, "APPLICATION_CREATED",
            "Application Created",
            "Your application to " + university.getName() + " has been created.");

        return toDto(saved);
    }

    @Transactional
    public ApplicationDto submit(Long applicationId, Long studentId) {
        StudentApplication app = getAndValidateOwnership(applicationId, studentId);

        if (app.getStatus() != ApplicationStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT applications can be submitted");
        }

        app.setStatus(ApplicationStatus.SUBMITTED);
        app.setSubmittedAt(LocalDateTime.now());
        applicationRepository.save(app);

        notificationService.sendNotification(studentId, "APPLICATION_SUBMITTED",
            "Application Submitted",
            "Your application to " + app.getUniversity().getName() + " has been submitted.");

        return toDto(app);
    }

    @Transactional
    public ApplicationDto updateStatus(Long applicationId, ApplicationStatus status, String adminNotes) {
        StudentApplication app = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));

        app.setStatus(status);
        if (adminNotes != null) app.setAdminNotes(adminNotes);
        if (status == ApplicationStatus.ACCEPTED || status == ApplicationStatus.REJECTED) {
            app.setDecisionAt(LocalDateTime.now());
        }

        applicationRepository.save(app);

        notificationService.sendNotification(app.getStudent().getId(), "APPLICATION_STATUS_UPDATE",
            "Application Status Updated",
            "Your application to " + app.getUniversity().getName() + " is now: " + status.name());

        return toDto(app);
    }

    @Transactional
    public ApplicationDto withdraw(Long applicationId, Long studentId) {
        StudentApplication app = getAndValidateOwnership(applicationId, studentId);

        if (app.getStatus() == ApplicationStatus.ENROLLED || app.getStatus() == ApplicationStatus.WITHDRAWN) {
            throw new IllegalStateException("Cannot withdraw an application in state: " + app.getStatus());
        }

        app.setStatus(ApplicationStatus.WITHDRAWN);
        applicationRepository.save(app);
        return toDto(app);
    }

    public Page<ApplicationDto> getAllApplications(ApplicationStatus status, Long universityId, Pageable pageable) {
        return applicationRepository.findWithFilters(status, universityId, pageable).map(this::toDto);
    }

    private StudentApplication getAndValidateOwnership(Long applicationId, Long studentId) {
        StudentApplication app = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));
        if (!app.getStudent().getId().equals(studentId)) {
            throw new UnauthorizedException("You can only manage your own applications");
        }
        return app;
    }

    private ApplicationDto toDto(StudentApplication app) {
        return ApplicationDto.builder()
            .id(app.getId())
            .universityId(app.getUniversity().getId())
            .universityName(app.getUniversity().getName())
            .universityCountry(app.getUniversity().getCountry())
            .courseId(app.getCourse() != null ? app.getCourse().getId() : null)
            .courseName(app.getCourse() != null ? app.getCourse().getName() : null)
            .studentId(app.getStudent().getId())
            .studentName(app.getStudent().getFullName())
            .status(app.getStatus())
            .intakeYear(app.getIntakeYear())
            .intakeSemester(app.getIntakeSemester())
            .personalStatement(app.getPersonalStatement())
            .notes(app.getNotes())
            .adminNotes(app.getAdminNotes())
            .offerLetterUrl(app.getOfferLetterUrl())
            .submittedAt(app.getSubmittedAt())
            .decisionAt(app.getDecisionAt())
            .createdAt(app.getCreatedAt())
            .updatedAt(app.getUpdatedAt())
            .build();
    }
}
