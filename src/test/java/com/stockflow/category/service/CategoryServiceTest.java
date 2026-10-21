package com.stockflow.category.service;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.stockflow.category.dto.CategoryRequest;
import com.stockflow.category.entity.Category;
import com.stockflow.category.repository.CategoryRepository;
import com.stockflow.common.exception.ConflictException;
import com.stockflow.common.exception.ResourceNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("null")
class CategoryServiceTest {

    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final CategoryService categoryService = new CategoryService(categoryRepository);

    @Test
    void rejectsDuplicateCategoryName() {
        when(categoryRepository.existsByName("Electronics")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> categoryService.create(new CategoryRequest("Electronics", null)));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void trimsCategoryNameBeforeSaving() {
        when(categoryRepository.existsByName("Electronics")).thenReturn(false);
        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = categoryService.create(new CategoryRequest(" Electronics ", null));

        assertEquals("Electronics", response.name());
    }

    @Test
    void reportsMissingCategory() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.findById(99L));
    }
}
