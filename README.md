
## Dev

> make sure you use >= jdk14

```shell script
# start db
docker-compose up

# build modules
./mvnw clean install

# start server
./mvnw spring-boot:run -pl server

# start client
./mvnw spring-boot:run -pl client
```

* Goto [Client-UI](http://localhost:8081/)

## TODO

* test schema evolution
* add tests
