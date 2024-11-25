package Enoca_Challenge.controller;

import Enoca_Challenge.dto.request.ProductRequest;
import Enoca_Challenge.dto.response.ProductResponse;
import Enoca_Challenge.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    void createProduct_ShouldReturnCreatedStatus_WhenValidRequest() throws Exception {
        // Arrange
        ProductResponse response = new ProductResponse(1L, "Product A", BigDecimal.TEN, 5);

        when(productService.createProduct(any(ProductRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content("{\"name\": \"Product A\", \"price\": 10.0, \"stock\": 5 }"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Product A"))
                .andExpect(jsonPath("$.price").value(10.0));

        verify(productService, times(1)).createProduct(any(ProductRequest.class));
    }

    @Test
    void getProduct_ShouldReturnProduct_WhenValidId() throws Exception {
        // Arrange
        ProductResponse response = new ProductResponse(1L, "Product A", BigDecimal.TEN, 5);

        when(productService.getProduct(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Product A"))
                .andExpect(jsonPath("$.price").value(10.0));

        verify(productService, times(1)).getProduct(1L);
    }

    @Test
    void updateProduct_ShouldReturnOk_WhenValidRequest() throws Exception {
        // Arrange
        ProductResponse response = new ProductResponse(1L, "Updated Product", BigDecimal.TEN, 5);

        when(productService.updateProduct(eq(1L), any(ProductRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/v1/products/{id}", 1L)
                        .contentType("application/json")
                        .content("{\"name\": \"Updated Product\", \"price\": 10.0, \"stock\": 5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.price").value(10.0));

        verify(productService, times(1)).updateProduct(eq(1L), any(ProductRequest.class));
    }

    @Test
    void deleteProduct_ShouldReturnNoContent_WhenValidId() throws Exception {
        // Arrange
        doNothing().when(productService).deleteProduct(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/products/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void createProduct_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content("{\"name\": \"\", \"price\": -10.0}"))
                .andExpect(status().isBadRequest());

        verify(productService, times(0)).createProduct(any(ProductRequest.class));
    }
}
