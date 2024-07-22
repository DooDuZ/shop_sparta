FROM amazoncorretto:21
WORKDIR /app
COPY . .
RUN chmod +x /app/gradlew
CMD ["./gradlew", "clean", "test", "bootRun"]
#CMD ["./gradlew", "clean", "test", "bootRun"]

#FROM amazoncorretto:21
#WORKDIR /app
#COPY . .

# wait-for-it.sh 스크립트 복사
#COPY wait-for-it.sh /wait-for-it.sh
#RUN chmod +x /wait-for-it.sh

#RUN chmod +x ./gradlew
#RUN ./gradlew :batch:build
#CMD ["java", "-jar", "/app/batch/build/libs/batch-0.0.1-SNAPSHOT.jar"]
