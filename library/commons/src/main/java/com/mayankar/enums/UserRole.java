package com.mayankar.enums;

import lombok.Getter;

public enum UserRole {
    OPSADMIN ("opsadmin", 1),
    USER("user", 2);

    @Getter
    private String role;
    @Getter
    private int value;

    UserRole(String role, int value) {
        this.role = role;
        this.value = value;
    }

    public static UserRole fromString(String role) {
        for (UserRole userRole : UserRole.values()) {
            if (userRole.getRole().equalsIgnoreCase(role)) {
                return userRole;
            }
        }
        return null;
    }

    public static UserRole fromValue(int value) {
        for (UserRole userRole : UserRole.values()) {
            if (userRole.getValue() == value) {
                return userRole;
            }
        }
        return null;
    }
}
