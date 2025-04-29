package com.project.personal_task.api.controller;

import com.project.personal_task.api.model.User;
import com.project.personal_task.api.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
  @Autowired
  private UserRepository repo;
  private final String UPLOAD_DIR = "uploads/avatars/";

  @GetMapping("/{id}")
  public ResponseEntity<User> getUser(@PathVariable Long id) {
    Optional<User> u = repo.findById(id);
    return u.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping(value = "/{id}/avatar", consumes = "multipart/form-data")
  public ResponseEntity<String> uploadAvatar(@PathVariable Long id, @RequestParam MultipartFile file) {
    try {
      String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
      Path path = Paths.get(UPLOAD_DIR + filename);
      Files.createDirectories(path.getParent());
      Files.write(path, file.getBytes());
      User user = repo.findById(id).orElseThrow();
      user.setAvatarUrl("/" + UPLOAD_DIR + filename);
      repo.save(user);
      return ResponseEntity.ok(user.getAvatarUrl());
    } catch (IOException e) {
      return ResponseEntity.internalServerError().body("Upload failed");
    }
  }
}