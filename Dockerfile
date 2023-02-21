FROM eclipse-temurin:17-alpine
EXPOSE 8080
COPY target/*.jar ffbad-tournaments-slack-bot.jar
ENTRYPOINT ["java", "-jar","ffbad-tournaments-slack-bot.jar"]
