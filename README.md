# ChatWave
Chat API in microservice infrastructure, created while learning java and spring.

## Tech Stack

**Backend:** `Java` `Spring Boot` `PostgreSQL`

**Deploying** `Docker` `Docker Compose`

## Functional services
Chat wave is decomposed into three core microservices. All of them have own database and have a different business role. 

![diagram showing the structure of the services](.doc/functional-services.png)
Services only accept and produce json.
Each endpoint used by users are protected by csrf protection. The following profile disables this protection:
```yaml
spring.profiles.active: csrf_disable
```


### Auth Service

An auth service implements easy client and user authorization system for each microservice.

Clients are authenticated by `client_secret_post` and authorized by `client_credentials`.
New clients are defined in the configuration file like below.
```yaml
app:
  clients:
    - id: micro-service
      secret: secret
      url: http://micro-service:port
```

User authorization is session based. Client can create new session for user, then client receives `accessToken` and `refreshToken` which can send to user.
To use `accessToken` we have to send it in `User-Authorization` header with `Bearer ` prefix like below.  
```http
User-Authorization: Bearer accessToken
```

| Method   | Path                                    | Description                                    | Authorization Type |
|:---------|:----------------------------------------|------------------------------------------------|:-------------------|
| `GET`    | `/sessions`                             | Get all not expired sessions of current user   | `USER`             |
| `GET`    | `/session/authentication`               | Get user's authentication data                 | `CLIENT`           |
| `POST`   | `/users`                                | Create a user                                  | `CLIENT`           |
| `POST`   | `/users/authenticate`                   | Authenticate an user                           | `CLIENT`           |
| `POST`   | `/sessions`                             | Create a session for user                      | `CLIENT`           |
| `POST`   | `/sessions/refresh`                     | Refresh accessToken                            | `NONE`             |
| `PUT`    | `/users/{userId}/password`              | Change user's password                         | `CLIENT`           |
| `DELETE` | `/sessions`                             | Expire all user's session                      | `USER`             |
| `DELETE` | `/sessions/{sessionId}`                 | Expire specified session                       | `USER`             |

### Account Service

The accounts service stores non-sensitive user data.

| Method | Path                             | Description                                | Authorization Type |
|:-------|:---------------------------------|--------------------------------------------|:-------------------|
| `GET`  | `/accounts/{accountId}/exist`    | Check that user with given id exist        | `CLIENT`           |
| `GET`  | `/accounts/{accountId}/showcase` | Get account's public information           | `USER`             |
| `POST` | `/accounts`                      | Create an account and user in auth service | `NONE`             |
| `POST` | `/accounts/authenticate`         | Authenticate a user                        | `NONE`             |
| `PATCH`| `/accounts/{accountId}`          | Update user                                | `USER`             |

### Chat Service

Chat service allows to send and get messages from chat.

| Method | Path                 | Description                                               | Authorization Type |
|:-------|:---------------------|-----------------------------------------------------------|:-------------------|
| `GET`  | `/chat/{memberId}`   | Get few messages from chat before or after specified date | `USER`             |
| `POST` | `/chat/{receiverId}` | Send message to user                                      | `USER`             |

## Infrastructure
![diagram showing the structure of microservices' infrastructure](.doc/infrastructure.png)

### Config service
Config service keeps configuration for each service.
If the service has the appropriate docker-compose configuration, simply add the following code to the application configuration file.
```yaml
spring:
  application:
    name: micro-service
  config:
    import: configserver:http://user:${CONFIG_PASSWORD}@config:8888
```

### Gateway
Gateway introduces easy access from one place to each of the microservices.

### Registry
Registry is a simple eureka server application that provides easy communication between services and many useful tools for tracking and managing microservices.

## Common libraries

### Auth Client
Provides filter for user's authorization and UserAuthentication class which represents data of authorized user. 
To use the filter we need to add the following code in SecurityFilterChain:

```java
http.addFilterAt(userAuthFilter, UsernamePasswordAuthenticationFilter.class);
```
Create a feign client:
```java
@FeignClient("auth-service")
public interface AuthService extends com.chatwave.authclient.client.AuthClient {}
```

And set up oauth2 client configuration in config files.
An example of full configuration:

```yaml
spring:
  security:
    oauth2:
      client:
        provider:
          microservices:
            issuer-uri: http://auth-service:8081
            jwk-set-uri: http://auth-service:8081/oauth2/jwks
        registration:
          microservices:
            client-id: micro-service
            client-secret: secret
            authorization-grant-type: client_credentials
            client-authentication-method: client_secret_post
            redirect-uri:  "{baseUrl}/login/oauth2/code/spring"
            scope: server, openid
  cloud:
    openfeign:
      oauth2:
        clientRegistrationId: microservices
        enabled: true
```

### Exception Library
The library provides ExceptionHandler for all microservices.
To use this library, we need to add the following annotation to the main class:
```java
@ComponentScan({"com.chatwave.microservice","com.chatwave.exception"})
```

## Author
- [Patryk Likus](https://www.linkedin.com/in/patryklikus/)

## License
All Rights Reserved
