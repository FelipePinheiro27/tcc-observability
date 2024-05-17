FROM eclipse-temurin:17-jre

ADD /otel-0.0.1-SNAPSHOT.jar /app.jar
ADD /opentelemetry-javaagent.jar /opentelemetry-javaagent.jar

ENTRYPOINT java -jar -javaagent:/opentelemetry-javaagent.jar /app.jar
