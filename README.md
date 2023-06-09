# TWITTUR

<span>
<img src="https://www.vectorlogo.zone/logos/java/java-icon.svg" alt="Java" title="Java" width="40px">
<img src="https://www.vectorlogo.zone/logos/springio/springio-icon.svg" alt="Spring" title="Spring" width="40px">
<img src="https://www.vectorlogo.zone/logos/postgresql/postgresql-icon.svg" alt="PostgreSQL" title="PostgreSQL" width="40px">
<img src="https://flywaydb.org/wp-content/uploads/2020/12/cropped-favicon-150x150.png" alt="Flyway" title="Flyway" width="40px">
<img src="https://www.vectorlogo.zone/logos/apache_kafka/apache_kafka-icon.svg" alt="Apache Kafka" title="Apache Kafka" width="40px">
<img src="https://www.vectorlogo.zone/logos/redis/redis-icon.svg" alt="Redis" title="Redis" width="40px">
<img src="https://www.vectorlogo.zone/logos/rundeck/rundeck-icon.svg" alt="Rundeck" title="Rundeck" width="40px">
<img src="https://www.vectorlogo.zone/logos/docker/docker-icon.svg" alt="Docker" title="Docker" width="40px">
<img src="https://img.icons8.com/color/512/java-web-token.png" alt="JWT" title="JWT" width="40px">
</span>

---

## ❗ Twittur in Microservices [here](https://github.com/VL037-twittur)

## Features

- Project
  - Register, Login, Logout, Refresh Token
  - Get, Update `Account` 
  - Follow, Unfollow `Account`
  - Create, Get, Update, Delete `Tweet`
  - Get, Send `Direct Message`
  - Get `Notification`
  - Send `Email`
- Tech
  - Log
  - Caching
  - JWT Auth
  - Dockerized
  - Data Streaming
  - Cron Job: [/rundeck](/rundeck)
  - API spec: [/api-spec](/api-spec)
  - Database Migration: [/db/migration](/src/main/resources/db/migration)
  - Exception Handling: [ExceptionController.java](/src/main/java/vincentlow/twittur/controller/ExceptionController.java)
  - Test: [/unit-test](/src/test/java/vincentlow/twittur), [/integration-test](/src/test/java/vincentlow/twittur/integration)

## Project Configuration

- Java version: [17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) (JAVA_HOME)
- Spring Boot version: 3.0.4
- Databases: [PostgreSQL](https://www.postgresql.org/download), [Redis](https://github.com/ServiceStack/redis-windows/tree/master/downloads)
- Message Broker: [Apache Kafka](https://kafka.apache.org/downloads)
- Email Service: Gmail SMTP
- Cron: [Rundeck](https://www.rundeck.com/downloads)
- Container: [Docker](https://docs.docker.com/get-docker)
- IDE: [Intellij IDEA Community Edition](https://www.jetbrains.com/idea/download)
- Build tool: [Maven 3.6.3](https://archive.apache.org/dist/maven/maven-3/3.6.3)

## Cron Job Configuration

- Update both recipients for [notification](/rundeck/twittur_resendFailedEmails.yaml) with your email
- After `Email Configuration` is done, download [Rundeck](https://www.rundeck.com/downloads)
- Follow the steps: [Rundeck Windows Configuration](https://docs.rundeck.com/docs/administration/install/windows.html)
- Go to [localhost:4440](http://localhost:4440)
- Login with `admin` `admin` (default)
- Create `New Project`
- Click on `Action` &rarr; `Upload Definition`
- Choose `YAML format` and upload job from [/rundeck](/rundeck)

## Email Configuration

- temporary disable your `anti-virus`
- fill `spring.mail.username` & `spring.mail.password` with your Google App Passwords credentials

## JWT Configuration

- fill `jwt.secret.key` with your secret key. You can visit https://www.allkeysgenerator.com

## Run Project with Docker

- Make sure you have `target` directory and `.jar` file inside.
- If you don't have `target` directory, you can run `mvn clean install -DskipTests`
- Update docker-compose environment at [docker-compose.yaml](https://github.com/VL-037/twittur/blob/main/docker-compose.yaml#L73-L75)
- Run `docker compose up`
- APIs will be served at [localhost:8080](http://localhost:8080)

## Run Project without Docker

- Run `Databases` & `Message Broker` locally
- Create Database named `twittur`
- Run `mvn spring-boot:run` or
- Add VM options `--add-opens java.base/java.lang=ALL-UNNAMED` to IDE Run/Debug configuration for orika object mapper to work
- APIs will be served at [localhost:8080](http://localhost:8080)
