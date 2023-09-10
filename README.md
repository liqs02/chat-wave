# ChatWave
Chat API in microservice infrastructure.
Still in active development.

## Tech Stack

**Backend:** `Java` `Spring Boot` `PostgreSQL`

**Deploying** `Docker` `Docker Compose`

## Functional services
Chat wave is decomposed into three core microservices. All of them have own database and have a different business role.

Each endpoint used by users are protected by csrf protection provided by spring boot. You can disable this protection by setting up a special spring profile on the service (however it is not recommended):
```yaml
spring.profiles.active: csrf_disable
```


### Auth Service

An auth service implements easy common client and user authorization system for microservices.

Clients are authenticated by `client_secret_post` and authorized by `client_credentials` provided by spring boot.
If you wish to add a new client, add the following lines to the configuration file
```yaml
app:
  clients:
    - id: micro-service
      secret: secret
      url: http://micro-service:8080
```

Users has custom authorization based on sessions. Client can create new session for user, then client receives `accessToken` and `refreshToken` which can send to user.
To use `accessToken` we have to send it in `User-Authorization` header with `Bearer ` prefix like below.  
```http
User-Authorization: Bearer accessToken
```

Service contains methods to operate with user and user's session. 
User is authenticated by login and password. 

| Method   | Path                                    | Description                                    | Authorization Type |
|:---------|:----------------------------------------|------------------------------------------------|:-------------------|
| `GET`    | `/sessions`                             | Get all not expired sessions of current user.  | `USER`             |
| `GET`    | `/session/authentication`               | Get user's authentication data                 | `CLIENT`           |
| `POST`   | `/users`                                | Create a user                                  | `CLIENT`           |
| `POST`   | `/users/authenticate`                   | Authenticate an user                           | `CLIENT`           |
| `POST`   | `/sessions`                             | Create a session for user.                     | `CLIENT`           |
| `POST`   | `/sessions/refresh`                     | Refresh accessToken.                           | `NONE`             |
| `PUT`    | `/users/{userId}/password`              | Change user's password.                        | `CLIENT`           |
| `DELETE` | `/sessions`                             | Expire all user's session.                     | `USER`             |
| `DELETE` | `/sessions/{sessionId}`                 | Expire specified session.                      | `USER`             |

### Account Service

The accounts service stores non-sensitive user data. It communicates with the auth-service for user authentication.

| Method | Path                             | Description                                | Authorization Type |
|:-------|:---------------------------------|--------------------------------------------|:-------------------|
| `GET`  | `/accounts/{accountId}/exist`    | Check that user with given id exist.       | `CLIENT`           |
| `GET`  | `/accounts/{accountId}/showcase` | Get account's public information           | `USER`             |
| `POST` | `/accounts`                      | Create an account and user in auth service | `NONE`             |
| `POST` | `/accounts/authenticate`         | Authenticate a user                        | `NONE`             |
| `PATCH`| `/accounts/{accountId}`          | Update user                                | `USER`             |

### Chat Service

Chat service allows to send and gets messages from chats.

| Method | Path                 | Description                                               | Authorization Type |
|:-------|:---------------------|-----------------------------------------------------------|:-------------------|
| `GET`  | `/chat/{memberId}`   | Get few messages from chat before or after specified date | `USER`             |
| `POST` | `/chat/{receiverId}` | Send message to user                                      | `USER`             |

## Infrastructure
The distributed systems patterns are provided by spring boot.

#### Config service
Config services keeps config files in static directory.
If the service has the appropriate docker-compose configuration, simply add the following code (with the changed service's name) to the application yaml file.
```yaml
spring:
  application:
    name: micro-service
  config:
    import: configserver:http://user:${CONFIG_PASSWORD}@config:8888
```

#### Gateway
Gateway introduces easy access for customer to microservices from single place.

#### Registry
Registry is a simple eureka server application that provides easy communication between services and many useful tools for tracking and managing microservices.

## Common libraries

#### Auth Client
Library provides filter for user's authorization and UserAuthentication class which represents data of authorized user. 

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
      resourceserver:
        jwt:
          issuer-uri: http://auth-service:8081
          jwk-set-uri: http://auth-service:8081/oauth2/jwks
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

#### Exception Library
The library provides common exception handler for all microservices.
To use this library, we need to add the following annotation to the main class:
```java
@ComponentScan({"com.chatwave.microservice","com.chatwave.exception"})
```


## Author
- [Patryk Likus](https://www.linkedin.com/in/patryk-l-80186326b/)


## License
All Rights Reserved