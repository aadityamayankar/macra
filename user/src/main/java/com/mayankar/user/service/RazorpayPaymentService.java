package com.mayankar.user.service;

import com.mayankar.model.TicketReservation;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;


import static com.mayankar.util.Constants.PaymentConstants.*;

@Service
public class RazorpayPaymentService {
    public static final Logger logger = LoggerFactory.getLogger(RazorpayPaymentService.class);

    @Autowired
    RazorpayClient razorpayClient;

    public Mono<Order> initiatePayment(String userId, TicketReservation ticketReservation) {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put(AMOUNT, ticketReservation.getTotalAmount() * 100);
        orderRequest.put(CURRENCY, CURRENCY_INR);
        orderRequest.put(RECEIPT, ticketReservation.getId());

        return Mono.fromCallable(() -> razorpayClient.orders.create(orderRequest))
                .doOnNext(order -> logger.info("Payment initiated for user: {}, order: {}", userId, order))
                .doOnError(e -> logger.error("Error while initiating payment for user: {}", userId, e))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
