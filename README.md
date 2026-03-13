# Rivarly — Tournament Management (Java Spring Boot)

A concise Spring Boot application for managing tournaments, events, registrations and teams.

## Key facts

- **Artifact:** `rivarlyJavaSpringApplication`
- **GroupId:** `com.example`
- **Version:** `0.0.1-SNAPSHOT`
- **Configured Java version (pom):** `25`

## Requirements

- JDK matching project `java.version` (see `pom.xml`).
- Maven (wrapper included) or use the included Maven wrapper scripts.
- Docker & Docker Compose (optional, for services like Postgres/MinIO).

## Quick start

On Windows (from project root):

```powershell
mvnw.cmd spring-boot:run
```

On macOS / Linux:

```bash
./mvnw spring-boot:run
```

Build a packaged JAR:

```bash
./mvnw package
# or on Windows: mvnw.cmd package
```

Run the produced JAR:

```bash
java -jar target/rivarlyJavaSpringApplication-0.0.1-SNAPSHOT.jar
```

Run with Docker Compose (if services provided in `docker-compose.yml`):

```bash
docker-compose up --build
```

Run tests:

```bash
./mvnw test
# or on Windows: mvnw.cmd test
```

## Configuration

- Main application config: `src/main/resources/application.properties`.
- Initial data: `src/main/resources/data.sql`.
- Secrets (credentials, MinIO config, etc.): `src/main/resources/secrets.properties` (see example below).

If you use Docker Compose, the composed file at the repo root may provide configured services for local development.

## `secrets.properties` (example)

Create `src/main/resources/secrets.properties` **locally** and never commit production secrets. Example keys the application expects or commonly uses:

```properties
# PostgreSQL datasource
spring.datasource.username=rivarly_user
spring.datasource.password=change-me

# MinIO (object storage) — used by `MinioService`
minio.access-key=minio-access-key
minio.secret-key=minio-secret-key

# JWT
jwt.secret=replace-with-strong-random-secret


```

- Replace placeholder values with secure credentials for your environment.
- Keep `secrets.properties` out of version control (add to `.gitignore` if not already ignored).

## Project structure (important locations)

- Application entry: `src/main/java/com/example/rivarly/RivarlyApplication.java`
- Security config: `src/main/java/com/example/rivarly/config/SecurityConfig.java`
- Controllers: `src/main/java/com/example/rivarly/controller/` (includes `AuthController`, `EventController`)
- Services: `src/main/java/com/example/rivarly/service/`
- Repositories: `src/main/java/com/example/rivarly/repository/`

## Notes

- The project uses Postgres as the runtime DB (see dependency) and MinIO for object storage.
- MapStruct + Lombok are used for DTO mapping and boilerplate reduction.

## License

This repository includes `LICENSE.md` at the project root. See that file for the full license text.

## Next steps

- Create `src/main/resources/secrets.properties` with the values above.
- Start local DB/MinIO (or use the composed file), then run the app.

## Documentation guidelines

To ensure consistency, contributors should document new features, entities, DTOs, or configurations as follows:

1. **Class-level comments:**
    - Provide a concise summary of the class's purpose.
    - List key responsibilities and interactions with other modules.

2. **Method-level comments:**
    - Clearly explain the method's functionality, input parameters, and expected outcome.
    - Indicate potential side effects or thrown exceptions.

3. **Configuration updates:**
    - Update this README or any related configuration file sections when introducing new environment variables, secrets,
      or dependencies.

4. **Folder structure:**
    - Document directory additions in the `Project structure` section below.

5. **Examples:**
    - For endpoints or integrations, provide sample usage or payloads wherever applicable.


## Contact

For questions or contributions, open an issue or contact the maintainer.
