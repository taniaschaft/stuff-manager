# Game Manager API

Spring Boot 3.0+ REST API for Game Management with CRUD operations and MySQL integration.

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
│       └── Game.java                    # Entity model
├── src/main/resources/
│   └── application.properties           # Configuration
├── Dockerfile                           # Container build configuration
├── docker-compose.yml                   # Multi-container orchestration
└── pom.xml                              # Maven dependencies
```

## API Endpoints

### Games CRUD Operations

- **GET /api/games** - Retrieve all games
- **GET /api/games/{id}** - Retrieve a specific game by ID
- **GET /api/games/genre/{genre}** - Retrieve games by genre
- **GET /api/games/platform/{platform}** - Retrieve games by platform
- **POST /api/games** - Create a new game
- **PUT /api/games/{id}** - Update an existing game
- **DELETE /api/games/{id}** - Delete a game

## Setup & Run

### Prerequisites

- Docker & Docker Compose installed
- Maven 3.9.6+ (for local development)
- Java 17+

### Build & Run with Docker Compose

```bash
# Start all services
docker compose up -d

# View logs
docker compose logs -f

# Stop services
docker compose down
```

### Local Development (without Docker)

```bash
# Install dependencies
mvn clean install

# Run the application
mvn spring-boot:run
```

**Note:** Requires MySQL running on localhost:3306

## Database Configuration

**Default Credentials:**
- Host: `mysql` (docker) or `localhost` (local)
- Database: `bootdb`
- Username: `user`
- Password: `password`
- Root Password: `root`

## Sample API Requests

### Create a Game
```bash
curl -X POST http://localhost:8080/api/games \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Elden Ring",
    "description": "Fantasy action RPG",
    "genre": "RPG",
    "price": 59.99,
    "platform": "PC",
    "releaseYear": 2022
  }'
```

### Get All Games
```bash
curl http://localhost:8080/api/games
```

### Get Game by ID
```bash
curl http://localhost:8080/api/games/1
```

### Update a Game
```bash
curl -X PUT http://localhost:8080/api/games/1 \
  -H "Content-Type: application/json" \
  -d '{"price": 49.99}'
```

### Delete a Game
```bash
curl -X DELETE http://localhost:8080/api/games/1
```

### Filter by Genre
```bash
curl http://localhost:8080/api/games/genre/RPG
```

## Docker Services

### MySQL Container
- Image: `mysql:8.0`
- Port: `3306`
- Storage: Persistent volume `mysql_data`

### Game Manager API Container
- Built from Dockerfile (multi-stage build)
- Port: `8080`
- Depends on MySQL health check

## Troubleshooting

### Check running containers
```bash
docker compose ps
```

### View container logs
```bash
docker compose logs game-manager-api
docker compose logs mysql
```

### Access MySQL directly
```bash
docker compose exec mysql mysql -u user -p bootdb
```

### Rebuild and restart
```bash
docker compose up -d --build
```

### Clean everything (remove volumes)
```bash
docker compose down -v
```

## Technology Stack

- **Spring Boot 3.2.1**
- **Spring Data JPA**
- **MySQL 8.0**
- **Maven 3.9.6**
- **Docker & Docker Compose**
- **Lombok** (reduces boilerplate)
- **Java 17**

## Development Notes

- Entities use Lombok annotations for cleaner code
- JPA `@Repository` handles all database operations
- Service layer contains business logic
- Controllers expose REST endpoints with error handling
- MySQL `ddl-auto=update` auto-creates/updates tables on startup
