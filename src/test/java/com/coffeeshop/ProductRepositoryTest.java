package com.coffeeshop;

import com.coffeeshop.model.Product;
import com.coffeeshop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @Rollback(false)
    public void testCreateProduct() {
        Product product = new Product();
        product.setName("Cappuccino");
        product.setDescription("Coffee with frothy milk");
        product.setPrice(BigDecimal.valueOf(3.00));
        product.setImageUrl("/images/cappuccino.jpg");
        product.setAvailable(true);
        product.setCategory("Coffee");

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Cappuccino");
    }
}