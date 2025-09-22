
# ---- BUILD ----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

COPY gradlew gradle/ ./
COPY gradle/wrapper ./gradle/wrapper
COPY settings.gradle build.gradle main.gradle ./
COPY gradle.properties ./gradle.properties

COPY applications ./applications
COPY domain ./domain
COPY infrastructure ./infrastructure

RUN chmod +x ./gradlew

RUN ./gradlew clean --no-daemon  :app-service:bootJar -x test

# ---- RUNTIME ----
FROM eclipse-temurin:17-jre

# Docker Profile
ENV SPRING_PROFILES_ACTIVE=docker \
    SERVER_PORT=8080 \
    SQS_LOAN_QUEUE=https://sqs.us-east-1.amazonaws.com/155974034244/loan_reports_queue \
    SQS_WAIT_TIME=20 \
    SQS_MAX_MESSAGE=10 \
    SQS_VISIBILITY_TIME=10 \
    SQS_NUMBER_THREADS=1 \
    DDB_PORT=8000 \
    DYNAMO_HOST=http://localhost:8000 \
    AUTH_JWKS_URI=http://auth-service:8080 \
    AUTH_ISSUER_URI=http://auth-service:8080 \
    CORS_ALLOWED_ORIGINS=*

# Usuario no root
RUN useradd -ms /bin/bash spring
USER spring
WORKDIR /home/spring

COPY --from=build /workspace/applications/app-service/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","/home/spring/app.jar"]
