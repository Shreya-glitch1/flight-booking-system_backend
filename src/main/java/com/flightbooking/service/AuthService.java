package com.flightbooking.service;

import com.flightbooking.dto.AuthResponse;
import com.flightbooking.dto.LoginRequest;
import com.flightbooking.dto.RegisterRequest;
import com.flightbooking.model.Role;
import com.flightbooking.model.User;
import com.flightbooking.repository.UserRepository;
import com.flightbooking.validator.AuthValidator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthValidator authValidator;

    public AuthService(UserRepository userRepository, AuthValidator authValidator) {
        this.userRepository = userRepository;
        this.authValidator = authValidator;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User register(RegisterRequest request) {
        authValidator.validateRegistration(request);

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
            try {
                user.setRole(Role.valueOf(request.getRole().trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                user.setRole(Role.PASSENGER);
            }
        } else {
            user.setRole(Role.PASSENGER);
        }

        return userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = authValidator.validateLogin(request, passwordEncoder);

        return new AuthResponse(
            user.getUserId(),
            user.getName(),
            user.getEmail(),
            user.getPhone(),
            user.getRole(),
            "Login successful"
        );
    }
}
