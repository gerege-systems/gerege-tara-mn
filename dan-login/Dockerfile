# ДАН-login — олон шатлалт Docker build (frontend + backend), TARA-Login шиг

# ── 0-р шат: frontend asset build (SCSS → CSS, JS → uglify) — disain бүтэц ──
FROM node:20 AS frontend
WORKDIR /app
COPY disain ./disain
RUN cd disain && npm install --no-audit --no-fund && npx gulp build
# Гаралт: /app/src/main/resources/static/{styles,scripts,assets}

# ── 1-р шат: backend build (Maven) ────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
# frontend шатнаас шинээр build хийсэн static-ийг дээр нь давхарлана
COPY --from=frontend /app/src/main/resources/static ./src/main/resources/static
RUN mvn -B -q clean package -DskipTests

# ── 2-р шат: runtime (хөнгөн JRE) ─────────────────────────────────────────
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/dan-login.jar app.jar
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "app.jar"]
