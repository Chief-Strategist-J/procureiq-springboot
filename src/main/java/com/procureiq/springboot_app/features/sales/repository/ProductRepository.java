package com.procureiq.springboot_app.features.sales.repository;

import com.procureiq.springboot_app.features.sales.types.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByIsDeletedFalse();
}
