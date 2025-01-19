package com.trading.orderbook.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class OrderBook {

  private final String symbol;
  private final TreeMap<BigDecimal, List<Order>> bids;
  private final TreeMap<BigDecimal, List<Order>> offers;

  ;

  public OrderBook(String symbol) {
    this.symbol = symbol;
    this.bids = new TreeMap<>();
    this.offers = new TreeMap<>();
    offers.put(BigDecimal.valueOf(Double.MAX_VALUE), new ArrayList<>());
    bids.put(BigDecimal.valueOf(Double.MIN_VALUE), new ArrayList<>());
  }

  public synchronized BidOrder adBidOrder(BidOrder bidOrder) {
    BidOrder adjustedOrder = match(bidOrder);
    if (adjustedOrder.getUnfilledQuantity() == 0) {
      return adjustedOrder;
    }
    bids.putIfAbsent(adjustedOrder.getPrice(), new ArrayList<>());
    bids.get(adjustedOrder.getPrice()).add(adjustedOrder);
    return adjustedOrder;
  }

  public synchronized OfferOrder addOfferOrder(OfferOrder offerOrder) {
    OfferOrder adjustedOrder = match(offerOrder);
    if (adjustedOrder.getUnfilledQuantity() == 0) {
      return adjustedOrder;
    }
    offers.putIfAbsent(adjustedOrder.getPrice(), new ArrayList<>());
    offers.get(adjustedOrder.getPrice()).add(adjustedOrder);
    return adjustedOrder;
  }

  private synchronized <T extends Order> T match(T order) {
    if (order instanceof BidOrder) {
      return matchBidOrder(order);
    } else {
      return matchOfferOrder(order);
    }
  }

  private <T extends Order> T matchBidOrder(T order) {
    BigDecimal lowestOfferPrice = offers.firstKey();
    if (lowestOfferPrice.compareTo(order.getPrice()) <= 0) {
      orderMatchedInOrderbook(order, offers.get(lowestOfferPrice));
      if (order.getUnfilledQuantity() > 0) {
        return matchBidOrder(order);
      }
      return order;
    }
    return order;
  }

  private synchronized <T extends Order> T matchOfferOrder(T order) {

    BigDecimal highestBidPrice = bids.lastKey();
    if (highestBidPrice.compareTo(order.getPrice()) >= 0) {
      orderMatchedInOrderbook(order, bids.get(highestBidPrice));
      if (order.getUnfilledQuantity() > 0) {
        return matchBidOrder(order);
      }
      return order;
    }
    return order;
  }

  private synchronized <T extends Order> void orderMatchedInOrderbook(
      T order, List<Order> ordersAtPrice) {
    List<Order> ordersToRemove = new ArrayList<>();

    ordersAtPrice.forEach(
        offer -> {
          // extra cycles
          int filledQuantity = Math.min(offer.getUnfilledQuantity(), order.getUnfilledQuantity());
          offer.setUnfilledQuantity(offer.getUnfilledQuantity() - filledQuantity);
          order.setUnfilledQuantity(order.getUnfilledQuantity() - filledQuantity);
          if (offer.getUnfilledQuantity() == 0) {
            ordersToRemove.add(offer);
          }
        });
    ordersAtPrice.removeAll(ordersToRemove);
  }

  public List<OrderBookDepth> orderbookDepth() {
    List<OrderBookDepth> orderbookDepthList = new ArrayList<>();

    bids.forEach(
        (price, orders) -> {
          int quantity = orders.stream().mapToInt(Order::getUnfilledQuantity).sum();
          if (quantity != 0) {
            orderbookDepthList.add(new OrderBookDepth(price, quantity, OrderType.BUY));
          }
        });
    offers.forEach(
        (price, orders) -> {
          int quantity = orders.stream().mapToInt(Order::getUnfilledQuantity).sum();
          if (quantity != 0) {
            orderbookDepthList.add(new OrderBookDepth(price, quantity, OrderType.SELL));
          }
        });

    return orderbookDepthList;
  }

  public Order addOrder(Order order) {
    if (order instanceof BidOrder) {
      return adBidOrder((BidOrder) order);
    } else {
      return addOfferOrder((OfferOrder) order);
    }
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }
}
