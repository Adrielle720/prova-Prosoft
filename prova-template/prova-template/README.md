# Template pré-pronto — Prova Projeto de Software

Este repo já traz pronto tudo que **não muda** de exercício para exercício.
Na hora da prova você só mexe no que for específico do enunciado.

## O que já vem pronto

- [x] Spring Boot + Postgres + JPA configurados (`pom.xml`, `application.properties`)
- [x] Entidade genérica `Item` com soft delete (renomear)
- [x] Repository com query de `startsWith` + soft delete pronta
- [x] Service + Controller com as 3 operações-padrão (listar/filtrar, criar, deletar lógico)
- [x] Tratamento de erros com `@RestControllerAdvice` (404, 400)
- [x] Testes unitários do service com Mockito (100% de cobertura do template)
- [x] Teste de integração das 3 rotas com MockMvc + H2 (não depende de Docker rodando)
- [x] `Dockerfile` multi-stage
- [x] GitHub Actions (`.github/workflows/deploy.yml`) com:
  - job de teste (roda em push E pull request)
  - job de deploy (só em push na main, depende do job de teste passar)
  - login DockerHub, build+push da imagem, deploy via SSH na AWS
- [x] `application.properties` já usando variáveis de ambiente com defaults (padrão da Aula de Redes Docker)

## ANTES da prova (fazer em casa, com calma)

1. **Criar o repositório no GitHub** a partir deste template.
2. **Criar a conta/imagem no Docker Hub** se ainda não tiver.
3. **Configurar os Secrets do repositório** (Settings → Secrets and variables → Actions):
   - `DOCKERHUB_USERNAME`
   - `DOCKERHUB_TOKEN` (token de acesso do Docker Hub, não a senha)
   - `HOST_AWS` (IP da máquina AWS que o professor vai dar)
   - `SSH_KEY` (conteúdo do arquivo `.pem` da AWS)
   - `DB_PASSWORD` (senha que você quer usar no Postgres)
4. **Testar o pipeline com um exercício fake**: suba esse template como está,
   confirme que `test` e `deploy` passam verdes no GitHub Actions e que a
   aplicação sobe na AWS. **Isso é o que garante que, no dia da prova, você
   só troca a lógica de negócio e o pipeline já funciona.**
5. Deixar a rede Docker e o container do Postgres **já criados na sua máquina
   AWS antes da prova** (se o professor permitir acesso prévio):
   ```bash
   docker network create -d bridge rede
   docker run -d --name postgres-aula \
     -e POSTGRES_DB=provadb -e POSTGRES_USER=usuario -e POSTGRES_PASSWORD=senha \
     --network rede -p 5432:5432 postgres
   ```
   Se não puder acessar antes, pelo menos **decore/cole esses dois comandos**
   num bloco de notas para copiar rápido no dia.
6. Rodar `mvn test` localmente uma vez para confirmar que compila e que o
   Jacoco gera relatório de cobertura (`target/site/jacoco/index.html`).

## DURANTE a prova (o que efetivamente muda por exercício)

1. Renomear `Item` → entidade do enunciado (ex: `Curso`), ajustar os atributos.
2. Ajustar `ItemRequest` com os campos pedidos.
3. Ajustar `ItemRepository` se precisar de outras queries.
4. Ajustar regras específicas em `ItemService` (a estrutura de listar/criar/deletar já está pronta).
5. Trocar `/itens` pela rota pedida no `ItemController`.
6. Copiar/ajustar os testes (unitário + integração) para os novos nomes —
   a estrutura de "mockar o repository" e "MockMvc nas 3 rotas" já está pronta,
   é só adaptar assinatura e valores.
7. `git add . && git commit -m "..." && git push` — pipeline dispara sozinho.
8. Se o enunciado pedir "uma rota via Pull Request": criar uma branch, implementar
   só aquela rota lá, abrir PR contra `main` — o job `test` do Actions dispara
   automaticamente por causa do trigger `pull_request` já configurado.

## Comandos que você pode precisar colar rápido

```bash
# rodar local sem docker
mvn spring-boot:run

# rodar todos os testes + ver cobertura
mvn test

# build da imagem manual (se quiser testar antes do Actions fazer sozinho)
docker build -t SEU_USUARIO/prova-api .
docker push SEU_USUARIO/prova-api

# ssh na AWS
ssh ubuntu@SEU_IP -i chave.pem
```
