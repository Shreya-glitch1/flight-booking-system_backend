package com.flightbooking.dto;

public class AuthResponse {
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String message;

    public AuthResponse(Long userId, String name, String email, String phone, String role, String message) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getRole() {
        return role;
    }

    public String getMessage() {
        return message;
    }
}
