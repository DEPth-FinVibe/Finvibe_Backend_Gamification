# 문서

이 디렉토리에는 프로젝트 관련 문서를 작성합니다.

## 추가할 수 있는 문서

- API 명세서 (예: `api-spec.md`)
- 아키텍처 문서
- 데이터베이스 스키마
- 배포 가이드
- 트러블슈팅 가이드

## 예시: API 명세서 템플릿

```markdown
# API 명세서

## 1. Sample API

### 샘플 목록 조회
`GET /samples`

**설명**: 모든 샘플을 조회합니다.

#### 응답 (200 OK)
| 필드명 | 타입 | 설명 |
| :--- | :--- | :--- |
| `id` | Number | 샘플 ID |
| `name` | String | 샘플 이름 |
| `description` | String | 샘플 설명 |

**응답 예시**:
\```json
[
  {
    "id": 1,
    "name": "Sample 1",
    "description": "This is a sample"
  }
]
\```
```
