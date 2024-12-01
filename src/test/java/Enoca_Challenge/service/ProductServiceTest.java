package Enoca_Challenge.service;

import Enoca_Challenge.dto.request.ProductRequest;
import Enoca_Challenge.dto.response.ProductResponse;
import Enoca_Challenge.exception.custom.ProductNotFoundException;
import Enoca_Challenge.model.Product;
import Enoca_Challenge.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ProductServiceTest extends BaseServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Test
    void shouldReturnProductResponse_WhenCreateProduct() {
        // arrange
        ProductRequest productRequest = new ProductRequest(
                "product name",
                BigDecimal.TEN,
                10
        );
        Product product = ProductRequest.from(productRequest);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // act
        ProductResponse savedProduct = productService.createProduct(productRequest);

        // assert
        assertNotNull(savedProduct);
        assertEquals(product.getName(), savedProduct.name());
    }

    @Test
    void shouldReturnProductResponse_WhenGetProductByIdAndProductExists() {
        // arrange
        Long id = 1L;
        Product product = new Product(
                "product name",
                BigDecimal.TEN,
                10
        );
        when(productRepository.findByIdAndIsDeletedFalse(id)).thenReturn(Optional.of(product));

        // act
        ProductResponse result = productService.getProduct(id);

        // assert
        assertNotNull(result);
        assertEquals(product.getName(), result.name());
    }

    @Test
    void shouldThrowProductNotFoundException_WhenGetProductByIdAndProductNotExists() {
        // arrange
        Long id = 1L;
        when(productRepository.findByIdAndIsDeletedFalse(id)).thenReturn(Optional.empty());

        // act
        Exception exception = assertThrows(ProductNotFoundException.class, () -> productService.getProduct(id));

        // assert
        assertEquals("Product with id " + id + " not found", exception.getMessage());
    }

    @Test
    void shouldReturnProductResponse_WhenUpdateProduct() {
        // arrange
        Long id = 1L;
        ProductRequest productRequest = new ProductRequest(
                "product name",
                BigDecimal.TEN,
                10
        );
        Product product = ProductRequest.from(productRequest);

        when(productRepository.findByIdAndIsDeletedFalse(id)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // act
        ProductResponse result = productService.updateProduct(id, productRequest);

        // assert
        assertNotNull(result);
        assertEquals(product.getName(), result.name());
    }

    @Test
    void shouldThrowProductNotFoundException_WhenUpdateProductAndProductNotExists() {
        // arrange
        Long id = 1L;
        ProductRequest productRequest = new ProductRequest(
                "product name",
                BigDecimal.TEN,
                10
        );
        when(productRepository.findByIdAndIsDeletedFalse(id)).thenReturn(Optional.empty());

        // act
        Exception exception = assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(id, productRequest));

        // assert
        assertEquals("Product with id " + id + " not found", exception.getMessage());
    }

    @Test
    void shouldDeleteProduct_WhenDeleteProduct() {
        // arrange
        Long id = 1L;
        Product product = new Product(
                "product name",
                BigDecimal.TEN,
                10
        );

        when(productRepository.findByIdAndIsDeletedFalse(id)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // act
        productService.deleteProduct(id);

        // assert
        assertTrue(product.isDeleted());
    }

    @Test
    void shouldThrowProductNotFoundException_WhenDeleteProductAndProductNotExists() {
        // arrange
        Long id = 1L;
        when(productRepository.findByIdAndIsDeletedFalse(id)).thenReturn(Optional.empty());

        // act
        Exception exception = assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(id));

        // assert
        assertEquals("Product with id " + id + " not found", exception.getMessage());
    }

    @Test
    void shouldReduceStock_WhenReduceStock() {
        // arrange
        Product product = new Product(
                "product name",
                BigDecimal.TEN,
                10
        );

        when(productRepository.save(any(Product.class))).thenReturn(product);

        // act
        productService.reduceProductStock(product, 5);

        // assert
        assertEquals(5, product.getStock());
    }
}