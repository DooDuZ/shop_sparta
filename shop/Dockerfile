FROM amazoncorretto:21 AS builder
WORKDIR /app
COPY . .
RUN chmod +x /app/gradlew
RUN ./gradlew clean build -x test
RUN ./gradlew bootJar

FROM amazoncorretto:21
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]