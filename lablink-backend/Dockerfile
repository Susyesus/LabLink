# Step 1: Build the Java application
FROM maven:3.9-eclipse-temurin-19 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies first (cached layer)
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# Step 2: Run the Java application
FROM eclipse-temurin:19-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render injects PORT as an env var (default 10000).
# Spring Boot picks up SERVER_PORT automatically.
ENV SERVER_PORT=8080
EXPOSE ${SERVER_PORT}

ENTRYPOINT ["java", "-jar", "app.jar"]