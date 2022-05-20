# Lab4-Group04

### Guides
The following guides illustrate how to set up the two modules concretely:
- [Set up Databases](#databases)
- [Set up Servers](#servers)


## Databases
To create a container with two databases, execute the following command in the command line: <br>
`docker run --name p_db1 -p 54320:5432 -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=db2  -d postgres`

These two databases have the following names respectively:
* _postgres_
* _db2_

Add a new Datasource in the IntelliJ project 
with username and password (should be _postgres_ and _postgres_ respectively) specified in the
[application-credentials.properties](login_service/src/main/resources/application-credentials.properties)
and the host and port (should be _localhost_ and _54320_) specified in the spring.datasource.url of the [application.properties](login_service/src/main/resources/application.properties)

In addition, to successfully run the app, execute the following steps:
1. build and run the [LoginServiceApplication.kt](login_service/src/main/kotlin/it/polito/wa2/login_service/LoginServiceApplication.kt) with the parameter _spring.jpa.hibernate.ddl-auto_ setted to _**create**_ in the file [application.properties](login_service/src/main/resources/application.properties)
2. manually insert the subsequent occurrences in the _roles_ table:

| id  | name     |
|-----|----------|
| 1   | CUSTOMER |
| 2   | ADMIN    |
3. re-build and run the [LoginServiceApplication.kt](login_service/src/main/kotlin/it/polito/wa2/login_service/LoginServiceApplication.kt) with the parameter _spring.jpa.hibernate.ddl-auto_ setted to _**validate**_ in the file [application.properties](login_service/src/main/resources/application.properties)


## Servers  

* LoginService server (DB: _postgres_) on port 8081
* TravelerService server (DB: _db2_) on port 8080

## Unit tests
To run unit tests of the login and traveler service, 
it is necessary to run the command for **creating** the required **container** explained above in this README.md
and run it during the test

## Integration tests
To run integrations tests, it is sufficient to run the command for **creating** the required **container** explained above in this README.md
      
