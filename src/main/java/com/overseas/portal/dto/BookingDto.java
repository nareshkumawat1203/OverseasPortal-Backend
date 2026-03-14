package com.overseas.portal.dto;

import com.overseas.portal.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    private String serviceName;
    private Long studentId;
    private String studentName;
    private Long providerId;
    private String providerName;
    private BookingStatus status;
    private LocalDateTime scheduledDate;
    private Double amount;
    private String currency;
    private String paymentStatus;
    private String paymentReference;
    private String studentNotes;
    private String providerNotes;
    private String cancellationReason;
    private LocalDateTime createdAt;
}
