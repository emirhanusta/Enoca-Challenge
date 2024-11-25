package Enoca_Challenge.controller;

import Enoca_Challenge.dto.request.CartItemRequest;
import Enoca_Challenge.dto.response.CartResponse;
import Enoca_Challenge.dto.response.CartItemResponse;
import Enoca_Challenge.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @Test
    void testGetCart() throws Exception {
        Long customerId = 1L;

        // Create a CartItemResponse for the response
        CartItemResponse itemResponse = new CartItemResponse(1L, 1L, 2);
        CartResponse cartResponse = new CartResponse(
                1L,
                customerId,
                List.of(itemResponse),
                BigDecimal.valueOf(50.0)
        );

        when(cartService.getCart(customerId)).thenReturn(cartResponse);

        mockMvc.perform(get("/api/v1/carts/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.totalPrice").value(50.0))
                .andExpect(jsonPath("$.cartItems.size()").value(1)) // Ensure there is 1 item
                .andExpect(jsonPath("$.cartItems[0].productId").value(1L))
                .andExpect(jsonPath("$.cartItems[0].quantity").value(2));

        verify(cartService).getCart(customerId);
    }

    @Test
    void testAddProductToCart() throws Exception {
        // Create a CartItemResponse for the response
        CartItemResponse itemResponse = new CartItemResponse(1L, 1L, 2);
        CartResponse cartResponse = new CartResponse(
                1L,
                1L,
                List.of(itemResponse),
                BigDecimal.valueOf(50.0)
        );

        when(cartService.addProductToCart(any(CartItemRequest.class))).thenReturn(cartResponse);

        mockMvc.perform(post("/api/v1/carts/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":1,\"customerId\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(50.0))
                .andExpect(jsonPath("$.cartItems.size()").value(1)) // Ensure there is 1 item
                .andExpect(jsonPath("$.cartItems[0].productId").value(1L))
                .andExpect(jsonPath("$.cartItems[0].quantity").value(2));

        verify(cartService).addProductToCart(any(CartItemRequest.class));
    }

    @Test
    void testReduceProductQuantity() throws Exception {
        // Create a CartItemResponse for the response
        CartItemResponse itemResponse = new CartItemResponse(1L, 1L, 1);
        CartResponse cartResponse = new CartResponse(
                1L,
                1L,
                List.of(itemResponse),
                BigDecimal.valueOf(25.0)
        );

        when(cartService.reduceProductQuantity(any(CartItemRequest.class))).thenReturn(cartResponse);

        mockMvc.perform(post("/api/v1/carts/reduce")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":1,\"customerId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(25.0))
                .andExpect(jsonPath("$.cartItems.size()").value(1)) // Ensure there is 1 item
                .andExpect(jsonPath("$.cartItems[0].productId").value(1L))
                .andExpect(jsonPath("$.cartItems[0].quantity").value(1));

        verify(cartService).reduceProductQuantity(any(CartItemRequest.class));
    }

    @Test
    void testRemoveItemFromCart() throws Exception {
        mockMvc.perform(delete("/api/v1/carts/remove-item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":1,\"customerId\":1}"))
                .andExpect(status().isNoContent());

        verify(cartService).removeItemFromCart(any(CartItemRequest.class));
    }

    @Test
    void testEmptyCart() throws Exception {
        Long customerId = 1L;

        mockMvc.perform(delete("/api/v1/carts/{customerId}", customerId))
                .andExpect(status().isNoContent());

        verify(cartService).emptyCart(eq(customerId));
    }
}
