# Trabalho Individual de Pesquisa e Aplicação — Aulas 10 e 11

## 1. Capa e integrantes
Aluno: brian kevin da silva figueredo
Disciplina: Back-end Frameworks
Professor: Fernando
Data: 23/11/2026

## 2. Introdução
Este trabalho apresenta uma API REST de cadastro de livros, desenvolvida com Spring Boot, Spring Data JPA e PostgreSQL. Além de implementar as operações de cadastro, listagem, busca, atualização e exclusão, o projeto aplica os conceitos das Aulas 10 e 11: testes automatizados com JUnit e Mockito, geração do JAR com Maven, configuração por variáveis de ambiente, Docker, Docker Compose e monitoramento com Actuator. O objetivo é mostrar o que faz uma API deixar de funcionar apenas na máquina do desenvolvedor e se tornar confiável, testável e executável em outros ambientes.

## 3. Testes automatizados

**O que é um teste automatizado?** É um código que executa outro código e confere sozinho se o resultado é o esperado.

**Diferença entre teste manual e automatizado:** no manual, uma pessoa executa e confere o resultado toda vez. No automatizado, o teste roda sozinho, é rápido e pode ser repetido a cada mudança, o que ajuda a achar erros cedo.

**Arrange, Act e Assert:** é um padrão para organizar o teste. Arrange prepara o cenário, Act executa o método testado e Assert verifica o resultado.

**Por que 100% de cobertura não garante que o sistema esteja correto?** A cobertura só mostra quais linhas foram executadas. Ela não garante que o teste confere o resultado certo nem que as regras de negócio estão corretas.

## 4. JUnit e Mockito

**Função do JUnit:** é o framework que permite escrever e executar testes em Java, com `@Test` e asserções como `assertEquals`.

**Função do Mockito:** cria objetos simulados (mocks), define o comportamento deles e verifica se foram chamados.

**O que é um Mock:** é um objeto falso que imita uma dependência real, como o Repository.

**Por que o teste de Service não deve depender do PostgreSQL:** o teste unitário deve ser rápido e isolado. Se dependesse do banco, poderia falhar por problema no banco e não por erro na regra de negócio.

**@Mock e @InjectMocks:** `@Mock` cria o objeto simulado. `@InjectMocks` cria o objeto real que será testado (o Service) e injeta os mocks nele.

**when/thenReturn, verify e assertThrows:** `when(...).thenReturn(...)` define o que o mock responde. `verify(...)` confere se um método do mock foi chamado. `assertThrows(...)` confere se uma exceção foi lançada.

**Exemplo comentado (`LivroServiceTest`):**

```java
@ExtendWith(MockitoExtension.class)   // liga o Mockito ao JUnit
class LivroServiceTest {
    @Mock private LivroRepository repository;   // Repository simulado, sem banco real
    @InjectMocks private LivroService service;  // Service real, recebe o mock

    @Test
    void deveCadastrarLivroValido() {
        Livro l = new Livro();                          // Arrange: cria o livro
        l.setTitulo("Clean Code"); l.setPreco(99.9);
        when(repository.save(l)).thenReturn(l);         // Arrange: configura o mock

        Livro r = service.cadastrar(l);                 // Act: executa o método

        assertEquals("Clean Code", r.getTitulo());      // Assert: confere o resultado
        verify(repository).save(l);                     // confere a interação com o Repository
    }
}
```

## 5. Testes de Service e Controller

**Diferença:** o teste de Service verifica as regras de negócio (por exemplo, rejeitar título vazio), com o Repository simulado. O teste de Controller verifica a camada web: rota, status HTTP e resposta, com o Service simulado.

**MockMvc:** simula requisições HTTP ao Controller sem subir o servidor. No projeto, `LivroControllerTest` testa que `GET /livros/9` retorna 404 quando o livro não existe.

**Resultado dos testes:** `mvn clean package` executou 6 testes, com 0 falhas e 0 erros (4 de Service e 2 de Controller). Ver evidência na seção 13.

## 6. Build e JAR

**O que é build:** transformar o código-fonte em algo executável: compilar, rodar os testes e empacotar.

**O que `mvn clean package` faz:** apaga a pasta `target`, compila o código, executa os testes e gera o JAR dentro de `target`.

**O que é um JAR:** é um arquivo compactado com as classes Java. No Spring Boot ele já inclui as dependências e o servidor embutido, então roda sozinho.

**Executar sem o IntelliJ:** com `java -jar target/livraria-0.0.1-SNAPSHOT.jar`. No projeto, o container executa exatamente esse comando (`java -jar app.jar`).

## 7. Configuração por ambiente

**Por que não deixar senhas no código:** quem acessa o repositório vê a senha, e ela seria a mesma em todos os ambientes, sem poder trocar facilmente.

**Variáveis de ambiente:** guardam valores fora do código, que mudam conforme o ambiente. No projeto, o `application.properties` usa `${DB_URL}`, `${DB_USER}` e `${DB_PASSWORD}`, e o Docker Compose define esses valores.

**Profiles do Spring:** são conjuntos de configuração para cada ambiente (como dev e prod), escolhidos com `spring.profiles.active`. Neste projeto não usei profiles, apenas variáveis de ambiente.

