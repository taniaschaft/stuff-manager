# Game Manager API

Spring Boot 3.2.1 REST API for Game Management with MySQL integration. Track games by publisher and maintain play time records.

<!--

To test REST connection, I created a small microservice which registers user and email but we will be creatively using it as a replace for publisher-manager over here.

docker pull taniaschaft/accessing-data-mysql:latest 


-->
## Project Structure

```
game-manager/
├── src/main/java/com/gamemanager/
│   ├── GameManagerApplication.java      # Main application class
│   ├── client/                          # External service clients (unused - see note below)
│   │   └── PublisherClient.java         # REST client for publisher validation
│   ├── config/
│   │   └── RestTemplateConfig.java      # HTTP client configuration
│   ├── controller/
│   │   ├── GameController.java          # REST API endpoints
│   │   └── GlobalExceptionHandler.java  # Centralized exception handling
│   ├── dto/
│   │   └── PublisherDTO.java            # Data transfer object for publisher
│   ├── model/
│   │   ├── Game.java                    # Game entity (JPA)
│   │   ├── GameHoursValidator.java      # Custom validator implementation
│   │   └── ValidGameHours.java          # Custom validation annotation
│   ├── repository/
│   │   └── GameRepository.java          # Data access layer
│   └── service/
│       ├── GameService.java             # Business logic
│       ├── PublisherService.java        # Publisher validation service (unused)
│       └── InvalidPublisherException.java  # Custom exception
├── src/main/resources/
│   ├── application.properties           # Production database configuration
│   └── application-dev.properties       # Development H2 configuration
├── src/test/                            # Test directory
├── Dockerfile                           # Container build configuration
├── docker-compose.yml                   # Multi-container orchestration
└── pom.xml                              # Maven dependencies
```

**Note on `client/` directory:** This directory was created for integration with an external publisher-manager REST service. However, the service was not integrated into this project due to compilation failures and multiple security vulnerabilities in its available Docker Hub image. 

Therefore, the service will be connected to a REST service created by myself and published publicly as the other service mentioend above. The service is a simple REST application that shares the DB with this project (like publisher manager would) to record user and email. It is available on:

https://hub.docker.com/repository/docker/taniaschaft/accessing-data-mysql

and can be locally run with:

docker run -p 8081:8081
-e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/bootdb
-e SPRING_DATASOURCE_USERNAME=user
-e SPRING_DATASOURCE_PASSWORD=password
taniaschaft/accessing-data-mysql:latest

You can test the applicatiomn endpoints using the endpoints bellow:

```bash
curl http://localhost:8081/publisher/add -d name=abcgames -d email=abcgamesCEO@abcgames.com
curl http://localhost:8081/publisher/add -d name=2Kgames -d email=2kgamesCEO@2kgames.com
curl http://localhost:8081/publisher/add -d name=sega -d email=sega@sega.com 
curl http://localhost:8081/publisher/add -d name=nintendo -d email=nintendoCEO@nintendo.com 
```

In the execution of this project, you need to git clone this repo and 
$ docker-compose up -d --build 
which will display something similar to:


<img width="1010" height="483" alt="image" src="https://github.com/user-attachments/assets/99a39ee7-e540-4b85-9a08-fc5482130bb6" />
or using the Docker Desktop -
<img width="2024" height="686" alt="image" src="https://github.com/user-attachments/assets/972c25c5-8083-4a61-92bf-bdf5f4ad8870" />



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

### Build & Run with Docker Compose (suggested commands)

```bash
# Build Docker images and run detached
docker-compose up -d --build 

# View specific service logs
docker-compose logs -f game-manager
docker-compose logs -f mysql-db

# Stop services
docker-compose down -v
```

**Services available:**
- Game Manager API: `http://localhost:8080/game`
- Publisher Manager API:  available at DockerHub taniaschaft/accessing-data-mysql:latest  

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
- **Function**: Manages game sessions with field restrictions and consistency. (soon) validates publishers
<!--
-->
### Publisher Manager Container -> Acessing Data MySQL service 
- **Image**: `taniaschaft/acessing-data-mysql:latest` (from Docker Hub)
- **Container**: `spring-app`
- **Port**: `8081`
- **Depends On**: MySQL (waits for health check)
- **Network**: `inatel` bridge network (accessible internally as `http://localhost:8081/publisher/`)
- **Environment Variables**:
  - `SERVER_HOST: 0.0.0.0`
  - `SERVER_PORT: 8081`
  - `MYSQL_HOST: mysql`
  - `MYSQL_PORT: 3306`
  - `SPRING_PROFILES_ACTIVE: dev`
- **Function**: Validates publisher information for incoming game registrations

## Integration: Game Manager with New Publisher Manager (homegrown REST service)

When creating a game via `POST /game`, the Game Manager service:
1. Receives game creation request with `publisherId`
2. Validates the `publisherId` by calling "Acessing Data Mysql˜ (`http://localhost:8081/publisher/`)
3. If publisher is valid, saves the game to MySQL
4. Returns the created game or validation error

This ensures data consistency across both services.
-->

## Technology Stack

- **Spring Boot**: 3.2.1
- **Spring Data JPA**: Data persistence
- **MySQL**: 8.0
- **Maven**: Build tool
- **Docker & Docker Compose**: Containerization
- **Lombok**: Reduces boilerplate code
- **Jakarta Validation**: Input validation
- **Java**: 21+ (source & target)

## Project Features

- ✅ UUID-based game identification
- ✅ Publisher-based game filtering
- ✅ Time tracking per game (date-hour mapping)
- ✅ Input validation with custom error messages
- ✅ Docker containerization for easy deployment
- ✅ MySQL persistence with auto-DDL
- ✅ CORS support for cross-origin requests
- ✅ Fixed CVE-2023-22102 (MySQL Connector vulnerability)

## Development Notes

- Entities use Lombok `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor` for clean code
- JPA `@ElementCollection` for storing timePlayed maps
- Spring Data JPA `Repository` with custom `findByPublisherId()` method
- Input validation using Jakarta Bean Validation annotations
- Service layer for business logic separation
- Controllers expose three main REST endpoints
- MySQL uses `spring.jpa.hibernate.ddl-auto=update` for schema management


// 


## Troubleshooting

### Check running containers
```bash
docker-compose ps
```

### Rebuild and restart
```bash
docker-compose up -d --build
```

### Clean everything (remove volumes and containers)
```bash
docker-compose down -v
```
