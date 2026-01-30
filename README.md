# Finvibe Gamification Service

Finvibe 서비스 내 게이미피케이션(개인 챌린지, 배지, 경험치, 스쿼드 랭킹)을 담당하는 Spring Boot 기반 서비스입니다.

## 핵심 기능

- 개인 챌린지 생성/보상 지급 (주간 스케줄러, Gemini 기반 LLM 생성 + 폴백 로직)
- 사용자 경험치 적립, 레벨 계산, 스쿼드 랭킹 및 기여도 랭킹 제공
- 배지 지급 이벤트 소비 및 배지 상태 조회
- 사용자 지표 업데이트 이벤트 소비 및 주간 지표 리셋
- Kafka 기반 이벤트 발행/구독 (지표/배지/XP)
- 분산 락: ShedLock + Redis, Redisson
- OpenAPI(Swagger UI) 제공

## 기술 스택

- Java 21, Spring Boot 4.0.1
- MariaDB (JPA), Redis, Kafka
- LangChain4j + Gemini (챌린지 생성)
- SpringDoc OpenAPI

## 빠른 시작

### 1) 로컬 인프라 실행

```bash
docker compose -f infra/docker-compose.yml up -d
```

### 2) 환경 변수 설정 (로컬)

- `GEMINI_API_KEY`: 챌린지 생성에 사용
- `JWT_HMAC_SECRET`: JWT 검증용 시크릿 (기본값 있음)

### 3) 애플리케이션 실행

```bash
./gradlew bootRun
```

로컬 프로파일 명시 실행:

```bash
SPRING_PROFILES_ACTIVE=local ./gradlew bootRun
```

## 환경 설정

### 프로파일

- 기본 활성 프로파일: `local` (`src/main/resources/application.yml`)
- `application-local.yml`에서 Kafka, DB, Redis, MongoDB 설정을 로컬 기준으로 로드
- `application-prod.yml`에서 운영 환경 변수 사용

### 운영 환경 변수

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`
- `KAFKA_BOOTSTRAP_SERVERS`
- `MONGO_URL`
- `GEMINI_API_KEY`
- `JWT_SECRET`

## API 엔드포인트

인증이 필요한 API는 `Authorization: Bearer <JWT>` 헤더가 필요합니다.
JWT 페이로드에는 `id`(UUID), `role` 클레임을 사용합니다.

- 챌린지
  - `GET /challenges/me` (인증 필요)
- 배지
  - `GET /badges`
  - `GET /badges/me` (인증 필요)
- 스쿼드
  - `GET /squads`
  - `GET /squads/me` (인증 필요)
  - `POST /squads/{squadId}/join` (인증 필요)
- 경험치/랭킹
  - `GET /xp/me` (인증 필요)
  - `GET /xp/squads/ranking`
  - `GET /xp/squads/contributions/me` (인증 필요)

## Kafka 토픽

- Consume
  - `gamification.update-user-metric.v1`
  - `gamification.reward-badge.v1`
- Produce
  - `gamification.reward-xp.v1`

## 스케줄러 (KST)

- 월요일 00:00: 스쿼드 랭킹 정산 및 주간 XP 초기화
- 일요일 23:55: 개인 챌린지 보상 지급
- 일요일 23:58: 주간 이벤트 보상 지급
- 월요일 00:05: 주간 지표 리셋 + 개인 챌린지 생성

## OpenAPI

- 로컬 기본 경로: `/swagger-ui/index.html`
- 운영 환경 경로:
  - Swagger UI: `/doc/gamification/swagger-ui.html`
  - OpenAPI JSON: `/doc/gamification/v3/api-docs`

## 프로젝트 구조

```
src/
├── main/
│   ├── java/depth/finvibe/gamification/
│   │   ├── boot/                     # 설정, 보안, 인프라 구성
│   │   ├── modules/gamification/     # 게이미피케이션 모듈
│   │   │   ├── api/external/         # 외부 REST API
│   │   │   ├── application/          # 유스케이스 및 서비스
│   │   │   ├── domain/               # 도메인 모델
│   │   │   ├── dto/                  # DTO
│   │   │   └── infra/                # 인프라 구현 (JPA, Kafka, LLM 등)
│   │   └── shared/                   # 공통 모듈 (에러, 락 등)
│   └── resources/
│       ├── application.yml
│       ├── application-local.yml
│       ├── application-kafka.yml
│       ├── application-prod.yml
│       └── prompts/                  # LLM 프롬프트
└── test/
```

## 빌드 및 테스트

```bash
./gradlew build
./gradlew test
```

## 개발 가이드

세부 컨벤션과 아키텍처 가이드는 `AGENTS.md`를 참고하세요.
