package com.flightbooking.controller;

import com.flightbooking.dto.FlightSummaryResponse;
import com.flightbooking.model.Flight;
import com.flightbooking.service.FlightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = {"http://localhost:4200"})
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping
    public ResponseEntity<List<Flight>> getAllFlights() {
        List<Flight> flights = flightService.getAllFlights();
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/summary")
    public ResponseEntity<List<FlightSummaryResponse>> getFlightSummaries() {
        return ResponseEntity.ok(flightService.getFlightSummaries());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Flight>> searchFlights(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String date) {
        List<Flight> flights = flightService.searchFlights(source, destination, date);
        return ResponseEntity.ok(flights);
    }
}
