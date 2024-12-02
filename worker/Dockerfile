# Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Add wait-for-it script to wait for dependent services
ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh .
RUN chmod +x wait-for-it.sh

# Create volume for logs
VOLUME /app/logs

# Expose the application port
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["./wait-for-it.sh", "postgres-batch:5432", "--", \
            "./wait-for-it.sh", "postgres-analytics:5432", "--", \
            "java", "-jar", "app.jar"]
