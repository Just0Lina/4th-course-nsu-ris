FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle build

FROM openjdk:21

WORKDIR /app

# Копируем JAR-файл common
COPY common/build/libs/common-1.0-SNAPSHOT-plain.jar common.jar

# Копируем JAR-файл worker-service
COPY worker-service/build/libs/worker-service-0.0.1-SNAPSHOT.jar worker-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "worker-service.jar"]