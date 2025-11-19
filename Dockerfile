FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

# Copia a base SQLite para o artefato final (dados iniciais do desafio).
COPY data ./data

RUN mvn -q -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/painel-investimentos-0.0.1-SNAPSHOT.jar app.jar
COPY --from=build /app/data ./data

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
