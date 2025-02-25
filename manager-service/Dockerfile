
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle build
FROM openjdk:21

WORKDIR /app

# Копируем JAR-файл common
COPY common/build/libs/common-1.0-SNAPSHOT-plain.jar common.jar

# Копируем JAR-файл manager-service
COPY manager-service/build/libs/manager-service-0.0.1-SNAPSHOT.jar manager-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "manager-service.jar"]