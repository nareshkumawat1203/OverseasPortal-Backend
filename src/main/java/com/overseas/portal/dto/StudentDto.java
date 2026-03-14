package com.overseas.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDto {
    // User fields
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String profilePicture;
    private boolean emailVerified;
    private boolean active;

    // Profile fields
    private Long profileId;
    private LocalDate dateOfBirth;
    private String nationality;
    private String passportNumber;
    private LocalDate passportExpiry;
    private String currentCountry;
    private String currentCity;
    private String targetCountry;
    private String targetDegree;
    private String fieldOfStudy;
    private String intendedStartDate;
    private String englishTest;
    private Double englishScore;
    private Double gpa;
    private Integer workExperienceYears;
    private Double budgetMin;
    private Double budgetMax;
    private String bio;
    private String linkedinUrl;
    private Integer profileCompletion;
}
