package com.project.personal_task.api.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.project.personal_task.api.model.User;

import java.util.Collection;
import java.util.Objects;

public class UserDetailsImpl implements UserDetails {
  private Long id;
  private String username;
  private String password;

  public UserDetailsImpl(User user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.password = user.getPassword();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  } // No authorities are used in this example

  @Override
  public String getPassword() {
    return password;
  } // Password is already set in the constructor

  @Override
  public String getUsername() {
    return username;
  } // Username is already set in the constructor

  @Override
  public boolean isAccountNonExpired() {
    return true; // Account is not expired
  } // No expiration check in this example

  @Override
  public boolean isAccountNonLocked() {
    return true; // Account is not locked
  } // No lock check in this example

  @Override
  public boolean isCredentialsNonExpired() {
    return true; // Credentials are not expired
  } // No expiration check in this example

  @Override
  public boolean isEnabled() {
    return true; // Account is enabled
  } // No enable check in this example

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof UserDetailsImpl))
      return false;
    UserDetailsImpl that = (UserDetailsImpl) o;
    return Objects.equals(id, that.id) && Objects.equals(username, that.username);
  } // Equals method to compare UserDetailsImpl objects

  @Override
  public int hashCode() {
    return Objects.hash(id, username); // Hash code based on id and username
  } // Hash code method to generate hash for UserDetailsImpl objects
}
