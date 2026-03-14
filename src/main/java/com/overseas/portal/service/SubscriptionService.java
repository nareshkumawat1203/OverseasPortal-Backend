package com.overseas.portal.service;

import com.overseas.portal.entity.*;
import com.overseas.portal.enums.SubscriptionType;
import com.overseas.portal.exception.ResourceNotFoundException;
import com.overseas.portal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final UserSubscriptionRepository userSubscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public List<SubscriptionPlan> getAllPlans() {
        return subscriptionPlanRepository.findByActiveTrue();
    }

    public UserSubscription getActiveSubscription(Long userId) {
        return userSubscriptionRepository
            .findActiveSubscription(userId, LocalDateTime.now())
            .orElse(null);
    }

    public Map<String, Object> getSubscriptionStatus(Long userId) {
        UserSubscription sub = getActiveSubscription(userId);
        if (sub == null) {
            SubscriptionPlan freePlan = subscriptionPlanRepository.findByType(SubscriptionType.FREE)
                .orElseThrow(() -> new ResourceNotFoundException("Free plan not found"));
            return Map.of(
                "active", false,
                "plan", freePlan.getName(),
                "type", freePlan.getType(),
                "maxApplications", freePlan.getMaxApplications(),
                "maxBookings", freePlan.getMaxBookings()
            );
        }
        return Map.of(
            "active", !sub.isExpired(),
            "plan", sub.getPlan().getName(),
            "type", sub.getPlan().getType(),
            "startDate", sub.getStartDate(),
            "endDate", sub.getEndDate(),
            "maxApplications", sub.getPlan().getMaxApplications(),
            "maxBookings", sub.getPlan().getMaxBookings(),
            "autoRenew", sub.isAutoRenew()
        );
    }

    @Transactional
    public UserSubscription subscribe(Long userId, Long planId, String paymentReference) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
            .orElseThrow(() -> new ResourceNotFoundException("Subscription plan", planId));

        // Deactivate existing active subscription
        userSubscriptionRepository.findActiveSubscription(userId, LocalDateTime.now())
            .ifPresent(existing -> {
                existing.setActive(false);
                userSubscriptionRepository.save(existing);
            });

        LocalDateTime now = LocalDateTime.now();
        UserSubscription subscription = UserSubscription.builder()
            .user(user)
            .plan(plan)
            .startDate(now)
            .endDate(now.plusDays(plan.getDurationDays()))
            .active(true)
            .paymentReference(paymentReference)
            .amountPaid(plan.getPrice())
            .build();

        UserSubscription saved = userSubscriptionRepository.save(subscription);

        notificationService.sendNotification(userId, "SUBSCRIPTION_ACTIVATED",
            "Subscription Activated",
            "Your " + plan.getName() + " plan is now active until " + saved.getEndDate().toLocalDate()
        );

        return saved;
    }

    @Transactional
    public void cancelSubscription(Long userId) {
        UserSubscription sub = userSubscriptionRepository
            .findActiveSubscription(userId, LocalDateTime.now())
            .orElseThrow(() -> new IllegalStateException("No active subscription found"));

        sub.setActive(false);
        sub.setAutoRenew(false);
        userSubscriptionRepository.save(sub);

        notificationService.sendNotification(userId, "SUBSCRIPTION_CANCELLED",
            "Subscription Cancelled",
            "Your subscription has been cancelled. You retain access until " + sub.getEndDate().toLocalDate()
        );
    }

    public boolean hasReachedApplicationLimit(Long userId, long currentApplicationCount) {
        UserSubscription sub = getActiveSubscription(userId);
        int maxApplications = (sub == null || sub.isExpired())
            ? subscriptionPlanRepository.findByType(SubscriptionType.FREE)
                .map(SubscriptionPlan::getMaxApplications).orElse(3)
            : sub.getPlan().getMaxApplications();

        return maxApplications != -1 && currentApplicationCount >= maxApplications;
    }

    public boolean hasReachedBookingLimit(Long userId, long currentBookingCount) {
        UserSubscription sub = getActiveSubscription(userId);
        int maxBookings = (sub == null || sub.isExpired())
            ? subscriptionPlanRepository.findByType(SubscriptionType.FREE)
                .map(SubscriptionPlan::getMaxBookings).orElse(2)
            : sub.getPlan().getMaxBookings();

        return maxBookings != -1 && currentBookingCount >= maxBookings;
    }
}
