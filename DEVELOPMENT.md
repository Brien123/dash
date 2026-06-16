# Development Guide

## Quick Start

### 1. Start Database Services

Option A: Using Docker Compose (Recommended)
```bash
docker-compose up -d
```

Option B: Manual Setup
- PostgreSQL: `sudo service postgresql start`
- MongoDB: `mongod --dbpath /data/db`
- Redis: `redis-server`

### 2. Configure Environment Variables

Create `.env` file in project root:
```bash
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=dash
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

MONGODB_URI=mongodb://localhost:27017
MONGODB_DB=dash

REDIS_HOST=localhost
REDIS_PORT=6379

SERVER_PORT=8080
```

### 3. Build and Run

```bash
mvn clean install -DskipTests
mvn spring-boot:run
```

### 4. Access API Documentation

Open browser: http://localhost:8080/swagger-ui.html

## Adding New Features

1. **Add Entity/Document**: Create in `entity/` or `document/` folder
2. **Create Repository**: Add to `repository/` package  
3. **Implement Service**: Add logic in `service/` package
4. **Create Controller**: Add REST endpoints in `controller/` package
5. **Update DTOs**: Define request/response models in `dto/`

## Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn clean verify
```

## Docker Compose Setup

The project includes a docker-compose.yml for easy local development:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: dash
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - pg_data:/var/lib/postgresql/data

  mongodb:
    image: mongo:7
    environment:
      MONGO_INITDB_DATABASE: dash
    ports:
      - "27017:27017"
    volumes:
      - mongo_data:/data/db

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

volumes:
  pg_data:
  mongo_data:
  redis_data:
```

Run with: `docker-compose up -d`

## Best Practices

- Use environment variables for sensitive data
- Keep database credentials secure
- Enable SSL in production
- Set appropriate cache TTLs
- Monitor connection pool sizes
- Use Redisson for advanced Redis features (distributed locks, etc.)
