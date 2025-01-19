package com.trading.orderbook.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("OfferOrder")
public class OfferOrder extends Order {
    public OfferOrder(String symbol, BigDecimal price, Integer quantity) {
        super(symbol, OrderType.SELL, price, quantity);
    }

    protected OfferOrder() {
    }
}
