package com.trading.orderbook.service;

import com.trading.orderbook.model.Order;
import com.trading.orderbook.model.OrderBook;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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

    public Order getOrderById(Long id) {
        return orders.stream().filter(order -> order.getId().equals(id)).findFirst().orElse(null);
    }

    public Order createOrder(Order order) {
        OrderBook orderBook = orderBookService.getOrderBook(order.getSymbol());
        Order adjustedOrder = orderBook.addOrder(order);
        orders.add(adjustedOrder);
        logger.info("Order created: " + adjustedOrder + " in order book: " + orderBook.orderbookDepth());
        return adjustedOrder;
    }

    public void deleteOrder(Long id) {
        Order o = getOrderById(id);
        if (o != null) {
            orders.remove(o);
        }
        throw new RuntimeException("Could not find order with id: " + id);
    }
}