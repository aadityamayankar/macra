# Macra

Macra is an event management system built with Spring Boot (Webflux), Nextjs[^1], and Kubernetes[^2]. It is a microservices-based architecture that consists of multiple modules, each handling a specific set of operations. 

<img src="https://github.com/user-attachments/assets/54cb41d1-e9ad-4959-88c4-bc79c143d32b" width="100%" />

The system is built with the following principles in mind:

- **Microservices**: Multiple modules, each handling a specific set of responsibilities. 
- **Event-Driven Architecture**: Ticket booking system is designed to be event-driven, with modules communicating over RabbitMQ.
- **Security**: Designed to be secure, following the OAuth2.0 standard, and session management.
- **Scalability**: Designed to be scalable, with each module being horizontally scalable.
- **Fault-Tolerance**: Designed to be fault-tolerant, with each module being resilient to failures.
- **High Availability**: Designed to be highly available, with each module being redundant.

Software requirements document: [here](https://docs.google.com/document/d/16kN-dNNyIGlELAH5iz33bxf3L2K8pikhJNp24A4VI0c/edit?usp=sharing)

Design document: [here](https://docs.google.com/document/d/1woZ_ILhgP8VbSZyOP-MTnl93qgWRW8__AlODFP00SrQ/edit?usp=sharing)

System Architecture
<img src="https://github.com/user-attachments/assets/02fd01e0-4128-4531-977d-de17834bfd8d" width="100%" />

## Table of Contents

- [Features](#features)
- [Project Structure](#project-structure)
- [Modules](#modules)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [References](#references)

## Features

### User Authentication and Authorization
The system offers a robust self-hosted authentication and authorization system, supporting user credential management and OAuth 2.0 Authorization Code Grant flow. Additionally, it integrates with Google for seamless and secure sign-in using Google accounts.

### Secure Session Management
The system employs cookie-based session management to maintain user sessions securely and efficiently. Timed authentication sessions are stored in a distributed cache, ensuring quick access and automatic expiration of inactive sessions for enhanced security.

### Distributed Cache Lock Mechanism
To prevent overbooking, we use a time-based distributed cache lock mechanism for ticket reservations. This ensures that multiple users cannot reserve the same ticket simultaneously, providing a smooth booking experience.

### Event-Driven Ticket Booking System
The ticket booking system is designed to be event-driven, utilizing RabbitMQ for handling booking events. This architecture ensures high performance, scalability, and reliability by processing booking requests asynchronously.

### Razorpay Payment Integration
System integrates with Razorpay for secure and seamless payment processing. Users can make payments using various methods, including credit/debit cards, net banking, UPI, and wallets, ensuring a flexible and user-friendly payment experience.

### Data Integrity and Consistency
The system implements soft deletion of records, allowing data to be marked as deleted without physical removal from the database. Additionally, a scheduled ticket reconciliation mechanism ensures consistency between the database and the cache, maintaining data integrity and reliability.

**Note**: To comply with the GDPR, the data is anonymized with the help of a utility tool after a certain period, ensuring user privacy and data protection.

### Scalability and Fault Tolerance
Designed to be horizontally scalable, it is ensured that each module can scale independently to handle increased load. The system is also fault-tolerant, with modules resilient to failures and designed for high availability through redundancy.

### Logging and Monitoring
SLF4J is used for logging, providing a unified logging interface for monitoring and debugging the application effectively. This helps in maintaining the health and performance of the system.

## Project Structure
```
macra 
├── authn
    |── src 
        |── main 
            |── java 
                |── com 
                    |── mayankar 
                        |── authn 
                            └── ...
├── authz
├── dataaccess
├── dev
	|── postgres-db-docker
		└── docker-compose.yml
	└── ...
├── eventsync
├── library
	|── commons
		└── ...
	└── entities
		└── ...
├── opsadmin
|── user
└── ...
```

### Modules

- **authn**: Handles authentication.
- **authz**: Manages authorization.
- **dataaccess**: Contains the canonical data model and repositories.
- **dev**: Contains docker-compose files for development.
- **eventsync**: Synchronizes the ticket reservations with redis cache and handles ticket booking events.
- **library**: Contains shared libraries and utilities.
- **opsadmin**: Handles operational administration tasks.
- **user**: Manages user-related operations.

## Prerequisites

- Java 21
- Maven 3.9.9
- PostgreSQL 16
- RabbitMQ 3-management

## Getting Started

### Clone the Repository

```sh
git clone https://github.com/aadityamayankar/Macra.git
cd Macra
```

### Setup Environment Variables

Create a `.env` file in the root directory and add the following environment variables:

```sh
	R2DBC_URL
	DB_USERNAME
	DB_PASSWORD
	REDIS_HOST
	REDIS_PORT
	GOOGLE_CLIENT_ID
	GOOGLE_CLIENT_SECRET
	SELF_CLIENT_ID
	SELF_CLIENT_SECRET
	RABBITMQ_HOST
	RABBITMQ_PORT
	RABBITMQ_USERNAME
	RABBITMQ_PASSWORD
	SESSION_TIMEOUT
	AUTHZ_CODE_TIMEOUT
	AUTHZ_CODE_SECRET
	AUTHZ_REFRESH_TIMEOUT
	TICKET_RESERVATION_CLEANUP_INTERVAL
	TICKET_RECONCILIATION_INTERVAL
	RZP_KEY_ID
	RZP_SECRET
	RZP_WEBHOOK_SECRET
	TICKET_PAYMENT_MAX_RETRIES
	TICKET_PAYMENT_RETRY_INTERVAL
	LOGGING_LEVEL
	NEXTJS_BASE_URL
	AUTHZ_BASE_URL
	AUTHN_BASE_URL
```

### Build the project

```sh
mvn clean install
```

### Run the infrastructure services 

Navigate to the `dev` module and start the docker-compose files:

```sh
cd dev
cd postgres-db-docker &
docker-compose up
cd ../rabbitmq-docker &
docker-compose up
cd ../redis-docker &
docker-compose up
```

### Migrate the database

Navigate to the `dataaccess` module and run the database migration:

```sh
mvn clean flyway:clean flyway:migrate -Dflyway.configFiles=flyway.conf
```

### Run the project

Navigate to each module you want to run and execute the following command:
```sh
mvn spring-boot:run
```

## Usage

The services can be accessed at the following URLs:

| Service Name | URL |
|--------------|-----|
| authn				 | http://localhost:7001 |
| authz				 | http://localhost:6001 |
| user				 | http://localhost:8001 |
| opsadmin		 | http://localhost:9001 |
| eventsync		 | http://localhost:10001 |

To access the database, use the psql command with the password `postgres`:

```sh
psql -h localhost -U postgres -p 5432 -d postgres
```

To access the RabbitMQ management console, navigate to `http://localhost:15672` and login with the following credentials:

- Username: `admin`
- Password: `admin`

To access the redis cache, use the redis-cli command:

```sh
redis-cli -h localhost -p 6379
```

## License

This project is licensed under the MIT License.

## References

[^1]: [macra-fe](https://github.com/aadityamayankar/macra-fe) - Frontend repository 

[^2]: [macra-k8](https://github.com/aadityamayankar/macra-k8) - Kubernetes repository
