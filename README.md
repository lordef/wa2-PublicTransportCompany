# Lab5-Group04

### Guides
The following guides illustrate how to set up the two modules concretely:
- [Set up Kafka](#kafka-containers)
- [Set up Databases](#databases)
- [Set up Servers](#servers)
- [Business Logic](#business-logic)
- [Run Unit tests](#unit-tests)
- [Run Integration tests](#integration-tests)


## Kafka containers
Run in the project command line:

`docker-compose up -d`

Once completed,<br>
**start** the two created containers (_kafka_kafka_1_ and _kafka_zookeeper_1_)

`docker start lab5-group04_kafka_1 lab5-group04_zookeeper_1`

## Databases
**Two** containers are necessary, each one with 2 databases respectively. 

### 1. Create containers
For creating these two containers, <br>
execute the following command in the command line:

`docker run --name lab5container -p 54320:5432 -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=db_traveler -d postgres`

and then

`docker run --name container2 -p 54321:5432 -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=db_payment -d postgres`

These four databases have the following names respectively: <br>
- Container _**lab5container**_ has the following two databases:
  * _postgres_, on port 54320, that contains info regarding **_login service_**
  * _db_traveler_, on port 54320, that contains info regarding **_traveler service_**

- Container _**container2**_ has the following two databases:
  * _postgres_, on port 54321, that contains info regarding **_ticket catalogue service_**
  * _db_payment_, on port 54321, that contains info regarding **_payment service_**

### 2. Start containers
**Start** the created containers with the following command:

`docker start lab5container container2`


### 3. Add Datasources
**In addition**, to successfully run the app,<br>
Add two new Datasources of type PostegreSQL in the IntelliJ project, <br>
for each one username and password (should be _postgres_ and _postgres_ respectively) specified in:
- [application-credentials.properties](login_service/src/main/resources/application-credentials.properties)
  - fields: _spring.datasource.username_ and _spring.datasource.password_
- [application.properties](ticket_catalogue_service/src/main/resources/application.properties)
  - fields: _spring.r2dbc.username_ and _spring.r2dbc.password_

and the host and port specified in
- _spring.datasource.url_ of the [application.properties](login_service/src/main/resources/application.properties) (should be _localhost_ and _54320_)
- _spring.r2dbc.url_ of the [application.properties](ticket_catalogue_service/src/main/resources/application.properties) (should be _localhost_ and _54321_)



## Servers  

* **_LoginService_** server (DB: _postgres_  on port 54320) on port 8081
* **_TravelerService_** server (DB: _db_traveler_ on port 54320) on port 8080

* **_TicketCatalogueService_** server (DB: _postgres_  on port 54321) on port 8082
* **_PaymentService_** server (DB: _db_payment_ on port 54321) on port 8083


## Business Logic
[//]: # (TODO)
I ticket sono stati pensati come segue :

- ogni ticket nella tabella tickets può essere di tipo "ordinal" o "seasonal"

- un admin può aggiungere solo ticket seasonal; i ticket ordinal sono inseriti nel DB all'avvio del catalogue
  service, e sono gestiti in modo hardcoded

- quando un utente compra un ticket, deve specificare i seguenti campi (e.g.) :
  {
  "quantity": 1,
  "ticketId": 2,
  "zoneId": "a",
  "notBefore": "24-07-2022", -> specifica la data da cui far partire la validità del ticket
  "creditCardNumber": "11111111111111",
  "expirationDate": "03-06-2023",
  "cvv":"333",
  "cardHolder": "ciaooo",
  "duration" : 30
  }

se il ticketId fa riferimento a un ordinal, non è necessario specificare la duration, in quanto in tal caso è hardcoded

- i tipi ordinal previsti per lo shop sono :
  1) "70 minutes" -> validFrom = istante in cui viene generato, exp = 70 minuti dopo
  2) "daily" -> dura 24, per un giorno qualsiasi specificato nella notBefore (validFrom = mezzanotte del giorno specificato nella notBefore, exp : mezzanotte del giorno dopo)
  3) "weekly" -> dura 7 giorni, dal lunedì al venerdi di una qualsiasi settimana. Per selezionare la settimana per cui si vuole acquistare,
     la notBefore DEVE essere il lunedì della specifica settimana selezionata. Non è possibile far partire un weekly da un giorno che non sia lunedì.

  4) "monthly" -> dura un mese, dal primo del mese sino alla fine. E' possibile selezionare lo specifico mese usando come notBefore nella POST
     una data che sia il primo giorno del mese selezionato. Non è possibile far partire un mensile da un giorno che non sia il primo giorno di un certo mese.




[//]: # (TODO)
## Unit tests
To run unit tests of the _login and traveler service_, 
it is necessary to **run** the command for **creating** the required **container** (_**lab5container**_) explained above in this README.md <br> 
and <br>
**run** it during the test

[//]: # (TODO)
## Integration tests
To run integrations tests, it is sufficient to run the command for **creating** the required **container** explained above in this README.md
      
