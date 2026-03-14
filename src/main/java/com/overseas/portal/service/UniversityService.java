package com.overseas.portal.service;

import com.overseas.portal.dto.UniversityDto;
import com.overseas.portal.entity.University;
import com.overseas.portal.exception.ResourceNotFoundException;
import com.overseas.portal.repository.CourseRepository;
import com.overseas.portal.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UniversityService {

    private final UniversityRepository universityRepository;
    private final CourseRepository courseRepository;

    public Page<UniversityDto> searchUniversities(String country, String keyword, Pageable pageable) {
        return universityRepository.searchUniversities(country, keyword, pageable).map(this::toDto);
    }

    public UniversityDto getById(Long id) {
        University university = universityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("University", id));
        return toDto(university);
    }

    @Transactional
    public UniversityDto create(UniversityDto dto) {
        University university = fromDto(dto);
        return toDto(universityRepository.save(university));
    }

    @Transactional
    public UniversityDto update(Long id, UniversityDto dto) {
        University university = universityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("University", id));

        if (dto.getName() != null) university.setName(dto.getName());
        if (dto.getCountry() != null) university.setCountry(dto.getCountry());
        if (dto.getCity() != null) university.setCity(dto.getCity());
        if (dto.getWebsite() != null) university.setWebsite(dto.getWebsite());
        if (dto.getDescription() != null) university.setDescription(dto.getDescription());
        if (dto.getEstablishedYear() != null) university.setEstablishedYear(dto.getEstablishedYear());
        if (dto.getWorldRanking() != null) university.setWorldRanking(dto.getWorldRanking());
        if (dto.getQsRanking() != null) university.setQsRanking(dto.getQsRanking());
        if (dto.getTuitionFeeMin() != null) university.setTuitionFeeMin(dto.getTuitionFeeMin());
        if (dto.getTuitionFeeMax() != null) university.setTuitionFeeMax(dto.getTuitionFeeMax());
        if (dto.getMinimumIelts() != null) university.setMinimumIelts(dto.getMinimumIelts());
        if (dto.getMinimumToefl() != null) university.setMinimumToefl(dto.getMinimumToefl());
        if (dto.getFacilities() != null) university.setFacilities(dto.getFacilities());
        university.setScholarshipAvailable(dto.isScholarshipAvailable());

        return toDto(universityRepository.save(university));
    }

    @Transactional
    public void delete(Long id) {
        University university = universityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("University", id));
        university.setActive(false);
        universityRepository.save(university);
    }

    private UniversityDto toDto(University u) {
        int totalCourses = courseRepository.findByUniversityIdAndActiveTrue(u.getId()).size();
        return UniversityDto.builder()
            .id(u.getId())
            .name(u.getName())
            .country(u.getCountry())
            .city(u.getCity())
            .website(u.getWebsite())
            .logoUrl(u.getLogoUrl())
            .bannerUrl(u.getBannerUrl())
            .description(u.getDescription())
            .establishedYear(u.getEstablishedYear())
            .worldRanking(u.getWorldRanking())
            .qsRanking(u.getQsRanking())
            .theRanking(u.getTheRanking())
            .acceptanceRate(u.getAcceptanceRate())
            .tuitionFeeMin(u.getTuitionFeeMin())
            .tuitionFeeMax(u.getTuitionFeeMax())
            .feeCurrency(u.getFeeCurrency())
            .applicationFee(u.getApplicationFee())
            .englishRequirement(u.getEnglishRequirement())
            .minimumIelts(u.getMinimumIelts())
            .minimumToefl(u.getMinimumToefl())
            .facilities(u.getFacilities())
            .scholarshipAvailable(u.isScholarshipAvailable())
            .active(u.isActive())
            .totalCourses(totalCourses)
            .build();
    }

    private University fromDto(UniversityDto dto) {
        return University.builder()
            .name(dto.getName())
            .country(dto.getCountry())
            .city(dto.getCity())
            .website(dto.getWebsite())
            .logoUrl(dto.getLogoUrl())
            .bannerUrl(dto.getBannerUrl())
            .description(dto.getDescription())
            .establishedYear(dto.getEstablishedYear())
            .worldRanking(dto.getWorldRanking())
            .qsRanking(dto.getQsRanking())
            .theRanking(dto.getTheRanking())
            .acceptanceRate(dto.getAcceptanceRate())
            .tuitionFeeMin(dto.getTuitionFeeMin())
            .tuitionFeeMax(dto.getTuitionFeeMax())
            .feeCurrency(dto.getFeeCurrency() != null ? dto.getFeeCurrency() : "USD")
            .applicationFee(dto.getApplicationFee())
            .englishRequirement(dto.getEnglishRequirement())
            .minimumIelts(dto.getMinimumIelts())
            .minimumToefl(dto.getMinimumToefl())
            .facilities(dto.getFacilities())
            .scholarshipAvailable(dto.isScholarshipAvailable())
            .build();
    }
}
