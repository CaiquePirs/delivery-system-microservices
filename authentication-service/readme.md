# 🔐 Authentication Service

This microservice acts as the **Identity and Access Management (IAM)** engine of the **Delivery System**. It bridges the gap between the domain microservices and **Keycloak**, ensuring centralized security, token management, and data synchronization.

---

## 📌 Overview

The Authentication Service is responsible for **user lifecycles and security sessions**. It handles:

- Login
- Token validation
- Initial registration of **Customers** and **Restaurants**

Its primary goal is to maintain a **single source of truth for identities** while keeping the domain services informed about user status changes.

---

## 🚀 Technical Stack

- **Java 21 & Spring Boot 3.5.7**
- **Spring Security & OAuth2 Resource Server**
- **Keycloak Admin Client**: Programmatic management of users, roles, and credentials
- **RabbitMQ**: Asynchronous communication for user deletion and synchronization
- **Redis**: Caching access tokens to optimize performance and reduce latency

---

## 🔑 Key Features

- **Centralized Login**: Validates user credentials against Keycloak and issues JWT tokens
- **Identity Sync**: Programmatically creates users in Keycloak with specific roles (`ROLE_CUSTOMER`, `ROLE_RESTAURANT`)
- **Security Filter**: Implements internal login logic for inter-service communication via a secure `internal-login` endpoint
- **User Lifecycle Management**: Manages the link between Keycloak IDs and local database IDs

---

## 📩 Messaging Architecture (RabbitMQ)

This service acts as a **Consumer** for deletion events to ensure that when a profile is removed from the system, its access is immediately revoked in Keycloak.

- **Consumed Queues**:
    - `customer-deleted-queue`: Listens for deletion events from the Customer Service. Upon receipt, removes the corresponding user from Keycloak.
    - `restaurant-deleted-queue`: Listens for deletion events from the Restaurant Service. Upon receipt, removes the restaurant's credentials from Keycloak.

---

## 🛠️ Main Endpoints (via Gateway :8080)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST   | /api/auth/login | Authenticates user and returns JWT | No |
| POST   | /api/auth/register/customer | Registers a new Customer in Keycloak | No |
| POST   | /api/auth/register/restaurant | Registers a new Restaurant in Keycloak | No |
| POST   | /api/auth/internal-login | Generates a service-to-service access token | Internal |

---

## ⚙️ Environment Variables Required

Ensure your `.env` file contains these keys for this module:

```env
SPRING_APPLICATION_NAME=authentication-service

# Keycloak
KEYCLOAK_TOKEN_URL=
KEYCLOAK_CLIENT_ID=
KEYCLOAK_CLIENT_SECRET=
SERVICE_TOKEN_URL=

# Redis
REDIS_HOST=
REDIS_PORT=

# RabbitMQ
RABBITMQ_USERNAME=
RABBITMQ_PASSWORD=
RABBITMQ_HOST=
RABBITMQ_PORT=
CUSTOMERS_DELETED_QUEUE=
RESTAURANT_DELETED_QUEUE=

# Eureka
EUREKA_ZONE_URL=
```

## 🧬 Service Logic Highlight: Parallel Registration
The service uses a robust error-handling mechanism: if the registration in Keycloak fails, the RabbitMQ message is not sent, preventing data inconsistency (Z-pattern / Saga-lite approach).