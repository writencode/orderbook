# orderbook
This is a simple trading order book implementation in Java & Spring Boot.

## To build
mvn clean install

## To run
mvn spring-boot:run

Use postman collection or curl to test the API.
For example:
```
curl -X POST http://localhost:8080/api/orders  -H "Content-Type: application/x-www-form-urlencoded" -d "symbol=AAPL&quantity=100&price=100.0&type=BUY"
```


## Apply formatting before check-in (or add check-in trigger)
mvn spotless:apply

