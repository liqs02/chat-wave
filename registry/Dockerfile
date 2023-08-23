FROM amazoncorretto:17-alpine3.17-jdk

RUN apk --no-cache add curl
COPY target/registry-0.0.1-SNAPSHOT.jar ./app.jar

CMD java -jar app.jar

HEALTHCHECK CMD curl -f localhost:8761/actuator/health || exit 1
EXPOSE 8761
