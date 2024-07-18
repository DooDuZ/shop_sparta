FROM amazoncorretto:21
WORKDIR /app
COPY . .
RUN chmod +x /app/gradlew
CMD ["./gradlew", "bootRun"]
#CMD ["./gradlew", "clean", "test", "bootRun"]
