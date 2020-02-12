FROM openjdk:8-alpine
RUN mkdir -p /opt/apps/unfima
COPY build/libs/unfima-with-dependencies.jar /opt/apps/unfima
ENTRYPOINT ["java", "-jar", "/opt/apps/unfima/unfima-with-dependencies.jar"]
