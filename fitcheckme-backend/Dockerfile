#Build stage
FROM maven:3.9.6-eclipse-temurin-17 as build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD . $HOME
RUN --mount=type=cache,target=/root/.m2 mvn -f $HOME/pom.xml clean package -DskipTests

#Package stage
FROM openjdk:17-jdk-alpine
COPY --from=build /usr/app/target/fitcheckme-backend.jar /app/target/fitcheckme-backend.jar
EXPOSE 8080
ENTRYPOINT java -jar /app/target/fitcheckme-backend.jar
