FROM amazoncorretto:17-alpine3.17-jdk

RUN apk --no-cache add curl
COPY target/config-0.0.1-SNAPSHOT.jar ./app.jar

CMD java -jar app.jar

HEALTHCHECK --interval=5s --timeout=5s --start-period=5s --retries=10 CMD curl -f localhost:8888/actuator/health || exit 1
EXPOSE 8888
