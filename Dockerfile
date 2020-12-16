FROM adoptopenjdk/openjdk11

ARG DATABASE_URL
ARG DATABASE_USERNAME
ARG DATABASE_PASSWORD

COPY ./target/*.jar /application.jar

RUN mkdir /logs

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/application.jar", "--spring.profiles.active=prod"]