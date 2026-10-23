# Guia de Migração para Kubernetes & Documentação Técnica - Marmitech

Este documento serve como diário de bordo e material de apoio para a equipe, documentando **todas as decisões arquiteturais, arquivos criados, motivos de cada escolha e instruções de funcionamento**. Ele também serve de guia de estudo para a **apresentação prática**.

---

## Visão Geral da Arquitetura

No ambiente anterior, a aplicação frontend apontava para uma URL pública externa e o backend rodava isoladamente. No Kubernetes local com Minikube:

1. **Frontend (`marmitech-web`)**: Executa dentro de um container com Nginx servindo os arquivos estáticos do Angular e atuando como **proxy reverso**.
2. **Backend (`marmitech-api`)**: Executa dentro do cluster com múltiplas réplicas para garantir disponibilidade e demonstrar balanceamento de carga.
3. **Banco de Dados (`mysql`)**: Executa com Pod próprio e volume persistente.
4. **Comunicação Segura e Transparente**: O frontend conversa com o backend através do nome de serviço interno do Kubernetes (`marmitech-api:8080`), sem expor a API diretamente para a rede externa e eliminando problemas de CORS.

---

## Etapa 1: Preparação do Frontend (`marmitech-web`)

### 1. Ajuste de Variável de Ambiente (`environment.prod.ts`)

* **Arquivo modificado:** `marmitech-web/src/environments/environment.prod.ts`
* **O que foi feito:**

  ```typescript
  export const environment = {
    production: true,
    apiUrl: '/backend'
  };
  ```

* **Por que fizemos isso?**
  * Anteriormente, o frontend continha uma URL absoluta (`https://marmitech-api-...run.app`).
  * Em um cluster Kubernetes com Minikube, os Pods internos possuem IPs dinâmicos e nomes de DNS internos que o navegador do usuário (fora do cluster) **não** consegue resolver diretamente.
  * Ao utilizarmos uma URL relativa (`/backend`), o navegador faz as requisições HTTP para a mesma origem de onde baixou a página (ex: `http://<ip-minikube>:30080/backend/api/produto`).
  * O servidor web (Nginx) intercepta essa rota e encaminha para o backend correto dentro do cluster.
  * **Vantagem bônus:** Como o navegador faz a requisição para a mesma origem, **elimina-se qualquer problema de CORS (Cross-Origin Resource Sharing)**.

---

### 2. Configuração do Nginx (`nginx.conf`)

* **Arquivo criado:** `marmitech-web/nginx.conf`
* **Como funciona:**

  ```nginx
  server {
      listen 80;
      server_name localhost;

      # 1. Suporte a SPA (Single Page Application)
      location / {
          root /usr/share/nginx/html;
          index index.html index.htm;
          try_files $uri $uri/ /index.html;
      }

      # 2. Proxy Reverso para o Backend
      location /backend/ {
          proxy_pass http://marmitech-api:8080/;
          proxy_http_version 1.1;
          proxy_set_header Upgrade $http_upgrade;
          proxy_set_header Connection 'upgrade';
          proxy_set_header Host $host;
          proxy_cache_bypass $http_upgrade;
          proxy_set_header X-Real-IP $remote_addr;
          proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
          proxy_set_header X-Forwarded-Proto $scheme;
      }
  }
  ```

* **Por que essas duas regras são vitais?**
  * **`try_files $uri $uri/ /index.html;` (SPA Routing):** O Angular é uma SPA e gerencia as rotas no navegador (HTML5 History API). Se o usuário estiver na tela `/admin/produtos` e der F5, o Nginx procurará um arquivo físico chamado `/admin/produtos/index.html`. Como ele não existe no disco, o Nginx responderia com erro **404 Not Found**. A diretiva `try_files` diz: *"Se não achar o arquivo estático, entregue o `index.html` e deixe o roteador do Angular assumir"*.
  * **`location /backend/` e `proxy_pass http://marmitech-api:8080/;`:**
    * Intercepta qualquer requisição iniciada com `/backend/`.
    * A barra final em `http://marmitech-api:8080/` é fundamental: ela diz ao Nginx para **remover o prefixo `/backend`** ao repassar para a API. Logo, `GET /backend/api/produto` vira `GET /api/produto` no Spring Boot.
    * `marmitech-api` é o nome do **Service** do Kubernetes, resolvido pelo CoreDNS do cluster.

