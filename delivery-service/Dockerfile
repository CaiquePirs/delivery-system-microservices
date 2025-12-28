FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/*.jar delivery-service.jar
ENTRYPOINT ["java", "-jar", "delivery-service.jar"]