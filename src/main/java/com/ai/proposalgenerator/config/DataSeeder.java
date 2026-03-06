package com.ai.proposalgenerator.config;

import com.ai.proposalgenerator.model.Product;
import com.ai.proposalgenerator.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {
    
    @Bean
    public CommandLineRunner loadData(ProductRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                Product p1 = new Product();
                p1.setName("Recycled Office Paper (Box)");
                p1.setPricePerUnit(45.0);
                p1.setStockQuantity(500);
                p1.setSustainabilityScore(8);
                
                Product p2 = new Product();
                p2.setName("LED Desk Lamp");
                p2.setPricePerUnit(30.0);
                p2.setStockQuantity(200);
                p2.setSustainabilityScore(7);

                Product p3 = new Product();
                p3.setName("Solar Powered Power Bank");
                p3.setPricePerUnit(60.0);
                p3.setStockQuantity(150);
                p3.setSustainabilityScore(9);

                Product p4 = new Product();
                p4.setName("Bamboo Standing Desk");
                p4.setPricePerUnit(350.0);
                p4.setStockQuantity(50);
                p4.setSustainabilityScore(10);

                Product p5 = new Product();
                p5.setName("Biodegradable Pens (100 pack)");
                p5.setPricePerUnit(25.0);
                p5.setStockQuantity(1000);
                p5.setSustainabilityScore(8);

                repository.save(p1);
                repository.save(p2);
                repository.save(p3);
                repository.save(p4);
                repository.save(p5);
            }
        };
    }
}
