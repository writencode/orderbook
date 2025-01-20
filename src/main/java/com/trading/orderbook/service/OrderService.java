package com.trading.orderbook.service;

import com.trading.orderbook.model.Order;
import com.trading.orderbook.model.OrderBook;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.trading.orderbook.model.OrderStatus;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
  private static final Logger logger = Logger.getLogger(OrderService.class.getName());

  private final List<Order> orders;
  private final OrderBookService orderBookService;

  public OrderService(OrderBookService orderBookService) {
    this.orders = new ArrayList<>();
    this.orderBookService = orderBookService;
  }

  public List<Order> getAllOrders() {
    return orders;
  }

  public Order getOrderById(String id) {
    return orders.stream()
        .filter(order -> order.getId().toString().equals(id))
        .findFirst()
        .orElse(null);
  }

  public Order createOrder(Order order) {
    OrderBook orderBook = orderBookService.getOrderBook(order.getSymbol());
    Order adjustedOrder = orderBook.addOrder(order);
    orders.add(adjustedOrder);
    logger.info(
        "Order created: " + adjustedOrder + " in order book: " + orderBook.orderbookDepth());
    return adjustedOrder;
  }

  public void cancelOrder(String id) {
    Order order = getOrderById(id);
    if (order != null) {
      if (order.getStatus() == OrderStatus.OPEN) {
        orderBookService.cancelOrder(order);
        orders.remove(order);
      } else {
        int filledQuantity = order.getQuantity() - order.getUnfilledQuantity();
        order.setQuantity(filledQuantity);
        order.setUnfilledQuantity(0); //fixing a bug
        orderBookService.cancelOrder(order);
      }
    } else {
      throw new RuntimeException("Could not find order with id: " + id);
    }
  }
}
