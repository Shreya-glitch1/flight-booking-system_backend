package com.flightbooking.repository;

import com.flightbooking.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query("SELECT f FROM Flight f WHERE " +
           "(LOWER(f.sourceAirport.code) = LOWER(:source) OR LOWER(f.sourceAirport.city) = LOWER(:source)) AND " +
           "(LOWER(f.destinationAirport.code) = LOWER(:destination) OR LOWER(f.destinationAirport.city) = LOWER(:destination)) AND " +
           "f.departureTime >= :startTime AND f.departureTime <= :endTime AND " +
           "f.availableSeats > 0")
    List<Flight> searchFlights(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
