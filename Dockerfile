FROM eclipse-temurin:17-jre-alpine AS builder
WORKDIR /app
COPY target/*.jar *.jar
RUN java -Djarmode=layertools -jar *.jar extract

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder app/dependencies/ ./
COPY --from=builder app/spring-boot-loader/ ./
COPY --from=builder app/snapshot-dependencies/ ./
COPY --from=builder app/application/ ./
ENTRYPOINT ["java", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "org.springframework.boot.loader.JarLauncher"]