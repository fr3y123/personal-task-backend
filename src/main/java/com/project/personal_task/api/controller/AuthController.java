package com.project.personal_task.api.controller;

import com.project.personal_task.api.controller.dto.*;
import com.project.personal_task.api.model.User;
import com.project.personal_task.api.repository.UserRepository;
import com.project.personal_task.api.security.JwtUtils;
import com.project.personal_task.api.security.UserDetailsImpl;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
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
  @Autowired
  private JwtUtils jwtUtils;

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

  @PostMapping("/login")
  public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest req) {
    var auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
    String token = jwtUtils.generateJwtToken((UserDetailsImpl) auth.getPrincipal());
    return ResponseEntity.ok(new JwtResponse(token));
  }

  @GetMapping("/check-email")
  public ResponseEntity<Map<String, Boolean>> checkEmailExists(@RequestParam String email) {
    boolean exists = userRepository.existsByEmail(email);
    return ResponseEntity.ok(Map.of("exists", exists));
  }

}