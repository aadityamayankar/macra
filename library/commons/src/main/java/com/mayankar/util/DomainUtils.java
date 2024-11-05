package com.mayankar.util;

import org.springframework.stereotype.Component;

@Component
public class DomainUtils {
    public String getUsernameFromEmail(String email) {
        return email.split("@")[0];
    }
}
