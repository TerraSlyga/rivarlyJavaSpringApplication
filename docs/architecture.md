## Project Architecture

### Overview
The project is built using a **Layered Architecture** pattern to ensure a clean separation of concerns:

* **Controller Layer** – Intercepts HTTP requests and delegates processing to services.
* **Service Layer** – Houses the core business logic and orchestration.
* **Repository Layer** – Manages data persistence via Spring Data JPA.
* **DTO (Data Transfer Objects)** – Facilitates data transfer between layers and prevents entity leakage to the API.

### Interaction Flow
The data flows sequentially through the application layers:

**Client** → **Controller** → **Service** → **Repository** → **Database**

### Key Design Decisions
* **MapStruct:** Employed for type-safe mapping between DTOs and Entities to reduce boilerplate code.
* **JWT Authentication:** Implemented via the **Spring Security filter chain** for stateless session management.
* **Caching:** Optimized for performance using **Spring Cache + Redis** for frequently accessed data (e.g., tournament standings or active event lists).