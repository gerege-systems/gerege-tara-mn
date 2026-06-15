# ДАН-admin — олон шатлалт Docker build

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/dan-admin.jar app.jar
EXPOSE 4000
ENTRYPOINT ["java", "-jar", "app.jar"]
