# 🚚 Delivery Service

The **Delivery Service** manages the final leg of the order lifecycle: the logistics. It is responsible for assigning **couriers** to paid orders, tracking delivery statuses, and managing the fleet of delivery partners.

---

## 📌 Overview

Once an order is paid, the Delivery Service coordinates between the **restaurant** and the **courier**.

- Uses a **simulated assignment logic** to find available couriers based on vehicle type (Motorcycle, Bike, Car)
- Ensures the delivery address matches the customer's request

This service represents the **final step** in the operational flow of the Delivery System.

---

## 🚀 Technical Stack

- **Java 21 & Spring Boot 3.5.7**
- **PostgreSQL**: Stores delivery records, courier profiles, and delivery history
- **RabbitMQ**: Consumes payment events to initiate delivery
- **OpenFeign**: Fetches details from Customer, Restaurant, and Order services synchronously
- **Webhooks**: Secure endpoint for restaurants to signal when an order is ready for pickup

---

## 🔑 Key Features

- **Courier Management**: Full CRUD for delivery partners, including vehicle types and performance tracking
- **Automated Assignment**: Assigns a courier to a delivery as soon as payment is confirmed
- **Status Workflow**: Manages transitions: `ASSIGNED → OUT_FOR_DELIVERY → DELIVERED`
- **Restaurant Webhook**: Dedicated integration point for authorized restaurants to trigger "Out for Delivery" status

---

## 📩 Messaging Architecture (RabbitMQ)

This service is a **critical consumer** in the event-driven chain:

- **Consumed Events**:
    - `payment-approved-delivery-queue`: Triggered by the Payment Service. Creates a new delivery record and assigns an available courier

- **Published Events**:
    - `delivery-ready-fanout`: Broadcasts when an order status changes (e.g., Shipped). Consumed by the Notification Service and the Order Service to update the master record

---

## 🛠️ Main Endpoints (via Gateway :8080)
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST   | /api/deliveries/webhook/{id} | Called by restaurants to mark a delivery as ready. Triggers the delivery assignment and status workflow. | Yes
---

## ⚙️ Environment Variables Required

Ensure these keys are configured in your `.env` for the Delivery module:

```env
DELIVERY_POSTGRES_DB=
DELIVERY_POSTGRES_USER=
DELIVERY_POSTGRES_PASSWORD=
DELIVERY_POSTGRES_URL=
DELIVERIES_RABBITMQ_SUBSCRIBE_PAYMENT_QUEUE=
DELIVERIES_RABBITMQ_PUBLISHER_DELIVERY_READY=
