FROM eclipse-temurin:17-jre-alpine
WORKDIR /mfg
COPY target/*.jar mfg.jar
EXPOSE 8091
ENTRYPOINT ["java","-jar","mfg.jar"]