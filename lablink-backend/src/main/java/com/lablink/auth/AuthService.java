package com.lablink.auth;

import com.lablink.core.User;
import com.lablink.core.UserRole;
import com.lablink.core.UserRepository;

import com.lablink.auth.dto.*;
import com.lablink.exception.BusinessException;
import com.lablink.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository        userRepository;
    private final PasswordEncoder       passwordEncoder;
    private final JwtService            jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${app.admin.register-secret:lablink-admin-secret-change-me}")
    private String adminRegisterSecret;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository        = userRepository;
        this.passwordEncoder       = passwordEncoder;
        this.jwtService            = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw BusinessException.badRequest("VALID-002", "Passwords do not match");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw BusinessException.conflict("DB-002", "Email is already registered");
        }
        if (request.getIdNumber() != null && !request.getIdNumber().isBlank()
                && userRepository.existsByIdNumber(request.getIdNumber())) {
            throw BusinessException.conflict("DB-003", "Student ID is already registered");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .idNumber(request.getIdNumber())
                .role(UserRole.STUDENT)
                .build();

        userRepository.save(user);
        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse registerAdmin(AdminRegisterRequest request) {
        if (!adminRegisterSecret.equals(request.getAdminSecret())) {
            throw BusinessException.badRequest("AUTH-004", "Invalid admin secret key");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw BusinessException.badRequest("VALID-002", "Passwords do not match");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw BusinessException.conflict("DB-002", "Email is already registered");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.ADMIN)
                .build();

        userRepository.save(user);
        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("AUTH-001", "User not found", HttpStatus.UNAUTHORIZED));
        return buildAuthResponse(user);
    }

    public void logout(String email) {
        // Stateless — client discards token.
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken  = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .user(AuthResponse.UserDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .name(user.getFullName())
                        .idNumber(user.getIdNumber())
                        .role(user.getRole())
                        .build())
                .token(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
