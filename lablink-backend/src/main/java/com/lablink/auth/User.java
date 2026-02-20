package com.lablink.auth;

import com.lablink.borrow.BorrowRecord;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "user_role", nullable = false)
    private UserRole role = UserRole.STUDENT;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<BorrowRecord> borrowRecords;

    // Required by JPA
    public User() {}

    public User(UUID id, String email, String passwordHash, String fullName, UserRole role) {
        this.id           = id;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.fullName     = fullName;
        this.role         = role;
        this.createdAt    = Instant.now();
    }

    // ── Getters / Setters ──────────────────────────────────────
    public UUID getId()              { return id; }
    public String getEmail()         { return email; }
    public void setEmail(String v)   { this.email = v; }
    public String getPasswordHash()  { return passwordHash; }
    public void setPasswordHash(String v) { this.passwordHash = v; }
    public String getFullName()      { return fullName; }
    public void setFullName(String v){ this.fullName = v; }
    public UserRole getRole()        { return role; }
    public void setRole(UserRole v)  { this.role = v; }
    public Instant getCreatedAt()    { return createdAt; }

    // ── UserDetails ────────────────────────────────────────────
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    @Override public String getPassword()             { return passwordHash; }
    @Override public String getUsername()             { return email; }
    @Override public boolean isAccountNonExpired()    { return true; }
    @Override public boolean isAccountNonLocked()     { return true; }
    @Override public boolean isCredentialsNonExpired(){ return true; }
    @Override public boolean isEnabled()              { return true; }

    // ── Builder ────────────────────────────────────────────────
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private String email, passwordHash, fullName;
        private UserRole role = UserRole.STUDENT;

        public Builder id(UUID id)                    { this.id = id; return this; }
        public Builder email(String v)                { this.email = v; return this; }
        public Builder passwordHash(String v)         { this.passwordHash = v; return this; }
        public Builder fullName(String v)             { this.fullName = v; return this; }
        public Builder role(UserRole v)               { this.role = v; return this; }

        public User build() {
            User u = new User();
            u.id           = this.id;
            u.email        = this.email;
            u.passwordHash = this.passwordHash;
            u.fullName     = this.fullName;
            u.role         = this.role != null ? this.role : UserRole.STUDENT;
            u.createdAt    = Instant.now();
            return u;
        }
    }
}
