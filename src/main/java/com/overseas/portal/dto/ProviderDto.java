package com.overseas.portal.dto;

import com.overseas.portal.enums.ProviderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderDto {
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
    private String companyName;
    private String companyRegistrationNumber;
    private ProviderType providerType;
    private String website;
    private String description;
    private String country;
    private String city;
    private String address;
    private String logoUrl;
    private Integer yearsOfExperience;
    private boolean verified;
    private Double rating;
    private Integer totalReviews;
}
