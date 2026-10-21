package com.stockflow.category.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryRequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void rejectsBlankCategoryName() {
        var violations = validator.validate(new CategoryRequest("  ", null));

        assertTrue(violations.stream().anyMatch(error -> error.getPropertyPath().toString().equals("name")));
    }
}
