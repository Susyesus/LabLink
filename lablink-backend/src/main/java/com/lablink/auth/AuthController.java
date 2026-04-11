package com.lablink.auth;

import com.lablink.auth.dto.*;
import com.lablink.core.ApiResponse;
import com.lablink.core.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** Student registration — role is always STUDENT. */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(authService.register(request)));
    }

    /**
     * Admin registration — requires ADMIN_REGISTER_SECRET in request body.
     * Endpoint is public but gated by the secret key.
     */
    @PostMapping("/register-admin")
    public ResponseEntity<ApiResponse<AuthResponse>> registerAdmin(
            @Valid @RequestBody AdminRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(authService.registerAdmin(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.login(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal User currentUser) {
        if (currentUser != null) {
            authService.logout(currentUser.getEmail());
        }
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
