# 🍴 Restaurant Service

The **Restaurant Service** manages the merchant ecosystem within the **Delivery System**.
- Handles restaurant profiles, operating statuses, and digital menus that customers interact with to place orders.

---

## 📌 Overview

- Serves as the **source of truth** for all restaurant-related data
- Ensures only **active and open restaurants** can receive orders
- Manages the complex relationship between a restaurant and its multiple menu items
- Fully integrated with **Keycloak** for secure lifecycle management

---

## 🚀 Technical Stack

- **Java 21 & Spring Boot 3.2.4**
- **PostgreSQL**: Stores restaurants, addresses, and menu items
- **Spring Data JPA**: Advanced data modeling and persistence
- **RabbitMQ**: Publishes deletion events to Authentication Service and listens for sync events
- **OpenFeign**: Provides data to the Order and Delivery services
- **Redis**: Caches restaurant status and menus for sub-second API Gateway responses

---

## 🔑 Key Features

- **Menu Management**: Full control over categories, pricing, and item availability
- **Operating Status**: Global "Open/Closed" toggle affects order eligibility in real-time
- **Data Integrity**: Prevents deletion of restaurants with active orders and ensures price consistency
- **Administrative Control**: Endpoints for restaurant owners to update profile and catalog

---

## 📩 Messaging Architecture (RabbitMQ)

- **Published Events**:
    - `restaurant-deleted-queue`: Triggered when a restaurant is removed

- **Subscribers**:
    - **Authentication Service** consumes this to delete corresponding credentials in Keycloak, revoking portal access immediately

---

## 🛠️ Main Endpoints (via Gateway :8080)

### Restaurant Management

| Method  | Endpoint                        | Description                                                      | Auth Required / Role |
|---------|---------------------------------|------------------------------------------------------------------|--------------------|
| POST    | /api/restaurants                 | Creates a new restaurant                                         | Internal Service   |
| GET     | /api/restaurants/{id}            | Fetches a restaurant by ID                                       | Internal Service   |
| DELETE  | /api/restaurants/{id}            | Deletes a restaurant and triggers Keycloak sync                  | Internal Service   |
| GET     | /api/restaurants                 | Lists all restaurants with optional query filters               | Customer / Restaurant |
| GET     | /api/restaurants/me              | Returns logged-in restaurant profile                              | Restaurant         |
| DELETE  | /api/restaurants/me              | Disables logged-in restaurant                                     | Restaurant         |
| PATCH   | /api/restaurants/me/status       | Toggles OPEN/CLOSED status for logged-in restaurant              | Restaurant         |

### Menu Management

| Method  | Endpoint                                      | Description                                                       | Auth Required / Role |
|---------|-----------------------------------------------|-------------------------------------------------------------------|--------------------|
| POST    | /api/restaurants/{restaurantId}/menus         | Adds a new menu item for a restaurant                              | Restaurant         |
| GET     | /api/restaurants/{restaurantId}/menus/{id}   | Fetches a specific available menu item                             | Restaurant / Internal |
| DELETE  | /api/restaurants/{restaurantId}/menus/{id}   | Disables a specific menu item                                      | Restaurant         |
| PATCH   | /api/restaurants/{restaurantId}/menus/{id}   | Toggles status (active/inactive) of a menu item                    | Restaurant         |

---

## ⚙️ Environment Variables Required

Ensure your `.env` contains the following keys for the Restaurant module:

```env
RESTAURANTS_POSTGRES_DB=
RESTAURANTS_POSTGRES_USER=
RESTAURANTS_POSTGRES_PASSWORD=
RESTAURANTS_POSTGRES_URL=
RABBITMQ_HOST=
RESTAURANT_DELETED_QUEUE=
EUREKA_ZONE_URL=
```

🧬 Service Logic Highlight: Availability Guard
* Before an order is finalized in the Order Service, the Restaurant Service is queried via Feign Client
* Performs a "Double Check":
* Is the restaurant currently Open?
* Is the specific menu item Available?
* Prevents customers from paying for items that cannot be prepared, reducing refunds and payment reversals