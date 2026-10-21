package com.stockflow.product.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import org.springframework.lang.NonNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Product data. Stock and active status are managed by the API.")
public record ProductRequest(
        @NotBlank
        @Size(max = 160)
        @Schema(example = "Wireless Mouse")
        String name,

        @NotBlank
        @Size(max = 64)
        @Schema(example = "MOU-1001")
        String sku,

        @Size(max = 2000)
        @Schema(example = "Ergonomic wireless mouse")
        String description,

        @NotNull
        @DecimalMin(value = "0.0")
        @Digits(integer = 15, fraction = 4)
        @Schema(example = "29.99")
        BigDecimal price,

        @NotNull
        @PositiveOrZero
        @Schema(example = "5")
        Integer minimumStock,

        @NotNull
        @PositiveOrZero
        @Schema(example = "1")
        @NonNull Long categoryId) {
}
