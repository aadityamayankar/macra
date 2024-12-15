package com.mayankar.dataaccess.repository;

import com.mayankar.model.OrderProfile;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mayankar.util.Constants.MISC_FLAG_DELETED;

@Repository
public interface OrderProfileRepository extends ReactiveCrudRepository<OrderProfile, Long>, BaseRepository {
    @Query("SELECT * FROM order_profile WHERE razorpay_order_id = :razorpayOrderId AND razorpay_payment_id = :razorpayPaymentId AND (miscflags & " + MISC_FLAG_DELETED + ") = 0")
    Mono<OrderProfile> getOrderProfileByRazorpayOrderIdPaymentId(String razorpayOrderId, String razorpayPaymentId);

    @Query("UPDATE order_profile SET payment_status = :status WHERE razorpay_order_id = :razorpayOrderId AND razorpay_payment_id = :razorpayPaymentId")
    Mono<OrderProfile> updateOrderProfileStatusByRazorpayOrderIdPaymentId(String razorpayOrderId, String razorpayPaymentId, String status);

    @Query("SELECT * FROM order_profile WHERE user_id = :userId AND (miscflags & " + MISC_FLAG_DELETED + ") = 0")
    Flux<OrderProfile> getOrderProfilesByUserId(Long userId);
}
