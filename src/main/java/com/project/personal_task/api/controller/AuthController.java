package com.project.personal_task.api.controller;

import com.project.personal_task.api.controller.dto.*;
import com.project.personal_task.api.model.User;
import com.project.personal_task.api.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  private AuthenticationManager authenticationManager;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder encoder;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
    if (userRepository.existsByUsername(req.getUsername()))
      return ResponseEntity.badRequest().body(Map.of("field", "username", "message", "Username is already taken"));
    if (userRepository.existsByEmail(req.getEmail()))
      return ResponseEntity.badRequest().body(Map.of("field", "email", "message", "Email is already in use"));
    User user = new User();
    user.setUsername(req.getUsername());
    user.setEmail(req.getEmail());
    user.setPassword(encoder.encode(req.getPassword()));
    userRepository.save(user);
    return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
  }

  // @PostMapping("/login")
  // public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest,
  // HttpServletResponse response) {
  // try {
  // // authenticate using Spring Security
  // Authentication authentication = authenticationManager.authenticate(
  // new UsernamePasswordAuthenticationToken(
  // loginRequest.getUsernameOrEmail(),
  // loginRequest.getPassword()));

  // SecurityContextHolder.getContext().setAuthentication(authentication);

  // // Add Remember-Me Cookeie if enabled
  // // if (loginRequest.isRememberMe()) {
  // // Cookie rememberMeCookie = new Cookie("REMEMBER_ME",
  // // loginRequest.getUsernameOrEmail());
  // // rememberMeCookie.setMaxAge(7 * 24 * 60 * 60);
  // // rememberMeCookie.setHttpOnly(true);
  // // rememberMeCookie.setPath("/");
  // // response.addCookie(rememberMeCookie);
  // // }

  // Map<String, String> body = Map.of("message", "login successful");
  // return ResponseEntity.ok(body);
  // } catch (AuthenticationException ex) {
  // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid
  // credentials");
  // }
  // }

  // inject whatever holds your authentication context
  @GetMapping("/me")
  public ResponseEntity<?> whoAmI(Authentication auth) {
    if (auth == null || !auth.isAuthenticated()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    // you can return a DTO with username, email, roles, etc.
    Map<String, Object> body = Map.of(
        "username", auth.getName(),
        "authorities", auth.getAuthorities());
    return ResponseEntity.ok(body);
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request,
      HttpServletResponse response) {
    // Invalidate session if you’re using one:
    request.getSession(false).invalidate();
    // Remove remember-me cookie if you set one:
    Cookie cookie = new Cookie("REMEMBER_ME", null);
    cookie.setMaxAge(0);
    cookie.setPath("/");
    response.addCookie(cookie);

    SecurityContextHolder.clearContext();
    return ResponseEntity.ok(Map.of("message", "logged out"));
  }

  @GetMapping("/check-email")
  public ResponseEntity<Map<String, Boolean>> checkEmailExists(@RequestParam String email) {
    boolean exists = userRepository.existsByEmail(email);
    return ResponseEntity.ok(Map.of("exists", exists));
  }

}