package com.overseas.portal.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "universities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class University {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String country;

    private String city;

    private String website;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "banner_url")
    private String bannerUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "established_year")
    private Integer establishedYear;

    @Column(name = "world_ranking")
    private Integer worldRanking;

    @Column(name = "qs_ranking")
    private Integer qsRanking;

    @Column(name = "the_ranking")
    private Integer theRanking;

    @Column(name = "acceptance_rate")
    private Double acceptanceRate;

    @Column(name = "tuition_fee_min")
    private Double tuitionFeeMin;

    @Column(name = "tuition_fee_max")
    private Double tuitionFeeMax;

    @Column(name = "fee_currency")
    @Builder.Default
    private String feeCurrency = "USD";

    @Column(name = "application_fee")
    private Double applicationFee;

    @Column(name = "english_requirement")
    private String englishRequirement;

    @Column(name = "minimum_ielts")
    private Double minimumIelts;

    @Column(name = "minimum_toefl")
    private Integer minimumToefl;

    @ElementCollection
    @CollectionTable(name = "university_facilities", joinColumns = @JoinColumn(name = "university_id"))
    @Column(name = "facility")
    private List<String> facilities;

    @Column(name = "scholarship_available")
    @Builder.Default
    private boolean scholarshipAvailable = false;

    @Column(name = "active")
    @Builder.Default
    private boolean active = true;

    @OneToMany(mappedBy = "university", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Course> courses;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
