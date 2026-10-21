package com.stockflow.product.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.stockflow.category.entity.Category;
import com.stockflow.category.repository.CategoryRepository;
import com.stockflow.common.exception.ConflictException;
import com.stockflow.common.exception.ResourceNotFoundException;
import com.stockflow.product.dto.ProductRequest;
import com.stockflow.product.entity.Product;
import com.stockflow.product.repository.ProductRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("null")
class ProductServiceTest {

    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final ProductService productService = new ProductService(productRepository, categoryRepository);

    @Test
    void rejectsDuplicateSku() {
        when(productRepository.existsBySku("MOU-1001")).thenReturn(true);

        assertThrows(ConflictException.class, () -> productService.create(validRequest()));
        verify(categoryRepository, never()).findById(any());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void rejectsMissingCategory() {
        when(productRepository.existsBySku("MOU-1001")).thenReturn(false);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.create(validRequest()));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void createsProductWithZeroStockAndActiveStatus() {
        Category category = new Category("Electronics", null);
        when(productRepository.existsBySku("MOU-1001")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = productService.create(validRequest());

        assertEquals(0, response.stock());
        assertEquals(Boolean.TRUE, response.active());
        assertEquals(new BigDecimal("29.99"), response.price());
    }

    @Test
    void deactivatesProductWithoutChangingStock() {
        Category category = new Category("Electronics", null);
        Product product = new Product("Mouse", "MOU-1001", null,
                new BigDecimal("29.99"), 5, category);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deactivate(1L);

        assertFalse(product.getActive());
        assertEquals(0, product.getStock());
    }

    @Test
    void reportsMissingProduct() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.findById(99L));
    }

    private ProductRequest validRequest() {
        return new ProductRequest("Wireless Mouse", "MOU-1001", null,
                new BigDecimal("29.99"), 5, 1L);
    }
}
