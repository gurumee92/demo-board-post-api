FROM adoptopenjdk/openjdk11

ARG DATABASE_URL
ARG DATABASE_USERNAME
ARG DATABASE_PASSWORD
ARG LOG_DIR
ARG APP_CLIENT_ID
ARG APP_CLIENT_SECRET
ARG GET_TOKEN_ENDPOINT_URL
ARG CHECK_TOKEN_ENDPOINT_URL

COPY ./target/*.jar /application.jar

RUN mkdir /logs

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/application.jar", "--spring.profiles.active=prod"]