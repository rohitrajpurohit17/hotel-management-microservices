# Microservices-based Hotel Management System

A beginner-friendly hotel management project built with Spring Boot and Spring Cloud.

This system is designed as a group of small services instead of one big application. Each service handles one business area, such as hotels, bookings, payments, users, inventory, or notifications. All services work together through an API Gateway, Eureka service discovery, Feign client calls, RabbitMQ messaging, and a Circuit Breaker using Resilience4j.

## Project Overview

This project shows how a real microservices system can be built in a clean and simple way.

It includes:

- 6 business microservices
- Eureka for service discovery
- API Gateway for one entry point
- OpenFeign for service-to-service REST calls
- RabbitMQ for asynchronous messaging
- Resilience4j Circuit Breaker for fault tolerance
- Docker and Docker Compose for container-based setup

In simple words:

- The API Gateway is the main door.
- Eureka is the phone book.
- Config Server is the central settings store.
- Feign helps one service call another service.
- RabbitMQ works like a message queue or post office.
- Circuit Breaker works like a safety switch when a service is failing.

## Architecture In Simple Terms

In a monolithic application, everything runs in one codebase and one process.

In this project, different features are split into separate services:

- hotel-service manages hotels and rooms
- booking-service creates bookings
- payment-service processes payments
- user-service manages user data
- notification-service stores notification events
- inventory-service manages room availability

These services do not need to know the physical location of each other.

Instead:

1. Every service registers itself in Eureka.
2. The API Gateway receives client requests.
3. The gateway forwards the request to the correct microservice.
4. Some services call other services using Feign.
5. Some events are sent to RabbitMQ so background processing can happen asynchronously.
6. Each service stores its own data.

## Architecture Flow

Main request flow:

```text
Client -> API Gateway -> Microservice -> Database
```

Full system flow:

```text
Client / Frontend
        |
        v
API Gateway (8080)
        |
        v
Eureka Service Discovery (8761)
        |
        +--> hotel-service ---------> hotel database
        +--> booking-service -------> booking database
        |         |
        |         +--> Feign -> user-service -------> user database
        |         +--> Feign -> inventory-service --> inventory database
        |         +--> Feign -> payment-service ----> payment database
        |         |
        |         +--> RabbitMQ -> notification-service -> notification database
        |
        +--> user-service
        +--> payment-service
        +--> inventory-service
        +--> notification-service

Config Server (8888) provides shared configuration to all services.
```

## How The Architecture Works

### 1. Client and API Gateway

The client does not call every service directly.

It calls the API Gateway first.

Example:

```text
http://localhost:8080/api/bookings
```

The gateway checks the route and sends the request to the correct service.

### 2. Eureka Service Discovery

Eureka helps services find each other.

For example, `booking-service` does not need to know the fixed IP address of `payment-service`. It can ask Eureka where `payment-service` is running.

### 3. Feign Client Communication

Feign makes REST calls easier.

For example, `booking-service` can call:

- `user-service` to validate the user
- `inventory-service` to reserve a room
- `payment-service` to process payment

### 4. RabbitMQ Messaging

RabbitMQ is used for asynchronous communication.

That means one service can publish an event, and another service can process it later.

Example:

- `booking-service` publishes booking events
- `payment-service` publishes payment events
- `notification-service` consumes those events and stores notification records

Note:

- This project currently uses RabbitMQ
- Kafka is not wired in the current implementation

### 5. Circuit Breaker with Resilience4j

The Circuit Breaker is used to protect the system when a dependent service is failing.

In this project, `booking-service` uses a Circuit Breaker around the payment-service call.

In very simple language:

- If payment-service keeps failing, the Circuit Breaker stops calling it again and again for some time
- This prevents slow failures from spreading across the system
- A fallback response is returned so the booking flow fails in a controlled way

## Microservices And Responsibilities

### Business Services

| Service | Responsibility | Default Port | Main API Base Path |
| --- | --- | --- | --- |
| hotel-service | Manages hotels and hotel rooms | 8081 | `/api/hotels` |
| booking-service | Creates bookings and coordinates other services | 8082 | `/api/bookings` |
| payment-service | Processes payments and stores payment transactions | 8083 | `/api/payments` |
| user-service | Manages customer or user information | 8084 | `/api/users` |
| notification-service | Stores notification history from booking and payment events | 8085 | `/api/notifications` |
| inventory-service | Manages room stock and room reservation/release | 8086 | `/api/inventory` |

### Infrastructure Services

| Service | Responsibility | Default Port |
| --- | --- | --- |
| api-gateway | Single entry point for client requests | 8080 |
| service-discovery | Eureka registry for service discovery | 8761 |
| config-server | Centralized external configuration | 8888 |
| rabbitmq | Message broker for asynchronous events | 5672 |
| rabbitmq management UI | RabbitMQ dashboard | 15672 |
| postgresql | Relational database server used in Docker setup | 5432 |

