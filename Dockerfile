FROM openjdk:21
WORKDIR /app
COPY build/libs/shop_sparta-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]