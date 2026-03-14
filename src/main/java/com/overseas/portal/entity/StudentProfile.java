package com.overseas.portal.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String nationality;

    @Column(name = "passport_number")
    private String passportNumber;

    @Column(name = "passport_expiry")
    private LocalDate passportExpiry;

    @Column(name = "current_country")
    private String currentCountry;

    @Column(name = "current_city")
    private String currentCity;

    @Column(name = "target_country")
    private String targetCountry;

    @Column(name = "target_degree")
    private String targetDegree;  // Bachelor's, Master's, PhD, etc.

    @Column(name = "field_of_study")
    private String fieldOfStudy;

    @Column(name = "intended_start_date")
    private String intendedStartDate;  // e.g., "Fall 2025"

    @Column(name = "english_test")
    private String englishTest;  // IELTS, TOEFL, Duolingo, etc.

    @Column(name = "english_score")
    private Double englishScore;

    @Column(name = "gpa")
    private Double gpa;

    @Column(name = "work_experience_years")
    private Integer workExperienceYears;

    @Column(name = "budget_min")
    private Double budgetMin;

    @Column(name = "budget_max")
    private Double budgetMax;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @Column(name = "profile_completion")
    @Builder.Default
    private Integer profileCompletion = 0;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
