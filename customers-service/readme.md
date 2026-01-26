# 🧍 Customer Service

The **Customer Service** is responsible for managing the lifecycle of users within the **Delivery System**. It handles profile data, personal information, and multiple delivery addresses, ensuring that all customer-related data is available for orders and deliveries.

---

## 📌 Overview

This service maintains the **Customer entity**. It is not just a simple CRUD; it is integrated into the **event-driven ecosystem**.

- When a customer is deleted, this service initiates a cascade of events to ensure **data consistency** and **security** across the entire platform.

---

## 🚀 Technical Stack

- **Java 21 & Spring Boot 3.2.4**
- **PostgreSQL**: Primary relational database for profiles and addresses
- **Spring Data JPA**: Robust data persistence
- **RabbitMQ**: Publishes deletion events to the Authentication Service
- **OpenFeign**: Allows other services to query customer data synchronously
- **Redis Cloud**: Caching customer profiles to reduce database load

---

## 🔑 Key Features

- **Profile Management**: Complete CRUD for customers and their addresses
- **Address Logic**: A customer can have multiple registered addresses (Home, Work, etc.)
- **Security Integration**: Only authenticated customers can access or modify their own data, verified via JWT
- **Self-Service Profile**: Dedicated controller for customers to fetch their own data using their access token

---

## 📩 Messaging Architecture (RabbitMQ)

This service acts as a **Publisher** for synchronization and cleanup tasks:

- **Published Events**:
    - `customer-deleted-queue`: Triggered when a customer profile is deleted through this service

- **Subscriber**:
    - The **Authentication Service** consumes this message to programmatically delete the user from Keycloak, ensuring they can no longer log in

---

## 🛠️ Main Endpoints (via Gateway :8080)

| Method | Endpoint | Description                               | Auth Required |
|--------|-----|-------------------------------------------|---------------|
| GET    | /api/customers/profile | Returns the logged-in user's profile data | Yes |
| GET    | /api/customers/{id} | Returns customer data by ID               | Yes |
| GET    | /api/customers/address/{id} | Retrieves a specific address by ID for the logged-in customer | Yes (CUSTOMER) |
| PUT    | /api/customers/{id} | Updates profile information               | Yes |
| POST   | /api/customers/address | Adds a new delivery address for the logged-in customer | Yes (CUSTOMER) |
| POST   | /api/customers | Creates a new customer                    | Yes |
| POST   | /api/customers/address | Adds a new delivery address               | Yes |
| DELETE | /api/customers/{id} | Deletes profile and triggers Keycloak sync | Yes |
| DELETE | /api/customers/profile| Disables a logged-in customer and sync with Keycloak to delete the user. | Yes |

---

## ⚙️ Environment Variables Required

Ensure your `.env` file contains these keys for the Customer module:

```env
CUSTOMER_POSTGRES_DB=
CUSTOMER_POSTGRES_USER=
CUSTOMER_POSTGRES_PASSWORD=
CUSTOMER_POSTGRES_URL=
RABBITMQ_HOST=
CUSTOMERS_DELETED_QUEUE=
EUREKA_ZONE_URL=
```

## 🧬 Service Logic Highlight: Parallel Data Fetching
During the order flow, this service is often called by the Order Service
Thanks to OpenFeign and Redis caching, profile information is returned with high performance
This allows the system to process order requests without bottlenecks