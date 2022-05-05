# Lab4-Group04

### Reference Documentation

For further reference, please consider the following sections:

* [TODO](https://www.google.it)


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

## Servers  
* LoginService server (DB: _postgres_) on port 8081
* TravelerService server (DB: _db2_) on port 8080