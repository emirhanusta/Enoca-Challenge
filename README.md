## Introduction

This project was developed for Enoca's Java Challenge.


## Technologies

---
- Java 17
- Spring Boot 3.1.6
- Open API Documentation
- Spring Data JPA
- H2 In Memory Database
- Maven
- Junit5
- Docker
- Docker Compose

## Prerequisites

---
- Maven or Docker
---

### Docker Run

Please follow the below directions in order to build and run the application with Docker Compose;

```sh

$ git clone https://github.com/emirhanusta/Enoca-Challenge.git
$ cd Enoca-Challenge
$ docker-compose up -d
```
### Maven Run
To build and run the application with `Maven`, please follow the directions below;

```sh
$ git clone https://github.com/emirhanusta/Enoca-Challenge.git
$ cd Enoca-Challenge
$ mvn clean install
$ mvn spring-boot:run
```
You can reach the swagger-ui via  `http://{HOST}:8080/swagger-ui/index.html`

---

## Screenshot
![image](https://github.com/user-attachments/assets/842ac110-dfca-4ee7-895f-5be135273683)
