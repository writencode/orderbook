package com.trading.orderbook.controller;

import com.trading.orderbook.model.OrderBook;
import com.trading.orderbook.model.OrderBookDepth;
import com.trading.orderbook.service.OrderBookService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for managing order books. */
@RestController
@RequestMapping("/api/order-books")
public class OrderBookController {

  private final OrderBookService orderBookService;

  /**
   * Constructs a new OrderBookController with the specified OrderBookService.
   *
   * @param orderBookService the service to manage order books
   */
  public OrderBookController(OrderBookService orderBookService) {
    this.orderBookService = orderBookService;
  }

  /**
   * Retrieves the order book for a given symbol.
   *
   * @param symbol the symbol of the order book to retrieve
   * @return a list of order book depths for the specified symbol
   */
  @GetMapping("/{symbol}")
  public List<OrderBookDepth> getOrderBook(@PathVariable String symbol) {
    OrderBook orderBook = orderBookService.getOrderBook(symbol);
    return orderBook.orderbookDepth();
  }
}
