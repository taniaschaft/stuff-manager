# Game Manager API

Spring Boot 3.2.1 REST API for Game Management with MySQL integration. Track games by publisher and maintain play time records.

## Project Structure

```
game-manager/
├── src/main/java/com/gamemanager/
│   ├── GameManagerApplication.java      # Main application class
│   ├── controller/
│   │   └── GameController.java          # REST API endpoints
│   ├── service/
│   │   └── GameService.java             # Business logic
│   ├── repository/
│   │   └── GameRepository.java          # Data access layer
│   └── model/
│       └── Game.java                    # Entity model (JPA)
├── src/main/resources/
│   └── application.properties           # Database configuration
├── Dockerfile                           # Container build configuration
├── docker-compose.yml                   # Multi-container orchestration
└── pom.xml                              # Maven dependencies
```

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
- Java 17+ (for local development)
- Maven 3.9.6+ (for local development)

### Build & Run with Docker Compose

```bash
# Build Docker images
docker-compose build

# Start all services (MySQL + Game Manager API + Publisher Manager)
docker-compose up -d

# View logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f game-manager
docker-compose logs -f publisher-manager

# Stop services
docker-compose down
```

**Services available:**
- Game Manager API: `http://localhost:8081/game`
- Publisher Manager API: `http://localhost:8080` (internal network: `http://publisher-manager:8080`)

### Local Development (without Docker)

```bash
# Install dependencies
mvn clean install

# RSystem Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Docker Network: inatel                    │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────────┐  ┌──────────────────┐                 │
│  │  Game Manager    │  │ Publisher Manager│                 │
│  │  Port: 8081      │  │  Port: 8080      │                 │
│  │ (Host: 8081)     │  │  (Host: 8080)    │                 │
│  └──────────────────┘  └──────────────────┘                 │
│         │                      │                             │
│         └──────────┬───────────┘                             │
│                    │                                         │
│           Validates via internal                            │
│            network connection                               │
│                    │                                         │
│         ┌──────────▼──────────┐                             │
│         │      MySQL 8.0      │                             │
│         │  Port: 3306         │                             │
│         │  (Host: 3306)       │                             │
│         │  Database: bootdb   │                             │
│         └─────────────────────┘                             │
│                                                              │
└─────────────────────────────────────────────────────────────┘

Game Manager:      Stores games, validates with Publisher Manager
Publisher Manager: Validates publisher information
MySQL:             Shared database for both services
``ronment:**
- Host: `mysql` (internal docker network)
- Database: `bootdb`
- User: `user`
- Password: ``
- Root Password: ``
- Port: `3306`

## Sample API Requests

### Create a Game
```bash
curl -X POST http://localhost:8081/game \
  -H "Content-Type: application/json" \
  -d '{
    "id": "c01cede4-cd45-11eb-b8bc-0242ac130003",
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
  "id": "c01cede4-cd45-11eb-b8bc-0242ac130003",
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

### Get All Games
```bash
curl http://localhost:8081/game
```

### Get Games by Publisher
```bash
curl http://localhost:8081/game?publisherId=nintendo
```

## Docker Services

### MySQL Container
- **Image**: `mysql:8.0`
- **Container**: `game-manager-mysql`
- **Port**: `3306`
- **Storage**: Persistent volume `mysql_data`
- **Health Check**: Enabled with `mysqladmin ping`

### Game Manager API Container
- **Image**: Built from Dockerfile (multi-stage Maven build)
- **Container**: `game-manager`
- **Port**: `8081`
- **Depends On**: MySQL (waits for health check)
- **Network**: `inatel` bridge network
- **Function**: Manages games and validates publishers

### Publisher Manager Container
- **Image**: `adautomendes/publisher-manager:latest` (from Docker Hub)
- **Container**: `publisher-manager`
- **Port**: `8080`
- **Depends On**: MySQL (waits for health check)
- **Network**: `inatel` bridge network (accessible internally as `http://publisher-manager:8080`)
- **Environment Variables**:
  - `SERVER_HOST: 0.0.0.0`
  - `SERVER_PORT: 8080`
  - `MYSQL_HOST: mysql`
  - `MYSQL_PORT: 3306`
  - `SPRING_PROFILES_ACTIVE: prod`
- **Function**: Validates publisher information for incoming game registrations

## Integration: Game Manager with Publisher Manager

When creating a game via `POST /game`, the Game Manager service:
1. Receives game creation request with `publisherId`
2. Validates the `publisherId` by calling Publisher Manager (`http://publisher-manager:8080`)
3. If publisher is valid, saves the game to MySQL
4. Returns the created game or validation error

This ensures data consistency across both services.

## Technology Stack

- **Spring Boot**: 3.2.1
- **Spring Data JPA**: Data persistence
- **MySQL**: 8.0
- **Maven**: Build tool
- **Docker & Docker Compose**: Containerization
- **Lombok**: Reduces boilerplate code
- **Jakarta Validation**: Input validation
- **Java**: 17 (source & target)

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

### View container logs
```bash
# Game Manager API logs
docker-compose logs -f game-manager

# Publisher Manager logs
docker-compose logs -f publisher-manager

# MySQL logs
docker-compose logs -f mysql

# All services
docker-compose logs -f
```

### Check specific service status
```bash
docker-compose ps
```

### Test Publisher Manager connection (from game-manager container)
```bash
docker-compose exec game-manager curl http://publisher-manager:8080/health
```

### Access MySQL directly
```bash
docker-compose exec mysql mysql -u user -p bootdb
```

### Validate docker-compose.yml
```bash
docker-compose config
```

### Rebuild and restart
```bash
docker-compose up -d --build
```

### Clean everything (remove volumes and containers)
```bash
docker-compose down -v
```
