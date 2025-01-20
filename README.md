
# Orderbook
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

### Additional requirements 
- add order status
- fix cancel order
- implement market order
- LATER: executions
- LATER: trade history

Order status:
Order status is a state of an order.
The order status can be one of the following:
- OPEN when order quantity is the same as unfilled quantity
- PARTIALLY_FILLED when order quantity is partially filled
- FILLED when order quantity is completely filled and unfilled quantity is 0

Fix cancel order:
Cancel order should delete an order only if the order is OPEN. 
Otherwise, order quantity should be updated to amount filled, e.g. the order quantity - remaining quantity.

Market order:
Market order is an order to buy or sell a stock at the current market price. It is a request to buy or sell a stock immediately at the best available current price.
Market orders are the most common type of order because they are easy to place. 
However, they do not guarantee a specific price. 
The price at which a market order is executed is not guaranteed. 
It is the best available price at the time the order is executed.
If any part of market order is not filled, the remaining quantity should be cancelled.

Trade history:
TBD
