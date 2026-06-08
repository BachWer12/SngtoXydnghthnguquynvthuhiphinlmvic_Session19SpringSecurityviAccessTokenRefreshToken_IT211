# PHẦN 1 - THIẾT KẾ KIẾN TRÚC

## 1. Kiến trúc tổng thể

```mermaid
flowchart LR

    Client[Client Browser]
    AuthController[Auth Controller]
    JwtFilter[JWT Filter]
    Security[Spring Security]

    UserDB[(User Database)]
    RevokedDB[(Revoked Token Store)]

    Access[Access Token 15m]
    Refresh[Refresh Token 7d]

    Client --> AuthController
    AuthController --> UserDB
    AuthController --> Access
    AuthController --> Refresh

    Client --> JwtFilter
    JwtFilter --> Security
    Security --> UserDB
    JwtFilter --> RevokedDB
```


# 2. Kịch bản Login

## Mục tiêu

Người dùng đăng nhập thành công và nhận:

- Access Token (15 phút)
- Refresh Token (7 ngày)

## Sequence Diagram

```mermaid
sequenceDiagram

    participant User
    participant AuthController
    participant AuthService
    participant Database
    participant JwtService

    User->>AuthController: POST /auth/login

    AuthController->>AuthService: authenticate()

    AuthService->>Database: findUser()

    Database-->>AuthService: User

    AuthService-->>AuthController: Success

    AuthController->>JwtService: Generate Access Token

    AuthController->>JwtService: Generate Refresh Token

    JwtService-->>AuthController: Tokens

    AuthController-->>User: Access + Refresh Token
```

---

# 3. Kịch bản Refresh Token

## Mục tiêu

Tự động cấp Access Token mới mà không cần đăng nhập lại.

## Sequence Diagram

```mermaid
sequenceDiagram

    participant Client
    participant AuthController
    participant JwtService
    participant RevokedStore

    Client->>AuthController: POST /auth/refresh

    AuthController->>JwtService: Validate Refresh Token

    JwtService->>RevokedStore: Check blacklist

    RevokedStore-->>JwtService: Not Revoked

    JwtService-->>AuthController: Valid

    AuthController->>JwtService: Generate new Access Token

    JwtService-->>AuthController: New Access Token

    AuthController-->>Client: New Access Token
```

---

# 4. Kịch bản Revoke Token

## Mục tiêu

Thu hồi hoàn toàn quyền truy cập của người dùng.

## Sequence Diagram

```mermaid
sequenceDiagram

    participant Admin
    participant AuthController
    participant RevokedStore

    Admin->>AuthController: POST /auth/revoke

    AuthController->>RevokedStore: Save Refresh Token

    RevokedStore-->>AuthController: Success

    AuthController-->>Admin: Token Revoked
```

---

# 5. Chiến lược lưu trữ Revoke

## Phương án sử dụng Database

```mermaid
erDiagram

    REVOKED_TOKENS {
        BIGINT id PK
        VARCHAR token
        TIMESTAMP revoked_at
        TIMESTAMP expired_at
    }
```

### Ưu điểm

- Dễ triển khai
- Dễ audit
- Dễ backup

### Nhược điểm

- Chậm hơn Redis

---

## Phương án sử dụng Redis

```mermaid
flowchart LR

    RefreshToken --> RedisBlacklist

    RedisBlacklist --> Exists

    Exists --> Allow
    Exists --> Reject
```

### Ưu điểm

- Tốc độ O(1)
- Phù hợp hệ thống lớn

### Nhược điểm

- Cần thêm Redis Server

---

# 6. Xử lý Edge Cases

## Access Token hết hạn

```mermaid
flowchart LR

    Request --> JwtFilter

    JwtFilter --> Expired

    Expired --> Response401
```

Response:

```json
{
  "message":"Access Token expired"
}
```

---

## Refresh Token bị Revoke

```mermaid
flowchart LR

    RefreshRequest --> BlackListCheck

    BlackListCheck --> Revoked

    Revoked --> Response401
```

Response:

```json
{
  "message":"Refresh Token revoked"
}
```

---

## Token giả mạo

```mermaid
flowchart LR

    Request --> SignatureValidation

    SignatureValidation --> Invalid

    Invalid --> Response401
```

Response:

```json
{
  "message":"Invalid token signature"
}
```