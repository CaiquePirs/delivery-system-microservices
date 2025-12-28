FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/*.jar authentication-service.jar
ENTRYPOINT ["java", "-jar", "authentication-service.jar"]