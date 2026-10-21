package com.stockflow.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Category data")
public record CategoryRequest(
        @NotBlank
        @Size(max = 120)
        @Schema(example = "Electronics")
        String name,

        @Size(max = 1000)
        @Schema(example = "Electronic devices and accessories")
        String description) {
}
