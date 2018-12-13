FROM openjdk:8-jre-slim

RUN mkdir /app

WORKDIR /app

ADD ./albumi-api/target/albumi-api-1.0-SNAPSHOT.jar /app

EXPOSE 8081

CMD java -jar albumi-api-1.0-SNAPSHOT.jar