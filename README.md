# ChatWave
ChatWave is a platform that combines the power of text communication in a modern application.
This state-of-the-art software is meticulously crafted utilizing a sophisticated microservices architecture, paving the way for unparalleled performance and versatility.

Still in active development.

## Tech Stack

**Backend:** `Java` `Spring Boot` `PostgreSQL`

**Deploying** `Docker Compose` `Docker`

## Functional services
Chat wave is decomposed into three core microservices. All of them have own database and have a different business role.

#### Auth Service

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

#### Account Service

By account service we can operate with accounts. We can create, authenticate or get other account's information.

| Method | Path                             | Description                                | Authorization Type |
|:-------|:---------------------------------|--------------------------------------------|:-------------------|
| `GET`  | `/accounts/current`              | Get current account's details              | `USER`             |
| `GET`  | `/accounts/{accountId}`          | Get account's details                      | `USER` or `CLIENT` |
| `GET`  | `/accounts/{accountId}/showcase` | Get account's public information           | `NONE`             |
| `POST` | `/accounts`                      | Create an account and user in auth service | `USER`             |
| `POST` | `/accounts/authenticate`         | Authenticate a user                        | `NONE`             |

#### Chat Service
Still in development...


## License
All Rights Reserved