package com.stockflow.product.dto;

import java.math.BigDecimal;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductRequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void rejectsBlankNameNegativePriceAndNegativeMinimumStock() {
        ProductRequest request = new ProductRequest(" ", "SKU-1", null,
                new BigDecimal("-0.01"), -1, 1L);

        var violations = validator.validate(request);

        assertTrue(violations.stream().anyMatch(error -> error.getPropertyPath().toString().equals("name")));
        assertTrue(violations.stream().anyMatch(error -> error.getPropertyPath().toString().equals("price")));
        assertTrue(violations.stream().anyMatch(error ->
                error.getPropertyPath().toString().equals("minimumStock")));
    }
}
