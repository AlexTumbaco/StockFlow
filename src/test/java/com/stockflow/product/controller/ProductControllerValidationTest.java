package com.stockflow.product.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.stockflow.product.service.ProductService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void rejectsClientProvidedStockAndActiveFields() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "Wireless Mouse",
                                  "sku": "MOU-1001",
                                  "price": 29.99,
                                  "minimumStock": 5,
                                  "categoryId": 1,
                                  "stock": 100,
                                  "active": false
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
