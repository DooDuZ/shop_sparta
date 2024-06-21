# 베이스 이미지 선택 (Amazon Corretto JDK 21 사용)
FROM amazoncorretto:21

# 작업 디렉토리 설정
WORKDIR /app

# 소스 코드 및 Gradle Wrapper 복사
COPY . .

# gradlew에 실행 권한 부여
RUN chmod +x ./gradlew

# 기본 명령어 설정
CMD ["./gradlew", "clean", "test"]
