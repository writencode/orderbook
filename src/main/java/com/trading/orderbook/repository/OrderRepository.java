package com.trading.orderbook.repository;

import com.trading.orderbook.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//TODO - enable later
//@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}