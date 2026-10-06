package com.flightbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long bookingId;
    private Long bookerUserId;
    private String bookerName;
    private Long flightId;
    private String flightNumber;
    private String airline;
    private String sourceCode;
    private String sourceCity;
    private String destinationCode;
    private String destinationCity;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private int passengerCount;
    private List<TicketSummaryResponse> tickets;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String paymentMethod;
    private String bookingStatus;
    private LocalDate bookingDate;
}
