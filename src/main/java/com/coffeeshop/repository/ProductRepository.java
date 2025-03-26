package com.coffeeshop.repository;

import com.coffeeshop.model.Category;
import com.coffeeshop.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByAvailableTrue(Pageable pageable);

    Page<Product> findByCategory(Category category, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :namePart, '%')) " +
            "AND p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findProductsByNameAndPriceRange(
            @Param("namePart") String namePart,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    List<Product> findByAvailableTrue();

    List<Product> findByCategory(Category category);

    @Query("SELECT p FROM Product p " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :namePart, '%')) " +
            "AND p.price BETWEEN :minPrice AND :maxPrice " +
            "ORDER BY p.price ASC")
    List<Product> findProductsByNameAndPriceRange(
            @Param("namePart") String namePart,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice);
}