# Livraria API
API REST (Spring Boot, JPA, PostgreSQL) para cadastro de livros.

## Executar
    mvn clean package          # compila e roda os testes
    docker compose up -d --build
    curl localhost:8080/actuator/health

## Endpoints
GET/POST /livros · GET/PUT/DELETE /livros/{id}

## Variáveis de ambiente
DB_URL, DB_USER, DB_PASSWORD
