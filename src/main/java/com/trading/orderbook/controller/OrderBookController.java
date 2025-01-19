package com.trading.orderbook.controller;

import com.trading.orderbook.model.OrderBook;
import com.trading.orderbook.model.OrderBookDepth;
import com.trading.orderbook.service.OrderBookService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order-books")
public class OrderBookController {

  private final OrderBookService orderBookService;

  public OrderBookController(OrderBookService orderBookService) {
    this.orderBookService = orderBookService;
  }

  @GetMapping("/{symbol}")
  public List<OrderBookDepth> getOrderBook(@PathVariable String symbol) {
    OrderBook orderBook = orderBookService.getOrderBook(symbol);
    return orderBook.orderbookDepth();
  }
}
