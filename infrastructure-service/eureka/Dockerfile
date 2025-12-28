FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/*.jar eureka-service.jar
ENTRYPOINT ["java", "-jar", "eureka-service.jar"]