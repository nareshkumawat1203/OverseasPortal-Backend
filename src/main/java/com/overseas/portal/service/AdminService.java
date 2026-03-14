package com.overseas.portal.service;

import com.overseas.portal.entity.User;
import com.overseas.portal.exception.ResourceNotFoundException;
import com.overseas.portal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final StudentApplicationRepository applicationRepository;
    private final BookingRepository bookingRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final UniversityRepository universityRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;

    public Map<String, Object> getDashboardStats() {
        long totalUsers     = userRepository.count();
        long totalProviders = providerProfileRepository.count();
        long totalUnis      = universityRepository.count();
        long totalApps      = applicationRepository.count();
        long totalBookings  = bookingRepository.count();

        return Map.of(
            "totalUsers",         totalUsers,
            "totalProviders",     totalProviders,
            "totalUniversities",  totalUnis,
            "totalApplications",  totalApps,
            "totalBookings",      totalBookings
        );
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    @Transactional
    public User toggleUserActive(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        user.setActive(!user.isActive());
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        user.setActive(false);
        userRepository.save(user);
    }
}
