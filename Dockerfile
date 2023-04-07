FROM maven:3-eclipse-temurin-17-alpine
WORKDIR /app
COPY . /app

RUN mvn dependency:go-offline -B
RUN mvn clean package -DskipTests

CMD java --add-opens java.base/java.lang=ALL-UNNAMED -jar target/*.jar