## 8. Docker e Docker Compose

**Imagem x container:** a imagem é o modelo pronto da aplicação. O container é a imagem em execução.

**Dockerfile comentado:**

```dockerfile
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
```

`FROM` define a imagem base, `WORKDIR` a pasta de trabalho, `COPY` copia arquivos para a imagem e `ENTRYPOINT` o comando que roda ao iniciar.

**Para que serve o Docker Compose:** define e sobe vários containers juntos (API e PostgreSQL), com rede, variáveis e volumes, em um único arquivo.

**Por que não usar `localhost` entre containers:** dentro do container da API, `localhost` é o próprio container. Por isso a API acessa o banco pelo nome do serviço: `jdbc:postgresql://db:5432/livraria`.

**Para que servem volumes:** guardam os dados fora do container. No projeto, o volume `pgdata` mantém os dados do PostgreSQL mesmo se o container for removido. Parar um container só o desliga; remover apaga o container, e sem volume o banco seria perdido.

**docker-compose.yml:** tem o serviço `db` (postgres:16, com volume e healthcheck) e o serviço `api` (construído pelo Dockerfile, porta 8080, variáveis de ambiente e `depends_on` esperando o banco ficar saudável).

## 9. Logs e Actuator

**Função dos logs:** registram o que a aplicação faz e ajudam a descobrir a causa de problemas. No projeto, o Service registra com `log.info` cada livro cadastrado.

**O que não deve aparecer nos logs:** senhas, tokens e dados pessoais. Por isso o log registra só o título do livro.

**Spring Boot Actuator:** adiciona endpoints para acompanhar a saúde da aplicação.

**`/actuator/health`:** retorna `{"status":"UP"}` quando a aplicação está funcionando. O `UP` não garante que todas as regras de negócio funcionam, só que a aplicação está de pé. Os endpoints do Actuator devem ser expostos com cuidado, porque podem revelar informações internas; por isso o projeto expõe apenas o `health`.

## 10. Solução do estudo de caso

- **Testes de Service e Controller:** resolvem a falta de testes. Aplicados com JUnit e Mockito (6 testes). Cuidado: testar casos de sucesso e de erro, não só cobertura.
- **Geração do JAR:** resolve a dependência da IDE. Aplicado com `mvn clean package`. Cuidado: o build só gera o JAR se os testes passarem.
- **Variáveis de ambiente:** resolvem as credenciais no `application.properties`. Aplicadas com `${DB_URL}`, `${DB_USER}` e `${DB_PASSWORD}`. Cuidado: o valor padrão do projeto é só para desenvolvimento e deve ser trocado fora dele.
- **Dockerfile:** resolve o "funciona na minha máquina". Aplicado empacotando o JAR numa imagem com Java 21. Cuidado: não colocar segredos no Dockerfile.
- **Docker Compose com PostgreSQL:** resolve a dificuldade de subir API e banco juntos. Aplicado com os serviços `api` e `db`. Cuidado: usar o nome do serviço, não `localhost`, e esperar o banco ficar saudável.
- **Armazenamento persistente:** resolve a perda de dados ao recriar o container. Aplicado com o volume `pgdata`. Cuidado: `docker compose down -v` apaga o volume e os dados.
- **Logs adequados:** resolvem a falta de visibilidade. Aplicados com `log.info` no Service. Cuidado: nunca registrar senhas ou dados sensíveis.
- **Actuator com health:** resolve a verificação automática de saúde. Aplicado com `/actuator/health`. Cuidado: expor só o necessário.

## 11. Conclusão

Uma API Spring Boot só se torna confiável quando tem testes automatizados, configuração fora do código, build repetível e execução independente da IDE. Neste trabalho apliquei isso em uma API de livros e vi na prática que testes, JAR, Docker, Compose e Actuator funcionam juntos. O que foi pesquisado está nas seções 3 a 9 e o que foi testado por mim está descrito na seção 13.

## 12. Referências

- JUnit 5 User Guide. JUnit. https://junit.org/junit5/docs/current/user-guide/ (acesso em 23/11/2026).
- Mockito Documentation. Mockito. https://site.mockito.org/ (acesso em 23/11/2026).
- Spring Boot Reference Documentation. VMware/Broadcom. https://docs.spring.io/spring-boot/ (acesso em 23/11/2026).
- Docker Documentation. Docker Inc. https://docs.docker.com/ (acesso em 23/11/2026).
- Apache Maven Documentation. Apache Software Foundation. https://maven.apache.org/guides/ (acesso em 23/11/2026).

## 13. Link do repositório e evidências de execução

Repositório: https://github.com/brianfigueredo0209-alt/Livraria-api

**Pesquisa conceitual:** seções 3 a 9. **Testado por mim:**
- 6 testes unitários com sucesso (BUILD SUCCESS) e JAR gerado em `target`.
- Containers `api` e `db` rodando (`docker compose ps`).
- `GET /actuator/health` retornando `{"status":"UP"}`.
- `POST /livros` gravando um livro no PostgreSQL e `GET /livros` listando.

Prints em `evidencias/` (inserir aqui).
