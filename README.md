Zest India IT - Product REST API

Technical assessment implementation for the "Java Backend Developer" position at "Zest India IT Pvt Ltd".

This project provides a secure, versioned RESTful API for managing Products and their Items using Java, Spring Boot, Spring Data JPA, MySQL, JWT authentication, refresh-token rotation, validation, testing, Swagger/OpenAPI, and Docker.

1. Technology Stack
•	Java 17+
•	Spring Boot 3.5.5
•	Spring Data JPA
•	Hibernate
•	MySQL
•	Spring Security
•	JWT Access Tokens
•	Rotating Opaque Refresh Tokens
•	Jakarta Validation
•	JUnit 5
•	Mockito
•	Spring Boot Test
•	H2 In-Memory Database for Testing
•	Swagger / OpenAPI
•	Docker
•	Docker Compose
•	Async Processing
•	CORS Configuration

2. Architecture
The application follows a layered architecture.
                    Client
                      |
               REST Controller
                      |
                  Service
                      |
                Repository
                      |
                   MySQL

3. API Versioning
All APIs use the required versioned URL structure:
/api/v1/

 4. Authentication API
Login
POST /api/v1/auth/login

Request:
json
{
  "username": "admin",
  "password": "Admin@123"
}

Successful response:
json
{
  "accessToken": "JWT_ACCESS_TOKEN",
  "refreshToken": "REFRESH_TOKEN",
  "role": "ADMIN"
}

The access token is a JWT.
The refresh token is an opaque randomly generated token. Only its SHA-256 hash is stored in the database.

 Refresh Token
POST /api/v1/auth/refresh

Request:
json
{
  "refreshToken": "your-refresh-token"
}

A successful refresh:
1. Validates the existing refresh token.
2. Checks expiration.
3. Checks whether it has already been revoked.
4. Revokes the old refresh token.
5. Generates a new access token.
6. Generates a new refresh token.
7. Stores the new refresh token hash.

This provides refresh-token rotation.

5. Product APIs

| Method | Endpoint                    | Authorization |
| ------ | --------------------------- | ------------- |
| GET    | /api/v1/products            | USER / ADMIN  |
| GET    | /api/v1/products/{id}       | USER / ADMIN  |
| POST   | /api/v1/products            | ADMIN         |
| PUT    | /api/v1/products/{id}       | ADMIN         |
| DELETE | /api/v1/products/{id}       | ADMIN         |
| GET    | /api/v1/products/{id}/items | USER / ADMIN  |


Get Products
GET /api/v1/products?page=0&size=10

Supports pagination.
Example:
?page=0&size=10

 Get Product By ID
GET /api/v1/products/{id}

Example:
GET /api/v1/products/1

Create Product
POST /api/v1/products

Requires:
ROLE_ADMIN

Request:
json
{
  "productName": "Laptop"
}

Update Product
PUT /api/v1/products/{id}

Requires:
ROLE_ADMIN

Request:
json
{
  "productName": "Gaming Laptop"
}

Delete Product
DELETE /api/v1/products/{id}

Requires:
ROLE_ADMIN

6. Product Items
Items have a relationship with Products.
Database relationship:
Product
   |
   | 1
   |
   |------ *
          |
         Item
The current endpoint for retrieving items is:
GET /api/v1/products/{productId}/items

Example:
GET /api/v1/products/1/items

Example response:
json
[
  {
    "id": 1,
    "quantity": 10
  },
  {
    "id": 2,
    "quantity": 20
    }
]

7. Database Structure
The application uses MySQL.
Main tables:
app_user
product
item
refresh_token

The Product → Item relationship is:
product.id
     |
     |
item.product_id
The `item.product_id` column is indexed to improve lookup performance.
8. Security
Spring Security is used for authentication and authorization.
 Roles
ADMIN
USER

Authorization rules
USER
 └── GET products
 └── GET product
 └── GET product items

ADMIN
 ├── GET products
 ├── GET product
 ├── GET product items
 ├── POST product
 ├── PUT product
 └── DELETE product

JWT authentication is implemented using a custom authentication filter.

