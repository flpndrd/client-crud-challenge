# Desafio — CRUD de Clientes

## 📄 Descrição

Este projeto implementa uma API REST com Spring Boot para gerenciamento de clientes, contemplando um CRUD completo com tratamento de exceções, validações e acesso a banco de dados em ambiente de testes.

## 🎯 Objetivo do desafio

Aplicar os conceitos de API REST, arquitetura em camadas, operações CRUD, validações e tratamento de exceções, conforme especificado no desafio.

## 🛠 Tecnologias utilizadas

- Java 25
- Spring Boot 4.1.1
- Spring Data JPA
- Hibernate
- Bean Validation
- Banco de dados H2
- JUnit
- Mockito
- AssertJ

## 📐 Modelo de Domínio

- **Client**: `clientId`, `name`, `cpf` (único), `income` (`BigDecimal`), `birthDate`, `children`

No JSON, o id continua como `id`, como no contrato do curso.

## 📦 Carga Inicial do Banco de Dados

A aplicação carrega automaticamente o banco de dados com 15 clientes, contendo dados iniciais para testes e validação da API.

## 🔎 Funcionalidades

A API disponibiliza os seguintes endpoints REST:

- GET /clients/{clientId}
- GET /clients (busca paginada)
- POST /clients
- PUT /clients/{clientId}
- DELETE /clients/{clientId}

A busca paginada retorna os clientes em `content` e os dados da página em `page` (`size`, `number`, `totalElements`, `totalPages`).

## ⚠️ Tratamento de Exceções

Os erros seguem o formato `ProblemDetail` (RFC 9457).

- `404 Not Found` — ID não encontrado (GET por id, PUT e DELETE)
- `409 Conflict` — CPF já cadastrado para outro cliente
- `422 Unprocessable Content` — erro de validação, com uma entrada por erro na propriedade `errors` (`fieldName` e `message`); um campo pode ter mais de uma
  - `name`: obrigatório
  - `cpf`: obrigatório, válido e só com números
  - `income`: obrigatório e não negativo
  - `birthDate`: obrigatória e não pode ser data futura
  - `children`: obrigatório e não negativo

## 🚀 Como Executar

1. Clonar o repositório
2. Importar o projeto em sua IDE como projeto Maven
3. Executar a classe principal da aplicação (anotada com `@SpringBootApplication`) pela IDE ou via terminal:

```bash
./mvnw spring-boot:run
```

## ✅ Validação

Após executar a aplicação, o banco de dados pode ser acessado via H2 Console para verificação das tabelas e dados inseridos.

- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Usuário: sa
- Senha: (vazia)

## 🧪 Testes

Os testes unitários cobrem as regras do serviço, e os testes de integração cobrem os itens do checklist do desafio, além do CPF duplicado:

```bash
./mvnw clean verify
```

## 📌 Exemplos de Requisições

### Busca de cliente por ID

```http
GET /clients/1
```

### Busca paginada de clientes

```http
GET /clients?page=0&size=6&sort=name
```

### Inserção de cliente

```http
POST /clients
```

```json
{
  "name": "Maria Silva",
  "cpf": "52998224725",
  "income": 6500.0,
  "birthDate": "1994-07-20",
  "children": 2
}
```

### Atualização de cliente

```http
PUT /clients/1
```

```json
{
  "name": "Maria Silvaaa",
  "cpf": "12345678909",
  "income": 6500.0,
  "birthDate": "1994-07-20",
  "children": 2
}
```

### Exclusão de cliente

```http
DELETE /clients/1
```
