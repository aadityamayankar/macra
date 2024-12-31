# Ibento

Ibento is an event management system built using Spring Boot (Webflux) and other technologies. This project includes multiple modules such as authentication, authorization, data access, user management, event synchronization, and operations administration.

## Project Structure
```
ibento 
├── ibento-authentication 
    |── src 
        |── main 
            |── java 
                |── com 
                    |── mayankar 
                        |── authn 
                            |── api 
                            |── config 
                            |── dto 
                            |── exception 
                            |── service 
├── ibento-authorization ...
├── ibento-data-access ...
├── ibento-dev ...
├── ibento-library ...
├── ibento-event-synchronization ...
├── ibento-operations-administration ...
└── ibento-user-management ...
```

### Modules

- **authn**: Handles authentication services.
- **authz**: Manages authorization services.
- **dataaccess**: Provides data access and repository services.
- **dev**: Contains docker-compose files for development.
- **eventsync**: Synchronizes event data.
- **library**: Contains shared libraries and utilities.
- **opsadmin**: Administers operational tasks.
- **user**: Manages user-related services.

## Prerequisites

- Java 21
- Maven 3.9.9
- PostgreSQL
- RabbitMQ

## Getting Started

### Clone the Repository

```sh
git clone https://github.com/aadityamayankar/Ibento.git
cd Ibento
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

### Run the project

Navigate to each module you want to run and execute the following command:
```sh
mvn spring-boot:run
```

### Datbase Migration

To run the database migration, navigate to the `ibento-data-access` module and execute the following command:
```sh
mvn clean flyway:migrate -Dflyway.configFiles=flyway.conf
```

## Usage

### Authentication

The authorization service manages user roles and permissions. It is available at `http://localhost:7001`.

### Authorization

The authorization service manages user roles and permissions. It is available at `http://localhost:6001`.

### User

The user service manages user-related operations. It is available at `http://localhost:8001`.

### Operations Administration

The operations administration service manages operational tasks. It is available at `http://localhost:9001`.

### Event Sync

The event synchronization service synchronizes event data. It takes care of the clearing up stale ticket reservations, and redis cache concilliation. It is available at `http://localhost:10001`.
