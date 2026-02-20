package com.lablink.auth.dto;

import com.lablink.auth.UserRole;
import java.util.UUID;

public class AuthResponse {

    private final UserDto user;
    private final String  token;
    private final String  refreshToken;

    public AuthResponse(UserDto user, String token, String refreshToken) {
        this.user         = user;
        this.token        = token;
        this.refreshToken = refreshToken;
    }

    public UserDto getUserDto()       { return user; }
    // Jackson serialises the field named "user"
    public UserDto getUser()          { return user; }
    public String getToken()          { return token; }
    public String getRefreshToken()   { return refreshToken; }

    public static Builder builder()   { return new Builder(); }

    public static class Builder {
        private UserDto user;
        private String token, refreshToken;

        public Builder user(UserDto v)         { this.user = v; return this; }
        public Builder token(String v)         { this.token = v; return this; }
        public Builder refreshToken(String v)  { this.refreshToken = v; return this; }
        public AuthResponse build() { return new AuthResponse(user, token, refreshToken); }
    }

    // ── Nested UserDto ─────────────────────────────────────────
    public static class UserDto {
        private final UUID     id;
        private final String   email;
        private final String   name;
        private final UserRole role;

        public UserDto(UUID id, String email, String name, UserRole role) {
            this.id    = id;
            this.email = email;
            this.name  = name;
            this.role  = role;
        }

        public UUID getId()      { return id; }
        public String getEmail() { return email; }
        public String getName()  { return name; }
        public UserRole getRole(){ return role; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private UUID id;
            private String email, name;
            private UserRole role;

            public Builder id(UUID v)       { this.id = v; return this; }
            public Builder email(String v)  { this.email = v; return this; }
            public Builder name(String v)   { this.name = v; return this; }
            public Builder role(UserRole v) { this.role = v; return this; }
            public UserDto build() { return new UserDto(id, email, name, role); }
        }
    }
}
