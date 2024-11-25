package Enoca_Challenge.repository;

import Enoca_Challenge.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProductRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldSaveProduct() {
        // arrange
        Product product = new Product();
        // act
        Product savedProduct = productRepository.save(product);
        // assert
        assertNotNull(savedProduct);
    }

    @Test
    void shouldReturnProduct_whenFindByIdAndIsDeletedFalse_andProductIsNotDeleted() {
        // arrange
        Product product = new Product();
        productRepository.save(product);
        // act
        Optional<Product> optionalProduct = productRepository.findByIdAndIsDeletedFalse(product.getId());
        // assert
        assertTrue(optionalProduct.isPresent());
        assertEquals(product, optionalProduct.get());
    }

    @Test
    void shouldNotReturnProduct_whenFindByIdAndIsDeletedFalse_andProductIsDeleted() {
        // arrange
        Product product = new Product();
        product.setDeleted(true);
        productRepository.save(product);
        // act
        Optional<Product> optionalProduct = productRepository.findByIdAndIsDeletedFalse(product.getId());
        // assert
        assertTrue(optionalProduct.isEmpty());
    }
}