package com.mayankar.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Base64;

@NoArgsConstructor
@AllArgsConstructor
public class CompositeID {
    private Long id;
    private String idString;

    public CompositeID(Long id) {
        this.id = id;
    }

    public CompositeID(String idString) {
        this.id = Long.parseLong(idString);
    }

    public static Long parseIdString(String idString) {
        if (idString == null || idString.isEmpty()) {
            throw new IllegalArgumentException("idString cannot be null or empty");
        }
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(idString);
            String decodedString = new String(decodedBytes);
            return Long.parseLong(decodedString);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid idString");
        }
    }

    public static String parseId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        try {
            return Base64.getEncoder().encodeToString(id.toString().getBytes());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid id");
        }
    }
}
