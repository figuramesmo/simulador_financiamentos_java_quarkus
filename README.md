# DESAFIO TÉCNICO: Simulador de Financiamentos - JAVA

Aplicação para simular operações de crédito com cálculo de juros compostos, geração de memória de cálculo detalhada e persistência em banco de dados H2 (em memória).

## Características

- **Versão do Java:** 25
- **Framework**: Quarkus
- **Banco de Dados**: H2 (em memória)
- **Precisão Financeira**: BigDecimal com 4 casas decimais (arredondamento HALF_EVEN)
- **API**: REST com documentação OpenAPI/Swagger
- **Testes**: implementados com JUnit5 (visualização da cobertura com JaCoCo)

## Como Executar o app

**Atenção:** todos os comandos deste documento devem ser executados num terminal no diretório raiz deste projeto (o mesmo diretório do arquivo README.MD).

### Executar em Modo Desenvolvimento

Execute a aplicação em modo desenvolvimento:

```bash
./mvnw quarkus:dev
```

A aplicação estará disponível em `http://localhost:8080`.

### Compilar Empacotar e Executar em Modo Produção

Compile e empacote a aplicação:

```bash
./mvnw package
```

Execute o JAR gerado:

```bash
java -jar ./target/quarkus-app/quarkus-run.jar
```

A aplicação estará disponível em `http://localhost:8080`.

## Como executar os testes e acessar o report de cobertura

**Atenção:** interrompa a execução da aplicação antes de executar os comandos de teste.

**Atenção:** todos os comandos deste documento devem ser executados num terminal no diretório raiz deste projeto (o mesmo diretório do arquivo README.MD).

Execute todos os testes:

```bash
./mvnw clean test
```

Após execução dos testes, visualize as estatíticas da cobertura com o JaCoCo report:

```bash
start target/jacoco-report/index.html
```

## Endpoints da Aplicação

**Atenção:** para acessar os endpoints da Aplicação, esteja com a aplicação em execução.

### Documentação da API

Acesse a documentação interativa via Swagger UI:

```
http://localhost:8080/q/swagger-ui/
```

Ou visualize o contrato OpenAPI em JSON:

```
http://localhost:8080/q/openapi.json
```



### Endpoints da API

- `POST /simulacoes` - Simula um financiamento
- `GET /simulacoes/{id}` - Retorna simulação completa com memória de cálculo

**Dica:** Teste manualmente a aplicação via Swagger UI ou faça upload do contrato openapi.json no Postman

## Estrutura do Projeto

src/main/java/org/caixa/financiamentos/
├── dto/               # Data Transfer Objects
├── entity/            # Entidades JPA
├── repository/        # Acesso a dados
├── resource/          # Controllers REST
├── service/           # Lógica de negócio
├── errorhandler/      # Mapeamento de exceções
└── utils/             # Serialização/Deserialização BigDecimal

src/test/java/org/caixa/financiamentos/
├── resource/          # Testes de integração REST
├── service/           # Testes das regras de negócio
└── utils/             # Testes de serialização/deserialização BigDecimal
