FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/*.jar orders-service.jar
ENTRYPOINT ["java", "-jar", "orders-service.jar"]
