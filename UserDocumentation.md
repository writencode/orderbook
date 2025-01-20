# Order Book Management System

## Overview

The Order Book Management System is a Spring Boot application designed to manage and match bid and offer orders for a specific trading symbol. It provides RESTful APIs to interact with the order book and perform operations such as adding, retrieving, and canceling orders.

## Project Structure

- **Java Version**: 23
- **Framework**: Spring Boot
- **Build Tool**: Maven

### Key Components

- **Controllers**: Handle HTTP requests and responses.
    - `OrderBookController`: Manages order books.
    - `OrderController`: Manages orders in the order book.
- **Models**: Represent the data structures.
    - `OrderBook`: Represents an order book for a specific trading symbol.
    - `OrderBookDepth`: Represents the depth of the order book.
    - `Order`, `BidOrder`, `OfferOrder`: Represent different types of orders.
- **Services**: Contain business logic.
    - `OrderBookService`: Manages order books.
    - `OrderService`: Manages orders.

## Prerequisites

- Java 23
- Maven
- IDE (e.g., IntelliJ IDEA)

## Setup

1. **Clone the repository**:
   ```sh
   git clone <repository-url>
   cd orderbook
   ```

2. **Build the project**:
   ```sh
   mvn clean install
   ```

3. **Run the application**:
   ```sh
   mvn spring-boot:run
   ```

## API Endpoints

### Order Book Management

- **Get Order Book**: Retrieves the order book for a given symbol.
    - **URL**: `/api/order-books/{symbol}`
    - **Method**: `GET`
    - **Response**: List of `OrderBookDepth`

### Order Management

- **Get All Orders**: Retrieves all orders.
    - **URL**: `/api/orders`
    - **Method**: `GET`
    - **Response**: List of `Order`

- **Get Order by ID**: Retrieves an order by its ID.
    - **URL**: `/api/orders/{id}`
    - **Method**: `GET`
    - **Response**: `Order` or 404 Not Found

- **Create Order**: Creates a new order.
    - **URL**: `/api/orders`
    - **Method**: `POST`
    - **Parameters**:
        - `symbol` (String): The symbol of the order.
        - `type` (String): The type of the order (`BUY` or `SELL`).
        - `price` (BigDecimal): The price of the order.
        - `quantity` (Integer): The quantity of the order.
    - **Response**: Created `Order`

- **Cancel Order**: Deletes an order by its ID.
    - **URL**: `/api/orders/{id}`
    - **Method**: `DELETE`
    - **Response**: 204 No Content

## Example Usage

### Create a New Order

```sh
curl -X POST "http://localhost:8080/api/orders" \
     -d "symbol=APPL" \
     -d "type=BUY" \
     -d "price=150.00" \
     -d "quantity=10"
```

### Get All Orders

```sh
curl -X GET "http://localhost:8080/api/orders"
```

### Get Order Book for a Symbol

```sh
curl -X GET "http://localhost:8080/api/order-books/APPL"
```

### Cancel an Order

```sh
curl -X DELETE "http://localhost:8080/api/orders/{id}"
```

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any improvements or bug fixes.