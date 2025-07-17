# Use JDK 21 for build
FROM eclipse-temurin:21 AS build

WORKDIR /app

# Copy project files
COPY . .

RUN chmod +x mvnw

# Build the app
RUN ./mvnw clean package -DskipTests

# Use JDK 21 for running the app
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
