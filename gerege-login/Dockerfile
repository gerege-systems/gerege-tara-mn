# Гэрэгэ-login — олон шатлалт Docker build (TARA-Login мөн Docker дэмждэг)

# ── 1-р шат: build ────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Эхлээд зөвхөн pom — кэшлэг dependency давхарга
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
# Дараа нь эх код
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ── 2-р шат: runtime (хөнгөн JRE) ─────────────────────────────────────────
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/gerege-login.jar app.jar
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "app.jar"]
