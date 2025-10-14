# Build stage
FROM maven:3.9.7-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

# Install diagnostic tools (curl, dnsutils, traceroute, and ping)
RUN apt-get update && apt-get install -y curl dnsutils traceroute iputils-ping && rm -rf /var/lib/apt/lists/*

# --- TEMPORARY DEBUGGING STEP ---
# The next two lines are commented out so we can run as root in the console.
# RUN addgroup --system spring && adduser --system spring --ingroup spring
# USER spring:spring

# Copy the built JAR file
COPY --from=build /app/target/*.jar app.jar

# Expose port 8083 (matches local Spring Boot configuration)
EXPOSE 8083

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8083/api/actuator/health || exit 1

# Run the application with optimized JVM settings
ENTRYPOINT ["java", "-Djava.net.preferIPv4Stack=true", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=70.0", "-jar", "app.jar"]
