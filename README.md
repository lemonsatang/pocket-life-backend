# 🌿 Pocket Life - Backend

Pocket Life 서비스의 핵심 비즈니스 로직과 데이터 관리를 담당하는 백엔드 애플리케이션입니다. 사용자의 건강한 생활 습관 형성을 돕기 위해 식단, 할 일, 가계부 데이터를 효율적으로 처리하고 안전하게 저장합니다.

## 🛠 기술 스택

| 분류 | 기술 |
| :--- | :--- |
| **언어** | Java 17 |
| **프레임워크** | Spring Boot 4.x |
| **데이터베이스** | MySQL |
| **ORM** | Spring Data JPA |
| **보안/인증** | Spring Security, JWT |
| **외부 API** | 한국천문연구원 특일 정보 조회 서비스 |
| **빌드 도구** | Gradle |

## 🚀 주요 기능

- **사용자 인증 및 보안**: Spring Security와 JWT를 기반으로 한 안전한 토큰 인증 방식을 제공합니다.
- **식단 관리**: 식단 기록 및 관리를 위한 REST API를 제공합니다.
- **일정 및 공휴일 관리**: 
    - 개인 일정 관리를 위한 할 일 목록 기능을 제공합니다.
    - 공공데이터포털 API를 연동하여 달력 내 공휴일 정보를 제공합니다.
- **자산 관리**: 지출 및 수입 내역을 기록하여 자산 흐름을 관리합니다.
- **장바구니**: 구매 예정 품목을 체계적으로 관리할 수 있습니다.
- **데이터 분석**: 수집된 사용자 데이터를 바탕으로 활동 패턴에 대한 통계 정보를 제공합니다.

## 📂 프로젝트 구조

```text
src/main/java/com/health/pocketlife/
├── config        # 애플리케이션 설정 (CORS, Security 등)
├── controller    # REST API 컨트롤러
├── dto           # 데이터 전송 객체
├── entity        # JPA 엔티티
├── jwt           # 인증 토큰 처리 로직
├── repository    # 데이터베이스 접근 인터페이스
└── service       # 비즈니스 로직 구현
```

## ⚙️ 시작하기

### 실행 방법

1.  **Repository 클론**
    ```bash
    git clone [repository-url]
    ```
2.  **데이터베이스 설정**
    - `src/main/resources/application.properties` 파일에서 MySQL 연결 계정 정보를 확인합니다.
3.  **애플리케이션 빌드 및 실행**
    ```bash
    ./gradlew bootRun
    ```
