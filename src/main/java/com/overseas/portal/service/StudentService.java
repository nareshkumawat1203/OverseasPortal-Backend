package com.overseas.portal.service;

import com.overseas.portal.dto.StudentDto;
import com.overseas.portal.entity.StudentProfile;
import com.overseas.portal.entity.User;
import com.overseas.portal.exception.ResourceNotFoundException;
import com.overseas.portal.exception.UnauthorizedException;
import com.overseas.portal.repository.StudentProfileRepository;
import com.overseas.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentDto getProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user: " + userId));
        return toDto(user, profile);
    }

    @Transactional
    public StudentDto updateProfile(Long userId, StudentDto dto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        StudentProfile profile = studentProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        // Update user fields
        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());

        // Update profile fields
        if (dto.getDateOfBirth() != null) profile.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getNationality() != null) profile.setNationality(dto.getNationality());
        if (dto.getPassportNumber() != null) profile.setPassportNumber(dto.getPassportNumber());
        if (dto.getPassportExpiry() != null) profile.setPassportExpiry(dto.getPassportExpiry());
        if (dto.getCurrentCountry() != null) profile.setCurrentCountry(dto.getCurrentCountry());
        if (dto.getCurrentCity() != null) profile.setCurrentCity(dto.getCurrentCity());
        if (dto.getTargetCountry() != null) profile.setTargetCountry(dto.getTargetCountry());
        if (dto.getTargetDegree() != null) profile.setTargetDegree(dto.getTargetDegree());
        if (dto.getFieldOfStudy() != null) profile.setFieldOfStudy(dto.getFieldOfStudy());
        if (dto.getIntendedStartDate() != null) profile.setIntendedStartDate(dto.getIntendedStartDate());
        if (dto.getEnglishTest() != null) profile.setEnglishTest(dto.getEnglishTest());
        if (dto.getEnglishScore() != null) profile.setEnglishScore(dto.getEnglishScore());
        if (dto.getGpa() != null) profile.setGpa(dto.getGpa());
        if (dto.getWorkExperienceYears() != null) profile.setWorkExperienceYears(dto.getWorkExperienceYears());
        if (dto.getBudgetMin() != null) profile.setBudgetMin(dto.getBudgetMin());
        if (dto.getBudgetMax() != null) profile.setBudgetMax(dto.getBudgetMax());
        if (dto.getBio() != null) profile.setBio(dto.getBio());
        if (dto.getLinkedinUrl() != null) profile.setLinkedinUrl(dto.getLinkedinUrl());

        profile.setProfileCompletion(calculateCompletion(profile));

        userRepository.save(user);
        studentProfileRepository.save(profile);

        return toDto(user, profile);
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new UnauthorizedException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public Page<StudentDto> getAllStudents(Pageable pageable) {
        return (Page<StudentDto>) userRepository.findAll(pageable).map(user -> {
            StudentProfile profile = studentProfileRepository.findByUserId(user.getId()).orElse(null);
            return profile != null ? toDto(user, profile) : null;
        }).filter(dto -> dto != null);
    }

    private int calculateCompletion(StudentProfile p) {
        int score = 0;
        if (p.getDateOfBirth() != null) score += 10;
        if (p.getNationality() != null) score += 10;
        if (p.getPassportNumber() != null) score += 10;
        if (p.getTargetCountry() != null) score += 10;
        if (p.getTargetDegree() != null) score += 10;
        if (p.getFieldOfStudy() != null) score += 10;
        if (p.getEnglishTest() != null && p.getEnglishScore() != null) score += 15;
        if (p.getGpa() != null) score += 10;
        if (p.getBio() != null) score += 10;
        if (p.getBudgetMin() != null) score += 5;
        return score;
    }

    private StudentDto toDto(User user, StudentProfile profile) {
        return StudentDto.builder()
            .userId(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .phone(user.getPhone())
            .profilePicture(user.getProfilePicture())
            .emailVerified(user.isEmailVerified())
            .active(user.isActive())
            .profileId(profile.getId())
            .dateOfBirth(profile.getDateOfBirth())
            .nationality(profile.getNationality())
            .passportNumber(profile.getPassportNumber())
            .passportExpiry(profile.getPassportExpiry())
            .currentCountry(profile.getCurrentCountry())
            .currentCity(profile.getCurrentCity())
            .targetCountry(profile.getTargetCountry())
            .targetDegree(profile.getTargetDegree())
            .fieldOfStudy(profile.getFieldOfStudy())
            .intendedStartDate(profile.getIntendedStartDate())
            .englishTest(profile.getEnglishTest())
            .englishScore(profile.getEnglishScore())
            .gpa(profile.getGpa())
            .workExperienceYears(profile.getWorkExperienceYears())
            .budgetMin(profile.getBudgetMin())
            .budgetMax(profile.getBudgetMax())
            .bio(profile.getBio())
            .linkedinUrl(profile.getLinkedinUrl())
            .profileCompletion(profile.getProfileCompletion())
            .build();
    }
}
