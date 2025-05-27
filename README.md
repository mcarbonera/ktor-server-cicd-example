# monitoring-system

Esse projeto contém um sistema com autenticação

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

Para rodar, deve-se fazer assim:

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

- Tá pronto o sorvetinho.

## Criação da esteira CI/CD

TO BE CONTINUED