package com.trading.orderbook.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class OrderTest {

  @ParameterizedTest
  @CsvSource({
    "AAPL, 100, 10, 10, OPEN",
    "AAPL, 100, 10, 5, PARTIALLY_FILLED",
    "AAPL, 100, 10, 0, FILLED"
  })
  void testBidOrderStatus(
      String symbol,
      BigDecimal price,
      int quantity,
      int unfilledQuantity,
      OrderStatus expectedStatus) {
    BidOrder bidOrder = new BidOrder(symbol, price, quantity);
    bidOrder.setUnfilledQuantity(unfilledQuantity);
    assertEquals(expectedStatus, bidOrder.getStatus());
  }

  @ParameterizedTest
  @CsvSource({
    "AAPL, 200, 5, 5, OPEN",
    "AAPL, 200, 5, 2, PARTIALLY_FILLED",
    "AAPL, 200, 5, 0, FILLED"
  })
  void testOfferOrderStatus(
      String symbol,
      BigDecimal price,
      int quantity,
      int unfilledQuantity,
      OrderStatus expectedStatus) {
    OfferOrder offerOrder = new OfferOrder(symbol, price, quantity);
    offerOrder.setUnfilledQuantity(unfilledQuantity);
    assertEquals(expectedStatus, offerOrder.getStatus());
  }
}
