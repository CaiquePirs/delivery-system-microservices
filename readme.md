# 🍔 Delivery System – Event-Driven Microservices Platform

A **production-grade distributed Delivery System**, inspired by platforms like **iFood**, designed with **scalability, resilience, security, and cloud-readiness** as first-class concerns.

This project demonstrates a **real-world microservices architecture**, orchestrating **orders, payments, deliveries, notifications, and authentication** through **asynchronous messaging** and **event-driven design**.

It was built to reflect **enterprise backend standards**, focusing on loose coupling, fault tolerance, and maintainability.

---

## 🧠 Architectural Vision

The system adopts a **Microservices + Event-Driven Architecture**, where each service:

* Owns its **own data (Database per Service)**
* Communicates asynchronously through **RabbitMQ**
* Is independently deployable
* Is secured via **centralized identity management (Keycloak)**

Synchronous communication (REST) is used **only when strictly necessary**, while **domain events** drive most business flows.

---

## 🏗️ System Architecture
<div align="center">
  <img src="delivery-system-architecture.png" width="850px" alt="Arquitetura do Sistema">
</div>

The platform is composed of a **mesh of autonomous microservices**, integrated through a **Message Broker** and exposed via an **API Gateway**.

### 🔷 Core Infrastructure Components

* **API Gateway (Spring Cloud Gateway)**
  Single entry point for all external traffic, responsible for routing, authentication filters, and request forwarding.

* **Service Discovery (Eureka Server)**
  Dynamic registration and discovery of microservice instances.

* **Identity Provider (Keycloak)**
  OAuth2 / OpenID Connect authentication server with Role-Based Access Control (RBAC).

* **Message Broker (RabbitMQ)**
  Central component for:

    * Asynchronous communication
    * Domain events
    * Webhooks
    * Loose coupling between services

* **Persistence Layer (Polyglot Persistence)**

    * **PostgreSQL (Amazon RDS)** – Customers, Restaurants, Deliveries
    * **MongoDB Atlas** – Orders and Payments
    * **Redis Cloud** – Session cache, access tokens, customers and restaurants cache

📌 **Architecture Diagram:**
Available in the repository under `/docs/delivery-system-architecture.pdf`

---

## 🛠️ Microservices & Responsibilities

### 🔐 Authentication Service

Direct integration with **Keycloak**, acting as the identity orchestration layer.

**Responsibilities:**

* Login and token management (JWT)
* Asynchronous user registration
* Data consistency between Keycloak and domain services

**Event-Driven Registration Flow:**

1. Customer or Restaurant is created
2. Authentication Service publishes an event via RabbitMQ
3. Domain services persist their data
4. User is registered in Keycloak simultaneously

Deleting a user in any domain service triggers **immediate deletion in Keycloak**.

---

### 🧍 Customer Service

Manages customer domain data and secure profile access.

* Full CRUD operations
* Multiple addresses per customer
* Profile endpoint protected by JWT
* Redis caching for customer data

---

### 🍽️ Restaurant Service

Responsible for restaurant management and operational state.

* Full CRUD operations
* Menu ownership
* Restaurant availability validation (OPEN / CLOSED)
* Redis cache for restaurant data

---

### 📋 Menu Service

Manages restaurant products.

* Each item belongs to exactly one restaurant
* Mandatory price, category, and availability
* Price validation (> 0)

---

### 🧾 Order Service

The **core service of the platform**, responsible for the full order lifecycle.

**Key Capabilities:**

* Automatic total price calculation
* Parallel data fetching using `CompletableFuture`
* Feign Clients for Customers and Restaurants
* Event publishing on order creation and updates
* Event consumption to react to payment and delivery changes

**Order Status Flow:**

* `PENDING_PAYMENT`
* `PAID`
* `OUT_FOR_DELIVERY`

Only **authenticated users** can place and access orders.

---

### 💳 Payment Service

Handles payment processing in a **fully asynchronous and decoupled manner**.

**Features:**

* Supported methods: **PIX, CARD, CASH**
* Simulated Payment Gateway
* Webhook endpoints for payment callbacks
* Publishes payment result events

**Payment Status Flow:**

* `PENDING`
* `AUTHORIZED`
* `FAILED`

---

### 🚚 Delivery Service

Manages delivery orchestration.

* Automatic courier assignment (simulated)
* Delivery fee calculation
* Secure webhook allowing only authenticated restaurants to start delivery
* Event-driven delivery lifecycle updates

**Delivery Status Flow:**

* `ASSIGNED`
* `OUT_FOR_DELIVERY`

---

### 🛵 Courier Service

Manages delivery personnel.

* Courier registration
* Vehicle types: BIKE, MOTORCYCLE, CAR
* Tracks delivered orders and delivery earnings

---

### 📧 Notification Service

Responsible for customer and restaurant notifications.

**Highlights:**

* Implements **Strategy Pattern** (Email / SMS)
* Dynamic HTML email templates
* Event-driven notification dispatch

**Notifications Sent:**

