# Auth Backend (Spring Boot 3 + MySQL + JWT + Swagger)

## Yêu cầu
- Java 17+
- Maven (hoặc IDE chạy class `AuthBackendMysqlApplication`)

## MySQL
Có thể tạo DB thủ công:
```sql
CREATE DATABASE demo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
Sau đó sửa `src/main/resources/application.properties` cho đúng user/password.

`spring.jpa.hibernate.ddl-auto=update` sẽ tự tạo/cập nhật bảng khi bạn thêm/sửa Entity.

## Chạy
```bash
mvn spring-boot:run
```
Swagger UI: http://localhost:8080/swagger-ui.html

## API
- POST `/api/auth/register` → { username, email, password }
- POST `/api/auth/login` → { username, password }
- GET `/api/users/me` → cần header `Authorization: Bearer <token>`
