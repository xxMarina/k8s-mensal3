# marmitech-api

API do Marmitech, um sistema de pedidos para cardápio online com fila de atendimento. Este repositório tem só o backend. O frontend Angular fica em `marmitech-web`.

O projeto nasceu como monorepo na disciplina de Engenharia de Software do 4º período. Separei em dois repositórios agora porque cada parte vai para um serviço diferente na nuvem e os deploys não precisam andar juntos.

## Stack

- Java 17
- Spring Boot 3.5.4
- Spring Data JPA
- Spring Security com BCrypt
- MySQL 8 em produção, H2 disponível para teste
- Maven

## Domínio

Sete entidades: Cliente, Usuario, Categoria, Produto, Pedido, PedidoItem e HistoricoCompra.

O pacote está organizado em camadas: `Controller`, `Services`, `Repository`, `Entity`, `DTO` (separado em request e response), `Mapper`, `Config`, `Enums` e `Exceptions`.

## Rodando local

Precisa de Java 17 e um MySQL rodando na 3306 com um banco chamado `marmitech`.

```bash
git clone https://github.com/<org>/marmitech-api.git
cd marmitech-api
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`. O Hibernate cria as tabelas sozinho no primeiro start.

Se seu MySQL local usa outra senha, exporte as variáveis antes de rodar:

```bash
export DB_USER=root
export DB_PASS=suasenha
./mvnw spring-boot:run
```

## Variáveis de ambiente

Nenhuma credencial fica no código. O `application.properties` só tem valores padrão que servem para desenvolvimento local.

| Variável | Padrão | Para que serve |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/marmitech` | String JDBC do banco |
| `DB_USER` | `root` | Usuário do banco |
| `DB_PASS` | `root` | Senha do banco |
| `PORT` | `8080` | Porta HTTP. Em produção quem define é o Cloud Run |
| `CORS_ORIGINS` | `http://localhost:4200` | Origens liberadas, separadas por vírgula |

Tem um `.env.example` na raiz com esses nomes. Copie para `.env` se quiser, mas o `.env` está no `.gitignore` e não sobe.

## Docker

```bash
docker build -t marmitech-api .
docker run -p 8080:8080 \
  -e DB_URL="jdbc:mysql://host.docker.internal:3306/marmitech" \
  -e DB_USER=root -e DB_PASS=suasenha \
  marmitech-api
```

O build é em dois estágios. O primeiro compila com Maven, o segundo só carrega o JRE e o jar. A imagem final fica bem menor do que se eu deixasse o Maven junto.

## Deploy no Cloud Run

O passo a passo completo está em `docs/04-runbook-deploy-gcp.md`. O resumo é:

```bash
gcloud run deploy marmitech-api \
  --source . \
  --region=southamerica-east1 \
  --allow-unauthenticated \
  --add-cloudsql-instances=PROJECT_ID:southamerica-east1:marmitech-db \
  --set-secrets=DB_PASS=db-pass:latest \
  --set-env-vars="DB_URL=jdbc:mysql:///marmitech?cloudSqlInstance=PROJECT_ID:southamerica-east1:marmitech-db&socketFactory=com.google.cloud.sql.mysql.SocketFactory,DB_USER=root,CORS_ORIGINS=https://PROJECT_ID.web.app"
```

A conexão com o Cloud SQL é por socket Unix, não por IP. O banco não tem endereço público, então só quem está dentro do projeto GCP consegue falar com ele.

## Coisas que ainda incomodam

`spring.jpa.hibernate.ddl-auto=update` deixa o Hibernate mexer no schema sozinho. Para o prazo do mensal está resolvendo, mas em qualquer coisa séria eu trocaria por Flyway. Fica anotado como melhoria.

Os endpoints estão todos com `permitAll` no `SecurityConfig`. O JWT existe no frontend mas a API ainda não está barrando ninguém. Precisa fechar isso antes da entrega final.

## Branches

Trabalhamos com Git Flow. `main` é o que está publicado, `develop` é a integração. Nada entra direto nas duas: só por Pull Request com uma aprovação. O guia completo está em `docs/02-git-flow-equipe.md`.

Commits seguem Conventional Commits (`feat:`, `fix:`, `chore:`, `docs:`, `refactor:`, `test:`).

## Equipe

| Nome | Papel |
|---|---|
| Heron | Scrum Master |
| Jihad Ghozayel | Product Owner |
| Marina | Developer |
| Fabricio Quintana | Developer |
| João Rodrigues | Developer |
