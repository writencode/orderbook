package com.trading.orderbook.model;

import java.math.BigDecimal;

// @Entity
// @DiscriminatorValue("AskOrder")
public class BidOrder extends Order {
  public BidOrder(String symbol, BigDecimal price, Integer quantity) {
    super(symbol, OrderType.BUY, price, quantity);
  }
}
