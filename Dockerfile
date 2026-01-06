# Etapa de build: Maven + JDK 21
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -Dmaven.test.skip package

# Etapa de runtime: Playwright + Java 21 + Chromium
FROM mcr.microsoft.com/playwright/java:v1.56.0-jammy AS runtime
WORKDIR /app

# Copia o JAR
COPY --from=build /app/target/*.jar /app/app.jar

# Exponha a porta (Ex: Render/Railway usam $PORT)
EXPOSE 8080

# Necessário para Playwright funcionar
ENV PLAYWRIGHT_BROWSERS_PATH=/ms-playwright

# Inicia o app usando a porta da plataforma
ENTRYPOINT ["sh","-c","java -Dserver.port=${PORT:-8080} -jar /app/app.jar"]