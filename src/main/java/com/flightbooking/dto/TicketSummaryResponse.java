package com.flightbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketSummaryResponse {
    private Long ticketId;
    private String ticketNumber;
    private String passengerFirstName;
    private String passengerLastName;
    private String ticketStatus;
}
