package com.overseas.portal.service;

import com.overseas.portal.dto.ProviderDto;
import com.overseas.portal.entity.ProviderProfile;
import com.overseas.portal.entity.User;
import com.overseas.portal.enums.ProviderType;
import com.overseas.portal.exception.ResourceNotFoundException;
import com.overseas.portal.repository.ProviderProfileRepository;
import com.overseas.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProviderService {

    private final UserRepository userRepository;
    private final ProviderProfileRepository providerProfileRepository;

    public ProviderDto getProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        ProviderProfile profile = providerProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));
        return toDto(user, profile);
    }

    public ProviderDto getPublicProfile(Long profileId) {
        ProviderProfile profile = providerProfileRepository.findById(profileId)
            .orElseThrow(() -> new ResourceNotFoundException("Provider", profileId));
        return toDto(profile.getUser(), profile);
    }

    @Transactional
    public ProviderDto updateProfile(Long userId, ProviderDto dto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        ProviderProfile profile = providerProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));

        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());

        if (dto.getCompanyName() != null) profile.setCompanyName(dto.getCompanyName());
        if (dto.getCompanyRegistrationNumber() != null)
            profile.setCompanyRegistrationNumber(dto.getCompanyRegistrationNumber());
        if (dto.getProviderType() != null) profile.setProviderType(dto.getProviderType());
        if (dto.getWebsite() != null) profile.setWebsite(dto.getWebsite());
        if (dto.getDescription() != null) profile.setDescription(dto.getDescription());
        if (dto.getCountry() != null) profile.setCountry(dto.getCountry());
        if (dto.getCity() != null) profile.setCity(dto.getCity());
        if (dto.getAddress() != null) profile.setAddress(dto.getAddress());
        if (dto.getYearsOfExperience() != null) profile.setYearsOfExperience(dto.getYearsOfExperience());

        userRepository.save(user);
        providerProfileRepository.save(profile);

        return toDto(user, profile);
    }

    public Page<ProviderDto> searchProviders(String country, String providerType, Boolean verified, Pageable pageable) {
        ProviderType type = providerType != null ? ProviderType.valueOf(providerType) : null;
        return providerProfileRepository.findWithFilters(country, type, verified, pageable)
            .map(p -> toDto(p.getUser(), p));
    }

    @Transactional
    public ProviderDto verifyProvider(Long profileId, boolean verified) {
        ProviderProfile profile = providerProfileRepository.findById(profileId)
            .orElseThrow(() -> new ResourceNotFoundException("Provider", profileId));
        profile.setVerified(verified);
        providerProfileRepository.save(profile);
        return toDto(profile.getUser(), profile);
    }

    private ProviderDto toDto(User user, ProviderProfile profile) {
        return ProviderDto.builder()
            .userId(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .phone(user.getPhone())
            .profilePicture(user.getProfilePicture())
            .emailVerified(user.isEmailVerified())
            .active(user.isActive())
            .profileId(profile.getId())
            .companyName(profile.getCompanyName())
            .companyRegistrationNumber(profile.getCompanyRegistrationNumber())
            .providerType(profile.getProviderType())
            .website(profile.getWebsite())
            .description(profile.getDescription())
            .country(profile.getCountry())
            .city(profile.getCity())
            .address(profile.getAddress())
            .logoUrl(profile.getLogoUrl())
            .yearsOfExperience(profile.getYearsOfExperience())
            .verified(profile.isVerified())
            .rating(profile.getRating())
            .totalReviews(profile.getTotalReviews())
            .build();
    }
}
