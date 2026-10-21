package com.stockflow.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Category representation")
public record CategoryResponse(Long id, String name, String description) {
}
