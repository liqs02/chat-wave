# ChatWave
ChatWave is a platform that combines the power of text communication in a modern application.
This state-of-the-art software is meticulously crafted utilizing a sophisticated microservices architecture, paving the way for unparalleled performance and versatility.

Still in active development.

## Tech Stack

**Backend:** `Java` `Spring Boot` `PostgreSQL`

**Deploying** `Docker Compose` `Docker`

## Functional services
Chat wave is decomposed into three core microservices. All of them have own database and have a different business role.

### Auth Service

An auth service implements authorization for clients and users.
Clients are authenticated by `client_secret_post` and authorized by `client_credentials`.

Users has custom authorization based on sessions. User gets `accessToken` and `refreshToken` after authentication.
To use `accessToken` we have to send it in `User-Authorization` header with `Bearer ` prefix like below.  
```http
User-Authorization: Bearer accessToken
```

Service contains methods to operate with user and user's session. 
Auth service contains just user's id and password. 
Storing and searching for a user by name is the task of account service contacting auth-service.

| Method   | Path                                    | Description                                   | Authorization Type |
|:---------|:----------------------------------------|-----------------------------------------------|:-------------------|
| `GET`    | `/users/authentication`                 | Get current user's authentication information | `CLIENT`           |
| `POST`   | `/users`                                | Create a user                                 | `CLIENT`           |
| `POST`   | `/users/authenticate`                   | Authenticate an existing user                 | `CLIENT`           |
| `PATCH`  | `/users/{userId}/password`              | Change user's password.                       | `CLIENT`           |
| `GET`    | `/users/{userId}/sessions`              | Get all not expired user's sessions.          | `USER`             |
| `POST`   | `/users/sessions/refresh`               | Refresh accessToken for user.                 | `NONE`             |
| `DELETE` | `/users/{userId}/sessions`              | Expire all user's session.                    | `USER`             |
| `DELETE` | `/users/{userId}/sessions/{sessionsId}` | Expire selected session of user.              | `USER`             |

### Account Service

By account service we can operate with accounts. We can create, authenticate or get other account's information.

| Method | Path                             | Description                                | Authorization Type |
|:-------|:---------------------------------|--------------------------------------------|:-------------------|
| `GET`  | `/accounts/current`              | Get current account's details              | `USER`             |
| `GET`  | `/accounts/{accountId}`          | Get account's details                      | `USER` or `CLIENT` |
| `GET`  | `/accounts/{accountId}/showcase` | Get account's public information           | `NONE`             |
| `POST` | `/accounts`                      | Create an account and user in auth service | `USER`             |
| `POST` | `/accounts/authenticate`         | Authenticate a user                        | `NONE`             |

### Chat Service
Still in development...


## Infrastructure
The distributed systems patterns are created by Spring Boot.

#### Config service
Config services keeps config files in static directory.
If the service has the appropriate docker-compose configuration, simply add the following code (with the changed name) to the application.yml file.
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
Library provides filter for user's authorization. To use the filter we need to add the following code in defaultSecurityFilterChain:

```
.addFilterBefore(userAuthFilter, UsernamePasswordAuthenticationFilter.class);
```
and create a feign client:
```java
@FeignClient("auth-service")
public interface AuthService extends com.chatwave.authclient.client.AuthService {}
```
The library also has UserAuthentication class.
This object is a representation of the information that is stored after successful user authorization. 
The object stores, for example: user's id, session's id, accessToken.

#### exception library
The library provides common exception handler for all microservices.
To use this library, we need to add the following annotation to the main class:
```
@ComponentScan({"com.chatwave.currentService","com.chatwave.exception"})
```

## License
All Rights Reserved