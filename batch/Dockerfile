#FROM amazoncorretto:21
#WORKDIR /app
#COPY . .
#RUN chmod +x /app/gradlew
#CMD ["./gradlew", "bootRun"]
#CMD ["./gradlew", "clean", "test", "bootRun"]

FROM amazoncorretto:21
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew :batch:build
CMD ["java", "-jar", "/app/batch/build/libs/batch-0.0.1-SNAPSHOT.jar"]
