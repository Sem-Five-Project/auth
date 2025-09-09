# Multi-stage Dockerfile for Spring Boot Application

# Stage 1: Build stage
FROM ghcr.io/railwayapp/maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime stage
FROM ghcr.io/railwayapp/eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Create non-root user for security
RUN groupadd -r springboot && useradd -r -g springboot springboot

# Install curl + netcat for health checks & wait-for-db
RUN apt-get update && apt-get install -y curl netcat && rm -rf /var/lib/apt/lists/*

# Copy JAR from build stage
COPY --from=builder /app/target/*.jar app.jar

# Add wait-for-db script
COPY wait-for-db.sh .
RUN chmod +x wait-for-db.sh

# Change ownership
RUN chown springboot:springboot app.jar wait-for-db.sh

# Switch to non-root user
USER springboot

# Expose port
EXPOSE 8080

# Start application with DB wait logic
ENTRYPOINT ["./wait-for-db.sh"]
