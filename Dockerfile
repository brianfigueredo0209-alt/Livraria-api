# Imagem base: só o Java 21 (JRE) para executar
FROM eclipse-temurin:21-jre
# Pasta de trabalho dentro do container
WORKDIR /app
# Copia o JAR gerado pelo Maven
COPY target/*.jar app.jar
# Porta da API
EXPOSE 8080
# Comando executado ao iniciar o container
ENTRYPOINT ["java", "-jar", "app.jar"]
