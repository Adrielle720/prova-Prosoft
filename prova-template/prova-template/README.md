# API de Produtos — Padrão Observer

API REST (Spring Boot 3 + JPA + Postgres) para gerenciar produtos de uma loja,
com **padrão Observer** para auditoria e alerta de estoque.

## Rotas

| Método | Rota             | Descrição                    |
|--------|------------------|------------------------------|
| POST   | `/produtos`      | Cria produto (201)           |
| GET    | `/produtos`      | Lista produtos               |
| GET    | `/produtos/{id}` | Busca por id (404 se não há) |
| DELETE | `/produtos/{id}` | Exclui produto (204 / 404)   |

Corpo do POST:

```json
{ "nome": "Teclado", "descricao": "Mecânico", "preco": 199.90, "quantidade": 5 }
```

## Padrão Observer

- **Subject:** `ProdutoService` — após `criar` (CREATE) e `excluir` (DELETE)
  publica um `ProdutoEvento` para todos os `ProdutoObserver` (injetados pelo Spring).
- **Observer 1 — `AuditoriaObserver`:** grava na tabela `auditoria` a operação,
  o timestamp e o id do produto.
- **Observer 2 — `EstoqueBaixoObserver`:** em um CREATE com `quantidade < 10`,
  escreve `ALERTA: estoque baixo ...` no log (nível WARN).

Para adicionar um novo observer basta criar um `@Component` que implemente `ProdutoObserver`.

## Testes

```bash
mvn verify
```

- `ProdutoServiceTest` — testes de unidade (Mockito) com **100% de cobertura** do
  `ProdutoService`. O `jacoco:check` no `pom.xml` falha o build se cair abaixo disso.
- `ProdutoIntegrationTest` — teste de integração da rota `POST /produtos`
  (MockMvc + H2), verificando a resposta, a persistência e o registro de auditoria.
- Relatório de cobertura: `target/site/jacoco/index.html`.

## CI/CD

Workflow em `.github/workflows/deploy.yml` (raiz do repositório):

1. **test** — em push e pull request para `main`: `mvn verify`.
2. **deploy** — só em push na `main`, após os testes: build e push da imagem
   no Docker Hub, depois SSH na AWS (`98.92.113.150`) para subir o container
   `prova-api` na rede `rede`, ligado ao `postgres-aula` (criados se não existirem).

Secrets necessários (Settings → Secrets and variables → Actions):

| Secret               | Valor                                              |
|----------------------|----------------------------------------------------|
| `DOCKERHUB_USERNAME` | usuário do Docker Hub                              |
| `DOCKERHUB_TOKEN`    | access token do Docker Hub                         |
| `HOST_AWS`           | `98.92.113.150`                                    |
| `SSH_KEY`            | conteúdo do arquivo `.pem`                         |
| `DB_PASSWORD`        | senha do Postgres `postgres-aula` na máquina AWS   |

A porta **8080** precisa estar liberada no Security Group da instância.
