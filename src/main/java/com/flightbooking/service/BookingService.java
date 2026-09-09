package com.flightbooking.service;

import com.flightbooking.dto.*;
import com.flightbooking.model.*;
import com.flightbooking.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {

    private static final int MAX_PASSENGERS_PER_BOOKING = 20;
    private static final String STATUS_CONFIRMED = "CONFIRMED";
    private static final String PAYMENT_STATUS_SUCCESS = "SUCCESS";
    private static final String PAYMENT_METHOD_MOCK = "MOCK_CARD";

    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final FlightRepository flightRepository;

    public BookingService(
            BookingRepository bookingRepository,
            PassengerRepository passengerRepository,
            TicketRepository ticketRepository,
            PaymentRepository paymentRepository,
            UserRepository userRepository,
            FlightRepository flightRepository) {
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.ticketRepository = ticketRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.flightRepository = flightRepository;
    }

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {
        User booker = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Booker not found"));

        Flight flight = flightRepository.findById(request.getFlightId())
                .orElseThrow(() -> new IllegalArgumentException("Flight not found"));

        List<PassengerRequest> passengerRequests = request.getPassengers();
        if (passengerRequests.size() > MAX_PASSENGERS_PER_BOOKING) {
            throw new IllegalArgumentException(
                    "Maximum " + MAX_PASSENGERS_PER_BOOKING + " passengers allowed per booking");
        }

        int seatCount = passengerRequests.size();
        if (flight.getAvailableSeats() < seatCount) {
            throw new IllegalArgumentException(
                    "Not enough seats available. Requested: " + seatCount
                            + ", available: " + flight.getAvailableSeats());
        }

        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setBookingDate(LocalDate.now());
        booking.setBookingStatus(STATUS_CONFIRMED);
        booking = bookingRepository.save(booking);

        List<TicketSummaryResponse> ticketSummaries = new ArrayList<>();
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        for (int i = 0; i < passengerRequests.size(); i++) {
            PassengerRequest passengerReq = passengerRequests.get(i);
            Passenger passenger = buildPassenger(passengerReq);
            passenger = passengerRepository.save(passenger);

            Ticket ticket = new Ticket();
            ticket.setBooking(booking);
            ticket.setPassenger(passenger);
            ticket.setFlight(flight);
            ticket.setTicketNumber(String.format("TKT-%s-%d-%02d", datePrefix, booking.getBookingId(), i + 1));
            ticket.setTicketStatus(STATUS_CONFIRMED);
            ticket = ticketRepository.save(ticket);

            ticketSummaries.add(new TicketSummaryResponse(
                    ticket.getTicketId(),
                    ticket.getTicketNumber(),
                    passenger.getFirstName(),
                    passenger.getLastName(),
                    ticket.getTicketStatus()
            ));
        }

        BigDecimal totalAmount = flight.getPrice().multiply(BigDecimal.valueOf(seatCount));

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(totalAmount);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentStatus(PAYMENT_STATUS_SUCCESS);
        payment.setPaymentMethod(PAYMENT_METHOD_MOCK);
        paymentRepository.save(payment);

        flight.setAvailableSeats(flight.getAvailableSeats() - seatCount);
        flightRepository.save(flight);

        return toBookingResponse(booking, booker, flight, ticketSummaries, totalAmount, payment);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }

        return bookingRepository.findByBookerUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::loadBookingResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        return loadBookingResponse(booking);
    }

    private BookingResponse loadBookingResponse(Booking booking) {
        User booker = booking.getBooker();
        List<Ticket> tickets = ticketRepository.findByBookingBookingId(booking.getBookingId());
        if (tickets.isEmpty()) {
            throw new IllegalStateException("Booking has no tickets");
        }

        Flight flight = tickets.getFirst().getFlight();
        Payment payment = paymentRepository.findByBookingBookingId(booking.getBookingId())
                .orElseThrow(() -> new IllegalStateException("Payment not found for booking"));

        List<TicketSummaryResponse> ticketSummaries = tickets.stream()
                .map(t -> new TicketSummaryResponse(
                        t.getTicketId(),
                        t.getTicketNumber(),
                        t.getPassenger().getFirstName(),
                        t.getPassenger().getLastName(),
                        t.getTicketStatus()))
                .toList();

        return toBookingResponse(booking, booker, flight, ticketSummaries, payment.getAmount(), payment);
    }

    private Passenger buildPassenger(PassengerRequest req) {
        Passenger passenger = new Passenger();
        passenger.setFirstName(req.getFirstName().trim());
        passenger.setLastName(req.getLastName().trim());
        passenger.setDateOfBirth(req.getDateOfBirth());
        passenger.setPassportNo(req.getPassportNo());
        passenger.setNationality(req.getNationality());

        if (req.getUserId() != null) {
            User linkedUser = userRepository.findById(req.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Passenger user not found: " + req.getUserId()));
            passenger.setLinkedUser(linkedUser);
        }

        return passenger;
    }

    private BookingResponse toBookingResponse(
            Booking booking,
            User booker,
            Flight flight,
            List<TicketSummaryResponse> tickets,
            BigDecimal totalAmount,
            Payment payment) {
        return new BookingResponse(
                booking.getBookingId(),
                booker.getUserId(),
                booker.getName(),
                flight.getFlightId(),
                flight.getFlightNumber(),
                flight.getAirline(),
                flight.getSourceAirport().getCode(),
                flight.getSourceAirport().getCity(),
                flight.getDestinationAirport().getCode(),
                flight.getDestinationAirport().getCity(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                tickets.size(),
                tickets,
                totalAmount,
                payment.getPaymentStatus(),
                payment.getPaymentMethod(),
                booking.getBookingStatus(),
                booking.getBookingDate()
        );
    }
}
