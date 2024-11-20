package com.mayankar.user.api;

import com.mayankar.controller.BaseController;
import com.mayankar.model.AuthnSession;
import com.mayankar.user.dto.TicketBookingRequest;
import com.mayankar.user.dto.UserTicketAssignmentDto;
import com.mayankar.user.service.BookingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mayankar.controller.BaseController.API_V1;

@RestController
@RequestMapping(API_V1 + "/bookings")
public class BookingController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    BookingService bookingService;

    @GetMapping("/history")
    public Flux<UserTicketAssignmentDto> getBookingHistory(ServerWebExchange exchange) {
        logger.debug("BookingController::getBookingHistory");
        AuthnSession authnSession = validateAuthnSession(exchange);
        return bookingService.getBookingHistory(authnSession.getUserId())
                .doOnComplete(() -> logger.info("Booking history fetched successfully"))
                .doOnError(throwable -> logger.error("Error fetching booking history"));
    }

    @PostMapping("/book")
    public Mono<UserTicketAssignmentDto> bookTicket(ServerWebExchange exchange, @Valid @RequestBody TicketBookingRequest ticketBookingRequest) {
        logger.debug("BookingController::bookTicket");
        AuthnSession authnSession = validateAuthnSession(exchange);
        return bookingService.bookTicket(authnSession.getUserId(), ticketBookingRequest)
                .doOnSuccess(userTicketAssignmentDto -> logger.info("Ticket booked successfully"))
                .doOnError(throwable -> logger.error("Error booking ticket"));
    }
}
