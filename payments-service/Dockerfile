FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/*.jar payments-service.jar
ENTRYPOINT ["java", "-jar", "payments-service.jar"]
