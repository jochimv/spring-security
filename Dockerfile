FROM maven:3.8.4-openjdk-17 AS builder
ARG JAR_FILE=target/*.jar
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

FROM openjdk:17
COPY --from=builder /app/target/*.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]