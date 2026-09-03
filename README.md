# Zest India IT - Product REST API

Technical assessment implementation for the Java Backend Developer position.

## Technology

- Java 17
- Spring Boot 3.5.5
- Spring Data JPA / Hibernate
- MySQL 8.4
- Spring Security
- JWT access tokens
- Rotating opaque refresh tokens
- Jakarta Validation
- JUnit 5 / Mockito / Spring Boot Test
- H2 for tests
- Swagger/OpenAPI
- Docker / Docker Compose
- Async audit processing
- CORS configuration

## Architecture

The application follows a layered structure:

```text
controller
   |
service
   |
repository
   |
entity
```

Supporting packages:

```text
config       -> Security, OpenAPI, async executor, seed data
dto          -> API request/response models
security     -> JWT filter and JWT service
exception    -> Standardized API error handling
```

Constructor injection is used throughout.

## API

### Authentication

`POST /api/v1/auth/login`

Example:

```json
{
  "username": "admin",
  "password": "Admin@123"
}
```

`POST /api/v1/auth/refresh`

```json
{
  "refreshToken": "your-refresh-token"
}
```

Refresh tokens are stored hashed in the database and rotated on every successful refresh.

### Products

- `GET /api/v1/products?page=0&size=10`
- `GET /api/v1/products/{id}`
- `POST /api/v1/products`
- `PUT /api/v1/products/{id}`
- `DELETE /api/v1/products/{id}`
- `GET /api/v1/products/{id}/items`

Product reads are available to `USER` and `ADMIN`.
Product create/update/delete operations require `ADMIN`.

Example product request:

```json
{
  "productName": "Laptop"
}
```

The database also contains the required `item` relationship and endpoint design can be extended with:

`GET /api/v1/products/{id}/items`

## Default development users

These are created only when missing:

| Username | Password | Role |
|---|---|---|
| admin | Admin@123 | ADMIN |
| user | User@123 | USER |

Change these credentials and JWT secrets before any real deployment.

## Run locally

1. Create a MySQL database named `product_db`.
2. Set:

```text
DB_URL=jdbc:mysql://localhost:3306/product_db?useSSL=false&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=root
JWT_SECRET=<long-random-secret>
```

3. Run:

```bash
mvn clean test
mvn spring-boot:run
```

Swagger:

`http://localhost:8080/swagger-ui.html`

## Run with Docker

```bash
docker compose up --build
```

Application:

`http://localhost:8080`

Swagger:

`http://localhost:8080/swagger-ui.html`

## Testing

Run:

```bash
mvn clean test
```

The test suite includes:

- Service unit tests with Mockito
- Controller test with MockMvc
- Spring Boot application context test
- H2 integration test for the product repository
- H2 in-memory database configuration

## Database and indexing

The `product` table has indexes for product name and creation timestamp.
The `item` table has an index on `product_id`.
The refresh token table has unique indexing on the token hash and an index on user id.
The application uses `GenerationType.IDENTITY`, which maps correctly to MySQL auto-increment.

## Error response

Validation and application errors use a consistent structure:

```json
{
  "timestamp": "2026-09-03T09:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/products",
  "validationErrors": {
    "productName": "productName is required"
  }
}
```

## Production hardening notes

For production:

- Store secrets outside source control.
- Use HTTPS/TLS at the load balancer or reverse proxy.
- Replace development credentials.
- Restrict CORS to trusted frontend origins.
- Prefer database migrations such as Flyway or Liquibase.
- Use secure secret management.
- Add rate limiting and audit persistence as required.
