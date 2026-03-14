package com.overseas.portal.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    @Column(nullable = false)
    private String name;

    @Column(name = "degree_level")
    private String degreeLevel;  // Bachelor, Master, PhD, Diploma, Certificate

    @Column(name = "field_of_study")
    private String fieldOfStudy;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_years")
    private Double durationYears;

    @Column(name = "tuition_fee")
    private Double tuitionFee;

    @Column(name = "fee_currency")
    @Builder.Default
    private String feeCurrency = "USD";

    @Column(name = "intake_months")
    private String intakeMonths;  // e.g., "January, September"

    @Column(name = "application_deadline")
    private String applicationDeadline;

    @Column(name = "minimum_gpa")
    private Double minimumGpa;

    @Column(name = "minimum_ielts")
    private Double minimumIelts;

    @Column(name = "minimum_toefl")
    private Integer minimumToefl;

    @Column(name = "work_permit_eligible")
    @Builder.Default
    private boolean workPermitEligible = true;

    @Column(name = "scholarship_available")
    @Builder.Default
    private boolean scholarshipAvailable = false;

    @Column(name = "active")
    @Builder.Default
    private boolean active = true;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
