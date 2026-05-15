# ===== BUILD STAGE =====
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# ===== RUNTIME STAGE =====
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
  "-Dspring.servlet.multipart.max-file-size=-1", \
  "-Dspring.servlet.multipart.max-request-size=-1", \
  "-Dserver.tomcat.max-swallow-size=-1", \
  "-Dserver.tomcat.max-http-form-post-size=-1", \
  "-jar", "app.jar"]