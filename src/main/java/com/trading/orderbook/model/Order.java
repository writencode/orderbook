// src/main/java/com/example/tradingapp/model/Order.java
package com.trading.orderbook.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

// @Entity(name = "orders")
// @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// @DiscriminatorColumn(name = "entity_type")
public abstract class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private UUID id;

  private String symbol;
  private OrderType type; // BUY or SELL
  private BigDecimal price;
  private Integer quantity;
  private Integer unfilledQuantity;

  public Order(String symbol, OrderType orderType, BigDecimal price, Integer quantity) {
    this.id = UUID.randomUUID();
    this.symbol = symbol;
    this.type = orderType;
    this.price = price;
    this.quantity = quantity;
    this.unfilledQuantity = quantity;
  }

  public UUID getId() {
    return id;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public OrderType getType() {
    return type;
  }

  public void setType(OrderType type) {
    this.type = type;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public Integer getUnfilledQuantity() {
    return unfilledQuantity;
  }

  public void setUnfilledQuantity(Integer unfilledQuantity) {
    this.unfilledQuantity = unfilledQuantity;
  }

  public OrderStatus getStatus() {
    if (unfilledQuantity.equals(quantity)) {
      return OrderStatus.OPEN;
    } else if (unfilledQuantity == 0) {
      return OrderStatus.FILLED;
    } else {
      return OrderStatus.PARTIALLY_FILLED;
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
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
        .append("id", id)
        .append("symbol", symbol)
        .append("type", type)
        .append("price", price)
        .append("quantity", quantity)
        .append("unfilledQuantity", unfilledQuantity)
        .append("status", getStatus())
        .toString();
  }
}
