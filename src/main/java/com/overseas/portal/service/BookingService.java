package com.overseas.portal.service;

import com.overseas.portal.dto.BookingDto;
import com.overseas.portal.entity.*;
import com.overseas.portal.enums.BookingStatus;
import com.overseas.portal.exception.ResourceNotFoundException;
import com.overseas.portal.exception.UnauthorizedException;
import com.overseas.portal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ServiceListingRepository serviceListingRepository;
    private final NotificationService notificationService;

    public Page<BookingDto> getMyBookings(Long studentId, BookingStatus status, Pageable pageable) {
        if (status != null) {
            return bookingRepository.findByStudentIdAndStatus(studentId, status, pageable).map(this::toDto);
        }
        return bookingRepository.findByStudentId(studentId, pageable).map(this::toDto);
    }

    public Page<BookingDto> getProviderBookings(Long providerId, Pageable pageable) {
        return bookingRepository.findByServiceProviderId(providerId, pageable).map(this::toDto);
    }

    public BookingDto getById(Long id, Long requestingUserId) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", id));

        boolean isStudent = booking.getStudent().getId().equals(requestingUserId);
        boolean isProvider = booking.getService().getProvider().getUser().getId().equals(requestingUserId);

        if (!isStudent && !isProvider) {
            throw new UnauthorizedException("You do not have access to this booking");
        }
        return toDto(booking);
    }

    @Transactional
    public BookingDto create(Long studentId, BookingDto dto) {
        User student = userRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("User", studentId));

        ServiceListing service = serviceListingRepository.findById(dto.getServiceId())
            .orElseThrow(() -> new ResourceNotFoundException("Service", dto.getServiceId()));

        if (!service.isActive()) {
            throw new IllegalStateException("This service is no longer available");
        }

        Booking booking = Booking.builder()
            .student(student)
            .service(service)
            .scheduledDate(dto.getScheduledDate())
            .amount(service.getPrice())
            .currency(service.getPriceCurrency())
            .studentNotes(dto.getStudentNotes())
            .status(BookingStatus.PENDING)
            .build();

        Booking saved = bookingRepository.save(booking);

        // Update service booking count
        service.setTotalBookings(service.getTotalBookings() + 1);
        serviceListingRepository.save(service);

        // Notify provider
        notificationService.sendNotification(
            service.getProvider().getUser().getId(),
            "NEW_BOOKING",
            "New Booking Request",
            student.getFullName() + " has requested a booking for: " + service.getTitle()
        );

        return toDto(saved);
    }

    @Transactional
    public BookingDto confirm(Long bookingId, Long providerId) {
        Booking booking = getAndValidateProvider(bookingId, providerId);
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        notificationService.sendNotification(
            booking.getStudent().getId(), "BOOKING_CONFIRMED",
            "Booking Confirmed",
            "Your booking for \"" + booking.getService().getTitle() + "\" has been confirmed."
        );
        return toDto(booking);
    }

    @Transactional
    public BookingDto complete(Long bookingId, Long providerId) {
        Booking booking = getAndValidateProvider(bookingId, providerId);
        booking.setStatus(BookingStatus.COMPLETED);
        booking.setCompletedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        notificationService.sendNotification(
            booking.getStudent().getId(), "BOOKING_COMPLETED",
            "Booking Completed",
            "Your booking for \"" + booking.getService().getTitle() + "\" is marked complete. Please leave a review."
        );
        return toDto(booking);
    }

    @Transactional
    public BookingDto cancel(Long bookingId, Long requestingUserId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));

        boolean isStudent = booking.getStudent().getId().equals(requestingUserId);
        boolean isProvider = booking.getService().getProvider().getUser().getId().equals(requestingUserId);

        if (!isStudent && !isProvider) {
            throw new UnauthorizedException("You do not have access to cancel this booking");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED || booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel a booking in state: " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationReason(reason);
        booking.setCancelledAt(LocalDateTime.now());
        bookingRepository.save(booking);

        Long notifyUserId = isStudent
            ? booking.getService().getProvider().getUser().getId()
            : booking.getStudent().getId();

        notificationService.sendNotification(notifyUserId, "BOOKING_CANCELLED",
            "Booking Cancelled",
            "A booking for \"" + booking.getService().getTitle() + "\" has been cancelled. Reason: " + reason
        );
        return toDto(booking);
    }

    private Booking getAndValidateProvider(Long bookingId, Long providerId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));
        if (!booking.getService().getProvider().getUser().getId().equals(providerId)) {
            throw new UnauthorizedException("You do not own this booking");
        }
        return booking;
    }

    private BookingDto toDto(Booking b) {
        return BookingDto.builder()
            .id(b.getId())
            .serviceId(b.getService().getId())
            .serviceName(b.getService().getTitle())
            .studentId(b.getStudent().getId())
            .studentName(b.getStudent().getFullName())
            .providerId(b.getService().getProvider().getId())
            .providerName(b.getService().getProvider().getCompanyName())
            .status(b.getStatus())
            .scheduledDate(b.getScheduledDate())
            .amount(b.getAmount())
            .currency(b.getCurrency())
            .paymentStatus(b.getPaymentStatus())
            .paymentReference(b.getPaymentReference())
            .studentNotes(b.getStudentNotes())
            .providerNotes(b.getProviderNotes())
            .cancellationReason(b.getCancellationReason())
            .createdAt(b.getCreatedAt())
            .build();
    }
}
