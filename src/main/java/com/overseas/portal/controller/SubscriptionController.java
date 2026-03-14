package com.overseas.portal.controller;

import com.overseas.portal.entity.SubscriptionPlan;
import com.overseas.portal.entity.User;
import com.overseas.portal.entity.UserSubscription;
import com.overseas.portal.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Subscription plans and management")
@SecurityRequirement(name = "bearerAuth")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/plans")
    @Operation(summary = "List all available subscription plans")
    public ResponseEntity<List<SubscriptionPlan>> getPlans() {
        return ResponseEntity.ok(subscriptionService.getAllPlans());
    }

    @GetMapping("/my-status")
    @Operation(summary = "Get the authenticated user's subscription status")
    public ResponseEntity<Map<String, Object>> getMyStatus(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionStatus(user.getId()));
    }

    @PostMapping("/subscribe")
    @Operation(summary = "Subscribe to a plan")
    public ResponseEntity<UserSubscription> subscribe(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> body) {
        Long planId = Long.valueOf(body.get("planId").toString());
        String paymentReference = body.getOrDefault("paymentReference", "").toString();
        return ResponseEntity.ok(subscriptionService.subscribe(user.getId(), planId, paymentReference));
    }

    @DeleteMapping("/cancel")
    @Operation(summary = "Cancel the current subscription")
    public ResponseEntity<Map<String, String>> cancel(@AuthenticationPrincipal User user) {
        subscriptionService.cancelSubscription(user.getId());
        return ResponseEntity.ok(Map.of("message", "Subscription cancelled successfully"));
    }
}
