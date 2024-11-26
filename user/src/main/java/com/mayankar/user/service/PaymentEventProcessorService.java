package com.mayankar.user.service;

import com.mayankar.dataaccess.cachedrepository.PaymentOrderReservationMappingRepository;
import com.mayankar.dataaccess.cachedrepository.TicketReservationRepository;
import com.mayankar.dataaccess.repository.OrderProfileRepository;
import com.mayankar.model.OrderProfile;
import com.mayankar.model.TicketReservation;
import com.mayankar.user.dto.UserTicketAssignmentDto;
import com.mayankar.util.CompositeID;
import com.mayankar.util.Constants;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class PaymentEventProcessorService {
    public static final Logger logger = LoggerFactory.getLogger(PaymentEventProcessorService.class);

    @Autowired
    OrderProfileRepository orderProfileRepository;

    @Autowired
    TicketBookingService ticketBookingService;

    @Autowired
    PaymentOrderReservationMappingRepository paymentOrderReservationMappingRepository;

    @Autowired
    TicketReservationRepository ticketReservationRepository;

    public Mono<?> processEvent(String eventType, String eventPayload) {
        try {
            logger.info("Processing Razorpay webhook event: {}", eventType);
            JSONObject eventPayloadJson = new JSONObject(eventPayload);

            return switch (eventType) {
                case "payment.captured" -> handlePaymentCapturedEvent(eventPayloadJson);
                case "payment.failed" -> handlePaymentFailedEvent(eventPayloadJson);
                default -> {
                    logger.warn("Unhandled event type: {}", eventType);
                    yield Mono.empty();
                }
            };
        } catch (Exception e) {
            logger.error("Error while processing webhook event", e);
            return Mono.error(e);
        }
    }


    /*
    CHECK ORDER PROFILE ISSUE
     */


    private Mono<UserTicketAssignmentDto> handlePaymentCapturedEvent(JSONObject eventPayload) {
        JSONObject payment = eventPayload.getJSONObject("payment");
        JSONObject entity = payment.getJSONObject("entity");
        String orderId = entity.getString("order_id");
        String paymentId = entity.getString("id");
        String status = entity.getString("status");

        logger.info("Payment captured, orderId: {}, paymentId: {}", orderId, paymentId);
        return ticketBookingService.finalizeBooking(orderId, paymentId)
                .flatMap(tuple2 -> createOrUpdateOrderProfile(orderId, paymentId, status, tuple2.getT1(), tuple2.getT2())
                        .thenReturn(tuple2.getT1()))
                .doOnSuccess(userTicketAssignmentDto -> logger.info("Ticket booking finalized, orderId: {}, paymentId: {}", orderId, paymentId))
                .doOnError(throwable -> logger.error("Error finalizing ticket booking, orderId: {}, paymentId: {}", orderId, paymentId));
    }


    private Mono<String> handlePaymentFailedEvent(JSONObject eventPayload) {
        JSONObject payment = eventPayload.getJSONObject("payment");
        JSONObject entity = payment.getJSONObject("entity");
        String orderId = entity.getString("order_id");
        String paymentId = entity.getString("id");
        String status = entity.getString("status");
        String errorDescription = entity.getString("error_description");

        logger.info("Payment failed, orderId: {}, paymentId: {}", orderId, paymentId);

        return orderProfileRepository.getOrderProfileByRazorpayOrderIdPaymentId(orderId, paymentId)
                .switchIfEmpty(Mono.just(new OrderProfile()))
                .flatMap(orderProfile -> {
                    if (orderProfile.getId() != null && Constants.PAYMENT_CAPTURED.equals(orderProfile.getPaymentStatus())) {
                        logger.warn("Payment already captured, orderId: {}, paymentId: {}. Ignoring failed event", orderId, paymentId);
                        return Mono.just(errorDescription);
                    }
                    return orderProfileRepository.updateOrderProfileStatusByRazorpayOrderIdPaymentId(orderId, paymentId, status)
                            .doOnSuccess(success -> logger.info("updated ticket order status, orderId: {}", orderId))
                            .doOnError(throwable -> logger.error("Error updating ticket order status, orderId: {}", orderId))
                            .thenReturn(errorDescription);
                });
    }

    //ticket reservation is already cleared at this point. Get the ticket assigment, total amount from finalize booking function call and use those here to creat the order profile
    private Mono<OrderProfile> createOrUpdateOrderProfile(String orderId, String paymentId, String paymentStatus, UserTicketAssignmentDto userTicketAssignmentDto, TicketReservation ticketReservation) {
        return orderProfileRepository.getOrderProfileByRazorpayOrderIdPaymentId(orderId, paymentId)
                .switchIfEmpty(Mono.just(new OrderProfile()))
                .flatMap(orderProfile -> {
                    if (orderProfile.getId() != null) {
                        logger.debug("Order found, updating order profile");
                        return orderProfileRepository.updateOrderProfileStatusByRazorpayOrderIdPaymentId(orderId, paymentId, paymentStatus);
                    }
                    logger.debug("Order not found, creating new order profile");
                    String userCId = ticketReservation.getUserId();
                    OrderProfile orderProfile1 = OrderProfile.builder()
                            .userId(CompositeID.parseIdString(userCId))
                            .eventId(CompositeID.parseIdString(ticketReservation.getEventId()))
                            .ticketId(CompositeID.parseIdString(ticketReservation.getTicketId()))
                            .quantity(ticketReservation.getReservedQuantity())
                            .totalAmount(ticketReservation.getTotalAmount())
                            .razorpayOrderId(orderId)
                            .razorpayPaymentId(paymentId)
                            .paymentStatus(paymentStatus)
                            .build();
                    return orderProfileRepository.save(orderProfile1);
                });
    }
}
