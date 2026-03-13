package com.lablink.auth.dto;

import jakarta.validation.constraints.*;

public class AdminRegisterRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Pattern(
        regexp = "^[a-zA-Z]+\\.[a-zA-Z]+@cit\\.edu$",
        message = "Email must follow the format firstname.lastname@cit.edu"
    )
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;

    /** Secret key to authorize admin account creation. Set via ADMIN_REGISTER_SECRET env var. */
    @NotBlank(message = "Admin secret key is required")
    private String adminSecret;

    public String getFullName()              { return fullName; }
    public void setFullName(String v)        { this.fullName = v; }
    public String getEmail()                 { return email; }
    public void setEmail(String v)           { this.email = v; }
    public String getPassword()              { return password; }
    public void setPassword(String v)        { this.password = v; }
    public String getConfirmPassword()       { return confirmPassword; }
    public void setConfirmPassword(String v) { this.confirmPassword = v; }
    public String getAdminSecret()           { return adminSecret; }
    public void setAdminSecret(String v)     { this.adminSecret = v; }
}
