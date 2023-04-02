# TWITTUR

<span>
<img src="https://www.vectorlogo.zone/logos/java/java-icon.svg" alt="Java" title="Java" width="40px">
<img src="https://www.vectorlogo.zone/logos/springio/springio-icon.svg" alt="Spring" title="Spring" width="40px">
<img src="https://www.vectorlogo.zone/logos/postgresql/postgresql-icon.svg" alt="PostgreSQL" title="PostgreSQL" width="40px">
<img src="https://flywaydb.org/wp-content/uploads/2020/12/cropped-favicon-150x150.png" alt="Flyway" title="Flyway" width="40px">
<img src="https://www.vectorlogo.zone/logos/apache_kafka/apache_kafka-icon.svg" alt="Apache Kafka" title="Apache Kafka" width="40px">
<img src="https://www.vectorlogo.zone/logos/redis/redis-icon.svg" alt="Redis" title="Redis" width="40px">
</span>

- Design: https://www.figma.com/file/E96Po8YZH8mMJdhhC5FAm2/Twittur?node-id=4%3A2&t=Dr04rG9HQSUs8DPT-1
- API spec: [/api-spec](/api-spec)

## Features

- Project
  - Create, Get, Update `Account` 
  - Follow / Unfollow `Account`
  - Create, Get, Update, Delete `Tweet`
  - Get, Send `Direct Message`
  - Get `Notification`
  - Send `Email`
- Tech
  - Log
  - Caching
  - Data Streaming
  - Database Migration: [/db/migration](/src/main/resources/db/migration)
  - Exception Handling: [ExceptionController.java](/src/main/java/vincentlow/twittur/controller/ExceptionController.java)
  - Test: [/unit-test](/src/test/java/vincentlow/twittur), [/integration-test](/src/test/java/vincentlow/twittur/integration)

## Project Configuration

- Java version: [17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) (JAVA_HOME)
- Spring Boot version: 3.0.4
- Databases: [PostgreSQL](https://www.postgresql.org/download), [Redis](https://github.com/ServiceStack/redis-windows/tree/master/downloads)
- Message Broker: [Apache Kafka](https://kafka.apache.org/downloads)
- Email Service: Gmail SMTP
- IDE: [Intellij IDEA Community Edition](https://www.jetbrains.com/idea/download)
- Build tool: [Maven 3.6.3](https://archive.apache.org/dist/maven/maven-3/3.6.3)

## Email Configuration

- fill `spring.mail.username` & `spring.mail.password` with your Google App Passwords credentials

## Run Project

- Run `Databases` & `Message Broker`
- Create Database named `twittur`
- Run `mvn spring-boot:run` or
- Add VM options `--add-opens java.base/java.lang=ALL-UNNAMED` to IDE Run/Debug configuration for orika object mapper to work
- APIs will be served at `http://localhost:8080`
