package com.trading.orderbook.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderbookTest {

  private OrderBook orderBook;

  @BeforeEach
  void setUp() {
    orderBook = new OrderBook("AAPL");
  }

  @Test
  void addBidOrderSuccessfully() {
    BidOrder bidOrder = new BidOrder("AAPL", BigDecimal.valueOf(100), 10);
    BidOrder result = orderBook.adBidOrder(bidOrder);
    assertEquals(result.getQuantity(), result.getUnfilledQuantity());
    assertTrue(
        orderBook.orderbookDepth().stream().anyMatch(depth -> depth.type() == OrderType.BUY));
  }

  @Test
  void addOfferOrderSuccessfully() {
    OfferOrder offerOrder = new OfferOrder("AAPL", BigDecimal.valueOf(200), 5);
    OfferOrder result = orderBook.addOfferOrder(offerOrder);
    assertEquals(result.getQuantity(), result.getUnfilledQuantity());
    assertTrue(
        orderBook.orderbookDepth().stream().anyMatch(depth -> depth.type() == OrderType.SELL));
  }

  @Test
  void matchBidOrderWithOfferOrder() {
    OfferOrder offerOrder = new OfferOrder("AAPL", BigDecimal.valueOf(100), 5);
    orderBook.addOfferOrder(offerOrder);
    BidOrder bidOrder = new BidOrder("AAPL", BigDecimal.valueOf(100), 5);
    BidOrder result = orderBook.adBidOrder(bidOrder);
    // bid and offer orders are matched
    assertEquals(0, bidOrder.getUnfilledQuantity());
    assertEquals(0, offerOrder.getUnfilledQuantity());
    assertTrue(orderBook.orderbookDepth().isEmpty());
  }

  @Test
  void matchOfferOrderWithBidOrder() {
    BidOrder bidOrder = new BidOrder("AAPL", BigDecimal.valueOf(100), 5);
    orderBook.adBidOrder(bidOrder);
    OfferOrder offerOrder = new OfferOrder("AAPL", BigDecimal.valueOf(100), 5);
    OfferOrder result = orderBook.addOfferOrder(offerOrder);
    assertEquals(0, bidOrder.getUnfilledQuantity());
    assertEquals(0, offerOrder.getUnfilledQuantity());
    assertTrue(orderBook.orderbookDepth().isEmpty());
  }

  @Test
  void partialMatchBidOrder() {
    OfferOrder offerOrder = new OfferOrder("AAPL", BigDecimal.valueOf(100), 5);
    orderBook.addOfferOrder(offerOrder);
    BidOrder bidOrder = new BidOrder("AAPL", BigDecimal.valueOf(100), 10);
    BidOrder result = orderBook.adBidOrder(bidOrder);
    assertEquals(5, bidOrder.getUnfilledQuantity());
    assertEquals(0, offerOrder.getUnfilledQuantity());
    List<OrderBookDepth> depths = orderBook.orderbookDepth();
    assertEquals(1, depths.size());
    assertEquals(OrderType.BUY, depths.get(0).type());
    assertEquals(5, depths.get(0).quantity());
  }

  @Test
  void partialMatchOfferOrder() {
    BidOrder bidOrder = new BidOrder("AAPL", BigDecimal.valueOf(100), 5);
    orderBook.adBidOrder(bidOrder);
    OfferOrder offerOrder = new OfferOrder("AAPL", BigDecimal.valueOf(100), 10);
    OfferOrder result = orderBook.addOfferOrder(offerOrder);
    assertEquals(5, result.getUnfilledQuantity());
    List<OrderBookDepth> depths = orderBook.orderbookDepth();
    assertEquals(1, depths.size());
    assertEquals(OrderType.SELL, depths.get(0).type());
    assertEquals(5, depths.get(0).quantity());
  }

  @Test
  void noMatchForBidOrder() {
    BidOrder bidOrder = new BidOrder("AAPL", BigDecimal.valueOf(50), 10);
    BidOrder result = orderBook.adBidOrder(bidOrder);
    assertEquals(10, result.getUnfilledQuantity());
    List<OrderBookDepth> depths = orderBook.orderbookDepth();
    assertEquals(1, depths.size());
    assertEquals(OrderType.BUY, depths.get(0).type());
    assertEquals(10, depths.get(0).quantity());
  }

  @Test
  void noMatchForOfferOrder() {
    OfferOrder offerOrder = new OfferOrder("AAPL", BigDecimal.valueOf(150), 10);
    OfferOrder result = orderBook.addOfferOrder(offerOrder);
    assertEquals(10, result.getUnfilledQuantity());
    List<OrderBookDepth> depths = orderBook.orderbookDepth();
    assertEquals(1, depths.size());
    assertEquals(OrderType.SELL, depths.get(0).type());
    assertEquals(10, depths.get(0).quantity());
  }
}
