FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY server/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]