package com.project.personal_task.api.controller.dto;

public class JwtResponse {
  private String token;
  private String type = "Bearer";

  public JwtResponse() {
  }

  public JwtResponse(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  public String getType() {
    return type;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public void setType(String type) {
    this.type = type;
  }
}
