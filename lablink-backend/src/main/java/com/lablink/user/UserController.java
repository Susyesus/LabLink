package com.lablink.user;

import com.lablink.auth.User;
import com.lablink.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /api/v1/users/me
     * Returns the authenticated user's profile.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileDto>> getProfile(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getProfile(currentUser)));
    }

    /**
     * PUT /api/v1/users/me
     * Updates fullName and/or idNumber.
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateProfile(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(userService.updateProfile(currentUser, request)));
    }

    /**
     * PUT /api/v1/users/me/password
     * Changes the authenticated user's password.
     * Requires current password for verification.
     */
    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(currentUser, request);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /**
     * POST /api/v1/users/me/photo
     * Uploads a profile photo (JPEG or PNG, max 5 MB).
     * File is converted to bytes and stored in the database.
     * Accepts multipart/form-data with field name "file".
     */
    @PostMapping(value = "/me/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserProfileDto>> uploadPhoto(
            @AuthenticationPrincipal User currentUser,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.ok(userService.uploadPhoto(currentUser, file)));
    }

    /**
     * GET /api/v1/users/me/photo
     * Returns the raw photo bytes with correct Content-Type header.
     * Returns 404 if no photo has been uploaded yet.
     */
    @GetMapping("/me/photo")
    public ResponseEntity<byte[]> getPhoto(
            @AuthenticationPrincipal User currentUser) {
        byte[] data = userService.getPhoto(currentUser);
        String type = userService.getPhotoType(currentUser);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, type)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"profile-photo\"")
                .body(data);
    }
}