* Order created
* Payment confirmed
* Order out for delivery
* Restaurant notified of new order

---

## 🔁 Event-Driven Business Flow (Example)

1. Customer places an order
2. Order Service publishes `ORDER_CREATED`
3. Payment Service consumes the event and processes payment
4. Payment Service publishes `ORDER_PAID`
5. Notification Service sends confirmation
6. Delivery Service assigns courier and updates status

---

## 🔒 Security Architecture

* OAuth2 / OpenID Connect via Keycloak
* JWT tokens for external and internal communication
* Gateway-level authentication and authorization
* Secured service-to-service communication
* Redis cache for tokens and session data

---

## 🚀 Technologies & Design Patterns

* **Spring Boot & Spring Cloud** (Gateway, Eureka, OpenFeign)
* **RabbitMQ** (Exchanges, Queues, Event Routing)
* **Redis Cloud** (Caching & performance)
* **MongoDB Atlas & Amazon RDS**
* **Docker & Docker Compose**

**Design Patterns Applied:**

* Strategy
* Observer (Event-Driven)
* Factory
* Controller Advice (Global Exception Handling)

---

## 🧪 Testing Strategy

* Unit Tests
* Integration Tests
* Postman collections for end-to-end validation

---

## 📦 CI/CD & Infrastructure

Each microservice includes a **professional CI/CD pipeline**:

* **GitHub Actions (CI)**

    * Build automation
    * Test execution
    * Docker image generation

* **GitHub Actions (CD)**

    * Automated deployment to **AWS EC2**
    * Self-hosted GitHub Runner on EC2

* **Containerization**

    * Individual Dockerfiles per service
    * Centralized Docker Compose orchestration

---

## 🧩 Business Rules Enforced

* [x] Access to protected resources requires a valid JWT token
* [x] Restaurants must be in `OPEN` status to receive orders
* [x] Menu items must be available (`available = true`)
* [x] User deletion propagates immediately to Keycloak
* [x] Item prices are validated to prevent invalid values

---

---

## 📧 Email Notification Templates

The system ensures real-time communication with both customers and restaurants through professional HTML emails. These templates are generated using **Thymeleaf** and dispatched asynchronously via the **Notification Service**.

<div align="center">
  <table style="width:100%">
    <tr>
      <td align="center"><b>Order Confirmation</b></td>
      <td align="center"><b>Payment Approved</b></td>
    </tr>
    <tr>
      <td align="center">
        <img src="./notification-service/src/main/resources/templates/Gmail%20-%20Order%20confirmed.png" width="350px">
      </td>
      <td align="center">
        <img src="./notification-service/src/main/resources/templates/Gmail%20-%20Order%20payment%20approved.png" width="350px">
      </td>
    </tr>
    <tr>
      <td align="center"><b>Out for Delivery</b></td>
      <td align="center"><b>New Order Received (Restaurant)</b></td>
    </tr>
    <tr>
      <td align="center">
        <img src="./notification-service/src/main/resources/templates/Gmail%20-%20Order%20is%20out%20for%20delivery.png" width="350px">
      </td>
      <td align="center">
        <img src="./notification-service/src/main/resources/templates/Gmail%20-%20New%20orders%20received.png" width="350px">
      </td>
    </tr>
  </table>
  <p><i>Emails generated using Thymeleaf and dispatched via Spring Mail.</i></p>
</div>

---

## ⚙️ How to Run the Project

### 📋 Prerequisites

* Java 21
* Docker & Docker Compose
* MongoDB Atlas (or local MongoDB)
* Redis Cloud (or local Redis)

### 🚀 Installation

Clone the repository:

```bash
git clone https://github.com/CaiquePirs/delivery-system-microservices.git
```

Configure environment variables using the `.env.example` file (based on `docker-compose.yml`).
"Copy .env.example to .env and fill in your credentials before running the project."

Start the full ecosystem:

```bash
docker-compose up -d --build
```

Verify registered services via Eureka Dashboard:
```
http://localhost:8761
```

## 🔐 Identity and Access Management (Keycloak)

This project uses **Keycloak** for authentication and authorization. To simplify the setup, a pre-configured realm file is provided.

### 🚀 Importing the Realm
1. Start the infrastructure using `docker-compose up`.
2. Access the Keycloak Admin Console (usually at `localhost:8081`).
3. In the sidebar, click on **Master** (the current realm) and select **Create Realm**.
4. Click **Browse** and select the file: `keycloak_realm_example.json`.
5. Click **Create** to import all clients and roles.

> **Note:** Ensure your `.env` file matches the credentials defined in this realm for seamless integration.

---

## 🗺️ Diagrams & Documentation

* UML Diagrams (Entities)
* System Architecture Diagram
* Business Rules Documentation

---

## 🔗 GitHub Repository

👉 [https://github.com/CaiquePirs/delivery-system-microservices](https://github.com/CaiquePirs/delivery-system-microservices)

---

## 👨‍💻 Author

**Caique Pirs**
Backend Engineer | Java | Spring Boot | Microservices | Cloud | Event-Driven Systems
