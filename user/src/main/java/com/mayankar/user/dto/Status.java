package com.mayankar.user.dto;

public enum Status {
    PAST, LIVE, UPCOMING;

    public static Status fromString(String status) {
        if (status == null) {
            return null;
        }
        for (Status s : Status.values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        return null;
    }

    public static boolean isValid(String status) {
        return fromString(status) != null;
    }

    public static boolean isPast(String status) {
        return PAST.equals(fromString(status));
    }

    public static boolean isUpcoming(String status) {
        return UPCOMING.equals(fromString(status));
    }
}
