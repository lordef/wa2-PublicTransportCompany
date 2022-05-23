# Lab5-Group04 - TODO: info related to the previous lab

### Guides
The following guides illustrate how to set up the two modules concretely:
- [Set up Databases](#databases)
- [Set up Servers](#servers)
- [Run Unit tests](#unit-tests)
- [Run Integration tests](#integration-tests)



## Databases

For the moment, i have created 2 containers, each one respectively with 2 dbs. (I haven't found a way to create 4 db 
in the same container)

To create a container with two databases, execute the following commands in the command line: <br>
` docker run --name lab5container -p 54320:5432 -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=db_traveler -d postgres`

and then
`docker run --name container2 -p 54321:5432 -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=db_payment -d postgres`


These four databases have the following names respectively:
* _postgres_  on port 54320, contains info regarding login service
* _db_traveler_ on port 54320 contains info regarding traveler service

* _postgres_  on port 54321, contains info regarding catalogue service
* _db_payment_ on port 54321 contains info regarding payment service

Add a new Datasource in the IntelliJ project 
with username and password (should be _postgres_ and _postgres_ respectively) specified in the
[application-credentials.properties](login_service/src/main/resources/application-credentials.properties)
#Questo da rivedere poi :
and the host and port (should be _localhost_ and _54320_) specified in the spring.datasource.url of the [application.properties](login_service/src/main/resources/application.properties)

In addition, to successfully run the app, start the created container.

## Servers  

* LoginService server (DB: _postgres_  on port 54320) on port 8081
* TravelerService server (DB: _db_traveler_ on port 54320) on port 8080

* TicketCatalogueService server (DB: _postgres_  on port 54321) on port 8082
* PaymentService server (DB: _db_payment_ on port 54321) on port 8083


#Questo da rivedere poi :

## Unit tests
To run unit tests of the login and traveler service, 
it is necessary to run the command for **creating** the required **container** explained above in this README.md and run it during the test

## Integration tests
To run integrations tests, it is sufficient to run the command for **creating** the required **container** explained above in this README.md
      
