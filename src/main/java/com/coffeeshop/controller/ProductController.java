package com.coffeeshop.controller;

import com.coffeeshop.dto.ProductDTO;
import com.coffeeshop.exception.ResourceNotFoundException;
import com.coffeeshop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.springframework.data.domain.Sort.Direction.ASC;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "API for managing products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieves a paginated list of all products with sorting support.")
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @PageableDefault(sort = "price", direction = ASC) Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves a single product by its ID.")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Optional<ProductDTO> productDTO = Optional.ofNullable(productService.getProductById(id));
        return productDTO.map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Searches products by name and price range with pagination and sorting.")
    public ResponseEntity<Page<ProductDTO>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(sort = "name", direction = ASC) Pageable pageable) {
        return ResponseEntity.ok(productService.searchProductsByNameAndPriceRange(name, minPrice, maxPrice, pageable));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category", description = "Retrieves a paginated list of products by category ID.")
    public ResponseEntity<Page<ProductDTO>> getProductsByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(sort = "price", direction = ASC) Pageable pageable) {
        Page<ProductDTO> products = productService.getProductsByCategory(categoryId, pageable);
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No products found for category id: " + categoryId);
        }
        return ResponseEntity.ok(products);
    }
}
