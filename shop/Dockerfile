#FROM amazoncorretto:21
#WORKDIR /app
#COPY . .
#RUN chmod +x /app/gradlew
#CMD ["./gradlew", "clean", "test", "bootRun"]
#CMD ["./gradlew", "clean", "test", "bootRun"]

#FROM amazoncorretto:21
#WORKDIR /app
#COPY . .
#RUN chmod +x ./gradlew
#RUN ./gradlew :shop:build
#CMD ["java", "-jar", "/app/shop/build/libs/shop-0.0.1-SNAPSHOT.jar"]
FROM amazoncorretto:21 AS builder
WORKDIR /app
COPY . .
RUN chmod +x /app/gradlew
RUN ./gradlew clean test
RUN ./gradlew bootJar

FROM amazoncorretto:21
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]