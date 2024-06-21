#FROM amazoncorretto:21
#COPY . .
#RUN chmod +x ./gradlew
#CMD ["./gradlew", "clean", "test"]

FROM amazoncorretto:21
WORKDIR /app
COPY . .
RUN chmod +x /app/gradlew
COPY build/libs/shop_sparta-0.0.1-SNAPSHOT.jar app.jar
CMD ["/app/start.sh"]