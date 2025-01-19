package com.trading.orderbook.model;

import java.math.BigDecimal;

public record OrderBookDepth(BigDecimal price, Integer quantity, OrderType type) {}
