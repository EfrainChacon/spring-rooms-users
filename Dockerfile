# Imagen de la aplicación: build con el wrapper de Maven y runtime solo con JRE.
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw -q -B dependency:go-offline
COPY src src
RUN ./mvnw -q -B package -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S app && adduser -S app -G app
USER app
WORKDIR /app
COPY --from=build /workspace/target/code-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
