# Getting Started

### User Service Resource Server

This API service provides user functionalities from unprotected end point to protected endpoint:
Some of the service it provides are:

* User Login implemented with the template method behavioral design pattern.
* User Refresh token is also provided
* New User creation or sign with email opt token feature using the template method behavioral design pattern.
* Brand User Creation and Updating
* Influencers User Creation and Updating.
* OTP verification feature
* Re-send OTP feature
* Password change facility protected and  unprotected feature.
* More tfeatures to be added

### Guides

#### Technologies Used

* `Gradle 8.5` build tool
* `Java 17`
* `Spring Boot` version 3.1.6
* `Spring Dependency Management` version 1.1.4
* `Spring Cloud Version` 2022.0.4
* `Spring OAuth2 Resource Server` dependency
* `Spring OAuth2 security jose` dependency
* `Spring Boot Actuator` dependency
* `Spring JPA-R2DBC` dependency
* `Spring WebFlux` dependency
* `Spring Config Client` dependency
* `Spring Cloud Bootstrap` dependency
* `Spring Eureka` dependency
* `Spring Cloud Stream Binder Rabbit` dependency
* `Postgres driver` dependency
* `Postgres-R2DBC driver` dependency
* `Prometheus` dependency
* `Google Gson` dependency
* `Project Lombok` dependency

#### Clean and Build the Project 
From the project root terminal

`./gradlew clean build -x test`

#### Run the Project
From the project root terminal

`java -jar tima-user-service-1-SNAPSHOT.jar`

The following guides illustrate how to use some features concretely:

* [Accessing data with R2DBC](https://spring.io/guides/gs/accessing-data-r2dbc/)
* [Service Registration and Discovery with Eureka and Spring Cloud](https://spring.io/guides/gs/service-registration-and-discovery/)
* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)

### Additional Links

These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)
* [R2DBC Homepage](https://r2dbc.io)

