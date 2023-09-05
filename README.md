# ChatWave
ChatWave is a platform that combines the power of text communication in a modern application.
This state-of-the-art software is meticulously crafted utilizing a sophisticated microservices architecture, paving the way for unparalleled performance and versatility.

Still in active development.

## Tech Stack

**Backend:** `Java` `Spring Boot` `PostgreSQL`

**Deploying** `Docker` `Docker Compose`

## Functional services
Chat wave is decomposed into three core microservices. All of them have own database and have a different business role.

Each endpoint used by users are protected by csrf protection provided by spring boot. You can disable this protection by setting up a special spring profile on the service (however it is not recommended):
```yaml
spring.profiles.active: CSRF_DISABLE
```


### Auth Service

An auth service implements authorization for clients and users.
Auth service is used by other services to implement easy common authorization system.


Clients are authenticated by `client_secret_post` and authorized by `client_credentials` provided by spring boot.

Users has custom authorization based on sessions. User gets `accessToken` and `refreshToken` after authentication.
To use `accessToken` we have to send it in `User-Authorization` header with `Bearer ` prefix like below.  
```http
User-Authorization: Bearer accessToken
```


Service contains methods to operate with user and user's session. 
Auth service contains just user's id and password. 
Storing login names is the task of account service.

| Method   | Path                                    | Description                                    | Authorization Type |
|:---------|:----------------------------------------|------------------------------------------------|:-------------------|
| `GET`    | `/users/authentication`                 | Get current user's authentication data         | `CLIENT`           |
| `POST`   | `/users`                                | Create a user                                  | `CLIENT`           |
| `POST`   | `/users/authenticate`                   | Authenticate an existing user                  | `CLIENT`           |
| `PATCH`  | `/users/{userId}`                       | Change user's data.                            | `CLIENT`           |
| `GET`    | `/sessions`                             | Get all not expired sessions of current user.  | `USER`             |
| `POST`   | `/sessions/refresh`                     | Refresh accessToken.                           | `NONE`             |
| `DELETE` | `/sessions`                             | Expire all current user's session.             | `USER`             |
| `DELETE` | `/sessions/{sessionsId}`                | Expire specified session.                      | `USER`             |

### Account Service

By account service we can operate with accounts. Account service for storing sensitive data (such as passwords) contacts the auth service where it is stored.

| Method | Path                             | Description                                | Authorization Type |
|:-------|:---------------------------------|--------------------------------------------|:-------------------|
| `GET`  | `/accounts/current`              | Get current account's details              | `USER`             |
| `GET`  | `/accounts/{accountId}/exist`    | Check that user with given id exist.       | `CLIENT`           |
| `GET`  | `/accounts/{accountId}/showcase` | Get account's public information           | `USER`             |
| `POST` | `/accounts`                      | Create an account and user in auth service | `NONE`             |
| `POST` | `/accounts/authenticate`         | Authenticate a user                        | `NONE`             |

### Chat Service
Chat service allows to send and gets messages from chats.

| Method | Path                 | Description                                               | Authorization Type |
|:-------|:---------------------|-----------------------------------------------------------|:-------------------|
| `GET`  | `/chat/{memberId}`   | Get few messages from chat before or after specified date | `USER`             |
| `POST` | `/chat/{receiverId}` | Send message to user                                      | `USER`             |

Api is still in development!

## Infrastructure
The distributed systems patterns are provided by spring boot.

#### Config service
Config services keeps config files in static directory.
If the service has the appropriate docker-compose configuration, simply add the following code (with the changed service's name) to the application.yml file.
```yaml
spring:
  application:
    name: name-service
  config:
    import: configserver:http://user:${CONFIG_PASSWORD}@config:8888
```

#### Gateway
Gateway introduces easy access for customer to microservices from a one ip and port.

#### Registry
Registry is a simple eureka server application that provides easy communication between services and many useful tools for tracking and managing microservices.

## Common libraries

#### auth client
Library provides filter for user's authorization and UserAuthentication class which represents data of authorized user. 

To use the filter we need to add the following code in defaultSecurityFilterChain:

```
.addFilterAt(userAuthFilter, UsernamePasswordAuthenticationFilter.class);
```
Create a feign client:
```java
@FeignClient("auth-service")
public interface AuthService extends com.chatwave.authclient.client.AuthClient {}
```

And set up oauth2 client configuration in config files.
An example:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://auth-service:8081
      client:
        provider:
          microservices:
            issuer-uri: http://auth-service:8081
        registration:
          microservices:
            client-id: service-name
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

#### exception library
The library provides common exception handler for all microservices.
To use this library, we need to add the following annotation to the main class:
```
@ComponentScan({"com.chatwave.currentService","com.chatwave.exception"})
```
CSRF
## License
All Rights Reserved