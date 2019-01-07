# Management API

Implementation of MS3 test task.
Requirements for implementation can be found in the `ms3test\management-rest-api\Senior Coding Challenge API.docx` file.
Consists RESTful api implemented with Spring boot and Spring MVC. JDK 11. Gradle 5.0

## Instructions for running the application

1. Run command prompt or (under windows) or open shell (under linux).
2. Change current dirrectory to the "ms3test/management-rest-api".

To run Junit tests - execute command 
```
gradlew.bat test
```
To run application with spring boot plugin - execute command
```
gradlew.bat bootRun
```

To build application - execute command
```
gradlew.bat build
```
it will generate file
 ```
 ms3test\management-rest-api\build\libs\management-rest-api-0.0.1-SNAPSHOT.jar
 ```
 to run built application - change current directory to directory with 
 `management-rest-api-0.0.1-SNAPSHOT.jar` file and execute command
 ```
 java -jar management-rest-api-0.0.1-SNAPSHOT.jar
 ```

The root of the api should be available at
```
http://localhost:8080
```

Get request to this URL should return something like
```
content-type: application/json
transfer-encoding: chunked
date: Sun, 06 Jan 2019 05:11:09 GMT
{"href": "http://localhost:8080/identifications"}
```
and you should be able to browse this api by jumping between the links api 
provides you with in the responses.

### Prerequisites

You have to have JRE (Java Runtime environment) installed(Tested with Open JDK 11). 
Instructions on the JRE installation can be found at 
[Java installation](https://java.com/en/download/help/download_options.xml)
and you have to have internet connection to let gradle to download required libraries. 

### Database information

Application was implemented with embedded HSQLDB, so you do not have to set up 
local database it will be run automatically with application start.

Below are SQL DDL for database creation generated automatically by JPA.

```
create sequence hibernate_sequence start with 1 increment by 1
create table address (id bigint not null, city varchar(255), number integer, state varchar(255), street varchar(255), type varchar(255), unit varchar(255), zip_code varchar(255), identification_id bigint, primary key (id))
create table communication (id bigint not null, preferred boolean, type varchar(255), value varchar(255), identification_id bigint, primary key (id))
create table identification (id bigint not null, dob date, first_name varchar(255), gender integer, last_name varchar(255), title varchar(255), primary key (id))
alter table address add constraint FK4t17c42xmh7ekk1hyby5xe3j3 foreign key (identification_id) references identification
alter table communication add constraint FK816uukfmisewws7hlofgdhpfj foreign key (identification_id) references identification
```

Diagram for table association picture can be found on this path
```
ms3test\management-rest-api\ER-Diagram.png
```

## Built With

* [gradle](https://docs.gradle.org/current/userguide/userguide.html) - Gradle User Manual
* [Java installation](https://java.com/en/download/help/download_options.xml)

## Helpful resources used during the implementation

[Http status codes](https://www.restapitutorial.com/httpstatuscodes.html) - description of return statuses for HTTP methods

[HTTP Methods](https://restfulapi.net/http-methods/) - description of return statuses for HTTP methods

[Building a Hypermedia-Driven RESTful Web Service](https://spring.io/guides/gs/rest-hateoas/) - manual on building and configuration of Hypermedia-Driven RESTful services with a Spring stack

[Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/) - basic manual on building and configuration of RESTful services with a Spring stack

[Customizing HTTP Endpoints in Spring Data REST](https://www.baeldung.com/spring-data-rest-customize-http-endpoints)

## Author

* **Pavel Sapronov**