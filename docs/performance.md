# Performance Report — Rivarly Information Web Platform

## 1. Methodology & Tools

The following tools were used to analyze the Spring Boot API performance:

- **IntelliJ Profiler (JFR):** method execution time analysis and CPU load profiling.
- **Spring Boot Actuator:** HTTP request processing metrics collection.
- **Hibernate SQL Logging:** analysis of query count and complexity against the PostgreSQL database.

---

## 2. Baseline Metrics

| Metric | Value | Notes |
|---|---|---|
| Response Time (Latency) | ~526 ms | For requests passing through the Security Filter |
| CPU Hotspot (Security) | 10.7% | Time consumed by `JwtAuthenticationFilter` |
| Database Queries (List) | 31 queries | When fetching a list of 10 events (N+1 Problem) |
| Startup Time | ~6.5 sec | Including Minio and DevTools initialization |

---

## 3. Identified Hotspots

### 3.1 Excessive Database Queries (N+1 Problem)

When fetching the event list (`GET /api/events`), Hibernate executes one primary query followed by a cascade of additional queries for each entity:

- `event_tags_mapping`
- `event_state`
- `event_registration_mapping`

**Impact:** Database load grows geometrically as the number of competitions in the list increases.

---

### 3.2 Filtering & Security (`JwtAuthenticationFilter`)

Profiling revealed that the `doFilterInternal` method accounts for over **10% of CPU usage**.

- **Issue:** The user profile is reloaded (`Fetching profile data for user: SuperAdmin`) multiple times within a single request.
- **Root Cause:** Missing user data caching within the Security Context, or redundant calls to `AuthService`.

---

### 3.3 `MinioService` Initialization

The service constructor consumes ~**1.9% CPU** at startup. Blocking connection-verification operations against the storage backend slow down the overall application readiness time.

---

## 4. Optimization Plan

### Step 1 — Database Optimization

- Introduce `@EntityGraph` or `JOIN FETCH` in repositories to load event tags and statuses in a single query.
- Add indexes on the `nickname` and `person_id` columns.

### Step 2 — Security Layer Optimization

- Implement `UserDetails` caching inside `JwtAuthenticationFilter`.
- Exclude Actuator endpoints from the heavy filter chain where security requirements allow it.

### Step 3 — Data Structure Improvements

- Address the Spring Data warning regarding `PageImpl` serialization by migrating to `PagedModel` to stabilize the JSON structure and reduce serialization overhead.

---

## 5. Post-Optimization Results

| Metric | Before | After | Improvement |
|---|---|---|---|
| Max Response Time (Latency) | 526 ms | 38 ms | ✅ 92% reduction |
| Database Queries (Event List) | 31 queries | 1 query | ✅ Resolved via `@EntityGraph` |
| CPU Load (Authorization) | Elevated | Minimized | ✅ Caffeine cache for `UserDetailsService` |