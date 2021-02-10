FROM maven:3.6.0-jdk-11-slim AS build
COPY crawler/src /home/app/src
COPY crawler/pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM tomcat:8.0.20-jre8
COPY --from=build /home/app/target/unidisk.war /usr/local/tomcat/webapps
EXPOSE 8080
CMD ["catalina.sh", "run"]
