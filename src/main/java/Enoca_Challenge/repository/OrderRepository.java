package Enoca_Challenge.repository;

import Enoca_Challenge.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByCustomerId(Long customerId, Pageable pageable);

    Optional<Order> findByCode(String orderCode);
}