---

### 3. Filtro de Arquivos para o Docker (`.dockerignore`)

* **Arquivo criado:** `marmitech-web/.dockerignore`
* **O que foi colocado:**

  ```
  node_modules
  dist
  .angular
  .git
  .gitignore
  .vscode
  npm-debug.log
  ```

* **Por que fizemos isso?**
  * Evita o envio de centenas de megabytes desnecessários durante o `docker build` (o chamado "Docker build context").
  * Impede conflitos de dependências compiladas na máquina do desenvolvedor (Linux/Mac/Windows) com o ambiente do container Alpine.

---

### 4. Conteinerização com Multi-Stage Build (`Dockerfile`)

* **Arquivo criado:** `marmitech-web/Dockerfile`
* **Estrutura:**
  * **Estágio 1 - Compilação (`FROM node:20-alpine AS build`):**
    * Baixa dependências (`npm ci` para garantir reprodutibilidade baseada no `package-lock.json`).
    * Executa o comando `npm run build -- --configuration production`.
    * Esse processo gera os arquivos HTML, JS e CSS minificados na pasta `dist/marmitech-web/browser`.
  * **Estágio 2 - Execução/Produção (`FROM nginx:alpine`):**
    * Copia a nossa configuração `nginx.conf` para `/etc/nginx/conf.d/default.conf`.
    * Copia **apenas** a pasta compilada (`dist/.../browser`) do estágio de build para `/usr/share/nginx/html`.
* **Por que usar Multi-Stage Build?**
  * **Segurança e Tamanho:** O Node.js, npm, código fonte original e a pasta `node_modules` (que pode ter mais de 500MB) são **descartados**.
  * A imagem final resultante contém apenas o servidor web Nginx e os arquivos estáticos, ficando com tamanho reduzido (geralmente menor que 30MB) e sem vulnerabilidades de pacotes de desenvolvimento.

---

## Etapa 2: Manifestos Kubernetes do Frontend (`k8s/web/`)

Nesta etapa, declaramos ao Kubernetes como queremos que o nosso frontend seja executado (`Deployment`) e como ele deve ser acessado de fora do cluster (`Service`).

### 1. O Deployment (`k8s/web/deployment.yaml`)

* **Arquivo criado:** `k8s/web/deployment.yaml`
* **Conteúdo:**

  ```yaml
  apiVersion: apps/v1
  kind: Deployment
  metadata:
    name: marmitech-web
    labels:
      app: marmitech-web
  spec:
    replicas: 2
    selector:
      matchLabels:
        app: marmitech-web
    template:
      metadata:
        labels:
          app: marmitech-web
      spec:
        containers:
          - name: marmitech-web
            image: marmitech-web:1.0
            imagePullPolicy: Never
            ports:
              - containerPort: 80
            resources:
              requests:
                memory: "64Mi"
                cpu: "50m"
              limits:
                memory: "256Mi"
                cpu: "200m"
  ```

