package com.lablink.auth;

import com.lablink.borrow.BorrowRecord;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
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

    @Column(name = "id_number", unique = true, length = 20)
    private String idNumber;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "role", nullable = false, columnDefinition = "user_role")
    private UserRole role = UserRole.STUDENT;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<BorrowRecord> borrowRecords;

    public User() {}

    // ── Getters / Setters ──────────────────────────────────────
    public UUID getId()                  { return id; }
    public String getEmail()             { return email; }
    public void setEmail(String v)       { this.email = v; }
    public String getPasswordHash()      { return passwordHash; }
    public void setPasswordHash(String v){ this.passwordHash = v; }
    public String getFullName()          { return fullName; }
    public void setFullName(String v)    { this.fullName = v; }
    public String getIdNumber()          { return idNumber; }
    public void setIdNumber(String v)    { this.idNumber = v; }
    public UserRole getRole()            { return role; }
    public void setRole(UserRole v)      { this.role = v; }
    public Instant getCreatedAt()        { return createdAt; }

    // ── UserDetails ────────────────────────────────────────────
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    @Override public String getPassword()              { return passwordHash; }
    @Override public String getUsername()              { return email; }
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }

    // ── Builder ────────────────────────────────────────────────
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private String email, passwordHash, fullName, idNumber;
        private UserRole role = UserRole.STUDENT;

        public Builder id(UUID v)           { this.id = v; return this; }
        public Builder email(String v)      { this.email = v; return this; }
        public Builder passwordHash(String v){ this.passwordHash = v; return this; }
        public Builder fullName(String v)   { this.fullName = v; return this; }
        public Builder idNumber(String v)   { this.idNumber = v; return this; }
        public Builder role(UserRole v)     { this.role = v; return this; }

        public User build() {
            User u = new User();
            u.id           = this.id;
            u.email        = this.email;
            u.passwordHash = this.passwordHash;
            u.fullName     = this.fullName;
            u.idNumber     = this.idNumber;
            u.role         = this.role != null ? this.role : UserRole.STUDENT;
            u.createdAt    = Instant.now();
            return u;
        }
    }
}
