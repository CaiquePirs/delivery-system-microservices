FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/*.jar notification-service.jar
ENTRYPOINT ["java", "-jar", "notification-service.jar"]
