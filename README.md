# Game Manager API

Spring Boot 3.2.1 REST API for Game Management with MySQL integration. Track games by publisher and maintain play time records with real-time publisher validation.

## Table of Contents
- [Project Structure](#project-structure)
- [Quick Start](#quick-start)
- [API Endpoints](#api-endpoints)
- [Setup & Run](#setup--run)
- [Docker Deployment](#docker-deployment)
- [Sample API Requests](#sample-api-requests)
- [Technology Stack](#technology-stack)
- [Features](#features)
- [Troubleshooting](#troubleshooting)

## Project Structure

```
game-manager/
│   │   └── PublisherClient.java         # REST client for publisher validation
│   ├── config/
│   │   └── RestTemplateConfig.java      # HTTP client configuration
│   ├── controller/
│   │   ├── GameController.java          # REST API endpoints
│   │   └── GlobalExceptionHandler.java  # Centralized exception handling
│   ├── dto/
│   │   ├── GameResponse.java            # Game response DTO
│   │   └── PublisherDTO.java            # Publisher data transfer object
│   ├── model/
│   │   ├── Game.java                    # Game entity (JPA)
│   │   ├── GameHoursValidator.java      # Custom validator implementation
│   │   └── ValidGameHours.java          # Custom validation annotation
│   ├── repository/
│   │   └── GameRepository.java          # Data access layer
│   └── service/
│       ├── GameService.java             # Business logic
│       ├── PublisherService.java        # Real-time publisher validation
│       └── InvalidPublisherException.java  # Custom exception
├── src/main/resources/
│   ├── application.properties           # Production database configuration
│   └── application-dev.properties       # Development H2 configuration
├── src/test/                            # Test directory
├── Dockerfile                           # Container build configuration
├── docker-compose.yml                   # Multi-container orchestration
└── pom.xml                              # Maven dependencies
```

## Quick Start

```bash
# Clone the repository
git clone <repository-url>
cd game-manager

# Start all services with Docker Compose
docker-compose up -d --build

# Add publishers to the publisher-manager service
curl http://localhost:8081/publisher/add -d name=nintendo -d email=nintendo@nintendo.com
curl http://localhost:8081/publisher/add -d name=sega -d email=sega@sega.com

# Create a game
curl -X POST http://localhost:8080/game \
  -H "Content-Type: application/json" \
  -d '{"publisherId":"nintendo","name":"Mario","timePlayed":{"2023-05-01":10}}'
```

## Publisher Manager Integration

The Game Manager validates publishers using an external **Publisher Manager** service available at:
- **Docker Hub**: `taniaschaft/accessing-data-mysql:latest`
- **Internal URL**: `http://localhost:8081/publisher/`
- **Validation**: Real-time (no caching) - every game creation makes a fresh API call

### Publisher Service Behavior
- **No Caching**: Each POST request validates the publisher by calling the publisher-manager service
- **Real-time Validation**: Ensures up-to-date publisher information
- **Shared Database**: Both services use the same MySQL database for consistency


-----

## API Endpoints

### Game Operations

1. **POST /game** - Create a new game
   - **Required fields**: `publisherId`, `name`, `timePlayed`
   - **Validation**: 
     - `publisherId`: Non-empty string
     - `name`: 3-20 characters
     - `timePlayed`: Non-empty Map of dates to hours played

2. **GET /game** - Retrieve all games
   - Returns all games from the database

3. **GET /game?publisherId={publisherId}** - Retrieve games by publisher
   - Filter games by specific publisher ID

## Setup & Run

### Prerequisites

- Docker & Docker Compose
- Java 21+ (for local development)
- Maven 3.9.6+ (for local development)

### Build & Run with Docker Compose

```bash
# Build and start all services
docker-compose up -d --build 

# View logs
docker-compose logs -f game-manager
docker-compose logs -f mysql-db

# Stop services
docker-compose down -v
```

**Services Started:**
- Game Manager API: `http://localhost:8080/game`
- Publisher Manager API: `http://localhost:8081/publisher/`
- MySQL Database: Internal network only  

### Local Development (without Docker)

This section covers running the application locally for development and testing purposes. The application uses an in-memory H2 database for testing, so no external MySQL instance is required.

#### Prerequisites

- **Java 21+** installed and available in your PATH
- **Maven 3.9.6+** installed and available in your PATH

#### Setup

```bash
# Clone the repository (if not already done)
git clone <repository-url>
cd game-manager

#### Running Tests
The project includes unit and integration tests that use an in-memory H2 database.

**Test Configuration:**

- Tests use H2 in-memory database (`jdbc:h2:mem:testdb`)
- Database schema is automatically created and dropped for each test run
- Test configuration file: `src/test/resources/application.properties`

```bash
# Run all tests
mvn test

# Run tests with verbose output
mvn test -X

# Run a specific test class
mvn test -Dtest=GameValidationTest

# Run tests and generate coverage report
mvn clean test jacoco:report
```

#### Running the Application Locally

To run the application locally for development (not recommended, use the docker service instead).

Set the H2 as the spring.datasource.url driver (more instructions to follow) and run bellow:

```bash
# Install dependencies and build the project
mvn clean install
# Run the Spring Boot application
mvn spring-boot:run
```

**Note:** Running the application locally without Docker will start it in development mode, but it will attempt to connect to a MySQL database (as configured in `src/main/resources/application.properties`). For development without MySQL, you have two options:

**Option 1: Use in-memory H2 database for local development**
1. Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:h2:mem:devdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```
2. Then run: `mvn spring-boot:run`

**Option 2: Run MySQL separately**
```bash
# Start MySQL using Docker (without the full docker-compose)
docker run -d \
  --name game-manager-mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=bootdb \
  -e MYSQL_USER=user \
  -e MYSQL_PASSWORD=password \
  -p 3306:3306 \
  mysql:8.0

# Then run the application
mvn spring-boot:run
```

#### Development Workflow

1. **Make code changes**
2. **Run tests to ensure nothing breaks:**
   ```bash
   mvn test
   ```
3. **Run the application locally:**
   ```bash
   mvn spring-boot:run
   ```
4. **Test API endpoints** (see [Sample API Requests](#sample-api-requests) section)
5. **Commit and push changes**

#### Build Artifacts

After running `mvn clean install`, the built JAR file is located at:
```
target/game-manager-1.0.0.jar
```

You can also run the JAR directly:
```bash
java -jar target/game-manager-1.0.0.jar
```

### Docker Deployment

#### System Architecture
<!--

To test REST connection, I created a small microservice which registers user and email but we will be creatively using it as a replace for publisher-manager over here.

docker pull taniaschaft/accessing-data-mysql:latest 


-->

```
┌─────────────────────────────────────────────────────────────┐
│                    Docker Network: inatel                   │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────────┐  ┌──────────────────┐                 │
│  │  Game Manager    │  │ ADM API.         |                 │
│  │  Port: 8080      │  │  Port: 8081      │                 │
│  │ (Host: 8080)     │  │  (Host: 8081)    │                 │
│  └──────────────────┘  └──────────────────┘                 │
│         │                      │                            │
│         └──────────┬───────────┘                            │
│                    │                                        │
│           Validates via internal                            │
│            network connection                               │
│                    │                                        │
│         ┌──────────▼──────────┐                             │
│         │      MySQL 8.0      │                             │
│         │  Port: 3306         │                             │
│         │  (Host: 3306)       │                             │
│         │  Database: bootdb   │                             │
│         └─────────────────────┘                             │
│                                                             │
└─────────────────────────────────────────────────────────────┘

Game Manager:      Stores games, validates with Publisher Manager
Publisher Manager: Validates publisher information
MySQL:             Shared database for both services
```

#### Database Environment

- **Host**: `mysql` (internal docker network)
- **Database**: `bootdb`
- **User**: `user`
- **Password**: (empty)
- **Root Password**: (empty)
- **Port**: `3306`

## Sample API Requests

### Create a Game

```bash
curl -X POST http://localhost:8080/game \
  -H "Content-Type: application/json" \
  -d '{
    "publisherId": "nintendo",
    "name": "Mario",
    "timePlayed": {
      "2023-05-01": 10,
      "2023-05-02": 2,
      "2023-05-03": 3,
      "2023-05-04": 4
    }
  }'
```
**Success Response (201 Created):**
```json
{
  "id": "d167b4a1-4748-4eda-807c-828078701257",
  "publisherId": "nintendo",
  "name": "Mario",
  "timePlayed": {
    "2023-05-01": 10,
    "2023-05-02": 2,
    "2023-05-03": 3,
    "2023-05-04": 4
  }
}
```
Another example (without publisherId):
```bash
curl -s -X POST http://localhost:8080/game \
  -H "Content-Type: application/json" \
  -d '{
    "id": "",
    "publisherId": "",
    "name": "Super Mario",
    "timePlayed": {
      "2023-05-01": 10
    }
  }'
 ```
**Unsucessful Response (400 Bad request):**
```json
{
  "publisherId": "publisherId cannot be empty"
}
``` 


### Get All Games
```bash
curl http://localhost:8080/game
```

### Get Games by Publisher
```bash
curl http://localhost:8080/game?publisherId=nintendo
```

## Docker Services

### MySQL Container
- **Image**: `mysql:8.0`
- **Container**: `mysql-db`
- **Port**: `3306` (not exposed to localhost, only to the game-service so I removed from docker-compose)
- **Storage**: Persistent volume `mysql_data`
- **Health Check**: Enabled with `mysqladmin ping`

### Game Manager API Container
- **Image**: Built from Dockerfile (multi-stage Maven build)
- **Container**: `game-manager`
- **Port**: `8080`
- **Depends On**: MySQL (waits for health check)
- **Network**: `inatel` bridge network
- **Function**: Manages game sessions with real-time publisher validation

### Publisher Manager Container
- **Image**: `taniaschaft/accessing-data-mysql:latest` (from Docker Hub)
- **Container**: `spring-app`
- **Port**: `8081`
- **Depends On**: MySQL (waits for health check)
- **Network**: `inatel` bridge network
- **Internal URL**: `http://localhost:8081/publisher/`
- **Environment Variables**:
  - `SERVER_HOST: 0.0.0.0`
  - `SERVER_PORT: 8081`
  - `MYSQL_HOST: mysql`
  - `MYSQL_PORT: 3306`
  - `SPRING_PROFILES_ACTIVE: dev`
- **Function**: Validates publisher information for game registrations

### Sample Publisher Commands
```bash
# Add publishers to the system
curl http://localhost:8081/publisher/add -d name=nintendo -d email=nintendo@nintendo.com
curl http://localhost:8081/publisher/add -d name=sega -d email=sega@sega.com
curl http://localhost:8081/publisher/add -d name=abcgames -d email=abcgames@abcgames.com
curl http://localhost:8081/publisher/add -d name=2kgames -d email=2kgames@2kgames.com

# Get all publishers
curl http://localhost:8081/publisher/all
```

## Integration: Game Manager with New Publisher Manager (homegrown REST service)

When creating a game via `POST /game`, the Game Manager service:
1. How It Works: Game Creation Flow

When creating a game via `POST /game`:

1. **Receive Request**: Game Manager receives game creation request with `publisherId`
2. **Real-time Validation**: Makes a fresh API call to Publisher Manager (`http://localhost:8081/publisher/`)
3. **Verify Publisher**: Checks if the publisher exists (no caching - always current data)
4. **Save or Reject**: 
   - ✅ If valid → saves the game to MySQL
   - ❌ If invalid → returns validation error
5. **Return Response**: Created game or error message

**Key Feature**: No caching means every game creation validates against the latest publisher data.
- **Spring Boot**: 3.2.1
- **Spring Data JPA**: Data persistence
- **MySQL**: 8.0
- **Maven**: Build tool
- **Docker & Docker Compose**: Containerization
- **Lombok**: Reduces boilerplate code
- **Jakarta Validation**: Input validation
- **Java**: 21+ (source & target)

## Features

- ✅ **UUID-based game identification** - Unique game IDs
- ✅ **Real-time publisher validation** - No caching, fresh validation every time
- ✅ **Publisher-based game filtering** - Query games by publisher
- ✅ **Time tracking per game** - Date-to-hours mapping
- ✅ **Input validation** - Custom error messages with Jakarta Validation
- ✅ **Docker containerization** - Easy deployment with docker-compose
- ✅ **MySQL persistence** - Auto-DDL schema management
- ✅ **CORS support** - Cross-origin requests enabled
- ✅ **Microservices integration** - REST communication between services
- ✅ **Security** - Fixed CVE-2023-22102 (MySQL Connector vulnerability)

## Development Notes

### Code Architecture
- **Entities**: Use Lombok annotations (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`) for clean code
- **Persistence**: JPA `@ElementCollection` for storing timePlayed maps
- **Repository**: Spring Data JPA with custom `findByPublisherId()` method
- **Validation**: Jakarta Bean Validation with custom validators
- **Service Layer**: Business logic separation with `GameService` and `PublisherService`
- **Controllers**: Three main REST endpoints with centralized exception handling
- **Database**: MySQL with `spring.jpa.hibernate.ddl-auto=update` for schema management

### Publisher Validation
- **No Caching**: `PublisherService` makes real-time API calls to validate publishers
- **Fresh Data**: Every game creation triggers a new validation request
- **Error Handling**: Graceful handling of publisher-manager service failures 

Running Containers
```bash
docker-compose ps
```

### View Logs
```bash
# All services
docker-compose logs

# Specific service
docker-compose logs -f game-manager
docker-compose logs -f spring-app
```

### Rebuild and Restart
```bash
docker-compose up -d --build
```

### Clean Everything
```bash
# Remove containers and volumes
docker-compose down -v
```

### Common Issues

**Publisher validation fails:**
- Ensure publisher-manager service is running: `docker-compose ps`
- Check if publisher exists: `curl http://localhost:8081/publisher/all`
- Add publisher: `curl http://localhost:8081/publisher/add -d name=<name> -d email=<email>`

**Database connection issues:**
- Wait for MySQL health check to complete (~30 seconds on first start)
- Check MySQL logs: `docker-compose logs mysql-db`

**Port conflicts:**
- Ensure ports 8080, 8081, and 3306 are available
- Use `lsof -i :8080` to check port usage Clean everything (remove volumes and containers)
```bash
docker-compose down -v
```
