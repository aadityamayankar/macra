package com.mayankar.user.api;

import com.mayankar.controller.BaseController;
import com.mayankar.model.AuthnSession;
import com.mayankar.user.dto.OrderHistoryResponseDto;
import com.mayankar.user.service.OrderProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import static com.mayankar.controller.BaseController.API_V1;

@RestController
@RequestMapping(API_V1 + "/order-history")
public class OrderProfileController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(OrderProfileController.class);

    @Autowired
    OrderProfileService orderProfileService;

    @GetMapping
    public Flux<OrderHistoryResponseDto> getOrderHistory(ServerWebExchange exchange) {
        logger.debug("OrderProfileController::getOrderHistory");
        AuthnSession authnSession = validateAuthnSession(exchange);
        String userCId = authnSession.getUserId();
        return orderProfileService.getOrderHistory(userCId)
                .doOnComplete(() -> logger.info("Order history retrieved successfully"))
                .doOnError(throwable -> logger.error("Error retrieving order history", throwable));
    }
}
