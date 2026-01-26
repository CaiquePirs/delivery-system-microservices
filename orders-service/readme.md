# 🧾 Order Service

The **Order Service** is the central orchestrator of the **Delivery System**. It manages the entire lifecycle of an order, from the moment a customer adds items to their cart until the final delivery confirmation.

- Designed for **high consistency** and **performance**, utilizing **non-blocking operations** to handle distributed data.

---

## 📌 Overview

- Acts as a **hub**, connecting **Customers**, **Restaurants**, and **Payments**
- Validates **business rules** (e.g., restaurant availability, pricing)
- Ensures every order is properly persisted and **broadcasted** to the event-driven ecosystem

---

## 🚀 Technical Stack

- **Java 21 & Spring Boot 3.2.4**
- **MongoDB Cloud**: NoSQL database for flexible and scalable order storage
- **RabbitMQ**: Publishes events for payment verification and listens for status updates
- **OpenFeign**: Synchronous communication to fetch customer and restaurant details
- **Redis**: Caching layer for frequently accessed restaurant menus and access tokens
- **CompletableFuture**: Asynchronous and parallel data fetching to minimize response time

---

## 🔑 Key Features

- **Advanced Order Logic**: Automatic calculation of totals, subtotals, and delivery taxes
- **State Machine**: Manages order statuses: `PENDING_PAYMENT → PAID → PREPARING → OUT_FOR_DELIVERY → DELIVERED`
- **Parallel Data Enrichment**: Fetches Customer and Restaurant data simultaneously via Feign Clients before order creation
- **Menu Validation**: Ensures ordered items are available and belong to the correct restaurant
- **Context-Aware Queries**: Endpoints tailored for **Customers** (history) and **Restaurants** (order management)

---

## 📩 Messaging Architecture (RabbitMQ)

The service is the **primary Event Producer** for the order flow:

- **Published Events**:
    - `verify-payment-fanout`: Broadcast as soon as an order is created
        - **Consumers**: Payment Service (process transaction), Notification Service (alert customer)

- **Consumed Events**:
    - `payment-approved-order-queue`: Updates order status to `PAID` once transaction is authorized
    - `delivery-shipped-update-queue`: Updates order to `OUT_FOR_DELIVERY` or `DELIVERED` based on delivery updates

---

## 🛠️ Main Endpoints (via Gateway :8080)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST   | /api/orders | Creates a new order using parallel validation | Customer |
| GET    | /api/orders/customer | Lists order history for the logged-in customer | Customer |
| GET    | /api/orders/restaurant/{id} | Lists orders received by a specific restaurant | Restaurant |
| GET    | /api/orders/{id} | Fetches full order details including status | Yes |

---

## ⚙️ Environment Variables Required

Ensure these keys are configured in your `.env` for the Orders module:

```env
ORDERS_MONGO_DB_URI=
RABBITMQ_HOST=
ORDERS_RABBITMQ_EXCHANGE_VERIFY_PAYMENT=
ORDERS_RABBITMQ_EXCHANGE_APPROVED_PAYMENT=
ORDERS_RABBITMQ_DELIVERY_READY_UPDATE=
EUREKA_ZONE_URL=
```
## 🧬 Service Logic Highlight: High-Performance Fetching
* Uses CompletableFuture to avoid sequential call bottlenecks
* When an order request arrives:
* Calls CustomerService and RestaurantService in parallel
* Waits for both to return using .join()
* Reduces total latency by nearly 50% compared to sequential Feign calls

