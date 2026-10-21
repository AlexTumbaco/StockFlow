package com.stockflow.product.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Product representation")
public record ProductResponse(
        Long id,
        String name,
        String sku,
        String description,
        BigDecimal price,
        Integer stock,
        Integer minimumStock,
        Boolean active,
        Long categoryId,
        String categoryName) {
}
