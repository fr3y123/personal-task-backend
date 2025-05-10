package com.project.personal_task.api.controller.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JsonUsernamePasswordFilter
    extends UsernamePasswordAuthenticationFilter {

  private final ObjectMapper mapper = new ObjectMapper();

  public JsonUsernamePasswordFilter(AuthenticationManager authManager) {
    setAuthenticationManager(authManager);
    setFilterProcessesUrl("/api/auth/login");
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest req,
      HttpServletResponse res) throws AuthenticationException {
    if (!"POST".equals(req.getMethod()) ||
        req.getContentType() == null ||
        !req.getContentType().startsWith("application/json")) {
      throw new AuthenticationServiceException("Unsupported login request");
    }
    try {
      LoginRequest creds = mapper
          .readValue(req.getInputStream(), LoginRequest.class);

      UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
          creds.getUsernameOrEmail(),
          creds.getPassword());
      setDetails(req, token);
      return this.getAuthenticationManager().authenticate(token);

    } catch (IOException e) {
      throw new AuthenticationServiceException(
          "Invalid JSON login request", e);
    }
  }
}
