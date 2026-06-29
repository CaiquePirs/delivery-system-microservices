# Delivery System — Microservices
A production-grade distributed delivery system inspired by platforms like iFood, built with scalability, resilience, security, and cloud-readiness as first-class concerns.

This repository is a monorepo composed of **9 independent Spring Boot microservices**. Each service owns its database, messaging contracts, Dockerfile, and CI/CD pipeline — reflecting enterprise backend standards focused on loose coupling, fault tolerance, and maintainability.

## Architecture Overview
<div align="center">
  <img src="delivery-system-architecture.png" width="850px" alt="System architecture diagram">
</div>

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Services](#services)
- [Main Flow](#main-flow)
- [Event Communication](#event-communication)
- [API Reference](#api-reference)
- [Business Rules](#business-rules)
- [Email Notification Templates](#email-notification-templates)
- [Technologies](#technologies)
- [Repository Structure](#repository-structure)
- [Getting Started](#getting-started)
- [Running Tests](#running-tests)
- [CI/CD & Infrastructure](#cicd--infrastructure)
- [Author](#author)

---

### Services communicate in two main ways:

| Channel | Used For |
| --- | --- |
| **REST via OpenFeign** | Synchronous domain queries — customer, restaurant, menu, and order data |
| **RabbitMQ** | Business events — order creation, payment approval, delivery updates, user deletion |

External traffic enters through the **API Gateway**, while **Eureka Server** handles dynamic service registration and discovery. **Keycloak** manages all authentication and authorization flows.

---

### Core Components

| Component | Responsibility |
| --- | --- |
| **API Gateway** | Single entry point — routes requests to microservices and validates OAuth2/JWT tokens. |
| **Eureka Server** | Service registry and discovery for all microservices. |
| **Keycloak 22** | Identity provider responsible for authentication, token issuing, and access control. |
| **RabbitMQ 4** | Message broker for all asynchronous inter-service communication. |
| **Redis** | Cache layer to reduce repeated database and token-validation calls. |
| **PostgreSQL 16** | Relational database used by customers, restaurants, deliveries, and Keycloak. |
| **MongoDB** | Document database used by orders and payments for flexible schema storage. |

---

## Services

| Service | Description | Persistence |
| --- | --- | --- |
| `authentication-service` | Handles login, customer/restaurant registration in Keycloak, and internal service-to-service token generation. | Keycloak + Redis |
| `customers-service` | Manages customer profiles and delivery addresses. Publishes a deletion event when a customer is removed. | PostgreSQL + Redis |
| `restaurants-service` | Manages restaurants, operating status, and menu items. Publishes a deletion event when a restaurant is removed. | PostgreSQL + Redis |
| `orders-service` | Creates orders, calculates totals, validates all entities, and tracks order status through its lifecycle. | MongoDB |
| `payments-service` | Processes payments asynchronously, simulates an external gateway, and publishes payment-confirmed events. | MongoDB |
| `delivery-service` | Creates deliveries after payment approval, assigns a courier, and updates delivery status via webhook. | PostgreSQL |
| `notification-service` | Consumes lifecycle events and dispatches HTML email notifications via Spring Mail and Thymeleaf. | — |
| `infrastructure-service/gateway` | API Gateway built on Spring Cloud Gateway. | — |
| `infrastructure-service/eureka` | Service discovery based on Netflix Eureka. | — |

---

## Main Flow

```
1. Customer/Restaurant registers via Authentication Service
   └── Keycloak identity created → Domain service profile created

2. Customer creates an order in Orders Service
   └── Validates: customer, delivery address, restaurant (OPEN), menu items (AVAILABLE)

3. Order published to:
   ├── Payments Service  → start payment processing
   └── Notification Service → "order received" email sent to customer

4. Payments Service simulates external gateway → receives webhook confirmation

5. Payment approved event published to:
   ├── Orders Service       → status updated to PAID
   ├── Delivery Service     → delivery created, courier assigned
   └── Notification Service → "payment approved" email sent to customer
                           → "new order received" email sent to restaurant

6. Delivery Service triggers OUT_FOR_DELIVERY via webhook

7. Delivery ready event published to:
   ├── Orders Service       → status updated
   └── Notification Service → "out for delivery" email sent to customer
```

---

## Event Communication

| Source | Exchange / Queue | Consumers | Purpose |
| --- | --- | --- | --- |
| Orders | `ORDERS_RABBITMQ_EXCHANGE_VERIFY_PAYMENT` | Payments, Notifications | Start payment processing; notify customer the order was received. |
| Payments | `PAYMENTS_RABBITMQ_PUBLISHER_EXCHANGE` | Orders, Delivery, Notifications | Notify that payment was authorized. |
| Delivery | `DELIVERIES_RABBITMQ_PUBLISHER_DELIVERY_READY` | Orders, Notifications | Notify delivery status updates. |
| Customers | `CUSTOMERS_DELETED_QUEUE` | Authentication | Remove the user from Keycloak after customer deletion. |
| Restaurants | `RESTAURANT_DELETED_QUEUE` | Authentication | Remove the user from Keycloak after restaurant deletion. |

> All queue and exchange names are configured via environment variables defined in `.env` and `docker-compose.yml`.

---

## API Reference

All endpoints are exposed by individual services and accessible through the API Gateway according to its route configuration.

### Authentication

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/auth/login` | Authenticates a user and returns an access token. |
| `POST` | `/api/auth/internal-login` | Generates a token for internal service-to-service communication. |
| `POST` | `/api/auth/signUp-customers` | Registers a customer and creates its identity in Keycloak. |
| `POST` | `/api/auth/signUp-restaurants` | Registers a restaurant and creates its identity in Keycloak. |

### Customers

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/customers` | Creates a customer. |
| `GET` | `/api/customers/{id}` | Retrieves a customer by ID. |
| `DELETE` | `/api/customers/{id}` | Deletes a customer and publishes a deletion event. |
| `GET` | `/api/customers/profile` | Retrieves the authenticated customer's profile. |
| `DELETE` | `/api/customers/profile` | Deletes the authenticated customer's profile. |
| `POST` | `/api/customers/my-addresses` | Creates an address for the authenticated customer. |
| `GET` | `/api/customers/my-addresses/{id}` | Retrieves an address by ID. |

### Restaurants & Menu

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/restaurants` | Creates a restaurant. |
| `GET` | `/api/restaurants` | Lists restaurants with optional filters. |
| `GET` | `/api/restaurants/{id}` | Retrieves a restaurant by ID. |
| `DELETE` | `/api/restaurants/{id}` | Deletes a restaurant and publishes a deletion event. |
| `GET` | `/api/restaurants/profile` | Retrieves the authenticated restaurant's profile. |
| `DELETE` | `/api/restaurants/profile` | Deletes the authenticated restaurant's profile. |
| `PATCH` | `/api/restaurants/profile` | Toggles restaurant status between `OPEN` and `CLOSED`. |
| `POST` | `/api/restaurants/{restaurantId}/menus` | Creates a menu item. |
| `GET` | `/api/restaurants/{restaurantId}/menus/{id}` | Retrieves an available menu item. |
| `PATCH` | `/api/restaurants/{restaurantId}/menus/{id}` | Toggles a menu item's availability. |
| `DELETE` | `/api/restaurants/{restaurantId}/menus/{id}` | Disables a menu item. |

### Orders, Payments & Deliveries

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/orders` | Creates an order. |
| `GET` | `/api/orders/{id}` | Retrieves an order by ID. |
| `GET` | `/api/orders/{id}/customers` | Lists orders for a given customer. |
| `GET` | `/api/orders/{id}/restaurants` | Lists orders for a given restaurant. |
| `POST` | `/api/payments/webhook` | Receives external payment confirmation. |
| `POST` | `/api/deliveries/webhook/{id}` | Updates a delivery to out for delivery. |

---

## Business Rules

- Only authenticated users can access protected resources.
- Customers may only access data related to their own profile.
- Restaurants must be `OPEN` to accept orders.
- Menu items must be `AVAILABLE` to be included in an order.
- Orders start with `PENDING_PAYMENT` status.
- Payments start as `PENDING` and transition to `AUTHORIZED` or `FAILED`.
- An authorized payment updates the order to `PAID` and initiates the delivery flow.
- Deliveries progress through `ASSIGNED` → `OUT_FOR_DELIVERY` → `DELIVERED` (or `CANCELLED`).
- Customer or restaurant deletion propagates credential removal to Keycloak.

---

## Email Notification Templates

The system sends real-time HTML emails to customers and restaurants at every key lifecycle step. Templates are rendered with **Thymeleaf** and dispatched asynchronously by the **Notification Service** via **Spring Mail**.

<div align="center">
  <table style="width:100%">
    <tr>
      <td align="center"><b>Order Confirmation</b></td>
      <td align="center"><b>Payment Approved</b></td>
    </tr>
    <tr>
      <td align="center">
        <img src="./notification-service/src/main/resources/templates/Gmail%20-%20Order%20confirmed.png" width="350px" alt="Order confirmation email">
      </td>
      <td align="center">
        <img src="./notification-service/src/main/resources/templates/Gmail%20-%20Order%20payment%20approved.png" width="350px" alt="Payment approved email">
      </td>
    </tr>
    <tr>
      <td align="center"><b>Out for Delivery</b></td>
      <td align="center"><b>New Order Received (Restaurant)</b></td>
    </tr>
    <tr>
      <td align="center">
        <img src="./notification-service/src/main/resources/templates/Gmail%20-%20Order%20is%20out%20for%20delivery.png" width="350px" alt="Out for delivery email">
      </td>
      <td align="center">
        <img src="./notification-service/src/main/resources/templates/Gmail%20-%20New%20orders%20received.png" width="350px" alt="New order received email for restaurant">
      </td>
    </tr>
  </table>
  <p><i>Emails generated using Thymeleaf and dispatched via Spring Mail.</i></p>
</div>

---

## Technologies

| Category | Technology |
| --- | --- |
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.2.4 |
| **API Layer** | Spring Web, Spring Cloud Gateway |
| **Security** | Spring Security OAuth2 Resource Server, Keycloak 22 |
| **Service Discovery** | Spring Cloud Netflix Eureka (2023.0.6) |
| **Service Communication** | Spring Cloud OpenFeign |
| **Messaging** | Spring AMQP, RabbitMQ 4 |
| **Persistence** | Spring Data JPA (PostgreSQL 16), Spring Data MongoDB, Spring Data Redis |
| **Email** | Spring Mail, Thymeleaf |
| **Build & Packaging** | Maven, Docker, Docker Compose |
| **Testing** | JUnit 5, Mockito, Spring Boot Test, H2 (in-memory) |
| **Infrastructure** | AWS EC2, GitHub Actions, Docker Hub |

---

## Repository Structure

```text
.
├── authentication-service/
├── customers-service/
├── delivery-service/
├── infrastructure-service/
│   ├── eureka/
│   └── gateway/
├── notification-service/
├── orders-service/
├── payments-service/
├── restaurants-service/
├── .github/
│   └── workflows/              # Per-service CI/CD pipelines
├── docker-compose.yml
├── keycloak_realm_example.json
├── delivery-system-architecture.png
└── delivery-system-uml.png
```

---

## Getting Started

### Prerequisites

- Java 21
- Maven
- Docker & Docker Compose

### Environment Setup

Copy the example environment file and fill in all required variables:

```bash
cp .env.example .env
```

The `.env` file controls all database URLs, RabbitMQ credentials, Redis connection, Keycloak settings, email credentials, and queue/exchange names.

**Keycloak realm setup:**

1. Start the stack (see below).
2. Access the Keycloak admin console at `http://localhost:${KEYCLOAK_PORT_O1}`.
3. Import `keycloak_realm_example.json` to create the realm, client, and roles expected by the services.

### Start the Stack

```bash
docker compose up -d
```

| Service | Default URL |
| --- | --- |
| API Gateway | `http://localhost:${GATEWAY_PORT}` |
| Eureka Dashboard | `http://localhost:${EUREKA_SERVER_PORT}` |
| RabbitMQ Management | `http://localhost:${RABBITMQ_WEB_PORT}` |
| Keycloak Admin | `http://localhost:${KEYCLOAK_PORT_O1}` |

> Port values are resolved from your `.env` file.

---

## Running Tests

Each microservice has an independent Maven build. To run tests for a single service:

```bash
cd customers-service
mvn test
```

To run tests across all services sequentially:

```bash
for service in authentication-service customers-service restaurants-service orders-service payments-service delivery-service notification-service infrastructure-service/eureka infrastructure-service/gateway; do
  (cd "$service" && mvn test)
done
```

---

## CI/CD & Infrastructure

Each microservice has a dedicated **GitHub Actions** pipeline triggered on push to `main` when files under that service's directory change. All pipelines run on a **self-hosted runner on AWS EC2**.

### Pipeline Steps

**`build` job:**

1. Checkout code
2. Set up Java 21 (Temurin) with Maven cache
3. `mvn clean install` — compile, test, and package
4. Build Docker image
5. Push image to Docker Hub

**`deploy` job** (depends on `build`):

1. Pull the newly published image
2. Inject secrets as `.env` file
3. `docker compose up -d --no-deps <service>` — rolling service update
4. `docker image prune -f` — remove unused images

### Docker Hub Images

| Service | Image |
| --- | --- |
| `authentication-service` | `caiquepirs/authentication-service` |
| `customers-service` | `caiquepirs/customer-service` |
| `restaurants-service` | `caiquepirs/restaurants-service` |
| `orders-service` | `caiquepirs/orders-service` |
| `payments-service` | `caiquepirs/payments-service` |
| `delivery-service` | `caiquepirs/delivery-service` |
| `notification-service` | `caiquepirs/notification-service` |
| `gateway` | `caiquepirs/gateway-service` |
| `eureka` | `caiquepirs/eureka-service` |

---

## Author

**Caique Pires**

Backend Engineer · Java · Spring Boot · Microservices · Event-Driven Architecture