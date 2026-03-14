package com.overseas.portal.repository;

import com.overseas.portal.entity.Booking;
import com.overseas.portal.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByStudentId(Long studentId, Pageable pageable);
    Page<Booking> findByServiceProviderId(Long providerId, Pageable pageable);
    Page<Booking> findByStudentIdAndStatus(Long studentId, BookingStatus status, Pageable pageable);
    long countByStudentId(Long studentId);
    long countByStudentIdAndStatusNot(Long studentId, BookingStatus status);
}
