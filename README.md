# Demo Spring Boot Application

A Spring Boot application integrated with PostgreSQL (relational DB), MongoDB (NoSQL DB), Redis (cache), and Swagger API documentation.

## Features

- **PostgreSQL**: Relational database for structured data storage
- **MongoDB**: NoSQL database for document-based storage
- **Redis**: In-memory cache for high-performance data access
- **Swagger/OpenAPI**: Interactive API documentation at `/swagger-ui.html`

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL (running locally or remote)
- MongoDB (running locally or remote)
- Redis (running locally or remote)

## Database Setup

### PostgreSQL

```bash
# Create database and user
createdb dash
psql -U postgres -c "CREATE USER dash_user WITH PASSWORD 'dash_password';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE dash TO dash_user;"
```

Environment variables:
- `POSTGRES_HOST=localhost`
- `POSTGRES_PORT=5432`
- `POSTGRES_DB=dash`
- `POSTGRES_USER=postgres`
- `POSTGRES_PASSWORD=postgres`

### MongoDB

```bash
# Start MongoDB (if not running)
mongod --dbpath /data/db

# Environment variables:
MONGODB_URI=mongodb://localhost:27017
MONGODB_DB=dash
```

### Redis

```bash
# Start Redis (if not running)
redis-server

# Environment variables:
REDIS_HOST=localhost
REDIS_PORT=6379
```

## Running the Application

### Using Maven

```bash
mvn clean install
mvn spring-boot:run
```

### Build JAR and Run

```bash
mvn clean package
java -jar target/dash-0.0.1-SNAPSHOT.jar
```

### Using Environment Variables

```bash
export POSTGRES_HOST=localhost
export POSTGRES_PORT=5432
export POSTGRES_DB=dash
export POSTGRES_USER=postgres
export POSTGRES_PASSWORD=postgres

export MONGODB_URI=mongodb://localhost:27017
export MONGODB_DB=dash

export REDIS_HOST=localhost
export REDIS_PORT=6379

mvn spring-boot:run
```

## API Documentation

Once the application is running, access Swagger UI at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Project Structure

```
src/main/java/com/example/dash/
├── DemoApplication.java          # Main application class
├── config/                       # Configuration classes
│   ├── DataSourceConfig.java     # PostgreSQL configuration
│   ├── MongoConfig.java          # MongoDB configuration
│   ├── RedisConfig.java          # Redis configuration
│   └── SwaggerConfig.java        # Swagger/OpenAPI configuration
├── controller/                   # REST controllers
│   └── UserController.java       # Sample user controller
├── dto/                          # Data Transfer Objects
│   └── UserDTO.java              # User DTO with validation
├── entity/                       # JPA entities (PostgreSQL)
│   └── UserEntity.java           # User entity
├── document/                     # MongoDB documents
│   └── UserDocument.java         # User document
├── repository/                   # Repository interfaces
│   ├── UserRepository.java       # PostgreSQL repository
│   └── mongo/                    # MongoDB repositories
│       └── UserMongoRepository.java
└── service/                      # Service layer
    ├── UserService.java          # Business logic
    └── CacheService.java         # Redis cache operations

src/main/resources/
├── application.yml               # Main configuration
└── application.properties        # Alternative properties file
```

## Configuration Options

All database and cache configurations support environment variables for easy deployment:

| Service | Environment Variable | Default Value |
|---------|---------------------|---------------|
| PostgreSQL | POSTGRES_HOST | localhost |
| | POSTGRES_PORT | 5432 |
| | POSTGRES_DB | dash |
| | POSTGRES_USER | postgres |
| | POSTGRES_PASSWORD | postgres |
| MongoDB | MONGODB_URI | mongodb://localhost:27017 |
| | MONGODB_DB | dash |
| Redis | REDIS_HOST | localhost |
| | REDIS_PORT | 6379 |

## License

MIT License
