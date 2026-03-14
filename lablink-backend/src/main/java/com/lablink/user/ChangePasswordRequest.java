package com.lablink.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Request body for PUT /api/v1/users/me/password */
public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "New password must be at least 8 characters")
    private String newPassword;

    @NotBlank(message = "Please confirm your new password")
    private String confirmPassword;

    public String getCurrentPassword()         { return currentPassword; }
    public void   setCurrentPassword(String v) { this.currentPassword = v; }
    public String getNewPassword()             { return newPassword; }
    public void   setNewPassword(String v)     { this.newPassword = v; }
    public String getConfirmPassword()         { return confirmPassword; }
    public void   setConfirmPassword(String v) { this.confirmPassword = v; }
}
