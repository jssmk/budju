FROM openjdk:8-alpine

COPY target/uberjar/budju.jar /budju/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/budju/app.jar"]
