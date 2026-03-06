package com.ai.proposalgenerator.repository;

import com.ai.proposalgenerator.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
