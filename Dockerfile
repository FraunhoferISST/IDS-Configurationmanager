FROM maven:latest AS maven

COPY pom.xml /tmp/

WORKDIR tmp
RUN mvn verify clean --fail-never

COPY src /tmp/src/

RUN mvn clean package

FROM adoptopenjdk/openjdk11:jre-11.0.8_10-alpine
RUN mkdir /app

COPY --from=maven /tmp/target/*.jar /app/configurationmanager-1.0.1.jar.jar

WORKDIR /app/

ENTRYPOINT ["java","-jar","configurationmanager-1.0.1.jar.jar"]
