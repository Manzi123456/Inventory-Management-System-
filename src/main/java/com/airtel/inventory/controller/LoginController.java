package com.airtel.inventory.controller;

import com.airtel.inventory.model.User;
import com.airtel.inventory.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("")
public class LoginController {

    private final UserService userService;

    /**
     * Display login page
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    /**
     * Process login form submission
     */
    @PostMapping("/login")
    public String processLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        // Authenticate user
        Optional<User> user = userService.authenticate(username, password);

        if (user.isPresent()) {
            // Store user in session
            session.setAttribute("loggedInUser", user.get());
            session.setAttribute("userId", user.get().getId());
            session.setAttribute("username", user.get().getUsername());
            session.setAttribute("userRole", user.get().getRole());
            session.setMaxInactiveInterval(30 * 60); // 30 minutes timeout
            
            return "redirect:/"; // Redirect to dashboard
        } else {
            model.addAttribute("error", "Invalid username or password!");
            return "login";
        }
    }

    /**
     * Logout - clear session
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    /**
     * Check if user is logged in (optional, for AJAX)
     */
    @GetMapping("/api/check-login")
    @ResponseBody
    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("loggedInUser") != null;
    }
}
