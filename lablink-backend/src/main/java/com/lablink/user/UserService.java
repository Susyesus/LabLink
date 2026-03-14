package com.lablink.user;

import com.lablink.auth.User;
import com.lablink.auth.UserRepository;
import com.lablink.exception.BusinessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class UserService {

    /** Accepted MIME types for profile photos. */
    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png");

    /** 5 MB hard limit for photo uploads. */
    private static final long MAX_PHOTO_BYTES = 5 * 1024 * 1024;

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ── GET /api/v1/users/me ─────────────────────────────────────────────────

    /** Returns the profile of the authenticated user. */
    @Transactional(readOnly = true)
    public UserProfileDto getProfile(User currentUser) {
        // Re-fetch to guarantee the entity is managed and photo flag is current.
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> BusinessException.notFound("USER-001", "User not found"));
        return UserProfileDto.from(user);
    }

    // ── PUT /api/v1/users/me ─────────────────────────────────────────────────

    /**
     * Updates fullName and/or idNumber.
     * ID number uniqueness is re-validated on change.
     */
    @Transactional
    public UserProfileDto updateProfile(User currentUser, UpdateProfileRequest request) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> BusinessException.notFound("USER-001", "User not found"));

        user.setFullName(request.getFullName());

        String newId = request.getIdNumber();
        if (newId != null && !newId.isBlank()) {
            // Only check uniqueness if the value actually changed
            if (!newId.equals(user.getIdNumber())
                    && userRepository.existsByIdNumber(newId)) {
                throw BusinessException.conflict("DB-003", "Student ID is already in use");
            }
            user.setIdNumber(newId);
        }

        return UserProfileDto.from(userRepository.save(user));
    }

    // ── PUT /api/v1/users/me/password ────────────────────────────────────────

    /**
     * Changes the authenticated user's password.
     * Verifies the current password before applying the update.
     */
    @Transactional
    public void changePassword(User currentUser, ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw BusinessException.badRequest("VALID-002", "New passwords do not match");
        }

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> BusinessException.notFound("USER-001", "User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw BusinessException.badRequest("AUTH-005", "Current password is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw BusinessException.badRequest("VALID-004",
                    "New password must be different from the current password");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // ── POST /api/v1/users/me/photo ──────────────────────────────────────────

    /**
     * Uploads a profile photo.
     * File is validated (type + size) then stored as bytes in the DB.
     * Returns the updated profile so the client can refresh hasPhoto.
     */
    @Transactional
    public UserProfileDto uploadPhoto(User currentUser, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw BusinessException.badRequest("VALID-005", "Photo file is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw BusinessException.badRequest("VALID-006",
                    "Only JPEG and PNG images are accepted");
        }

        if (file.getSize() > MAX_PHOTO_BYTES) {
            throw BusinessException.badRequest("VALID-007",
                    "Photo must be smaller than 5 MB");
        }

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> BusinessException.notFound("USER-001", "User not found"));

        try {
            user.setPhotoData(file.getBytes());
            user.setPhotoType(contentType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read uploaded file", e);
        }

        return UserProfileDto.from(userRepository.save(user));
    }

    // ── GET /api/v1/users/me/photo ───────────────────────────────────────────

    /**
     * Returns raw photo bytes for the authenticated user.
     * Throws 404 if no photo has been uploaded yet.
     */
    @Transactional(readOnly = true)
    public byte[] getPhoto(User currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> BusinessException.notFound("USER-001", "User not found"));

        if (user.getPhotoData() == null) {
            throw BusinessException.notFound("USER-002", "No profile photo found");
        }
        return user.getPhotoData();
    }

    /** Returns the stored MIME type for the photo (e.g. "image/jpeg"). */
    @Transactional(readOnly = true)
    public String getPhotoType(User currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> BusinessException.notFound("USER-001", "User not found"));
        return user.getPhotoType() != null ? user.getPhotoType() : "image/jpeg";
    }
}