* **Conceitos Importantes e Explicação dos Campos:**
  * **O que é um Deployment?** É um controlador de alto nível do Kubernetes responsável por garantir que um número especificado de Pods esteja sempre ativo, gerenciando atualizações de versão (rollouts), reinicializações e auto-recuperação (self-healing).
  * **`replicas: 1`:** Define quantas instâncias idênticas do Pod do frontend devem rodar simultaneamente. Se um Pod for deletado manualmente ou falhar, o Deployment cria outro imediatamente.
  * **`image: marmitech-web:1.0`:** Nome da imagem que construímos localmente.
  * **`imagePullPolicy: Never` (CRÍTICO NO MINIKUBE):**
    * Por padrão, o Kubernetes tenta baixar imagens de repositórios públicos (Docker Hub).
    * Como a nossa imagem foi gerada localmente no computador/Minikube, o K8s falharia com o erro `ErrImagePull` ou `ImagePullBackOff`.
    * O `Never` força o cluster a utilizar **exclusivamente a imagem existente na máquina local**.
  * **`selector.matchLabels` e `template.metadata.labels`:**
    * O Deployment usa um sistema de etiquetas (**Labels**). Ele procura e gerencia todos os Pods que tenham o rótulo `app: marmitech-web`.
  * **`resources` (requests e limits):**
    * **Requests (solicitação mínima):** Garante que o nó tenha pelo menos 64MB de RAM e 50m (0.05 CPU) livres para agendar o Pod.
    * **Limits (teto máximo):** Impede que o container consuma mais de 256MB de memória ou 200m de CPU, evitando que um vazamento de memória afete os outros serviços (API e MySQL).

---

### 2. O Service (`k8s/web/service.yaml`)

* **Arquivo criado:** `k8s/web/service.yaml`
* **Conteúdo:**

  ```yaml
  apiVersion: v1
  kind: Service
  metadata:
    name: marmitech-web
    labels:
      app: marmitech-web
  spec:
    type: NodePort
    selector:
      app: marmitech-web
    ports:
      - name: http
        port: 80
        targetPort: 80
        nodePort: 30080
  ```

* **Conceitos Importantes e Explicação dos Campos:**
  * **O que é um Service?** No Kubernetes, os Pods são efêmeros (nascem, morrem e mudam de IP constantemente). O Service fornece um **ponto de acesso fixo e estável** (um IP virtual e nome DNS) com balanceamento de carga automático para os Pods que atendem ao `selector`.
  * **`selector: app: marmitech-web`:** Liga este Service a qualquer Pod que possua a etiqueta `app: marmitech-web` (criada pelo Deployment acima).
  * **Por que `type: NodePort`?**
    * No Kubernetes existem 3 tipos principais de Service:
      1. `ClusterIP` (padrão): Acessível apenas **dentro** do cluster (ideal para banco e API interna).
      2. `NodePort`: Abre uma porta de alta numeração (30000-32767) em **todos os nós do cluster**, permitindo que pessoas de fora acessem o serviço diretamente.
      3. `LoadBalancer`: Usado em nuvens públicas (AWS, GCP, Azure) para provisionar balanceadores gerenciados.
    * Para expor o frontend no **Minikube** sem custos e de forma direta, o `NodePort` é o padrão recomendado.
  * **Entendendo as 3 Portas:**
    * **`port: 80`:** A porta interna do próprio Service. Outros Pods no cluster podem chamar `http://marmitech-web:80`.
    * **`targetPort: 80`:** A porta onde o container (Nginx) está escutando dentro do Pod.
    * **`nodePort: 30080`:** A porta fixa exposta para o mundo externo. Qualquer requisição para `http://<IP-DO-MINIKUBE>:30080` será encaminhada para o frontend.

---

## Como Executar e Validar o Frontend no Minikube

Com a pasta `marmitech-web/` e `k8s/web/` prontas, os comandos para rodar são:

```bash
# 1. Iniciar o cluster Minikube (caso não esteja rodando)
minikube start --driver=docker

# 2. Apontar o terminal para o Docker interno do Minikube (para que o K8s enxergue a imagem construída)
eval $(minikube docker-env)

# 3. Construir a imagem dentro do Minikube
docker build -t marmitech-web:1.0 ./marmitech-web

# 4. Aplicar os manifestos no Kubernetes
kubectl apply -f k8s/web/

# 5. Conferir se o Pod e o Service subiram
kubectl get pods -o wide
kubectl get services
kubectl get deployments

# 6. Abrir a aplicação no navegador
minikube service marmitech-web
```

---

## Validação Prática Realizada com Sucesso

Durante o primeiro teste com os Pods, realizamos validações ponta a ponta e identificamos um ponto arquitetural valioso para a apresentação:

### 1. Resolução Dinâmica de DNS no Nginx (Resiliência)

