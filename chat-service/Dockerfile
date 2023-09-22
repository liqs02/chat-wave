FROM amazoncorretto:20-alpine3.17-jdk

RUN apk --no-cache add curl
COPY target/chat-service-0.0.1-SNAPSHOT.jar /app.jar

CMD java -jar app.jar
EXPOSE 8083