package com.trading.orderbook.controller;

import com.trading.orderbook.model.BidOrder;
import com.trading.orderbook.model.OfferOrder;
import com.trading.orderbook.model.Order;
import com.trading.orderbook.service.OrderService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** REST controller for managing orders in the order book. */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

  @Autowired private OrderService orderService;

  /**
   * Retrieves all orders.
   *
   * @return a list of all orders
   */
  @GetMapping
  public List<Order> getAllOrders() {
    return orderService.getAllOrders();
  }

  /**
   * Retrieves an order by its ID.
   *
   * @param id the ID of the order to retrieve
   * @return the order with the specified ID, or a 404 Not Found response if the order does not
   *     exist
   */
  @GetMapping("/{id}")
  public ResponseEntity<Order> getOrderById(@PathVariable String id) {
    Order order = orderService.getOrderById(id);
    if (order != null) {
      return ResponseEntity.ok(order);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Creates a new order.
   *
   * @param symbol the symbol of the order
   * @param type the type of the order (BUY or SELL)
   * @param price the price of the order
   * @param quantity the quantity of the order
   * @return the created order
   * @throws IllegalArgumentException if the order type is invalid
   */
  @PostMapping
  public Order createOrder(
      @RequestParam String symbol,
      @RequestParam String type,
      @RequestParam BigDecimal price,
      @RequestParam Integer quantity) {
    if (type.equals("SELL")) {
      return orderService.createOrder(new OfferOrder(symbol, price, quantity));
    } else if (type.equals("BUY")) {
      return orderService.createOrder(new BidOrder(symbol, price, quantity));
    } else {
      throw new IllegalArgumentException("Invalid order type: " + type);
    }
  }

  /**
   * Deletes an order by its ID.
   *
   * @param id the ID of the order to delete
   * @return a 204 No Content response
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> cancelOrder(@PathVariable String id) {
    orderService.cancelOrder(id);
    return ResponseEntity.noContent().build();
  }
}