* **Cenário:** O Nginx, por padrão, tenta resolver no startup o host configurado no `proxy_pass`. Como a API ainda não existia no cluster, ele crashava com `[emerg] host not found in upstream "marmitech-api"`.
* **Solução Implementada no `nginx.conf`:**

  ```nginx
  resolver 10.96.0.10 valid=10s ipv6=off;
  set $backend_upstream http://marmitech-api:8080;
  rewrite ^/backend/(.*) /$1 break;
  proxy_pass $backend_upstream;
  ```

  *(O IP `10.96.0.10` é o endereço padrão do serviço `kube-dns` do Kubernetes)*.
* **Resultado:** O Nginx passa a resolver o DNS da API sob demanda (lazy evaluation). O frontend sobe e opera perfeitamente independente do estado do backend.

### 2. Testes de Requisição HTTP

* **Acesso à Aplicação Web:**

  ```bash
  curl -I http://192.168.49.2:30080
  ```

  **Resultado:** `HTTP/1.1 200 OK` — O Nginx entregou a página do Angular renderizada, arquivos de estilização (MDB / Marmitech) e scripts JS.
* **Acesso à Rota de API (sem backend ativo ainda):**

  ```bash
  curl -I http://192.168.49.2:30080/backend/api/produto
  ```

  **Resultado:** `HTTP/1.1 502 Bad Gateway` — Prova concreta de que a regra do proxy reverso está funcionando perfeitamente! Ela interceptou o tráfego `/backend`, tentou encaminhar para o service interno `marmitech-api:8080` e retornou 502 porque a API ainda não está no ar.

---

## 🗄️ Etapa 3: Banco de Dados MySQL no Kubernetes (`k8s/mysql/`)

Nesta etapa, implementamos o banco de dados relacional **MySQL 8.0** de forma 100% nativa dentro do cluster Kubernetes, garantindo persistência dos dados e comunicação segura com a API.

### 1. O Secret do Banco (`k8s/mysql/secret.yaml`)

* **Arquivo criado:** `k8s/mysql/secret.yaml`
* **Conteúdo:**

  ```yaml
  apiVersion: v1
  kind: Secret
  metadata:
    name: mysql-secret
    labels:
      app: mysql
  type: Opaque
  stringData:
    mysql-root-password: "root"
  ```

* **Conceito:** O `Secret` armazena credenciais confidenciais. Ao usar `stringData`, o Kubernetes cuida da codificação base64 automaticamente. Esse valor é injetado como variável de ambiente no container sem expor a senha em texto plano no Deployment.

---

### 2. Persistência de Dados com PVC (`k8s/mysql/pvc.yaml`)

* **Arquivo criado:** `k8s/mysql/pvc.yaml`
* **Conteúdo:**

  ```yaml
  apiVersion: v1
  kind: PersistentVolumeClaim
  metadata:
    name: mysql-pvc
    labels:
      app: mysql
  spec:
    accessModes:
      - ReadWriteOnce
    resources:
      requests:
        storage: 1Gi
  ```

* **Conceitos Importantes:**
  * **O que é um PersistentVolumeClaim (PVC)?** Pods são efêmeros por natureza (se o Pod morrer, tudo dentro dele é apagado). O PVC funciona como uma "solicitação de disco virtual independente". O Minikube provisiona automaticamente um volume físico no host via sua `default-storageclass`.
  * **`accessModes: ReadWriteOnce`:** Significa que o volume pode ser montado para leitura e escrita por **um único nó/Pod por vez**, o que é ideal para bancos relacionais como o MySQL.
  * **Garantia de Não Perda de Dados:** Se o Pod do MySQL for deletado ou reiniciado, o novo Pod reconecta automaticamente ao mesmo PVC (`mysql-pvc`), mantendo todas as tabelas e registros intactos.

---

### 3. O Deployment do MySQL (`k8s/mysql/deployment.yaml`)

