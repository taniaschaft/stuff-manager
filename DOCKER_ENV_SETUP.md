# Docker Compose Configuration

## Environment Variables Setup

The `docker-compose.yml` uses environment variables from `.env.docker` file for configuration management instead of hardcoded values. This allows for flexible deployment across different environments.

### Files

- **`.env.docker`** - Main environment configuration file (ignored by Git)
- **`.env.docker.example`** - Template file showing all available variables

### Getting Started

1. **Copy the example file to create your configuration:**
   ```bash
   cp .env.docker.example .env.docker
   ```

2. **Edit `.env.docker` with your desired values:**
   ```bash
   # Example: Change MySQL password for production
   MYSQL_PASSWORD=your_secure_password
   SPRING_DATASOURCE_PASSWORD=your_secure_password
   ```

3. **Run Docker Compose with the environment file:**
   ```bash
   docker-compose --env-file .env.docker up -d
   ```

### Available Variables

#### MySQL Configuration
- `MYSQL_ROOT_PASSWORD` - Root password for MySQL
- `MYSQL_DATABASE` - Database name
- `MYSQL_USER` - Database user
- `MYSQL_PASSWORD` - Database user password
- `MYSQL_PORT` - Port mapping (host:container is `${MYSQL_PORT}:3306`)

#### Game Manager Application
- `GAME_MANAGER_PORT` - Port mapping (host:container is `${GAME_MANAGER_PORT}:8081`)
- `GAME_MANAGER_VERSION` - Docker image version tag

#### Publisher Manager Service
- `PUBLISHER_MANAGER_URL` - URL for game-manager to call publisher-manager
- `PUBLISHER_MANAGER_PORT` - Port mapping (host:container is `${PUBLISHER_MANAGER_PORT}:8080`)
- `PUBLISHER_MANAGER_VERSION` - Docker image version tag
- `PUBLISHER_SERVER_HOST` - Server host for publisher-manager
- `PUBLISHER_SERVER_PORT` - Server port for publisher-manager

#### Spring Application Configuration
- `SPRING_DATASOURCE_URL` - Database connection URL
- `SPRING_DATASOURCE_USERNAME` - Database user for Spring
- `SPRING_DATASOURCE_PASSWORD` - Database password for Spring
- `SPRING_PROFILES_ACTIVE` - Active Spring profile (dev/prod)

#### Publisher Manager Database
- `PUBLISHER_MYSQL_HOST` - MySQL host for publisher-manager
- `PUBLISHER_MYSQL_PORT` - MySQL port for publisher-manager

### Example: Different Environments

**Development Environment (.env.docker):**
```env
MYSQL_PASSWORD=dev_password
SPRING_PROFILES_ACTIVE=dev
GAME_MANAGER_PORT=8081
```

**Production Environment (.env.docker.prod):**
```env
MYSQL_PASSWORD=secure_prod_password
SPRING_PROFILES_ACTIVE=prod
GAME_MANAGER_PORT=80
```

Then run:
```bash
docker-compose --env-file .env.docker.prod up -d
```

### Security Notes

- **Never commit `.env.docker` to Git** - it contains sensitive information
- Keep `.env.docker.example` updated with template values
- Use strong passwords for production deployments
- Consider using Docker secrets for highly sensitive data in production
