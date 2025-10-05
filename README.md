# Kafka Tips — Quarkus, Kafka (Avro), Redis, and Hexagonal Architecture

This project is a minimal reference application that demonstrates:
- Producing User events to Apache Kafka using Avro
- Consuming those events and caching Users in Redis
- Exposing REST endpoints to enqueue users and to query the cached users
- A simple Hexagonal (Ports & Adapters) architecture in a Quarkus application

## How it works
- POST /users accepts a JSON payload for a user and enqueues it to Kafka as an Avro message.
- A background Kafka consumer reads the Avro messages from the topic and stores them in Redis.
- GET /cache/users and GET /cache/user/{name} let you query what has been cached.

Key pieces:
- Inbound ports (use cases): `UserCommandUseCase`, `CacheQueryUseCase`
- Application services: `UserCommandService`, `CacheQueryService`
- Outbound ports: `UserEventPublisherPort` (Kafka), `UserCachePort` (Redis)
- Adapters: `UserProducerService` (Kafka producer), `UserConsumerService` (Kafka consumer), `UserCacheRepository` (Redis)

## Prerequisites
- Docker Desktop or Docker Engine + Docker Compose
- Java 17+
- Maven (wrapper included)

## Quickstart

1) Start infrastructure (Kafka, Schema Registry, Kafka UI, Redis):

- Using Docker Compose from the project root:
  - Linux/macOS: `docker compose up -d`
  - Windows (PowerShell): `docker compose up -d`

Services started by compose.yaml:
- Kafka broker: localhost:29092 (mapped from container)
- Confluent Schema Registry: http://localhost:8081
- Kafka UI: http://localhost:8082
- Redis: localhost:6379

2) Run the application in dev mode (live reload):
- Linux/macOS: `./mvnw quarkus:dev`
- Windows (PowerShell): `mvnw.cmd quarkus:dev`

Quarkus will listen on http://localhost:8080

3) Use the API
- Enqueue a user (produces Avro to Kafka):
  - `curl -X POST http://localhost:8080/users -H "Content-Type: application/json" -d '{"name":"alice","age":30}'`
  - Expected response: `202 Accepted` with body `User enqueued`
- List cached users (read from Redis):
  - `curl http://localhost:8080/cache/users`
- Get one cached user by name:
  - `curl http://localhost:8080/cache/user/alice`

Note: The consumer stores records under the user name key. Validation requires non-blank name and age >= 0.

## Configuration
Defaults are provided in `src/main/resources/application.properties`:
- `kafka.bootstrap.servers=localhost:29092`
- `kafka.users.topic=users`
- `kafka.user.group.id=users-consumer`
- `schema.registry.url=http://localhost:8081`
- `quarkus.redis.hosts=redis://localhost:6379`

You can override any of these with environment variables or system properties if needed.

## Troubleshooting
- Ports in use: Stop any service already using 29092 (Kafka), 8081 (Schema Registry), 8082 (Kafka UI), 6379 (Redis), or adjust compose.yaml and application.properties.
- Clean local Kafka data: Stop compose and remove the `kafka-data` directory if you need a fresh cluster state.
- No users in cache: Ensure the app is running, Kafka and Schema Registry are up, and the consumer logs show it is subscribed to the `users` topic.

## Build and package (optional)
- JVM run jar: `./mvnw package` then `java -jar target/quarkus-app/quarkus-run.jar`
- Uber-jar: `./mvnw package -Dquarkus.package.jar.type=uber-jar`
- Native (requires GraalVM or container build): `./mvnw package -Dnative` or `./mvnw package -Dnative -Dquarkus.native.container-build=true`
