package com.trading.orderbook.controller;

import com.trading.orderbook.model.BidOrder;
import com.trading.orderbook.model.OfferOrder;
import com.trading.orderbook.model.Order;

import com.trading.orderbook.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order != null) {
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public Order createOrder(@RequestParam String symbol, @RequestParam String type, @RequestParam BigDecimal price, @RequestParam Integer quantity) {
        if (type.equals("SELL")) {
            return orderService.createOrder(new OfferOrder(symbol, price, quantity));
        } else if (type.equals("BUY")) {
            return orderService.createOrder(new BidOrder(symbol, price, quantity));
        } else {
            throw new IllegalArgumentException("Invalid order type: " + type);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}