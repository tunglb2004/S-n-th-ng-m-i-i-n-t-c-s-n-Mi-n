package com.example.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.auth.JwtUtil;
import com.example.auth.dto.request.RegisterRequest;
import com.example.auth.dto.response.AuthResponse;
import com.example.auth.entity.User;
import com.example.auth.entity.UserProfile;
import com.example.auth.repository.UserProfileRepository;
import com.example.auth.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, UserProfileRepository userProfileRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public Object register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            return "Username already taken";
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            return "Email already registered";
        }
        User user = new User(req.getUsername(), req.getEmail(), passwordEncoder.encode(req.getPassword()));
        user.setPhone(req.getPhone());
        userRepository.save(user);

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setFullName(req.getFullName());
        profile.setGender(req.getGender());
        profile.setBirthday(req.getDateOfBirth());
        profile.setAddress(req.getAddress());
        profile.setCity(null);
        profile.setCountry(null);
        userProfileRepository.save(profile);

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token);
    }

    public AuthResponse login(com.example.auth.dto.request.LoginRequest req) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        String token = jwtUtil.generateToken(req.getUsername());
        return new AuthResponse(token);
    }
}