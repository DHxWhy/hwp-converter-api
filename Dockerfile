FROM eclipse-temurin:11-jdk-alpine AS build
RUN apk add --no-cache maven
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve
COPY src ./src
RUN mvn package -DskipTests -q

FROM eclipse-temurin:11-jre-alpine
WORKDIR /app
COPY --from=build /app/target/hwp-converter-api-1.0-SNAPSHOT-jar-with-dependencies.jar /app/hwp-converter-api.jar
RUN mkdir sample_hwp

EXPOSE 7000

ENTRYPOINT ["java", "-jar", "./hwp-converter-api.jar"]
