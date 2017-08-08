FROM frolvlad/alpine-oraclejdk8:slim

ENV JAVA_OPTS=""

RUN mkdir -p data/persistence data/fixtures
VOLUME data/persistence

COPY configuration.yml configuration.yml
COPY device-mgmt-api-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar", "server", "configuration.yml"]