FROM amazoncorretto:20-alpine3.17-jdk

RUN apk --no-cache add curl
COPY target/auth-service-0.0.1-SNAPSHOT.jar /app.jar

CMD java -jar app.jar

HEALTHCHECK CMD curl -f localhost:8081/actuator/health || exit 1
EXPOSE 8081
