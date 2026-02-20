# LabLink Backend — Project Scaffold

## What This Delivers

This is the **foundation layer** only — no domain entities, no controllers yet. It establishes:

| File/Package | Purpose |
|---|---|
| `pom.xml` | All Maven dependencies (Spring Boot 3.2, Security, JPA, JWT, Lombok, PostgreSQL) |
| `application*.properties` | Config split into base / dev / prod profiles |
| `common/ApiResponse.java` | Standard JSON wrapper matching the SDD contract |
| `exception/GlobalExceptionHandler.java` | Maps all exceptions to `ApiResponse` error format |
| `exception/BusinessException.java` | Domain exception with error code + HTTP status |
| `config/SecurityConfig.java` | JWT filter chain, CORS, RBAC endpoint rules |
| `config/ApplicationConfig.java` | `BCryptPasswordEncoder`, `AuthenticationProvider`, `AuthenticationManager` beans |
| `security/JwtService.java` | Token generation, validation, claims extraction |
| `security/JwtAuthenticationFilter.java` | `OncePerRequestFilter` — validates Bearer token per request |
| `security/UserDetailsServiceImpl.java` | Spring Security bridge (stubbed, wired in Auth module) |
| `resources/schema.sql` | Full PostgreSQL schema with indexes, enums, constraints |

---

## Project Structure

```
lablink-backend/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/lablink/
    │   │   ├── LablinkApplication.java
    │   │   ├── common/
    │   │   │   └── ApiResponse.java
    │   │   ├── config/
    │   │   │   ├── ApplicationConfig.java
    │   │   │   └── SecurityConfig.java
    │   │   ├── exception/
    │   │   │   ├── BusinessException.java
    │   │   │   └── GlobalExceptionHandler.java
    │   │   └── security/
    │   │       ├── JwtAuthenticationFilter.java
    │   │       ├── JwtService.java
    │   │       └── UserDetailsServiceImpl.java
    │   └── resources/
    │       ├── application.properties
    │       ├── application-dev.properties
    │       ├── application-prod.properties
    │       └── schema.sql
    └── test/
        └── java/com/lablink/
```

**Next modules will add** (following the same package convention):

```
├── auth/
│   ├── AuthController.java
│   ├── AuthService.java
│   ├── dto/  (RegisterRequest, LoginRequest, AuthResponse)
│   └── User.java / UserRepository.java
├── equipment/
│   ├── EquipmentController.java
│   ├── EquipmentService.java
│   ├── Equipment.java
│   └── EquipmentRepository.java
└── borrow/
    ├── BorrowController.java
    ├── BorrowService.java        ← @Transactional lives here
    └── BorrowRecord.java
```

---

## How to Run (Dev)

### Prerequisites
- Java 17+
- PostgreSQL 14+ running locally
- Maven 3.9+

### 1. Create the database
```sql
CREATE DATABASE lablink_dev;
```

### 2. Apply the schema
```bash
psql -U postgres -d lablink_dev -f src/main/resources/schema.sql
```

### 3. Run the application
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The server starts on `http://localhost:8080`.

---

## Environment Variables (Production)

Set these on Railway/Render before deploying:

| Variable | Description |
|---|---|
| `DATABASE_URL` | `jdbc:postgresql://host:5432/lablink_prod` |
| `DATABASE_USER` | DB username |
| `DATABASE_PASSWORD` | DB password |
| `JWT_SECRET` | Base64-encoded 256-bit secret (see generation below) |
| `SPRING_PROFILES_ACTIVE` | `prod` |

### Generate a production JWT secret
```bash
openssl rand -base64 32
```

⚠️ **Never commit a real `JWT_SECRET` to source control.**

---

## Key Design Decisions

### RBAC (Role-Based Access Control)
Enforced at two levels:
1. **`SecurityConfig`** — URL-level rules (fast, no DB hit)
2. **`@PreAuthorize`** — Method-level rules in Service classes (fine-grained)

Both are required because URL patterns alone can be bypassed if routing changes.

### BCrypt Strength 12
The SDD specifies strength 12 (~250ms hash time). This is intentionally slow to resist brute-force. Don't lower it.

### `BusinessException` Pattern
Controllers should never construct HTTP responses. They call Services which throw `BusinessException`. `GlobalExceptionHandler` maps these to `ApiResponse`. This keeps all HTTP concerns in one place.

### Schema Constraint: One Active Borrow Per Item
```sql
CONSTRAINT uq_equipment_active_borrow
    EXCLUDE USING btree (equipment_id WITH =)
    WHERE (status = 'ACTIVE')
```
This is a **database-level exclusion constraint** — even if two concurrent requests pass the application-layer check simultaneously (race condition), the DB rejects the second insert. This satisfies Journey 11 (borrowing failure on simultaneous requests).

---

## What's Next

| Module | Key Files |
|---|---|
| **Auth** | `User.java`, `AuthController`, `AuthService`, `RegisterRequest/LoginRequest DTOs` — wires `UserDetailsServiceImpl` |
| **Equipment** | `Equipment.java`, `Category.java`, `EquipmentController`, paginated GET + admin CRUD |
| **Borrow** | `BorrowRecord.java`, `BorrowService` with `@Transactional` — the core business logic |

Tell me which module to build next.
