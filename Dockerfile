FROM openjdk:8 as build
COPY . /unfima-build
WORKDIR /unfima-build
RUN ./gradlew shadowJar

FROM openjdk:8-alpine
RUN mkdir -p /opt/apps/unfima
COPY --from=build /unfima-build/build/libs/unfima-with-dependencies.jar /opt/apps/unfima
EXPOSE 5050
ENTRYPOINT ["java", "-jar", "/opt/apps/unfima/unfima-with-dependencies.jar"]
