package com.project.personal_task.api.security;

import com.project.personal_task.api.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository repo;

    @Autowired
    public UserDetailsServiceImpl(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String login) {
        return repo.findByUsername(login)
                .or(() -> repo.findByEmail(login))
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("No user with username or email = " + login));
    }
}
