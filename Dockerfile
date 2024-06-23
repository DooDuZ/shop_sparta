FROM amazoncorretto:21
WORKDIR /app
COPY . .
RUN yum update -y && yum install -y dos2unix
RUN dos2unix /app/gradlew
RUN chmod +x /app/gradlew
CMD ["./gradlew", "clean", "test", "bootRun"]
ffff