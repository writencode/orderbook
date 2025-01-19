package com.trading.orderbook.service;

import com.trading.orderbook.model.OrderBook;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class OrderBookService {
  Map<String, OrderBook> symbolToOrderBook;

  public OrderBookService() {
    symbolToOrderBook = new HashMap<>();
  }

  public synchronized OrderBook getOrderBook(String symbol) {
    symbolToOrderBook.putIfAbsent(symbol, new OrderBook(symbol));
    return symbolToOrderBook.get(symbol);
  }
}
