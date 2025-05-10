package com.project.personal_task.api.security;

import com.project.personal_task.api.controller.dto.JsonUsernamePasswordFilter;
import com.project.personal_task.api.security.UserDetailsServiceImpl;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Autowired
  private UserDetailsServiceImpl userDetailsService;
  @Autowired
  private AuthEntryPointJwt unauthorizedHandler;

  // @Bean
  // public JwtAuthTokenFilter authenticationJwtTokenFilter() {
  // return new JwtAuthTokenFilter();
  // }

  @Bean
  public AuthenticationManager authManager(
      HttpSecurity http,
      PasswordEncoder passwordEncoder,
      UserDetailsService userDetailsService) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder = http
        .getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    return authenticationManagerBuilder.build();
  }

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      AuthenticationManager authManager) throws Exception {
    // 1) build the JSON filter
    JsonUsernamePasswordFilter jsonFilter = new JsonUsernamePasswordFilter(authManager);
    jsonFilter.setAuthenticationSuccessHandler(
        (req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK));
    jsonFilter.setAuthenticationFailureHandler(
        (req, res, ex) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Auth failed"));

    http
        // 2) CORS and CSRF
        .cors(Customizer.withDefaults())
        .csrf(csrf -> csrf.disable())

        // 3) exception handling → JSON 401s not HTML redirects
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((req, res, authEx) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED)))

        // 4) define public vs. protected
        .authorizeHttpRequests(authz -> authz
            // allow login & register
            .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register")
            .permitAll()
            // everything else needs a valid session
            .anyRequest().authenticated())

        // 5) remove default form & basic
        .formLogin(form -> form.disable())
        .httpBasic(basic -> basic.disable())

        // 6) insert your JSON‐login filter
        .addFilterAt(jsonFilter, UsernamePasswordAuthenticationFilter.class)

        // 7) logout endpoint if you like
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .logoutSuccessHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK)));

    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder = http
        .getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    return authenticationManagerBuilder.getOrBuild();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
