FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/*.jar restaurant-service.jar
ENTRYPOINT ["java", "-jar", "restaurant-service.jar"]
