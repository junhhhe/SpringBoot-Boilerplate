# SpringBoot-Boilerplate

JWT 인증, Swagger, Redis, JPA, Validation, 공통 응답, 예외 처리 등

---

## 📁 프로젝트 구성
```
springboot.boilerplate
├── auth # 인증/인가 전용 모듈
│ ├── controller
│ ├── domain
│ ├── dto
│ ├── enums
│ ├── repository
│ └── service
├── global # 전역 공통 패키지
│ ├── common # 공통 응답 DTO
│ ├── config # Swagger, Security, Redis 설정
│ ├── exception # ErrorCode 기반 예외 처리
│ ├── redis # Redis 유틸
│ ├── security # JWT 필터, 토큰 유틸
│ └── swagger # Swagger 커스텀 어노테이션 설정
└── BoilerplateApplication.java
```

## 🚀 실행 방법
```
# 1. 환경 변수 파일 생성
.env

# 2. 실행 (dev 환경)
SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun
```

## 📄 Swagger API 문서
```
Swagger UI: /api-docs
```
