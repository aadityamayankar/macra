package com.mayankar.util;

import org.springframework.beans.factory.annotation.Value;

public class Constants {
    public static final String CODE = "code";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String GRANT_TYPE = "grant_type";
    public static final String AUTHORIZATION_CODE = "authorization_code";
    public static final String SESSION_ID = "session_id";
    public static final Long THIRTY_MINUTES_IN_SECONDS = 1800L;
    public static final Long TEN_MINUTES_IN_SECONDS = 600L;
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer"; // REMEMBER TO USE BEARER + " " + token
    public static final long OPS_USER_MISCFLAG = 1L<<30;
    public static final long MISC_FLAG_DELETED = 1L<<31;
    public static final String READ = "read";
    public static final String WRITE = "write";
    public static final String OPENID = "openid";
    public static final String CREATE = "create";
    public static final String UPDATE = "update";
    public static final String DELETE = "delete";
    public static final String TICKET = "ticket";
    @Value("${ibento.user.ticket.max_retries}")
    public static final long MAX_TICKET_BOOKING_RETRIES = 3;
    @Value("${ibento.user.ticket.retry_interval}")
    public static final long TICKET_BOOKING_RETRY_INTERVAL = 5000L;
    public static final String PAYMENT_CAPTURED = "payment.captured";

    public static class MessagingConstants {
        public static final String EVENT_SYNC_EXCHANGE = "event-sync-exchange";
        public static final String EVENT_SYNC_QUEUE = "event-sync-queue";
        public static final String EVENT_SYNC_ROUTING_KEY = "event-sync-key";
        public static final String PAYMENT_EVENT_EXCHANGE = "payment-event-exchange";
        public static final String PAYMENT_EVENT_QUEUE = "payment-event-queue";
        public static final String PAYMENT_EVENT_ROUTING_KEY = "payment-event-key";
        public static final String PROTOBUF_CONTENT_TYPE = "application/x-protobuf";
    }

    public static class PaymentConstants {
        public static final String AMOUNT = "amount";
        public static final String CURRENCY = "currency";
        public static final String ORDER_ID = "order_id";
        public static final String RECEIPT = "receipt";
        public static final String NOTES = "notes";
        public static final String CURRENCY_INR = "INR";
    }
}
