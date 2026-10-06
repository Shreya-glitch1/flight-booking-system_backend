package com.flightbooking.config;

import com.flightbooking.model.Airport;
import com.flightbooking.model.Flight;
import com.flightbooking.repository.AirportRepository;
import com.flightbooking.repository.FlightRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AirportRepository airportRepository;
    private final FlightRepository flightRepository;

    public DataInitializer(AirportRepository airportRepository, FlightRepository flightRepository) {
        this.airportRepository = airportRepository;
        this.flightRepository = flightRepository;
    }

    @Override
    public void run(String... args) {
        if (airportRepository.count() == 0) {
            // Airports
            Airport bom = airportRepository.save(new Airport(null, "BOM", "Chhatrapati Shivaji Maharaj Intl", "Mumbai", "India"));
            Airport del = airportRepository.save(new Airport(null, "DEL", "Indira Gandhi Intl", "Delhi", "India"));
            Airport blr = airportRepository.save(new Airport(null, "BLR", "Kempegowda Intl", "Bengaluru", "India"));
            Airport dxb = airportRepository.save(new Airport(null, "DXB", "Dubai International Airport", "Dubai", "UAE"));
            Airport lhr = airportRepository.save(new Airport(null, "LHR", "London Heathrow Airport", "London", "UK"));
            Airport jfk = airportRepository.save(new Airport(null, "JFK", "John F. Kennedy Intl", "New York", "USA"));
            Airport sin = airportRepository.save(new Airport(null, "SIN", "Singapore Changi Airport", "Singapore", "Singapore"));

            LocalDateTime today = LocalDateTime.now();

            // 1. Domestic BOM -> DEL
            flightRepository.save(new Flight(
                null, "AI-101", "Air India", bom, del,
                today.plusDays(1).withHour(8).withMinute(0),
                today.plusDays(1).withHour(10).withMinute(15),
                new BigDecimal("5500.00"), 180
            ));

            // 2. Domestic BOM -> BLR
            flightRepository.save(new Flight(
                null, "6E-502", "IndiGo", bom, blr,
                today.plusDays(1).withHour(12).withMinute(30),
                today.plusDays(1).withHour(14).withMinute(15),
                new BigDecimal("4200.00"), 180
            ));

            // 3. Domestic DEL -> BOM
            flightRepository.save(new Flight(
                null, "UK-815", "Vistara", del, bom,
                today.plusDays(2).withHour(17).withMinute(0),
                today.plusDays(2).withHour(19).withMinute(10),
                new BigDecimal("6100.00"), 160
            ));

            // 4. International BOM -> DXB
            flightRepository.save(new Flight(
                null, "EK-501", "Emirates", bom, dxb,
                today.plusDays(1).withHour(4).withMinute(30),
                today.plusDays(1).withHour(6).withMinute(15),
                new BigDecimal("18500.00"), 350
            ));

            // 5. International DEL -> LHR
            flightRepository.save(new Flight(
                null, "BA-138", "British Airways", del, lhr,
                today.plusDays(2).withHour(2).withMinute(15),
                today.plusDays(2).withHour(7).withMinute(0),
                new BigDecimal("45000.00"), 300
            ));

            // 6. International BOM -> JFK
            flightRepository.save(new Flight(
                null, "AI-191", "Air India", bom, jfk,
                today.plusDays(3).withHour(1).withMinute(30),
                today.plusDays(3).withHour(7).withMinute(55),
                new BigDecimal("72000.00"), 340
            ));

            // 7. Domestic BLR -> DEL
            flightRepository.save(new Flight(
                null, "6E-204", "IndiGo", blr, del,
                today.plusDays(1).withHour(9).withMinute(15),
                today.plusDays(1).withHour(11).withMinute(50),
                new BigDecimal("4800.00"), 180
            ));

            // 8. International BOM -> LHR
            flightRepository.save(new Flight(
                null, "QR-557", "Qatar Airways", bom, lhr,
                today.plusDays(2).withHour(10).withMinute(0),
                today.plusDays(2).withHour(18).withMinute(30),
                new BigDecimal("52000.00"), 280
            ));

            // 9. International BOM -> SIN
            flightRepository.save(new Flight(
                null, "SQ-423", "Singapore Airlines", bom, sin,
                today.plusDays(2).withHour(23).withMinute(35),
                today.plusDays(3).withHour(7).withMinute(20),
                new BigDecimal("28000.00"), 300
            ));

            // 10. Sold Out Edge Case: BOM -> DEL (availableSeats = 0)
            flightRepository.save(new Flight(
                null, "AI-999", "Air India", bom, del,
                today.plusDays(1).withHour(20).withMinute(0),
                today.plusDays(1).withHour(22).withMinute(15),
                new BigDecimal("7500.00"), 0
            ));

            System.out.println("DataInitializer: 10 sample airports and flights seeded successfully!");
        }
    }
}
