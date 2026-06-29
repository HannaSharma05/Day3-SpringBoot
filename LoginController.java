package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.regex.Pattern;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    // Username: only letters, no spaces, no special chars
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z]+$");

    // Password: at least 1 uppercase, 1 digit, 1 special char, length 4-16
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?])[A-Za-z\\d!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]{4,16}$"
    );

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(
            @RequestParam String username,
            @RequestParam String password,
            Model model) {

        // --- 1. Validate username ---
        if (username == null || username.trim().isEmpty()) {
            model.addAttribute("error", "Username cannot be empty.");
            return "login";
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            model.addAttribute("error", "Username must contain only letters (no spaces, no special characters).");
            return "login";
        }

        // --- 2. Validate password ---
        if (password == null || password.isEmpty()) {
            model.addAttribute("error", "Password cannot be empty.");
            return "login";
        }
        if (password.length() < 4 || password.length() > 16) {
            model.addAttribute("error", "Password must be between 4 and 16 characters.");
            return "login";
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            model.addAttribute("error",
                    "Password must contain at least 1 uppercase letter, 1 number, and 1 special character.");
            return "login";
        }

        // --- 3. Authenticate against database (with exception handling) ---
        try {
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isPresent() && password.equals(userOptional.get().getPassword())) {
                // Success
                model.addAttribute("username", username);
                return "welcome";
            } else {
                // Invalid credentials – user‑friendly message
                model.addAttribute("error", "Please enter the correct password.");
                return "login";
            }
        } catch (Exception e) {
            // Any unexpected exception (DB down, etc.) – also show friendly message
            model.addAttribute("error", "Please enter the correct password.");
            return "login";
        }
    }

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }
}