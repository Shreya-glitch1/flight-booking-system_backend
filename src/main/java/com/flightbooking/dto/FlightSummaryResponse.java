package com.flightbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** A flat flight view intended for simple clients and demo/testing screens. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightSummaryResponse {
    private Long flightId;
    private String flightNumber;
    private String airline;
    private String route;
    private String source;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal price;
    private Integer availableSeats;
    private String availability;
}
