package com.mayankar.user.config;

import com.razorpay.RazorpayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayConfig {

    @Value("${ibento.user.razorpay.key}")
    String razorpayKey;

    @Value("${ibento.user.razorpay.secret}")
    String razorpaySecret;

    @Bean
    public RazorpayClient razorpayClient() {
        try {
            return new RazorpayClient(razorpayKey, razorpaySecret, false);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating Razorpay client", e);
        }
    }
}
