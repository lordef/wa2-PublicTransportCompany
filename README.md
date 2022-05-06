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



## Servers  
* LoginService server (DB: _postgres_) on port 8081
* TravelerService server (DB: _db2_) on port 8080



### Reference Documentation

For further reference, please consider the following sections:

* Examples of secured REST API
  * [REST service persisting data in MongoDB](https://medium.com/techwasti/enable-spring-security-using-kotlin-6b9abb36d218)
  * [The kotlin security DSL](https://www.baeldung.com/kotlin/spring-security-dsl)
  * [Turning Kotlin applications into secure native executables](  https://tanzu.vmware.com/developer/tv/tanzu-tuesdays/0046/)
* Implementing a SpringBoot JWT server
  * [Spring Boot Token based Authentication with Spring Security & JWT](https://bezkoder.com/spring-boot-jwt-authentication)
    * A detailed tutorial (in Java)
  * [Kotlin API Authentication using JWT]( https://morioh.com/p/c630eaa08d00)
    * A slightly different one, relying on http-only cookies
      
