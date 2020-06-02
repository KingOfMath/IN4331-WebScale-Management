Full document about these transaction processes :
https://docs.yugabyte.com/v1.1/architecture/transactions/transactional-io-path/

A quick demo : https://github.com/YugaByte/orm-examples/tree/master/java/spring

Sample: 
https://github.com/yugabyte/microservices-demo
yugastore-java

https://github.com/yugabyte/orm-examples/tree/master/java/spring

Details of the talk with the professor (20200514):

Kubernete: Test locally and else will be the same. Client automatically generate workload. Depends on the number of the payment and orders. Set up a kubernete clusters and give resources.

Front end: We need a gateway service, e.g. REST api.

Managed kubernete service.

Evaluation: The client is used for evaluation. Request per second for indication of performance. Find the bottle neck (no. of database and instances) -> Finding a balance for reosurce control.

Transactions detail: Communicate between different DBs between micro-services -> set up one ydb with multiple instance -> all of the micro-service use the same database. Micro-service likes to be there own. Transaction cannot simultaneously change the transaction result within different microservices. Best solution: find some way that the tracsaction will happen across micro-service as one transaction (Yugabyte DB has in-store transaction protocols).

Orchestrator service: a communication service connecting microservices in terms of transaction consistency(?).

General goal: What can we do to enforce transaction consistency across different microservices in one yugabyte db.

Final state of the database will be compared against a database where all requests/transactions happened sequentially (Lecture)

Local deployment with proper user service via spring boot have succeeded.

# Local test progress:

Deploy the yugabyteDB
```
./bin/yb-ctl --rf 3 create
```

Deploy the spring boot service

```
mvn clean
mvn -DskipTests package
mvn spring-boot:run
```

Using the commands sheet for adding users

Check the results with localhost:8080/users


## Model User
### List user
```
curl http://localhost:8080/users
```
### Create user
```
curl --data '{"credit" : 333}' \
       -v -X POST -H 'Content-Type:application/json' http://localhost:8080/users/create
```
### Find user
```
curl http://localhost:8080/users/find/{userid}
```
### Remove user
```
curl -X DELETE http://localhost:8080/users/remove/{userid}
```
### Add user credit
```
curl -X POST http://localhost:8080/users/credit/add/{userid}/{amount}
```
### Subtract user credit
```
curl -X POST http://localhost:8080/users/credit/subtract/{userid}/{amount}
```
