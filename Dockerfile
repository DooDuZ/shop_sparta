FROM openjdk:21
WORKDIR /app
RUN chmod +x ./gradlew
COPY build/libs/shop_sparta-0.0.1-SNAPSHOT.jar app.jar
CMD ["./gradlew", "clean", "test"]