## Key Features

- Microservices-based design
- API Gateway routing
- Eureka service discovery
- Centralized configuration with Spring Cloud Config
- OpenFeign communication between services
- RabbitMQ event-driven messaging
- Circuit Breaker with Resilience4j
- Docker and Docker Compose support
- Separate databases for services in Docker setup
- Clean separation of responsibilities

## Technologies Used

| Category | Technology |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot 3 |
| Cloud | Spring Cloud |
| Gateway | Spring Cloud Gateway |
| Service Discovery | Netflix Eureka |
| Config Management | Spring Cloud Config Server |
| Inter-service Communication | OpenFeign |
| Messaging | RabbitMQ |
| Fault Tolerance | Resilience4j |
| Data Access | Spring Data JPA |
| Local Development Database | H2 |
| Docker Database | PostgreSQL |
| Build Tool | Maven |
| Containerization | Docker, Docker Compose |

## Project Structure

```text
hotel-management-system/
|-- api-gateway/
|-- booking-service/
|-- config-server/
|-- docker/
|   `-- postgres/
|-- hotel-service/
|-- inventory-service/
|-- notification-service/
|-- payment-service/
|-- scripts/
|-- service-discovery/
|-- user-service/
|-- docker-compose.yml
|-- pom.xml
`-- README.md
```

## Setup Instructions

### Prerequisites

Before you start, make sure you have:

- Java 17
- Maven 3.9 or above
- Docker Desktop or Docker Engine
- Git

### Run Locally Without Docker

This option is useful if you want to understand the system step by step.

By default, the services use H2 in-memory databases for local development, which makes local setup easier.

### Option A: Use The Helper Scripts

1. Build all services:

```powershell
.\scripts\build-all.ps1
```

2. Start all services in separate PowerShell windows:

```powershell
.\scripts\run-local.ps1
```

### Option B: Run Manually

1. Build the project:

```powershell
mvn clean package -DskipTests
```

2. Start services in this order:

```text
1. service-discovery
2. config-server
3. hotel-service
4. user-service
5. inventory-service
6. payment-service
7. notification-service
8. booking-service
9. api-gateway
```

3. Run each service:

```powershell
mvn spring-boot:run
```

from its own module folder.

### Run Using Docker

This is the easiest way to run the full system.

The Docker setup includes:

- all services
- PostgreSQL
- RabbitMQ
- shared Docker network

### Step-by-step Docker Run

1. Build all jar files first:

```powershell
mvn clean package -DskipTests
```

2. Start the full system:

```powershell
docker compose up --build -d
```

3. Check that containers are running:

```powershell
docker compose ps
```

4. Open important URLs:

- API Gateway: `http://localhost:8080`
- Eureka Dashboard: `http://localhost:8761`
- Config Server: `http://localhost:8888`
- RabbitMQ Dashboard: `http://localhost:15672`

5. Stop everything:

```powershell
docker compose down
```

6. Remove containers plus volumes:

```powershell
docker compose down -v
```

## How Services Communicate In Docker

When Docker Compose starts the project, all containers join the same Docker network.

This means services can call each other by service name.

Examples:

- `booking-service` can call `http://payment-service:8083`
- `booking-service` can call `http://user-service:8084`
- services can reach Eureka at `http://service-discovery:8761/eureka`
- services can reach Config Server at `http://config-server:8888`
- services can reach RabbitMQ at `rabbitmq:5672`
- services can reach PostgreSQL at `postgres:5432`

This is why Docker networking is very useful in microservices. You do not need to manually manage IP addresses.

## API Route Examples

All examples below use the API Gateway as the entry point:

```text
http://localhost:8080
```

### 1. Create User

**Request**

```http
POST /api/users
Content-Type: application/json

{
  "fullName": "Aarav Sharma",
  "email": "aarav@example.com",
  "phone": "+919876543210",
  "role": "CUSTOMER"
}
```

### 2. Create Hotel

**Request**

```http
POST /api/hotels
Content-Type: application/json

{
  "name": "Lake View Residency",
  "city": "Jaipur",
  "address": "MI Road",
  "rating": 4
}
```

### 3. Add Room To A Hotel

**Request**

```http
POST /api/hotels/1/rooms
Content-Type: application/json

{
  "roomNumber": "101",
  "type": "DELUXE",
  "pricePerNight": 3500.00,
  "totalUnits": 10
}
```

### 4. Create Or Update Inventory

**Request**

```http
POST /api/inventory
Content-Type: application/json

{
  "roomId": 1,
  "availableUnits": 10
}
```

