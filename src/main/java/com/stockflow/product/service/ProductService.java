package com.stockflow.product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stockflow.category.entity.Category;
import com.stockflow.category.repository.CategoryRepository;
import com.stockflow.common.exception.ConflictException;
import com.stockflow.common.exception.ResourceNotFoundException;
import com.stockflow.product.dto.ProductRequest;
import com.stockflow.product.dto.ProductResponse;
import com.stockflow.product.entity.Product;
import com.stockflow.product.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        String sku = request.sku().trim();
        if (productRepository.existsBySku(sku)) {
            throw new ConflictException("A product with that SKU already exists");
        }

        Category category = findCategory(request.categoryId());
        Product product = new Product(request.name().trim(), sku, request.description(),
                request.price(), request.minimumStock(), category);
        return toResponse(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findAll(@NonNull Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductService::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(@NonNull Long id) {
        return productRepository.findById(id)
                .map(ProductService::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    @Transactional
    public ProductResponse update(@NonNull Long id, ProductRequest request) {
        Product product = findProduct(id);
        String sku = request.sku().trim();
        if (!product.getSku().equals(sku) && productRepository.existsBySkuAndIdNot(sku, id)) {
            throw new ConflictException("A product with that SKU already exists");
        }

        Category category = findCategory(request.categoryId());
        product.update(request.name().trim(), sku, request.description(),
                request.price(), request.minimumStock(), category);
        return toResponse(product);
    }

    @Transactional
    public void deactivate(@NonNull Long id) {
        findProduct(id).deactivate();
    }

    private Category findCategory(@NonNull Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    private Product findProduct(@NonNull Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    private static ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getMinimumStock(),
                product.getActive(),
                product.getCategory().getId(),
                product.getCategory().getName());
    }
}
