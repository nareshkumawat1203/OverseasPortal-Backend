package com.overseas.portal.repository;

import com.overseas.portal.entity.SubscriptionPlan;
import com.overseas.portal.enums.SubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    Optional<SubscriptionPlan> findByType(SubscriptionType type);
    List<SubscriptionPlan> findByActiveTrue();
}