* **Arquivo criado:** `k8s/mysql/deployment.yaml`
* **Conteúdo:**

  ```yaml
  apiVersion: apps/v1
  kind: Deployment
  metadata:
    name: mysql
    labels:
      app: mysql
  spec:
    replicas: 1
    selector:
      matchLabels:
        app: mysql
    template:
      metadata:
        labels:
          app: mysql
      spec:
        containers:
          - name: mysql
            image: mysql:8.0
            args:
              - "--default-authentication-plugin=mysql_native_password"
            ports:
              - containerPort: 3306
            env:
              - name: MYSQL_ROOT_PASSWORD
                valueFrom:
                  secretKeyRef:
                    name: mysql-secret
                    key: mysql-root-password
              - name: MYSQL_DATABASE
                value: "marmitech"
            volumeMounts:
              - name: mysql-persistent-storage
                mountPath: /var/lib/mysql
            resources:
              requests:
                memory: "256Mi"
                cpu: "100m"
              limits:
                memory: "512Mi"
                cpu: "500m"
        volumes:
          - name: mysql-persistent-storage
            persistentVolumeClaim:
              claimName: mysql-pvc
  ```

* **Decisões de Arquitetura:**
  * **Por que `replicas: 1` no banco?** Aplicações com estado (*Stateful*) que gravam no mesmo disco não podem rodar em múltiplos Pods sem replicação complexa (como replicação Master-Slave). Como o edital exige 2 réplicas apenas de *"um dos componentes quando a arquitetura permitir"*, o MySQL deve rodar em 1 réplica para evitar corrupção de dados.
  * **`MYSQL_DATABASE: "marmitech"`:** O MySQL cria automaticamente o banco de dados `marmitech` na primeira inicialização.
  * **`volumeMounts` em `/var/lib/mysql`:** Pasta interna padrão onde o MySQL armazena seus arquivos de dados (tabelas e índices). Ao apontar para o `mysql-pvc`, todo dado gravado fica salvo no volume persistente.

---

### 4. O Service Interno do MySQL (`k8s/mysql/service.yaml`)

* **Arquivo criado:** `k8s/mysql/service.yaml`
* **Conteúdo:**

  ```yaml
  apiVersion: v1
  kind: Service
  metadata:
    name: mysql
    labels:
      app: mysql
  spec:
    type: ClusterIP
    selector:
      app: mysql
    ports:
      - name: mysql
        port: 3306
        targetPort: 3306
  ```

* **Conceitos Importantes:**
  * **Por que `type: ClusterIP` e NÃO `NodePort`?**
    * Segurança: O banco de dados **nunca** deve ser exposto diretamente para a internet ou para a rede externa da máquina host.
    * O tipo `ClusterIP` gera um endereço IP interno e um nome DNS exclusivo (`mysql:3306`) acessível **somente** por outros Pods dentro do cluster.
    * A API Spring Boot conecta nele diretamente usando a URL padrão: `jdbc:mysql://mysql:3306/marmitech`.

---

### 5. Validação Prática do MySQL e Teste de Persistência

Durante os testes no Minikube:

1. O volume persistente (`mysql-pvc`) foi vinculado (`Bound`) com sucesso.
2. O Pod do MySQL subiu e inicializou o banco `marmitech`.
3. **Teste de Persistência (Self-Healing com integridade dos dados):**
   * Criamos uma tabela de teste e inserimos um registro (`1, 'K8s PVC Funcionando!'`).
   * Executamos `kubectl delete pod <pod-do-mysql>` para simular uma falha de hardware ou reinicialização.
   * O Kubernetes recriou o Pod instantaneamente com um novo ID.
   * Consultamos a tabela no novo Pod e o registro estava **100% preservado**, provando que o PVC impede a perda de dados.

---

## ⚙️ Etapa 4: Backend Spring Boot no Kubernetes (`k8s/api/`)

Nesta etapa, implementamos a API Spring Boot em **alta disponibilidade (2 réplicas)**, aplicando separação rigorosa entre configurações públicas (`ConfigMap`) e credenciais sensíveis (`Secret`), e demonstrando o balanceamento de carga nativo do Kubernetes.

### 1. Separação de Configurações: ConfigMap vs Secret

