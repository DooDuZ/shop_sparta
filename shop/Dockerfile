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
RUN ./gradlew :shop:build
CMD ["java", "-jar", "/app/shop/build/libs/shop-0.0.1-SNAPSHOT.jar"]
