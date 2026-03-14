package com.overseas.portal.entity;

import com.overseas.portal.enums.SubscriptionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscription_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private SubscriptionType type;

    @Column(nullable = false)
    private Double price;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(name = "max_applications")
    @Builder.Default
    private Integer maxApplications = 3;  // -1 = unlimited

    @Column(name = "max_bookings")
    @Builder.Default
    private Integer maxBookings = 2;  // -1 = unlimited

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "active")
    @Builder.Default
    private boolean active = true;
}
