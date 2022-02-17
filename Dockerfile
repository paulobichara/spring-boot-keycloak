FROM openjdk:16-oracle
RUN mkdir app
COPY /build/libs/spring-boot-api-0.0.1-SNAPSHOT.jar /app/spring-boot-api-0.0.1-SNAPSHOT.jar
ENTRYPOINT [ "sh", "-c", "java -jar /app/spring-boot-api-0.0.1-SNAPSHOT.jar" ]