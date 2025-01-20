package com.trading.orderbook.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Represents an order book for a specific trading symbol. Manages bid and offer orders and matches
 * them based on price and quantity.
 */
public class OrderBook {

  private final String symbol;
  private final TreeMap<BigDecimal, List<Order>> bids;
  private final TreeMap<BigDecimal, List<Order>> offers;

  /**
   * Constructs an OrderBook for the given symbol.
   *
   * @param symbol the trading symbol for this order book
   */
  public OrderBook(String symbol) {
    this.symbol = symbol;
    this.bids = new TreeMap<>();
    this.offers = new TreeMap<>();
    offers.put(BigDecimal.valueOf(Double.MAX_VALUE), new ArrayList<>());
    bids.put(BigDecimal.valueOf(Double.MIN_VALUE), new ArrayList<>());
  }

  /**
   * Adds a bid order to the order book and attempts to match it.
   *
   * @param bidOrder the bid order to add
   * @return the adjusted bid order after matching
   */
  public synchronized BidOrder adBidOrder(BidOrder bidOrder) {
    BidOrder adjustedOrder = match(bidOrder);
    if (adjustedOrder.getUnfilledQuantity() == 0) {
      return adjustedOrder;
    }
    bids.putIfAbsent(adjustedOrder.getPrice(), new ArrayList<>());
    bids.get(adjustedOrder.getPrice()).add(adjustedOrder);
    return adjustedOrder;
  }

  /**
   * Adds an offer order to the order book and attempts to match it.
   *
   * @param offerOrder the offer order to add
   * @return the adjusted offer order after matching
   */
  public synchronized OfferOrder addOfferOrder(OfferOrder offerOrder) {
    OfferOrder adjustedOrder = match(offerOrder);
    if (adjustedOrder.getUnfilledQuantity() == 0) {
      return adjustedOrder;
    }
    offers.putIfAbsent(adjustedOrder.getPrice(), new ArrayList<>());
    offers.get(adjustedOrder.getPrice()).add(adjustedOrder);
    return adjustedOrder;
  }

  /**
   * Matches the given order with existing orders in the order book.
   *
   * @param order the order to match
   * @param <T> the type of the order (BidOrder or OfferOrder)
   * @return the adjusted order after matching
   */
  private synchronized <T extends Order> T match(T order) {
    if (order instanceof BidOrder) {
      return matchBidOrder(order);
    } else {
      return matchOfferOrder(order);
    }
  }

  /**
   * Matches a bid order with existing offer orders in the order book.
   *
   * @param order the bid order to match
   * @param <T> the type of the order
   * @return the adjusted bid order after matching
   */
  private <T extends Order> T matchBidOrder(T order) {
    BigDecimal lowestOfferPrice = offers.firstKey();
    if (lowestOfferPrice.compareTo(order.getPrice()) <= 0) {
      orderMatchedInOrderbook(order, offers.get(lowestOfferPrice));
      if (offers.get(lowestOfferPrice).isEmpty()) {
        offers.remove(lowestOfferPrice);
      }
      if (order.getUnfilledQuantity() > 0) {
        return matchBidOrder(order);
      }
      return order;
    }
    return order;
  }

  /**
   * Matches an offer order with existing bid orders in the order book.
   *
   * @param order the offer order to match
   * @param <T> the type of the order
   * @return the adjusted offer order after matching
   */
  private synchronized <T extends Order> T matchOfferOrder(T order) {
    BigDecimal highestBidPrice = bids.lastKey();
    if (highestBidPrice.compareTo(order.getPrice()) >= 0) {
      orderMatchedInOrderbook(order, bids.get(highestBidPrice));
      if (bids.get(highestBidPrice).isEmpty()) {
        bids.remove(highestBidPrice);
      }
      if (order.getUnfilledQuantity() > 0) {
        return matchBidOrder(order);
      }
      return order;
    }
    return order;
  }

  /**
   * Matches the given order with a list of orders at a specific price.
   *
   * @param order the order to match
   * @param ordersAtPrice the list of orders at the matching price
   * @param <T> the type of the order
   */
  private synchronized <T extends Order> void orderMatchedInOrderbook(
      T order, List<Order> ordersAtPrice) {
    // List to keep track of orders that are fully matched and need to be removed
    List<Order> ordersToRemove = new ArrayList<>();

    // Iterate through each order in the list of orders at the matching price
    ordersAtPrice.forEach(
        matchedOrder -> {
          // Calculate the quantity that can be filled for the current order
          int filledQuantity =
              Math.min(matchedOrder.getUnfilledQuantity(), order.getUnfilledQuantity());

          // Update the unfilled quantity of the current order and the matching order
          matchedOrder.setUnfilledQuantity(matchedOrder.getUnfilledQuantity() - filledQuantity);
          order.setUnfilledQuantity(order.getUnfilledQuantity() - filledQuantity);

          // If the current order is fully matched, add it to the list of orders to remove
          if (matchedOrder.getUnfilledQuantity() == 0) {
            ordersToRemove.add(matchedOrder);
          }
        });

    // Remove all fully matched orders from the list of orders at the matching price
    ordersAtPrice.removeAll(ordersToRemove);
  }

  /**
   * Retrieves the depth of the order book, including bid and offer quantities at each price level.
   *
   * @return a list of order book depths
   */
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

  /**
   * Adds an order to the order book and attempts to match it.
   *
   * @param order the order to add
   * @return the adjusted order after matching
   */
  public Order addOrder(Order order) {
    if (order instanceof BidOrder) {
      return adBidOrder((BidOrder) order);
    } else {
      return addOfferOrder((OfferOrder) order);
    }
  }

  public synchronized void cancelOrder(Order o) {
    if (o instanceof BidOrder && bids.containsKey(o.getPrice())) {
      bids.get(o.getPrice()).remove(o);
    } else if (o instanceof OfferOrder && offers.containsKey(o.getPrice())) {
      offers.get(o.getPrice()).remove(o);
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
