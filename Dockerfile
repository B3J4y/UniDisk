FROM maven:3.6.3-jdk-8-slim AS build

COPY crawler/src /home/app/src
COPY crawler/pom.xml /home/app
RUN mvn -f /home/app/pom.xml package -Dmaven.test.skip=true

FROM tomcat:8.5
COPY --from=build /home/app/target/unidisk.war /usr/local/tomcat/webapps
EXPOSE 8080
CMD ["catalina.sh", "run"]
