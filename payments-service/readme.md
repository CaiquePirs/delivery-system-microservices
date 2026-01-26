# 💳 Payment Service

The **Payment Service** is the financial engine of the **Delivery System**. It handles transaction processing, payment status management, and integrates with external gateways (simulated) via **webhooks** to ensure orders are processed only after payment is secured.

---

## 📌 Overview

- Manages the transition of an order from `PENDING_PAYMENT` → `PAID` or `FAILED`
- Designed to be **highly resilient**, ensuring payment status can be synchronized via **asynchronous callbacks (Webhooks)** even in case of network failures

---

## 🚀 Technical Stack

- **Java 21 & Spring Boot 3.2.4**
- **MongoDB Cloud**: Stores payment documents, transaction logs, and gateway metadata
- **RabbitMQ**: Consumes order events and publishes authorization results
- **OpenFeign**: Internal communication to validate order values and metadata
- **Webhook Architecture**: Secure endpoints for receiving payment confirmations from external providers

---

## 🔑 Key Features

- **Multi-Method Support**: Handles PIX, CARD, and CASH payments
- **Gateway Simulation**: Demonstrates full transaction flow with a simulated payment provider
- **Webhook Callbacks**: Receives notifications from the "bank" or gateway about transaction success/failure
- **Idempotency & Safety**: Prevents duplicate payments and logs failures for auditing

---

## 📩 Messaging Architecture (RabbitMQ)

The Payment Service bridges **Order creation** and **Delivery start**:

- **Consumed Events**:
    - `verify-payment-queue`: From Order Service. Receives new order data to initiate payment

- **Published Events**:
    - `approved-payment-fanout`: Broadcasts confirmed payments
    - **Subscribers**: Order Service (update status), Delivery Service (start logistics), Notification Service (inform customer)

---

## 🛠️ Main Endpoints (via Gateway :8080)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST   | /api/payments/webhook | Internal/External callback for payment confirmation | Key/Webhook Secret |

---

## ⚙️ Environment Variables Required

Ensure these keys are configured in your `.env` for the Payment module:

```env
PAYMENTS_MONGO_DB_URI=
RABBITMQ_HOST=
PAYMENTS_RABBITMQ_SUBSCRIBER_QUEUE=
PAYMENTS_RABBITMQ_PUBLISHER_EXCHANGE=
EUREKA_ZONE_URL=
```

🧬 Service Logic Highlight: Webhook Synchronization
* Simulates real-world payment flows:
* When a payment is requested, the service does not immediately mark it as "Success"
* Communicates with a simulated gateway
* Status remains PENDING until the Webhook endpoint receives a valid payload
* Once confirmed, the database is updated and a RabbitMQ event is published to release the order for delivery
* This ensures asynchronous consistency and demonstrates mastery of resilient, non-blocking payment handling.