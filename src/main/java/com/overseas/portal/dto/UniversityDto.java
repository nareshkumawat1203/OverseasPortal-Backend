package com.overseas.portal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UniversityDto {
    private Long id;

    @NotBlank(message = "University name is required")
    private String name;

    @NotBlank(message = "Country is required")
    private String country;

    private String city;
    private String website;
    private String logoUrl;
    private String bannerUrl;
    private String description;
    private Integer establishedYear;
    private Integer worldRanking;
    private Integer qsRanking;
    private Integer theRanking;
    private Double acceptanceRate;
    private Double tuitionFeeMin;
    private Double tuitionFeeMax;
    private String feeCurrency;
    private Double applicationFee;
    private String englishRequirement;
    private Double minimumIelts;
    private Integer minimumToefl;
    private List<String> facilities;
    private boolean scholarshipAvailable;
    private boolean active;
    private Integer totalCourses;
}
