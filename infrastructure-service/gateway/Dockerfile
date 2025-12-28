FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/*.jar gateway-service.jar
ENTRYPOINT ["java", "-jar", "gateway-service.jar"]
