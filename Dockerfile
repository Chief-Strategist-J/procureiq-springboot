FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app
# Copy pom.xml FIRST so Maven deps are cached unless pom.xml changes
COPY pom.xml .
RUN mvn dependency:go-offline -q
# Copy source only after deps are cached
COPY src ./src
RUN mvn clean package -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:TieredStopAtLevel=1", "-XX:+UseSerialGC", "-Xms128m", "-Xmx384m", "-jar", "app.jar"]
