FROM openjdk:8-jdk-alpine

RUN addgroup -S torrera && adduser -S torrera -G torrera
RUN mkdir logs
RUN chgrp -R torrera logs
RUN chown -R torrera logs

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
RUN chgrp -R torrera /app.jar
RUN chown -R torrera /app.jar

USER torrera:torrera


ENTRYPOINT ["java","-jar","/app.jar"]