The filter:
1. Reads the `Authorization` header.
2. Extracts the Bearer token.
3. Validates the JWT.
4. Extracts the username.
5. Loads the user.
6. Creates the Spring Security authentication.
7. Adds the user's role to the SecurityContext.

Example header:
Authorization: Bearer <JWT_TOKEN>

9. Input Validation
Jakarta Validation is used for validating API requests.
Invalid requests return a standardized error response.
Example:
json
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

10. Global Exception Handling
Application exceptions are handled centrally using:
GlobalExceptionHandler

This provides consistent JSON error responses instead of exposing internal exceptions directly to API clients.

11. Pagination
The product collection endpoint supports pagination.
Example:
GET /api/v1/products?page=0&size=10

Parameters:
page = page number
size = number of records per page

12. Database Indexing
Indexes are used for frequently queried columns.
 Product
Indexes include:
product name
created timestamp


Item
product_id

Refresh Token
token_hash
user_id

The `token_hash` is uniquely indexed.

13. Async Processing
Async processing is configured for operations that do not need to block the main API request, such as product audit processing.
The application contains:
AsyncConfig
ProductAuditService

14. CORS
CORS configuration is provided through Spring Security.
Allowed development origins include:

http://localhost:3000
http://localhost:8080

For production deployment, CORS should be restricted to trusted frontend origins.

15. Swagger / OpenAPI
Swagger UI is available at:
http://localhost:8080/swagger-ui.html

OpenAPI documentation:
http://localhost:8080/v3/api-docs

The protected APIs require a Bearer JWT token.
Authentication header:
Authorization: Bearer <JWT_TOKEN>

16. Default Development Users
The application initializes development users when they do not already exist.
| Username | Password  | Role  |
| -------- | --------- | ----- |
| admin    | Admin@123 | ADMIN |
| user     | User@123  | USER  |

These credentials are for development/testing only.
They must be changed before production deployment.

17. Configuration
The application reads sensitive configuration from environment variables.
Example:
DB_URL=jdbc:mysql://localhost:3306/product_db?useSSL=false&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=root
JWT_SECRET=<long-random-secret>
Do not commit real production secrets to GitHub.

18. Running Locally
Step 1 - Create Database
Create a MySQL database:
CREATE DATABASE product_db;

 Step 2 - Configure Environment Variables
Set:
DB_URL=jdbc:mysql://localhost:3306/product_db?useSSL=false&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=root
JWT_SECRET=<long-random-secret>

 Step 3 - Run Tests
Using Maven:
mvn clean test

Step 4 - Start Application
mvn spring-boot:run

Application:
http://localhost:8080

Swagger:
http://localhost:8080/swagger-ui.html

19. Running With Docker
Build and start the application using:
docker compose up --build

Application:
http://localhost:8080

Swagger:
http://localhost:8080/swagger-ui.html

Stop containers:
docker compose down

20. Testing
Run:
mvn clean test

The test suite includes:
•	JUnit 5
•	Mockito
•	Spring Boot Test
•	MockMvc controller testing
•	Service unit testing
•	Repository integration testing
•	H2 in-memory database

Tests are separated from the production MySQL database.

21. Test Database
The test environment uses H2.
Example:
src/test/resources/application-test.properties
This prevents tests from modifying the development MySQL database.

22. Docker Files
The repository contains:
text
Dockerfile
docker-compose.yml

These files provide containerized application execution and database setup.

23. Error Handling
The API uses standardized error responses.
Example:
json
{
  "timestamp": "2026-09-03T09:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found",
  "path": "/api/v1/products/999",
  "validationErrors": {}
}

24. Production Hardening
Before production deployment:
•	Replace development credentials.
•	Store secrets outside source control.
•	Use HTTPS/TLS.
•	Restrict CORS to trusted origins.
•	Use secure secret management.
•	Add database migrations using Flyway or Liquibase.
•	Add rate limiting.
•	Add monitoring and logging.
•	Configure production database connection pooling.
•	Do not expose sensitive exception details.
•	Rotate JWT signing secrets according to the security policy.

26. Author
Chaitanya Ashok Sakrate
Java Backend Developer

Technical Assessment:
Zest India IT Pvt Ltd
