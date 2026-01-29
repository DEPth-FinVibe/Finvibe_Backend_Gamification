# Finvibe MSA Service Boilerplate

MSA 시스템의 새 서비스를 빠르게 시작하기 위한 Spring Boot 기반 보일러플레이트입니다.

## 특징

- **Spring Boot 4.0.1, Java 21** - 최신 Spring Boot와 Java LTS 버전 사용
- **Hexagonal Architecture (Clean Architecture)** - 명확한 계층 분리와 의존성 역전
- **다양한 인프라 지원**
  - JPA (MariaDB/H2)
  - MongoDB
  - Redis (분산 락, 캐싱)
  - Kafka (이벤트 기반 메시징)
- **JWT 인증** - 사용자 인증 및 권한 관리
- **분산 락 (Redisson)** - 동시성 제어
- **Docker Compose 로컬 환경** - 개발 환경 쉽게 구성
- **GitHub Actions CI/CD 파이프라인** - 자동화된 빌드 및 배포

## 시작하기

### 1. 프로젝트명 및 패키지명 변경

보일러플레이트를 새 프로젝트에 맞게 커스터마이징하세요:

1. **패키지명 변경**: `depth.finvibe.gamification` → 원하는 패키지명
   - 전체 프로젝트에서 일괄 치환 (IDE의 Refactor 기능 활용)
2. **메인 클래스명**: `FinvibeBoilerplateApplication` → 원하는 클래스명
3. **build.gradle 수정**:
   - `description`: `'finvibe-boilerplate'` → 프로젝트 설명
   - `mainClass`: 메인 클래스 경로 변경
4. **settings.gradle 수정**:
   - `rootProject.name = 'boilerplate'` → 프로젝트명

### 2. Sample 모듈 확인 또는 제거/수정

`modules/sample/` 디렉토리는 예제 모듈입니다:

- **참고용으로 유지**: 새 모듈 생성 시 템플릿으로 활용
- **제거**: 필요 없다면 삭제
- **복사하여 새 모듈 생성**: sample을 복사하여 새 도메인 모듈 생성

### 3. 설정 파일 수정

#### 로컬 개발 (`application-local.yml`)
- DB 접속 정보 (기본값: `finvibe/finvibe`)
- Redis 접속 정보 (기본값: `localhost:6379`)
- Kafka 접속 정보 (기본값: `localhost:9092`)
- MongoDB 접속 정보 (기본값: `mongodb://localhost:27017/finvibe`)

