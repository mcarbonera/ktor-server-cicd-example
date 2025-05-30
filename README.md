# monitoring-system

Esse projeto contém um sistema Ktor Server com autenticação.

## Rotas

- localhost:8080/auth/sign-up
- localhost:8080/auth/sign-in
- localhost:8080/auth/me
- localhost:8080/auth/sign-out

## Rodando com Docker Compose:

O arquivo docker-compose.yml define 3 containers: o banco postgres, o cliente de banco de dados adminer (para acesso rápido ao banco) e a aplicação ktor-server.

- No .env, as URLs devem ser postgres (utilizar localhost para rodar local, mas sem docker). Para rodar, utilizar o comando:

```
docker compose up
```

Para acessar o banco de dados pelo adminer, acessar localhost:8000 e utilizar os seguintes dados:

```
Servidor: postgres (o acesso é feito pelo docker, por isso não é localhost)
Usuário: postgres
Senha: postgres
Base de Dados: evennt (Criado pelo ktor-server na inicialização, a partir de um migration, acredito eu).
```

## Testando a aplicação

- Sign Up:

```
POST http://localhost:8080/auth/sign-up
body:
{
  "email": "marcelocarbonera@live.com",
  "password": "#Aa123456",
  "name": "Marcelo"
}
```

- Sign In:

```
POST http://localhost:8080/auth/sign-in
body:
{
  "email": "marcelocarbonera@live.com",
  "password": "#Aa123456"
}
```

- Get User:

```
GET http://localhost:8080/auth/me
Bearer token:
<<TOKEN_RECEBIDO_A_PARTIR_DA_ROTA_SIGN_IN>>
```

## Rodando com Kubernetes:

Perceba que o container da aplicação não deve conter variáveis de ambiente, para que não possa ser inspecionada. Isso é recomendado independente do registry de imagens ser público ou não.

No Docker Compose, há um parâmetro env_file para injetar as variáveis. No caso do Kubernetes, uma forma de fazer isso é utilizando Secrets. Contudo, para tal abordagem, foi necessário gerar o namespace do projeto antes de criar o secret, e por isso foram utilizados dois arquivos de manifesto kubernetes (namespace.yaml e monitoring-system.yaml).

A etapa de gerar o secret deve funcionar diferente dependendo de ser local ou produção, pois, localmente, temos o arquivo .env e em produção estas variáveis são injetadas pela esteira de CI/CD. Para funcionar de modo semelhante foi criado um shell script (deploy.sh, o nome está ruim, mas vou continuar assim no momento).

Para rodar lembrar que, se a imagem não está no Docker Hub ainda, o manifesto deve conter "imagePullPolicy: Never" para o Deployment ktor-server. Gerar a imagem local com:

```
docker build -t mcarbonera/monitoring-system-ktor-server .
```

Subir a aplicação via Kubernetes com os seguintes comandos:

- Criar o namespace:

```
kubectl apply -f k8s/namespace.yaml
```

- Criar o Secret:

```
./k8s/deploy.sh
```

- Voar voar, subir subir!!

```
kubectl apply -f k8s/monitoring-system.yaml
```

## Criação da esteira CI/CD

### CI

Para a parte de CI, foi necessário criar alguns secrets para utilização na esteira, mostradas a seguir:

#### Para publicar no dockerhub:

- DOCKERHUB_USERNAME
- DOCKERHUB_PASSWORD

#### Para a aplicação ktor-server:

- JWT_ACCESS_SECRET
- JWT_REFRESH_SECRET
- JWT_ISSUER
- JWT_AUDIENCE
- JWT_ACCESS_EXPIRATION
- JWT_REFRESH_EXPIRATION
- JWT_REALM
- DB_URL
- DB_USERNAME
- DB_PASSWORD
- DB_DRIVER

### CD

- Foi necessário criar um runner local a afim de permitir à task acessar o comando kubectl do cluster.

- Para criar, no github, acessar Configuração -> Actions -> Runners -> New Runner -> New self-hosted runner. Selecionar:

```
Runner image: Linux
Architecture: x64
```

- A página do github vai exibir instruções de como rodar o runner localmente. Acessar o windows WSL (cmd -> comando "wsl" no terminal) e rodar os comandos.

- No arquivo que define o pipeline (ci-cd.yml), o job que vai rodar localmente deve ter a configuração "runs-on: self-hosted"








<<<<< ISSO AQUI NAO ROLOU >>>>

- Para permitir que o job de deploy acesse o cluster Kubernetes, utilizei FluxCD (escolhido por ter abordagem parecida com a configuração de um agente Gitlab).

- Adicionar o fluxcd:

```
helm repo add fluxcd https://fluxcd-community.github.io/helm-charts
helm repo update

helm install flux fluxcd/flux2 \
  --namespace flux-system \
  --create-namespace
```

Gerar a chave criptográfica usada para deploy.

```
ssh-keygen -t ed25519 -C "flux" -f ./flux-github-key -N ""
```

- Salvar como um secret kubernetes (a chave privada é nossa, a pública é deles).

```
kubectl create secret generic flux-git-deploy-key \
  --namespace flux-system \
  --from-file=identity=./flux-github-key
```

- Para o github ter acesso à chave pública, acessar Settings -> Deploy keys -> Add deploy key

```
Nome: flux
Conteúdo: o que estiver dentro de "flux-github-key.pub"
Allow write access: TRUE!!!!
```

- Foi criado o arquivo flux-sync.yaml e sua configuração aplicada com:

```
kubectl apply -f k8s/flux-sync.yaml
```









<<<<< PARECE QUE ISSO AQUI NAO DEU CERTO NAO, MEU PARCEIRO >>>>

Para a parte de CD, foi necessário configurar o acesso do job de deploy ao cluster Kubernetes. Para isso, acessar o WSL (no linux fica mais fácil), rodar o comando:

```
base64 ~/.kube/config > kubeconfig.base64
cat kubeconfig.base64
```

Copiar o valor em base64 e criar um secret no github:

```
Chave: KUBECONFIG_FILE
Valor : <<conteudo do .kube/config em base64>>
```