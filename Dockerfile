FROM openjdk:8

VOLUME /tmp
ARG JAR_FILE
ADD ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]