FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/*.jar customers-service.jar
ENTRYPOINT ["java", "-jar", "customers-service.jar"]