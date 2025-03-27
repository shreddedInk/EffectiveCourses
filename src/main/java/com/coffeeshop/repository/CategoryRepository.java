package com.coffeeshop.repository;

import com.coffeeshop.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    Page<Category> findAll(Pageable pageable);

    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :namePart, '%'))")
    Page<Category> findByNameContainingIgnoreCase(@Param("namePart") String namePart, Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.available = true")
    Page<Category> findAvailableCategories(Pageable pageable);

    @Query("SELECT c FROM Category c WHERE " +
            "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Category> searchByName(
            @Param("name") String name,
            Pageable pageable);
}