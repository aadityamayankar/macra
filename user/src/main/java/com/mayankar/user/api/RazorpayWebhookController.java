package com.mayankar.user.api;

import com.mayankar.controller.BaseController;
import com.mayankar.user.dto.UserTicketAssignmentDto;
import com.mayankar.user.sender.PaymentEventSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.mayankar.controller.BaseController.API_V1;

@RestController
@RequestMapping(API_V1 + "/webhooks/razorpay")
public class RazorpayWebhookController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(RazorpayWebhookController.class);

    @Autowired
    private PaymentEventSender paymentEventSender;

    @PostMapping()
    public Mono<Void> handleRazorPayWebhook(ServerWebExchange exchange, @RequestHeader("X-Razorpay-Signature") String signature, @RequestBody String payload) {
        //@TODO: Implement signature verification
        logger.debug("RazorpayWebhookController::handleRazorPayWebhook");

        return paymentEventSender.sendPaymentEventMessage(payload)
                .doOnSuccess(unused -> logger.info("Payment event message sent successfully"))
                .doOnError(throwable -> logger.error("Error while sending payment event message"));
    }
}
