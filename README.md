Customer Registration Service

Customer Registration Service is a Spring Boot 3.5 RESTful microservice for managing customer information.
It provides full CRUD functionality, partial updates using PATCH, PII masking, correlation ID–based logging,
and a comprehensive testing strategy.

This service demonstrates clean layered architecture, enterprise-grade observability,
security-aware design, and RESTful API principles commonly used in banking and fintech applications.


Features

- Full customer CRUD operations (Create, Read, Update, Patch, Delete)
- REST API versioning using /api/v1
- H2 in-memory database for development and testing
- Global exception handling using ControllerAdvice
- Lombok for reduced boilerplate code
- Centralized logging using Logback
- Automatic correlation ID generation using X-Correlation-Id
- PII masking for sensitive fields such as email and mobile
- Support for both full update (PUT) and partial update (PATCH)
- Postman collection for API testing
- OpenAPI-ready contract-first design


Technology Stack

- Java 17
- Spring Boot 3.5
- Spring Web
- Spring Data JPA
- H2 Database
- Lombok
- Logback (text and JSON logs)
- MDC (Mapped Diagnostic Context)
- JUnit 5 and Mockito


Project Structure

src/main/java/com/dj/customer
controller
service
repository
entity
dto
exception
filter
CorrelationIdFilter.java
CustomerRegistrationServiceApplication.java

src/main/resources
application.yml
logback-spring.xml

src/test/java/com/dj/customer
repository
service
controller
integration

postman
customer-registration.postman_collection.json


Running the Application

Using Maven
Run the following command from the project root:
mvn spring-boot:run

Using an IDE
Run the main class:
CustomerRegistrationServiceApplication.java

The application will start on:
http://localhost:8080


H2 Database Console

The H2 console can be accessed at:
http://localhost:8080/h2-console

Connection details:
JDBC URL: jdbc:h2:mem:customerdb
Username: sa
Password: empty


REST API Endpoints

Base URL:
http://localhost:8080/api/v1/customers

List all customers
GET /customers

Get customer by ID
GET /customers/{id}

Create customer
POST /customers

Update customer (full update)
PUT /customers/{id}

Update customer (partial update)
PATCH /customers/{id}

Delete customer
DELETE /customers/{id}


Testing Strategy

This project follows a complete enterprise testing pyramid.

Repository Tests
Uses DataJpaTest with a real H2 database to validate JPA mappings and queries.

Service Tests
Pure unit tests using Mockito to validate business logic and exception scenarios.

Controller Tests
Uses WebMvcTest and MockMvc to validate HTTP behavior, response structure,
status codes, and masked response fields.

Integration Tests
Uses SpringBootTest and AutoConfigureMockMvc to validate end-to-end flows
including filters, exception handling, persistence, and controllers.


Logging and Correlation ID

Each request automatically generates a correlation ID.
The correlation ID is stored in MDC and propagated across all logs.
It is also returned in the HTTP response header as X-Correlation-Id.

Example log entry:
2025-12-24 00:39:43.861 INFO [792c5e2c-de08-4647-82f5-d15829eb5b7f]
CustomerController - Fetching customer id=1


Security and Compliance

PII masking is applied to API responses to avoid sensitive data exposure.

Examples:
Email: s****@gmail.com
Mobile: 98******10

Masking is enforced through response interception mechanisms,
ensuring sensitive data is not exposed in logs or API responses.


Error Handling

Centralized exception handling is implemented using ControllerAdvice.
Custom exceptions such as ResourceNotFoundException are mapped to clean HTTP responses
with appropriate status codes such as 400 and 404.



