# 📧 Notification Service

The **Notification Service** is the communication hub of the **Delivery System**. It monitors the message broker for status changes and automatically dispatches **formatted notifications (Emails)** to customers and restaurants.

---

## 📌 Overview

- This service is entirely **event-driven**.
- It does **not expose a REST API** for business logic; instead, it reacts to events published by the **Order**, **Payment**, and **Delivery** services.
- Ensures that every critical step of the **"Order Journey"** is communicated to the right person at the right time.

---

## 🚀 Technical Stack

- **Java 21 & Spring Boot 3.2.4**
- **Spring Mail**: For SMTP integration and email dispatching
- **Thymeleaf**: HTML engine for professional and dynamic email templates
- **RabbitMQ**: Consumes multiple queues to trigger notifications
- **OpenFeign**: Fetches missing order/customer details before sending notifications
- **Design Pattern – Strategy**: Decouples the notification logic from the delivery medium, allowing easy extension (e.g., adding SMS or WhatsApp notifications)

---

## 🔑 Key Features

- **Dynamic Templates**: Professional HTML emails for:
    - Order Created (Welcome/Confirmation)
    - Payment Approved
    - Order Out for Delivery
    - New Order Alert (for Restaurants)

- **Reliable Consumption**: Process-safe message handling to avoid duplicate notifications
- **Multi-Service Integration**: Uses Feign Clients to gather real-time data from other microservices to populate email fields

---

## 📩 Messaging Architecture (RabbitMQ)

This service is a **Heavy Consumer** that listens to the entire ecosystem:

- **Consumed Queues**:
    - `verify-payment-notify-queue`: Notifies the customer that their order was received and is awaiting payment
    - `payment-approved-notify-queue`: Confirms the payment to the customer and alerts the Restaurant about the new order to be prepared
    - `delivery-ready-notify-queue`: Sent when the status changes to "Out for Delivery," informing the customer that the courier is on the way

---

## 🛠️ Design Pattern: Strategy

- Notification logic is decoupled from the **delivery medium**
- New strategies (e.g., SMS, WhatsApp, Push) can be added **without modifying existing email logic**
- Enhances scalability and maintainability

---

## ⚙️ Environment Variables Required

Ensure these keys are configured in your `.env` to enable email sending and internal service communication:

```env
EMAIL_ADDRESS=
EMAIL_PASSWORD=
RABBITMQ_HOST=
NOTIFICATIONS_RABBITMQ_ORDER_CONFIRMATION_QUEUE=
NOTIFICATIONS_RABBITMQ_PAYMENT_APPROVED_QUEUE=
NOTIFICATIONS_RABBITMQ_DELIVERY_READY_QUEUE=

# Keycloak/Feign config for internal data fetching
KEYCLOAK_CLIENT_ID=
KEYCLOAK_CLIENT_SECRET=
SERVICE_TOKEN_URL=
```

🧬 Service Logic Highlight: Parallel Data Enrichment
* RabbitMQ messages often contain only IDs
* The Notification Service uses Feign Clients to call Customer Service and Order Service in parallel
* Retrieves customer's name, email, and order details
* Ensures notifications are personalized and accurate