package com.overseas.portal.repository;

import com.overseas.portal.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByProviderIdAndVisibleTrue(Long providerId, Pageable pageable);
    Page<Review> findByServiceIdAndVisibleTrue(Long serviceId, Pageable pageable);
    Optional<Review> findByBookingId(Long bookingId);
    boolean existsByBookingId(Long bookingId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.provider.id = :providerId AND r.visible = true")
    Double getAverageRatingByProvider(@Param("providerId") Long providerId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.service.id = :serviceId AND r.visible = true")
    Double getAverageRatingByService(@Param("serviceId") Long serviceId);
}
