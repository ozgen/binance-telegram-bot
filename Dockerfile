# For Java 11
FROM --platform=linux/amd64 adoptopenjdk:11-jre-hotspot

# Refer to Maven build -> finalName
ARG JAR_FILE=target/*.jar

# cd /opt/app
WORKDIR /opt/app

# cp target/spring-boot-web.jar /opt/app/app.jar
COPY ${JAR_FILE} app.jar

# java -jar /opt/app/app.jar
ENTRYPOINT ["java","-jar","app.jar"]
