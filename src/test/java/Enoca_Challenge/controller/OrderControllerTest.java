package Enoca_Challenge.controller;

import Enoca_Challenge.dto.response.OrderResponse;
import Enoca_Challenge.service.OrderService;
import Enoca_Challenge.dto.response.OrderItemResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
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
    void testGetAllOrdersForCustomer() throws Exception {
        // arrange
        Long customerId = 1L;
        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());

        OrderResponse order1 = new OrderResponse(1L, 1L, BigDecimal.valueOf(10), "orderCode1", Collections.emptyList());
        OrderResponse order2 = new OrderResponse(2L, 1L, BigDecimal.valueOf(20), "orderCode2", Collections.emptyList());
        Page<OrderResponse> ordersPage = new PageImpl<>(List.of(order1, order2), pageable, 2);

        when(orderService.getAllOrdersForCustomer(customerId, pageable)).thenReturn(ordersPage);

        // act & assert
        mockMvc.perform(get("/api/v1/orders/list/{customerId}", customerId)
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(order1.id()))
                .andExpect(jsonPath("$.content[1].id").value(order2.id()))
                .andExpect(jsonPath("$.content[0].totalPrice").value(order1.totalPrice()))
                .andExpect(jsonPath("$.content[1].totalPrice").value(order2.totalPrice()));

        verify(orderService).getAllOrdersForCustomer(customerId, pageable);
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
