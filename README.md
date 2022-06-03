# Lab5-Group04

### Guides
The following guides illustrate how to set up the two modules concretely:
- [Set up Databases](#databases)
- [Set up Servers](#servers)
- [Run Unit tests](#unit-tests)
- [Run Integration tests](#integration-tests)



## Databases
**Two** containers are necessary, each one with 2 databases respectively. 

For creating these two containers, <br>
we need to execute the following command in the command line:

` docker run --name lab5container -p 54320:5432 -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=db_traveler -d postgres`

and then

`docker run --name container2 -p 54321:5432 -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=db_payment -d postgres`

These four databases have the following names respectively: <br>
- Container _**lab5container**_ has the following two databases:
  * _postgres_, on port 54320, that contains info regarding **_login service_**
  * _db_traveler_, on port 54320, that contains info regarding **_traveler service_**

- Container _**container2**_ has the following two databases:
  * _postgres_, on port 54321, that contains info regarding **_ticket catalogue service_**
  * _db_payment_, on port 54321, that contains info regarding **_payment service_**

Add two new Datasources in the IntelliJ project, <br>
for each one username and password (should be _postgres_ and _postgres_ respectively) specified in:
- [application-credentials.properties](login_service/src/main/resources/application-credentials.properties)
  - fields: _spring.datasource.username_ and _spring.datasource.password_
- [application.properties](ticket_catalogue_service/src/main/resources/application.properties)
  - fields: _spring.r2dbc.username_ and _spring.r2dbc.password_

and the host and port specified in
- _spring.datasource.url_ of the [application.properties](login_service/src/main/resources/application.properties) (should be _localhost_ and _54320_)
- _spring.r2dbc.url_ of the [application.properties](ticket_catalogue_service/src/main/resources/application.properties) (should be _localhost_ and _54321_)

**In addition**, to successfully run the app, **start** the created container.

## Servers  

* LoginService server (DB: _postgres_  on port 54320) on port 8081
* TravelerService server (DB: _db_traveler_ on port 54320) on port 8080

* TicketCatalogueService server (DB: _postgres_  on port 54321) on port 8082
* PaymentService server (DB: _db_payment_ on port 54321) on port 8083

[//]: # (TODO)
## Unit tests
To run unit tests of the login and traveler service, 
it is necessary to run the command for **creating** the required **container** explained above in this README.md and run it during the test

[//]: # (TODO)
## Integration tests
To run integrations tests, it is sufficient to run the command for **creating** the required **container** explained above in this README.md
      
