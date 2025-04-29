package com.project.personal_task.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.openmbee.mms.localuser.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
// import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

@Component
public class JwtUtils {
  @Value("${app.jwtSecret}")
  private String jwtSecret;
  @Value("${app.jwtExpirationMs}")
  private int jwtExpirationMs;

  private SecretKey key;

  @PostConstruct
  public void init() {
    key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }

  public String generateJwtToken(com.project.personal_task.api.security.UserDetailsImpl userDetails) {
    return Jwts.builder()
        .claim("sub", userDetails.getUsername())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
        .signWith(key)
        .compact();
  }

  public String getUserNameFromJwtToken(String token) {
    return Jwts
        .parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }

  public boolean validateJwtToken(String token) {
    try {
      // if this throws no exception, the token is both well‑formed and verified
      Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }
}
