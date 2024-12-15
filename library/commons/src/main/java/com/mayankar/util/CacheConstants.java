package com.mayankar.util;

public class CacheConstants {
    public static final String AUTHN_SESSION_PREFIX = "authn_session";
    public static final String AUTHORIZATION_CODE_PREFIX = "authorization_code";
    public static final String TICKET_PROFILE_PREFIX = "ticket_profile";
    public static final String TICKET_RESERVATION_ORDER_PREFIX = "ticket_reservation_order";
    public static final String TICKET_RESERVATION_KEY = "ticket_reservation:event:{0}:ticket:{1}";
    public static final Long TICKET_RESERVATION_EXPIRY = 300L; // make this configurable
    public static final String TICKET_LOCK_KEY = "lock:event:{0}:ticket:{1}";
    public static final String LAST_SYNC_TIME_KEY = "last_sync_time";
    public static final Long TICKET_LOCK_EXPIRY = 5L; // make this configurable
    public static final String TICKET_LOCK_VAL = "locked";
}
