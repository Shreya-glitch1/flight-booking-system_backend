package com.flightbooking.service;

import com.flightbooking.model.Flight;
import com.flightbooking.repository.FlightRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public List<Flight> searchFlights(String source, String destination, String dateStr) {

        boolean hasSource = source != null && !source.trim().isEmpty();
        boolean hasDestination = destination != null && !destination.trim().isEmpty();

        if (!hasSource && !hasDestination) {
            return getAllFlights();
        }

        // If only one is provided, throw validation error
        if (!hasSource || !hasDestination) {
            throw new IllegalArgumentException("Both source and destination are required for search");
        }

        LocalDateTime startTime;
        LocalDateTime endTime;

        if (dateStr != null && !dateStr.trim().isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(dateStr.trim());
                startTime = date.atStartOfDay();
                endTime = date.atTime(LocalTime.MAX);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format. Expected YYYY-MM-DD");
            }
        } else {
            // Default: search from start of today up to 1 year in future
            startTime = LocalDate.now().atStartOfDay();
            endTime = LocalDateTime.now().plusYears(1);
        }

        return flightRepository.searchFlights(source.trim(), destination.trim(), startTime, endTime);
    }
}