* **ConfigMap (`k8s/api/configmap.yaml`):** Guarda variáveis não sigilosas de infraestrutura.

  ```yaml
  apiVersion: v1
  kind: ConfigMap
  metadata:
    name: api-config
    labels:
      app: marmitech-api
  data:
    DB_URL: "jdbc:mysql://mysql:3306/marmitech"
    DB_USER: "root"
    PORT: "8080"
    CORS_ORIGINS: "*"
  ```

* **Secret (`k8s/api/secret.yaml`):** Guarda segredos e credenciais criptografadas/protegidas.

  ```yaml
  apiVersion: v1
  kind: Secret
  metadata:
    name: api-secret
    labels:
      app: marmitech-api
  type: Opaque
  stringData:
    DB_PASS: "root"
    JWT_SECRET: "chave-apenas-para-desenvolvimento-local-nao-usar-em-producao"
    JWT_EXPIRATION_MS: "86400000"
  ```

* **Por que essa divisão é fundamental para o edital?**
  Demonstra conformidade com o princípio de menor privilégio e boas práticas de segurança: quem tem permissão para ler o `ConfigMap` não tem necessariamente acesso às senhas do `Secret`.

---

### 2. O Deployment da API (`k8s/api/deployment.yaml`)

* **Arquivo criado:** `k8s/api/deployment.yaml`
* **Conteúdo:**

  ```yaml
  apiVersion: apps/v1
  kind: Deployment
  metadata:
    name: marmitech-api
    labels:
      app: marmitech-api
  spec:
    replicas: 2
    selector:
      matchLabels:
        app: marmitech-api
    template:
      metadata:
        labels:
          app: marmitech-api
      spec:
        terminationGracePeriodSeconds: 35
        containers:
          - name: marmitech-api
            image: marmitech-api:1.0
            imagePullPolicy: Never
            ports:
              - containerPort: 8080
            lifecycle:
              preStop:
                exec:
                  command: ["/bin/sh", "-c", "sleep 5"]
            envFrom:
              - configMapRef:
                  name: api-config
              - secretRef:
                  name: api-secret
            resources:
              requests:
                memory: "384Mi"
                cpu: "150m"
              limits:
                memory: "768Mi"
                cpu: "500m"
  ```

* **Decisões Importantes:**
  * **`replicas: 2`:** Garante redundância e atende ao requisito de balanceamento de carga entre Pods.
  * **`envFrom`:** Injeta automaticamente todas as chaves do `ConfigMap` e do `Secret` como variáveis de ambiente no container Spring Boot, casando perfeitamente com o `application.properties`.
  * **`imagePullPolicy: Never`:** Garante o uso da imagem compilada localmente no Minikube.
  * **Graceful Shutdown (`terminationGracePeriodSeconds: 35` + `preStop` hook):**
    * O gancho `preStop` executa `sleep 5` antes de enviar o sinal de encerramento ao Java, garantindo que o `kube-proxy` do Kubernetes tenha tempo de remover o Pod das rotas do Service e parar de enviar novas conexões.
    * O Spring Boot com `server.shutdown=graceful` aguarda todas as requisições ativas (em andamento) terminarem de processar antes de desligar, impedindo que usuários recebam erros ou transações sejam interrompidas no meio.

---

### 3. O Service Interno da API (`k8s/api/service.yaml`)

* **Arquivo criado:** `k8s/api/service.yaml`
* **Conteúdo:**

  ```yaml
  apiVersion: v1
  kind: Service
  metadata:
    name: marmitech-api
    labels:
      app: marmitech-api
  spec:
    type: ClusterIP
    selector:
      app: marmitech-api
    ports:
      - name: http
        port: 8080
        targetPort: 8080
  ```

* **Conceito Chave:**
  * O nome do Service é **`marmitech-api`**.
  * É exatamente este nome que o nosso Nginx do frontend chama (`http://marmitech-api:8080/`).
  * O Kubernetes resolve esse nome pelo CoreDNS e **distribui automaticamente as requisições entre os 2 Pods da API** (balanceamento de carga via `kube-proxy`).

---

