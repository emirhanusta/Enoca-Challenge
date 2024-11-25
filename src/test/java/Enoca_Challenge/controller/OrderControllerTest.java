package Enoca_Challenge.controller;

import Enoca_Challenge.dto.response.OrderResponse;
import Enoca_Challenge.service.OrderService;
import Enoca_Challenge.dto.response.OrderItemResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    void testPlaceOrder() throws Exception {
        Long customerId = 1L;
        BigDecimal totalPrice = new BigDecimal("100.0");
        String orderCode = "orderCode123";

        // Mocked OrderItemResponse
        OrderItemResponse itemResponse = new OrderItemResponse(1L, "Product 1", 2, new BigDecimal("50.00"));

        OrderResponse mockResponse = new OrderResponse(
                1L,
                customerId,
                totalPrice,
                orderCode,
                Collections.singletonList(itemResponse)
        );

        when(orderService.placeOrder(customerId)).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/orders/{customerId}", customerId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(orderCode))
                .andExpect(jsonPath("$.totalPrice").value(totalPrice))
                .andExpect(jsonPath("$.orderItems[0].productName").value("Product 1"))
                .andExpect(jsonPath("$.orderItems[0].quantity").value(2));

        verify(orderService, times(1)).placeOrder(customerId);
    }

    @Test
    void testGetOrderForCode() throws Exception {
        String orderCode = "orderCode123";

        // Mocked OrderItemResponse
        OrderItemResponse itemResponse = new OrderItemResponse(1L, "Product 1", 2, BigDecimal.TEN);

        OrderResponse mockResponse = new OrderResponse(
                1L,
                1L,
                new BigDecimal("10.0"),
                orderCode,
                Collections.singletonList(itemResponse)
        );

        when(orderService.getOrderForCode(orderCode)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/orders/{orderCode}", orderCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(orderCode))
                .andExpect(jsonPath("$.totalPrice").value("10.0"))
                .andExpect(jsonPath("$.orderItems[0].productName").value("Product 1"))
                .andExpect(jsonPath("$.orderItems[0].quantity").value(2));

        verify(orderService, times(1)).getOrderForCode(orderCode);
    }
}
