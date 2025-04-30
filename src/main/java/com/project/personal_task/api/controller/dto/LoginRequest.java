package com.project.personal_task.api.controller.dto;

public class LoginRequest {
  private String usernameOrEmail;
  private String password;
  private boolean rememberMe;

  public LoginRequest() {
  }

  // All-args constructor
  public LoginRequest(String usernameOrEmail, String password, boolean rememberMe) {
    this.usernameOrEmail = usernameOrEmail;
    this.password = password;
    this.rememberMe = rememberMe;
  }

  public String getUsernameOrEmail() {
    return usernameOrEmail;
  }

  public void setUsernameOrEmail(String usernameOrEmail) {
    this.usernameOrEmail = usernameOrEmail;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isRememberMe() {
    return rememberMe;
  }

  public void setRememberMe(boolean rememberMe) {
    this.rememberMe = rememberMe;
  }

}
