FROM openjdk:17-jdk

WORKDIR /app

COPY target/FitCheckMe-0.0.1-SNAPSHOT.jar /app/FitCheckMe-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/FitCheckMe-0.0.1-SNAPSHOT.jar"]
