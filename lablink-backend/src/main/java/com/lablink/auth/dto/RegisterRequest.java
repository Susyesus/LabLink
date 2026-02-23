package com.lablink.auth.dto;

import jakarta.validation.constraints.*;

public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    /**
     * Must be a CIT institutional email: firstname.lastname@cit.edu
     * Regex breakdown:
     *   [a-zA-Z]+   — first name (letters only)
     *   \.          — literal dot separator
     *   [a-zA-Z]+   — last name (letters only)
     *   @cit\.edu   — fixed domain
     */
    @NotBlank(message = "Email is required")
    @Pattern(
        regexp = "^[a-zA-Z0-9\\-]+\\.[a-zA-Z0-9\\-]+@cit\\.edu$",
        message = "Email must be a valid CIT address (e.g., firstname.lastname@cit.edu) and may include hyphens or numbers"
    )
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;

    /**
     * CIT student ID format: XX-XXXX-XXX
     * Regex breakdown:
     *   \d{2}   — 2-digit year prefix (e.g. 21)
     *   -       — hyphen
     *   \d{4}   — 4-digit sequence number
     *   -       — hyphen
     *   \d{3}   — 3-digit suffix
     * Example: 21-1234-567
     */
    @NotBlank(message = "Student ID is required")
    @Pattern(
        regexp = "^\\d{2}-\\d{4}-\\d{3}$",
        message = "Student ID must follow the format XX-XXXX-XXX (e.g. 21-1234-567)"
    )
    private String idNumber;

    public String getFullName()             { return fullName; }
    public void setFullName(String v)       { this.fullName = v; }
    public String getEmail()                { return email; }
    public void setEmail(String v)          { this.email = v; }
    public String getPassword()             { return password; }
    public void setPassword(String v)       { this.password = v; }
    public String getConfirmPassword()      { return confirmPassword; }
    public void setConfirmPassword(String v){ this.confirmPassword = v; }
    public String getIdNumber()             { return idNumber; }
    public void setIdNumber(String v)       { this.idNumber = v; }
}
