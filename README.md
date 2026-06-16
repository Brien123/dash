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
# PostgreSQL
export POSTGRES_HOST=localhost
export POSTGRES_PORT=5432
export POSTGRES_DB=dash
export POSTGRES_USER=postgres
export POSTGRES_PASSWORD=postgres

# MongoDB
export MONGODB_URI=mongodb://localhost:27017
export MONGODB_DB=dash
export MONGODB_USER=admin
export MONGODB_PASSWORD=admin
export MONGODB_AUTH_DB=admin

# Redis
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=

# Elasticsearch
export ELASTICSEARCH_HOST=http://localhost:9200
export ELASTICSEARCH_USERNAME=elastic
export ELASTICSEARCH_PASSWORD=password

# JWT
export JWT_SECRET=dash-secret-key-change-in-production-at-least-256-bits-long
export JWT_EXPIRATION=86400000
export JWT_REFRESH_EXPIRATION=604800000

# File Upload
export MAX_FILE_SIZE=50MB
export MAX_REQUEST_SIZE=50MB
export FILE_UPLOAD_DIR=~/.dash/uploads

# Mail
export EMAIL_HOST=smtp.gmail.com
export EMAIL_PORT=587
export EMAIL_USERNAME=
export EMAIL_PASSWORD=
export DEFAULT_FROM_EMAIL=""

# Server
export SERVER_PORT=8080
export APP_BASE_URL=http://localhost:8080

mvn spring-boot:run
```

## API Documentation

Once the application is running, access Swagger UI at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Configuration Options

All configurations support environment variables for easy deployment:

| Service | Environment Variable | Default Value |
|---------|---------------------|---------------|
| PostgreSQL | POSTGRES_HOST | localhost |
| | POSTGRES_PORT | 5432 |
| | POSTGRES_DB | dash |
| | POSTGRES_USER | postgres |
| | POSTGRES_PASSWORD | postgres |
| MongoDB | MONGODB_URI | mongodb://localhost:27017 |
| | MONGODB_DB | dash |
| | MONGODB_USER | admin |
| | MONGODB_PASSWORD | admin |
| | MONGODB_AUTH_DB | admin |
| | MONGODB_REPLICA_SET | (empty) |
| Redis | REDIS_HOST | localhost |
| | REDIS_PORT | 6379 |
| | REDIS_PASSWORD | (empty) |
| Elasticsearch | ELASTICSEARCH_HOST | http://localhost:9200 |
| | ELASTICSEARCH_USERNAME | elastic |
| | ELASTICSEARCH_PASSWORD | password |
| JWT | JWT_SECRET | dash-secret-key-... |
| | JWT_EXPIRATION | 86400000 |
| | JWT_REFRESH_EXPIRATION | 604800000 |
| File Upload | MAX_FILE_SIZE | 50MB |
| | MAX_REQUEST_SIZE | 50MB |
| | FILE_UPLOAD_DIR | ~/.dash/uploads |
| Mail | EMAIL_HOST | smtp.gmail.com |
| | EMAIL_PORT | 587 |
| | EMAIL_USERNAME | (empty) |
| | EMAIL_PASSWORD | (empty) |
| | DEFAULT_FROM_EMAIL | (empty) |
| Server | SERVER_PORT | 8080 |
| | APP_BASE_URL | http://localhost:8080 |

## License

MIT License
