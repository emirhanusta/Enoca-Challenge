package Enoca_Challenge.service;

import Enoca_Challenge.dto.request.ProductRequest;
import Enoca_Challenge.dto.response.ProductResponse;
import Enoca_Challenge.exception.custom.ProductNotFoundException;
import Enoca_Challenge.model.Product;
import Enoca_Challenge.repository.ProductRepository;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse createProduct(ProductRequest productRequest) {
        log.info("Creating product with details: {}", productRequest);
        Product savedProduct = productRepository.save(ProductRequest.from(productRequest));
        log.info("Product with id {} created successfully", savedProduct.getId());
        return ProductResponse.from(savedProduct);
    }

    public ProductResponse getProduct(Long id) {
        log.debug("Fetching product with id {}", id);
        Product product = getProductById(id);
        log.debug("Product with id {} found", id);
        return ProductResponse.from(product);
    }

    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        log.info("Updating product with id {}", id);
        Product product = getProductById(id);
        product.setName(productRequest.name());
        product.setPrice(productRequest.price());
        product.setStock(productRequest.stock());
        Product updatedProduct = productRepository.save(product);
        log.info("Product with id {} updated successfully", updatedProduct.getId());
        return ProductResponse.from(updatedProduct);
    }

    public void deleteProduct(Long id) {
        log.info("Deleting product with id {}", id);
        Product product = getProductById(id);
        product.setDeleted(true);
        productRepository.save(product);
        log.info("Product with id {} marked as deleted", id);
    }

    protected Product getProductById(Long id) {
        log.debug("Fetching product with id {}", id);
        return productRepository.findByIdAndIsDeletedFalse(id).orElseThrow(
                () -> {
                    log.error("Product with id {} not found", id);
                    return new ProductNotFoundException("Product with id " + id + " not found");
                }
        );
    }

    protected void reduceProductStock(Product product, Integer quantity) {
        log.debug("Reducing stock for product with id {} by {}", product.getId(), quantity);
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
        log.info("Product with id {} stock reduced by {}. New stock: {}", product.getId(), quantity, product.getStock());
    }
}
