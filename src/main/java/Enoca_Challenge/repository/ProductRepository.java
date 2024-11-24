package Enoca_Challenge.repository;

import Enoca_Challenge.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>{
    Optional<Product> findByIdAndIsDeletedFalse(Long id);
}
