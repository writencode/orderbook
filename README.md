# orderbook
This is a simple trading order book implementation in Java & Spring Boot.

# Disclaimer
<div style="background-color: #FA8072; color: black; padding: 10px; border-radius: 5px;">
This software is provided for educational purposes only. It is not intended for use in production environments or for critical applications. You use this software at your own risk. The authors and contributors make no guarantees or warranties, expressed or implied, about the functionality, reliability, or safety of this software.
In no event shall the authors or contributors be liable for any damages (including, but not limited to, direct, indirect, incidental, special, exemplary, or consequential damages, including damages for loss of data or profits) arising out of or in connection with the use of this software.
</div>

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

