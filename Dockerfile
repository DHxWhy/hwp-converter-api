FROM eclipse-temurin:11-jdk-alpine AS build
RUN apk add --no-cache maven
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests -q && \
    ls -la target/*.jar && \
    find target -name "*.jar" -not -name "original-*" | head -1 | xargs -I{} cp {} target/app.jar

FROM eclipse-temurin:11-jre-alpine
WORKDIR /app
COPY --from=build /app/target/app.jar /app/app.jar
RUN mkdir -p sample_hwp

EXPOSE 7000

ENTRYPOINT ["java", "-jar", "./app.jar"]
