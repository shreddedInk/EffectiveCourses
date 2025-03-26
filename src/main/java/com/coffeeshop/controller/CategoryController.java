package com.coffeeshop.controller;

import com.coffeeshop.dto.CategoryDTO;
import com.coffeeshop.exception.ResourceNotFoundException;
import com.coffeeshop.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/categories")
@Tag(name = "Categories", description = "API for managing categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @Operation(summary = "Create a new category", description = "Creates a new category with the provided details.")
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok(createdCategory);
    }

    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieves a paginated list of all categories with sorting support.")
    public ResponseEntity<Page<CategoryDTO>> getAllCategories(
            @Parameter(description = "Pagination and sorting parameters")
            @PageableDefault(sort = "name", size = 20) Pageable pageable) {
        return ResponseEntity.ok(categoryService.getAllCategories(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Retrieves a single category by its ID.")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        Optional<CategoryDTO> categoryDTO = Optional.ofNullable(categoryService.getCategoryById(id));
        return categoryDTO.map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search categories by name", description = "Searches categories by name with pagination and sorting.")
    public ResponseEntity<Page<CategoryDTO>> searchCategories(
            @RequestParam(required = false) String name,
            @Parameter(description = "Pagination and sorting parameters")
            @PageableDefault(sort = "name", size = 10) Pageable pageable) {
        return ResponseEntity.ok(categoryService.searchCategories(name, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category", description = "Updates an existing category by its ID.")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryDTO categoryDTO) {
        try {
            CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);
            return ResponseEntity.ok(updatedCategory);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "Deletes a category by its ID.")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