### 4. Demonstração de Balanceamento de Carga

* O endpoint `/health` da API foi ajustado para responder:

  ```text
  OK - Pod: <HOSTNAME-DO-POD>
  ```

* Ao disparar requisições para `http://<IP-DO-MINIKUBE>:30080/backend/health`, as respostas alternam entre os dois Pods ativos da API, comprovando o balanceamento em tempo real.

---

## 📋 Checklist de Progresso do Projeto

### Frontend (`k8s/web`)

* [x] Ajustar variáveis de ambiente do Frontend (`/backend`)
* [x] Criar `nginx.conf` com proxy reverso e SPA fallback
* [x] Criar `.dockerignore`
* [x] Criar `Dockerfile` multi-stage do Frontend
* [x] Criar manifestos Kubernetes (`deployment.yaml` com 2 réplicas e `service.yaml`)
* [x] Validar execução e self-healing no Minikube

### Banco de Dados (`k8s/mysql`)

* [x] Criar Secret com senha de root (`k8s/mysql/secret.yaml`)
* [x] Criar PVC de 1Gi para persistência (`k8s/mysql/pvc.yaml`)
* [x] Criar Deployment do MySQL 8.0 (`k8s/mysql/deployment.yaml`)
* [x] Criar Service ClusterIP interno (`k8s/mysql/service.yaml`)
* [x] Validar execução e persistência no Minikube

* [x] Validar comunicação de ponta a ponta e balanceamento no Minikube

---

## Roteiro Prático para a Apresentação

Para apresentar o projeto ao professor com nota máxima, siga este roteiro de 5 passos rápidos:

### 1. Mostrar todos os componentes funcionando no cluster

```bash
kubectl get pods -o wide
kubectl get services
kubectl get deployments
```

* **O que explicar:**
  * O Frontend (`marmitech-web`) tem **2 réplicas** e expõe a porta `30080` (NodePort).
  * O Backend (`marmitech-api`) tem **2 réplicas** e usa `ClusterIP: 8080`.
  * O Banco (`mysql`) tem **1 réplica** com PVC de 1GB e usa `ClusterIP: 3306`.
  * Nenhum componente roda fora do Kubernetes.

### 2. Demonstrar o Balanceamento de Carga (Load Balancing)

Execute no terminal:

```bash
for i in $(seq 1 6); do curl -s http://192.168.49.2:30080/backend/health; echo ""; sleep 0.3; done
```

* **O que acontece:** O terminal exibirá alternadamente:

  ```text
  OK - Pod: marmitech-api-7b86c4b545-hj2qr
  OK - Pod: marmitech-api-7b86c4b545-hhtmd
  OK - Pod: marmitech-api-7b86c4b545-hj2qr
  OK - Pod: marmitech-api-7b86c4b545-hhtmd
  ```

* **O que explicar:** O Service `marmitech-api` distribui as requisições em round-robin entre os 2 Pods da API de forma transparente e nativa.

### 3. Demonstrar a Autorrecuperação (Self-Healing)

Em um terminal, rode:

```bash
kubectl get pods -w
```

No outro terminal, mate um Pod de qualquer componente (ex: da API):

```bash
kubectl delete pod <nome-do-pod-da-api>
```

* **O que acontece:** O Pod antigo entra em `Terminating` e um novo Pod nasce no mesmo segundo (`ContainerCreating` ➡️ `Running`).

### 4. Demonstrar a Persistência do Banco (PVC)

```bash
kubectl get pvc
```

* **O que explicar:** O MySQL usa um `PersistentVolumeClaim` com modo `ReadWriteOnce`. Mesmo que o Pod do MySQL seja destruído, o volume físico é preservado no host e o novo Pod remonta os dados intactos.

### 5. Demonstrar no Navegador

Abra:
`http://192.168.49.2:30080` (ou execute `minikube service marmitech-web`).

* A interface Angular carrega sem nenhum erro de CORS.
* O login e os dados transitam por: **Navegador ➡️ Nginx (Port 30080) ➡️ API (Port 8080) ➡️ MySQL (Port 3306)**.
