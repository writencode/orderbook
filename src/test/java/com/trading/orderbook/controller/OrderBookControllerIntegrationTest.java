package com.trading.orderbook.controller;

import com.trading.orderbook.model.BidOrder;
import com.trading.orderbook.model.OfferOrder;
import com.trading.orderbook.model.Order;
import com.trading.orderbook.service.OrderBookService;
import com.trading.orderbook.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderBookControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private OrderService orderService;

  @Autowired
  private OrderBookService orderBookService;

  private Order testOrder;

  @BeforeEach
  public void setUp() {

  }

  @Test
  public void testCancelOrderInOrderBook() throws Exception {
      testOrder = orderService.createOrder(new BidOrder("AAPL", new BigDecimal("150.00"), 10));
    mockMvc.perform(delete("/api/orders/" + testOrder.getId().toString()))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/orders/" + testOrder.getId().toString()))
        .andExpect(status().isNotFound());
  }

    @Test
    public void testCancelPartiallyFilledOrder() throws Exception {
        testOrder = orderService.createOrder(new BidOrder("AAPL", new BigDecimal("150.00"), 10));
        // Simulate partial fill
        testOrder.setUnfilledQuantity(5);
        orderService.createOrder(testOrder);

        // Cancel the partially filled order
        mockMvc.perform(delete("/api/orders/" + testOrder.getId().toString()))
                .andExpect(status().isNoContent());

        // Verify the order quantity is updated to the amount filled
        mockMvc.perform(get("/api/orders/" + testOrder.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(5));




    }



    @Test
    public void testCancelPartiallyFilledOrder_byMatching() throws Exception {
        testOrder = orderService.createOrder(new BidOrder("AAPL", new BigDecimal("150.00"), 10));
        //create an offer order that should be executed and will partially fill the testOrder
        Order offerOrderToMach = orderService.createOrder(new OfferOrder("AAPL", new BigDecimal("150.00"), 3));

        //assert that offerOrderToMatch is filled
        await().atMost(5, SECONDS).untilAsserted(() -> {
            mockMvc.perform(get("/api/orders/" + offerOrderToMach.getId().toString()))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.status").value("FILLED"));
                });

        await().atMost(5, SECONDS).untilAsserted(() -> {mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk());
        });

        //assert that testOrder is partially filled
        await().atMost(5, SECONDS).untilAsserted(() -> {mockMvc.perform(get("/api/orders/" + testOrder.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PARTIALLY_FILLED"));
        });

        //now cancel the testOrder
        await().atMost(5, SECONDS).untilAsserted(() -> {mockMvc.perform(delete("/api/orders/" + testOrder.getId().toString()))
                .andExpect(status().isNoContent());
        });

        //assert that testOrder is filled, since we are canceling unfilled quantity
        await().atMost(5, SECONDS).untilAsserted(() -> {mockMvc.perform(get("/api/orders/" + testOrder.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FILLED"));
        });
    }

    @Test
    public void testCancelOrderInOrderBook_whichIsFilled() throws Exception {
        testOrder = orderService.createOrder(new BidOrder("AAPL", new BigDecimal("150.00"), 10));
        //create an offer order that should be executed and will partially fill the testOrder
        Order offerOrderToMach = orderService.createOrder(new OfferOrder("AAPL", new BigDecimal("150.00"), 3));
        //assert that offerOrderToMatch is filled
        mockMvc.perform(get("/api/orders/" + offerOrderToMach.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FILLED"));

        //now cancel the offerOrderToMach - which is already filled
        mockMvc.perform(delete("/api/orders/" + offerOrderToMach.getId().toString()))
                .andExpect(status().isNoContent());

        //the order should still be filled, and remain visible
        mockMvc.perform(get("/api/orders/" + offerOrderToMach.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FILLED"));

        //clean up - cancel the testOrder
        mockMvc.perform(delete("/api/orders/" + testOrder.getId().toString()))
                .andExpect(status().isNoContent());
    }


}