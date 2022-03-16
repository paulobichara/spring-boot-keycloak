# Spring Boot Keycloak Demo

This project is a simple demo for the integration between Spring Boot and Keycloak as the authentication and identity
provider.

## Local Infrastructure

This project includes a `docker-compose` directory including the definition file `docker-compose.yaml` with the required
local infra to run the application. The containers included in the docker-compose cluster are:

- PostgreSQL - database for the Keycloak server
- PgAdmin4 - for managing and querying the database (available at http://localhost/)
- Keycloak - authentication server and identity provider (available at http://localhost:8080/)

To start the local infrastructure, just start the docker-compose cluster by running:

```shell script
docker-compose -f docker-compose/docker-compose.yaml up -d
```

## Build and Test

The project consists of a simple gradle project. In order to build and test it, just clone the project, set the
directory to project root and execute:

```shell script
./gradlew clean build
``` 

## Running

After the infrastructure is running and the application is built, we can run it by executing, from the project root
directory:

```shell script
java -jar build/libs/spring-boot-api-0.0.1-SNAPSHOT.jar
```

## Keycloak Integration Demo

The project API is composed by two end-points: one for creating users and another for getting the information of the
currently authenticated user.

### Creating a User

Here is a cURL command for creating a user:

```shell
curl --location --request POST 'http://localhost:8081/users' --header 'Content-Type: application/json' --data-raw '{"firstName":"User","lastName":"Surname", "email":"user@domain", "enabled":"true", "username":"test-user", "password": "password"}'
```

### Getting User Info

After creating the user, now you can access http://localhost:8081/users/me . This URL will hit a protected end-point, so
you will be redirected to the Keycloak login page. After successfully authenticating using the credentials from the user
created above, you will be able to see the user information.