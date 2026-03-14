package com.lablink.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Request body for PUT /api/v1/users/me */
public class UpdateProfileRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 255, message = "Full name must be 255 characters or fewer")
    private String fullName;

    @Pattern(
        regexp = "^\\d{2}-\\d{4}-\\d{3}$",
        message = "Student ID must follow the format XX-XXXX-XXX (e.g. 21-1234-567)"
    )
    private String idNumber;

    public String getFullName()       { return fullName; }
    public void   setFullName(String v){ this.fullName = v; }
    public String getIdNumber()       { return idNumber; }
    public void   setIdNumber(String v){ this.idNumber = v; }
}
