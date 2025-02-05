package com.trading.orderbook.service;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.trading.orderbook.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class OrderServiceTest {

  @Mock private OrderBookService orderBookService;

  @Mock private OrderBook orderBook;

  @InjectMocks private OrderService orderService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(orderBookService.getOrderBook(anyString())).thenReturn(orderBook);
  }

  @Test
  void getAllOrdersReturnsEmptyListInitially() {
    List<Order> orders = orderService.getAllOrders();
    assertTrue(orders.isEmpty());
  }

  @Test
  void createBidOrderSuccessfully() {
    BidOrder order = new BidOrder("AAPL", BigDecimal.valueOf(100), 10);
    when(orderBook.addOrder(order)).thenReturn(order);

    BidOrder createdOrder = (BidOrder) orderService.createOrder(order);

    assertEquals(order, createdOrder);
    assertEquals(1, orderService.getAllOrders().size());
    verify(orderBookService).getOrderBook("AAPL");
    verify(orderBook).addOrder(order);
  }

  @Test
  void createOfferOrderSuccessfully() {
    OfferOrder order = new OfferOrder("AAPL", BigDecimal.valueOf(200), 5);
    when(orderBook.addOrder(order)).thenReturn(order);

    OfferOrder createdOrder = (OfferOrder) orderService.createOrder(order);

    assertEquals(order, createdOrder);
    assertEquals(1, orderService.getAllOrders().size());
    verify(orderBookService).getOrderBook("AAPL");
    verify(orderBook).addOrder(order);
  }

  @Test
  void getOrderByIdReturnsBidOrder() {
    BidOrder order = new BidOrder("AAPL", BigDecimal.valueOf(100), 10);
    when(orderBook.addOrder(order)).thenReturn(order);
    orderService.createOrder(order);

    BidOrder foundOrder = (BidOrder) orderService.getOrderById(order.getId().toString());

    assertEquals(order, foundOrder);
  }

  @Test
  void getOrderByIdReturnsOfferOrder() {
    OfferOrder order = new OfferOrder("AAPL", BigDecimal.valueOf(200), 5);
    when(orderBook.addOrder(order)).thenReturn(order);
    orderService.createOrder(order);

    OfferOrder foundOrder = (OfferOrder) orderService.getOrderById(order.getId().toString());

    assertEquals(order, foundOrder);
  }

  @Test
  void getOrderByIdReturnsNullWhenNotFound() {
    Order foundOrder = orderService.getOrderById(UUID.randomUUID().toString());
    assertNull(foundOrder);
  }

  @Test
  void deleteBidOrderSuccessfully() {
    BidOrder order = new BidOrder("AAPL", BigDecimal.valueOf(100), 10);
    when(orderBook.addOrder(order)).thenReturn(order);
    orderService.createOrder(order);

    orderService.cancelOrder(order.getId().toString());

    assertTrue(orderService.getAllOrders().isEmpty());
  }

  @Test
  void deleteOfferOrderSuccessfully() {
    OfferOrder order = new OfferOrder("AAPL", BigDecimal.valueOf(200), 5);
    when(orderBook.addOrder(order)).thenReturn(order);
    orderService.createOrder(order);

    orderService.cancelOrder(order.getId().toString());

    assertTrue(orderService.getAllOrders().isEmpty());
  }

  @Test
  void deleteOrderThrowsExceptionWhenNotFound() {
    String id = UUID.randomUUID().toString();
    Exception exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              orderService.cancelOrder(id);
            });

    assertEquals("Could not find order with id: " + id, exception.getMessage());
  }

  @Test
  void cancelBidOrderSuccessfully() {
    BidOrder order = new BidOrder("AAPL", BigDecimal.valueOf(100), 10);
    when(orderBook.addOrder(order)).thenReturn(order);
    when(orderBook.orderbookDepth()).thenReturn(emptyList());
    doNothing().when(orderBookService).cancelOrder(order);
    doNothing().when(orderBook).cancelOrder(order);

    orderService.createOrder(order);
    orderService.cancelOrder(order.getId().toString());

    assertTrue(orderService.getAllOrders().isEmpty());
    verify(orderBook).addOrder(order);
  }

  @Test
  void cancelOfferOrderSuccessfully() {
    OfferOrder order = new OfferOrder("AAPL", BigDecimal.valueOf(200), 5);
    when(orderBook.addOrder(order)).thenReturn(order);
    when(orderBook.orderbookDepth()).thenReturn(emptyList());
    doNothing().when(orderBook).cancelOrder(order);
    orderService.createOrder(order);

    orderService.cancelOrder(order.getId().toString());

    assertTrue(orderService.getAllOrders().isEmpty());
    verify(orderBook).addOrder(order);
  }

  @Test
  void cancelPartiallyFilledOrderSuccessfully() {
    BidOrder bidOrder = new BidOrder("AAPL", BigDecimal.valueOf(100), 10);
    OfferOrder offerOrder = new OfferOrder("AAPL", BigDecimal.valueOf(100), 4);
    when(orderBook.addOrder(bidOrder)).thenReturn(bidOrder);
    when(orderBook.addOrder(offerOrder)).thenReturn(offerOrder);
    when(orderBook.orderbookDepth()).thenReturn(emptyList());
    doNothing().when(orderBook).cancelOrder(bidOrder);

    orderService.createOrder(bidOrder);
    orderService.createOrder(offerOrder);

    // Simulate partial fill
    bidOrder.setUnfilledQuantity(bidOrder.getQuantity()-offerOrder.getQuantity());
    offerOrder.setUnfilledQuantity(0);

    orderService.cancelOrder(bidOrder.getId().toString());

    assertFalse(
        orderService.getAllOrders().stream()
            .noneMatch(order -> order.getId().equals(bidOrder.getId())));
    orderService.getOrderById(bidOrder.getId().toString());
    assertEquals(0, bidOrder.getUnfilledQuantity()); //partially filled order was cancelled
    assertEquals(4, bidOrder.getQuantity());
    assertEquals(OrderStatus.FILLED, bidOrder.getStatus());

    verify(orderBook).addOrder(bidOrder);
  }
}
