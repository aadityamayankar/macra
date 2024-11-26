package com.mayankar.dataaccess.cachedrepository;

import com.mayankar.dataaccess.service.ReactiveRedisService;
import com.mayankar.model.PaymentOrderReservationMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import static com.mayankar.util.CacheConstants.*;

@Repository
public class PaymentOrderReservationMappingRepository {
    @Autowired
    ReactiveRedisService<PaymentOrderReservationMapping> reactiveRedisService;

    public Mono<PaymentOrderReservationMapping> savePaymentOrderReservationMapping(String orderId, PaymentOrderReservationMapping paymentOrderReservationMapping) {
        return reactiveRedisService.save(TICKET_RESERVATION_ORDER_PREFIX, orderId, paymentOrderReservationMapping, TICKET_RESERVATION_EXPIRY);
    }

    public Mono<PaymentOrderReservationMapping> getTicketReservationId(String orderId) {
        return reactiveRedisService.get(TICKET_RESERVATION_ORDER_PREFIX, orderId, PaymentOrderReservationMapping.class);
    }
}
