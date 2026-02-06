# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Finvibe Gamification Service - Spring Boot 기반 게이미피케이션 서비스 (개인 챌린지, 배지, 경험치, 스쿼드 랭킹)

- **Language**: Java 21 (Temurin JDK)
- **Framework**: Spring Boot 4.0.1
- **Build Tool**: Gradle 9.2.1 (use wrapper)
- **Architecture**: Modular Monolith + Hexagonal/Clean Architecture
- **Root Package**: `depth.finvibe.gamification`

## Essential Commands

### Build & Run
```bash
./gradlew build                    # compile + test
./gradlew clean build              # clean build
./gradlew bootRun                  # run application
SPRING_PROFILES_ACTIVE=local ./gradlew bootRun  # run with local profile
```

### Testing
```bash
./gradlew test                                         # all tests
./gradlew test --tests PersonalChallengeServiceTest    # single test class
./gradlew test --tests PersonalChallengeServiceTest.create_success  # single test method
./gradlew test --tests "*.gamification.*"              # pattern match
./gradlew test --rerun-tasks                           # ignore cache
```

### Local Infrastructure
```bash
docker compose -f infra/docker-compose.yml up -d    # start (MariaDB, Redis, Kafka, MongoDB)
docker compose -f infra/docker-compose.yml down     # stop
```

### Required Environment Variables
- `GEMINI_API_KEY`: Required for LLM-based challenge generation
- `JWT_HMAC_SECRET`: JWT verification (default: `testsecret12312312313123123123`)

## Architecture

### Module Structure
```
modules/
  gamification/   # 게이미피케이션 도메인 (챌린지, 배지, XP, 스쿼드)
  study/          # 학습 도메인
```

### Hexagonal Architecture Layers (per module)
```
modules/{module}/
  api/external    # public REST controllers (DTO 변환 책임)
  api/internal    # inter-module APIs (다른 모듈 호출용 계약)
  application     # use cases, services, ports (트랜잭션 경계)
  domain          # entities, value objects, domain logic, error codes
  dto             # request/response DTOs (도메인 노출 금지)
  infra           # persistence (JPA), messaging (Kafka), clients, LLM, HTTP mappers
```

### Dependency Rules
- **Allowed**: `modules.{A}.application` → `modules.{B}.api`
- **Forbidden**:
  - Cross-module domain coupling (`modules.{A}.domain` → `modules.{B}.domain`)
  - JPA entity relationships across modules
- **Shared kernel** (`shared/`) must stay minimal (only common utilities, errors, locks)

### Error Handling Architecture
Domain/application layers throw `DomainException` with module-specific `DomainErrorCode` enums. HTTP mapping happens in `infra/error/`:

1. Domain/Application: throw `DomainException(ErrorCode)`
2. `GlobalExceptionHandler` catches exception
3. Searches for `DomainErrorHttpMapper` that supports the error code
4. Mapper converts to HTTP status + `ErrorResponse`

**When adding a new module:**
1. Create `{Module}ErrorCode implements DomainErrorCode` in domain
2. Create `{Module}ErrorHttpMapper implements DomainErrorHttpMapper` in `infra/error/`
3. Implement `supports()` and `toStatus()` methods
4. Never throw HTTP exceptions inside domain/application layers

## Code Conventions

### Indentation & Formatting
- **Java code**: 4 spaces (not 2 as mentioned in AGENTS.md)
- Braces: always use braces
- Line length: ~120 characters

### Import Order
1. Java/Jakarta (`java.*`, `javax.*`, `jakarta.*`)
2. Third-party (Spring, Lombok, etc.)
3. Project (`depth.finvibe.gamification.*`)
4. Static imports (last)

### Naming
- Classes/Interfaces: PascalCase (no `I` prefix)
- Methods/Variables: camelCase
- Constants: UPPER_SNAKE_CASE
- Test classes: `*Test`
- Test methods: snake_case (e.g., `create_success`)

### Lombok Usage
- Entities: `@Getter`, `@SuperBuilder`, `@NoArgsConstructor(access = AccessLevel.PROTECTED)`
- Services: `@RequiredArgsConstructor`
- DTOs: `@Getter`, `@Builder`, `@AllArgsConstructor`, `@NoArgsConstructor`
- **Avoid**: `@Data` or public `@Setter` on entities

### Type Guidelines
- Money: `BigDecimal` (never `double`/`float`)
- IDs: `Long` for entity IDs, `UUID` for user identifiers
- Collections: prefer interfaces (`List`, `Set`, `Map`)
- Optional: repository return types only, never parameters

### Testing Conventions
- Structure: Given/When/Then
- `@DisplayName` must be in Korean
- Unit tests: `@ExtendWith(MockitoExtension.class)`
- Integration tests: `@SpringBootTest`
- Assertions: AssertJ
- `BigDecimal` comparison: `isEqualByComparingTo`
- Exception tests: `assertThatThrownBy(...).isInstanceOf(DomainException.class)`

## Key Technical Components

### Kafka Topics
- **Consume**: `gamification.update-user-metric.v1`, `gamification.reward-badge.v1`
- **Produce**: `gamification.reward-xp.v1`

### Scheduled Jobs (KST Timezone)
- Monday 00:00: 스쿼드 랭킹 정산 및 주간 XP 초기화
- Sunday 23:55: 개인 챌린지 보상 지급
- Sunday 23:58: 주간 이벤트 보상 지급
- Monday 00:05: 주간 지표 리셋 + 개인 챌린지 생성 (LLM)

### Distributed Locking
- ShedLock with Redis (scheduler locking)
- Redisson (distributed locks for critical sections)

### LLM Integration
- LangChain4j + Gemini for challenge generation
- Fallback logic when LLM fails
- Prompts located in `src/main/resources/prompts/`

### Authentication
- JWT-based authentication with `Authorization: Bearer <JWT>` header
- JWT claims: `id` (UUID), `role`
- Custom resolver: `JwtArgumentResolver` injects `@AuthUser Requester` into controllers

### OpenAPI
- Local: `/swagger-ui/index.html`
- Production: `/doc/gamification/swagger-ui.html`

## Profiles & Configuration
- Default active profile: `local`
- `application-local.yml`: Local development (uses docker-compose)
- `application-prod.yml`: Production (uses environment variables)
- `application-kafka.yml`: Kafka-specific configuration

## Git Conventions
- Commit messages must be in Korean
- Example: `"feat(challenge): 개인 챌린지 생성 로직 추가"`
- Run `./gradlew test` before committing

## Communication
- Always respond to the user in Korean (한국어로 응답)