### 5. Create Booking

**Request**

```http
POST /api/bookings
Content-Type: application/json

{
  "userId": 1,
  "hotelId": 1,
  "roomId": 1,
  "amount": 4999.00,
  "currency": "INR",
  "paymentMethod": "CARD",
  "cardNumber": "4111111111111111",
  "startDate": "2026-04-01",
  "endDate": "2026-04-03"
}
```

### 6. Get All Bookings

**Request**

```http
GET /api/bookings
```

### 7. Process Payment Directly

**Request**

```http
POST /api/payments/process
Content-Type: application/json

{
  "bookingReference": "BOOK-1234ABCD",
  "amount": 4999.00,
  "currency": "INR",
  "paymentMethod": "CARD",
  "cardNumber": "4111111111111111"
}
```

### 8. Get Notifications

**Request**

```http
GET /api/notifications
```

## Example Business Flow

Here is a simple booking flow:

1. User sends a booking request to the API Gateway.
2. Gateway forwards the request to `booking-service`.
3. `booking-service` checks the user using `user-service`.
4. `booking-service` reserves room stock using `inventory-service`.
5. `booking-service` calls `payment-service`.
6. If payment succeeds, booking is confirmed.
7. Booking and payment events are published to RabbitMQ.
8. `notification-service` consumes the events and stores notification records.

## Why This Project Is Useful

This project is good for learning:

- how microservices are split by responsibility
- how services discover each other
- how service-to-service calls work
- how event-driven communication works
- how fault tolerance works
- how to run a distributed system using Docker

## Understanding the Code

This project includes detailed comments in all Java source files to help beginners understand the code. Each file contains explanations of:

### Code Comments Structure

- **Import statements**: Explain what each imported class/library does
- **Class-level comments**: Describe the purpose and responsibilities of each class
- **Method comments**: Explain what each method does and its parameters
- **Field comments**: Describe the purpose of each field/variable
- **Annotation comments**: Explain what Spring annotations do
- **Business logic comments**: Break down complex operations step-by-step

### Key Concepts Explained

Each service explains core concepts:

- **Spring Boot**: How applications start, dependency injection, auto-configuration
- **JPA/Hibernate**: Database entities, repositories, relationships
- **REST APIs**: Controllers, HTTP methods, request/response handling
- **Microservices**: Service discovery, Feign clients, inter-service communication
- **Circuit Breakers**: Resilience4j, fault tolerance patterns
- **Messaging**: RabbitMQ, asynchronous event-driven architecture
- **Docker**: Containerization, orchestration with Docker Compose

### Learning Path

Start with these files in order:

1. **Main Application Classes** (`*Application.java`): Understand how Spring Boot starts
2. **Entities** (`domain/*.java`): Learn about data modeling with JPA
3. **DTOs** (`dto/*.java`): Understand data transfer objects and validation
4. **Repositories** (`repository/*.java`): See how database operations work
5. **Services** (`service/*.java`): Study business logic and orchestration
6. **Controllers** (`web/*.java`): Learn REST API design
7. **Clients** (`client/*.java`): Understand inter-service communication
8. **Configuration** (`config/*.java`): See how messaging and other features are configured

### Example: Reading a Service Class

When reading a service class like `BookingService.java`:

- Look for the class-level comment explaining the service's role
- Read constructor comments to understand dependencies
- Follow method comments to understand the business flow
- Pay attention to error handling and compensation logic
- Note how circuit breakers and messaging are integrated

This commenting approach ensures that even beginners can follow the code and understand how a real microservices system works.

## Future Improvements

Possible next improvements:

- JWT authentication and authorization
- API rate limiting at the gateway
- Flyway or Liquibase for database migrations
- centralized logging dashboards
- retry and dead-letter queue support
- Kubernetes deployment manifests for production
- CI/CD pipeline with GitHub Actions or Jenkins
- real payment gateway integration
- monitoring dashboards and alerting

## Author

**Name:** Rohit Narendra Rajpurohit

## Environment Variables

The project uses environment variables for sensitive configuration like database and message broker passwords.

### Setting Up Environment Variables

1. Copy the example environment file:
   ```bash
   cp .env.example .env
   ```

2. Edit `.env` with your desired passwords:
   ```bash
   # Set secure passwords
   POSTGRES_PASSWORD=your_secure_db_password
   RABBITMQ_DEFAULT_PASS=your_secure_mq_password
   ```

3. The Docker Compose file will automatically use these variables.

### Default Values

If no `.env` file is present, the system uses default values:
- PostgreSQL password: `hotel_admin`
- RabbitMQ password: `hotelmq123`

**Important:** Change these defaults before deploying to production!
