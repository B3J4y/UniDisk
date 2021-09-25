FROM maven:3.6.3-jdk-8-slim AS build

# Copy pom first and call verify to download dependencies
# and let docker handle caching
COPY pom.xml /home/app/pom.xml
RUN mvn -f /home/app/pom.xml verify clean --fail-never

COPY src /home/app/src
# # skip tests
RUN mvn -f /home/app/pom.xml package -Dmaven.test.skip=true

FROM tomcat:8.5
COPY --from=build /home/app/target/unidisk.war /usr/local/tomcat/webapps
EXPOSE 8080
CMD ["catalina.sh", "run"]
