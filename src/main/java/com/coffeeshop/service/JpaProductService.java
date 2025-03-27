package com.coffeeshop.service;

import com.coffeeshop.dto.ProductDTO;
import com.coffeeshop.exception.ResourceNotFoundException;
import com.coffeeshop.model.Category;
import com.coffeeshop.model.Product;
import com.coffeeshop.repository.CategoryRepository;
import com.coffeeshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class JpaProductService implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public JpaProductService(ProductRepository productRepository,
                             CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public Page<ProductDTO> getProductsByCategory(Long categoryId, Pageable pageable) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        return productRepository.findByCategory_Id(categoryId, pageable)
                .map(this::convertToDTO);
    }


    @Override
    public Page<ProductDTO> searchProductsByNameAndPriceRange(
            String namePart,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable) {

        BigDecimal effectiveMin = minPrice != null ? minPrice : BigDecimal.ZERO;
        BigDecimal effectiveMax = maxPrice != null ? maxPrice : new BigDecimal(Long.MAX_VALUE);

        return productRepository.findProductsByNameAndPriceRange(
                namePart != null ? namePart : "",
                effectiveMin,
                effectiveMax,
                pageable
        ).map(this::convertToDTO);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setImageUrl(productDTO.getImageUrl());
        product.setAvailable(productDTO.isAvailable());

        if (productDTO.getCategory() != null) {
            Category category = categoryRepository.findByName(String.valueOf(productDTO.getCategory()))
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + productDTO.getCategory()));
            product.setCategory(category);
        }

        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setImageUrl(productDTO.getImageUrl());
        existingProduct.setAvailable(productDTO.isAvailable());

        if (productDTO.getCategory() != null) {
            Category category = categoryRepository.findByName(String.valueOf(productDTO.getCategory()))
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + productDTO.getCategory()));
            existingProduct.setCategory(category);
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductDTO convertToDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setAvailable(product.isAvailable());
        dto.setCategory(product.getCategory());

        return dto;
    }

}
