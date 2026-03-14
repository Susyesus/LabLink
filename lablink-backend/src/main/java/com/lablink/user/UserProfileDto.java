package com.lablink.user;

import com.lablink.auth.User;
import com.lablink.auth.UserRole;

import java.time.Instant;
import java.util.UUID;

/**
 * Public-facing user profile.
 * Never exposes passwordHash or raw photo bytes —
 * photo is surfaced as a boolean flag + a URL.
 */
public class UserProfileDto {

    private UUID      id;
    private String    email;
    private String    fullName;
    private String    idNumber;
    private UserRole  role;
    private boolean   hasPhoto;
    private Instant   createdAt;

    public UserProfileDto() {}

    /** Build from entity. */
    public static UserProfileDto from(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.id        = user.getId();
        dto.email     = user.getEmail();
        dto.fullName  = user.getFullName();
        dto.idNumber  = user.getIdNumber();
        dto.role      = user.getRole();
        dto.hasPhoto  = user.getPhotoData() != null;
        dto.createdAt = user.getCreatedAt();
        return dto;
    }

    public UUID     getId()        { return id; }
    public String   getEmail()     { return email; }
    public String   getFullName()  { return fullName; }
    public String   getIdNumber()  { return idNumber; }
    public UserRole getRole()      { return role; }
    public boolean  isHasPhoto()   { return hasPhoto; }
    public Instant  getCreatedAt() { return createdAt; }
}
