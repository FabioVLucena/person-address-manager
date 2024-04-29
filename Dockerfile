FROM openjdk:21-jdk
WORKDIR /app
COPY /target/person-address-manager.jar /app/person-address-manager.jar
ENTRYPOINT ["java", "-jar", "/app/person-address-manager.jar"]