#### 운영 환경 (`application-prod.yml`)
- 환경 변수로 설정:
  - `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
  - `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`
  - `KAFKA_BOOTSTRAP_SERVERS`
  - `MONGO_URL`
  - `JWT_SECRET`

### 4. 로컬 실행

#### 인프라 구성 (Docker Compose)
```bash
docker compose -f infra/docker-compose.yml up -d
```

#### 애플리케이션 실행
```bash
./gradlew bootRun
```

또는 특정 프로파일로 실행:
```bash
SPRING_PROFILES_ACTIVE=local ./gradlew bootRun
```

### 5. CI/CD 설정

`.github/workflows/ci.yml` 파일을 수정하세요:

1. **Docker Hub 설정** (58, 74번 라인):
   - `YOUR_DOCKERHUB_USERNAME/YOUR_SERVICE_NAME` → 실제 이미지명으로 변경
   - GitHub Secrets 추가:
     - `DOCKERHUB_USERNAME`
     - `DOCKERHUB_PASSWORD`

2. **Manifest Repository (GitOps 사용 시)** (67, 73, 84번 라인):
   - `YOUR_ORG/YOUR_MANIFEST_REPO` → 실제 Manifest 리포지토리로 변경
   - `manifest/YOUR_SERVICE_NAME` → 실제 서비스 디렉토리로 변경
   - GitHub Secrets 추가:
     - `MANIFEST_REPO_TOKEN`

3. **GitOps를 사용하지 않는 경우**:
   - 9️⃣~1️⃣1️⃣ 단계를 주석 처리하거나 삭제

## 프로젝트 구조

```
finvibe-boilerplate/
├── src/
│   ├── main/
│   │   ├── java/depth/finvibe/boilerplate/
│   │   │   ├── boot/                # 부트스트랩 및 설정
│   │   │   │   ├── config/          # Spring 설정 (Kafka, Redis, QueryDSL 등)
│   │   │   │   └── security/        # JWT 인증 관련
│   │   │   ├── modules/             # 도메인 모듈
│   │   │   │   └── sample/          # 예제 모듈
│   │   │   │       ├── api/         # REST API (external, internal)
│   │   │   │       ├── application/ # 유스케이스 및 서비스
│   │   │   │       ├── domain/      # 도메인 엔티티 및 에러
│   │   │   │       ├── dto/         # Request/Response DTO
│   │   │   │       └── infra/       # 인프라 구현 (Repository, ErrorMapper)
│   │   │   ├── shared/              # 공통 구성요소
│   │   │   │   ├── domain/          # 공통 도메인 클래스
│   │   │   │   ├── error/           # 에러 처리
│   │   │   │   ├── infra/error/     # 글로벌 예외 핸들러
│   │   │   │   └── lock/            # 분산 락
│   │   │   └── FinvibeBoilerplateApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-local.yml
│   │       ├── application-kafka.yml
│   │       └── application-prod.yml
│   └── test/
├── infra/
│   └── docker-compose.yml           # 로컬 개발 인프라
├── deploy/
│   └── Dockerfile                   # 애플리케이션 Docker 이미지
├── .github/
│   └── workflows/
│       └── ci.yml                   # CI/CD 파이프라인
├── .cursor/
│   └── rules/                       # Cursor AI 규칙
├── build.gradle
├── settings.gradle
├── AGENTS.md                        # 개발 가이드 및 컨벤션
└── README.md
```

## 모듈 구조 (Hexagonal Architecture)

각 도메인 모듈은 다음 구조를 따릅니다:

```
modules/{module}/
├── api/
│   ├── external/            # 외부 노출 REST API
│   └── internal/            # 내부 모듈 간 통신 API
├── application/
│   ├── port/
│   │   ├── in/              # 유스케이스 인터페이스 (명령/조회)
│   │   └── out/             # 리포지토리 인터페이스
│   └── {Module}Service.java # 서비스 구현체
├── domain/
│   ├── {Module}.java        # 엔티티
│   └── error/
│       └── {Module}ErrorCode.java
├── dto/
│   └── {Module}Dto.java     # Request/Response DTO
└── infra/
    ├── error/
    │   └── {Module}ErrorHttpMapper.java
    └── persistence/
        ├── {Module}JpaRepository.java
        └── {Module}RepositoryImpl.java
```

## API 테스트

### Sample API 엔드포인트

- `GET /samples` - 샘플 목록 조회
- `GET /samples/{id}` - 샘플 단건 조회
- `POST /samples` - 샘플 생성 (JWT 필요)
- `PUT /samples/{id}` - 샘플 수정 (JWT 필요)
- `DELETE /samples/{id}` - 샘플 삭제 (JWT 필요)
- `GET /internal/samples/{id}` - 내부 API (모듈 간 통신용)

### JWT 토큰 생성 예시

```bash
# JwtTokenGenerator를 사용하여 테스트용 토큰 생성
# 또는 별도의 Auth 서비스에서 토큰 발급
```

## 빌드 및 테스트

### 빌드
```bash
./gradlew build          # 컴파일 + 테스트
./gradlew clean build    # 클린 빌드
./gradlew bootJar        # 실행 가능한 JAR 생성
```

### 테스트
```bash
./gradlew test                              # 모든 테스트 실행
./gradlew test --tests SampleServiceTest    # 특정 테스트 클래스 실행
```

### Docker 이미지 빌드
```bash
./gradlew build
docker build -f deploy/Dockerfile -t my-service:latest .
```

## 개발 가이드

자세한 개발 가이드와 컨벤션은 [AGENTS.md](AGENTS.md)를 참고하세요:

- 빌드, 실행, 테스트 명령어
- 코드 스타일 및 네이밍 컨벤션
- 아키텍처 및 패키지 구조
- 에러 핸들링 가이드
- Git 컨벤션

## 기술 스택

- **Language**: Java 21 (Temurin JDK)
- **Framework**: Spring Boot 4.0.1
- **Build**: Gradle 9.2.1
- **Database**: 
  - JPA (MariaDB, H2 for test)
  - MongoDB
- **Cache/Lock**: Redis, Redisson
- **Messaging**: Kafka
- **Security**: JWT (Custom implementation)
- **Container**: Docker
- **CI/CD**: GitHub Actions

## 라이선스

이 보일러플레이트는 자유롭게 사용하실 수 있습니다.
