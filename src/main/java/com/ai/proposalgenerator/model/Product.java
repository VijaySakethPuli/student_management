package com.ai.proposalgenerator.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @Column(name = "price_per_unit")
    private Double pricePerUnit;
    
    @Column(name = "stock_quantity")
    private Integer stockQuantity;
    
    @Column(name = "sustainability_score")
    private Integer sustainabilityScore;
}
