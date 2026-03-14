package com.overseas.portal.repository;

import com.overseas.portal.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    @Query("SELECT us FROM UserSubscription us WHERE us.user.id = :userId " +
           "AND us.active = true AND us.endDate > :now ORDER BY us.endDate DESC")
    Optional<UserSubscription> findActiveSubscription(@Param("userId") Long userId,
                                                      @Param("now") LocalDateTime now);

    Optional<UserSubscription> findTopByUserIdAndActiveTrueOrderByEndDateDesc(Long userId);
}
