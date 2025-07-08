package com.cloud.spring.service.impl;

import com.cloud.spring.Main8084;
import com.cloud.spring.service.ProductService;
import com.cloud.spring.sharedEntity.Product;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import com.cloud.spring.repository.ProductRepository;
import com.cloud.spring.service.impl.ProductServiceImpl;
import com.cloud.spring.sharedEntity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleProduct = Product.builder()
                .productId(UUID.randomUUID())
                .productName("MacBook")
                .productDescription("Apple laptop")
                .productPrice(1299.99)
                .productQuantity(10)
                .productUrl("https://example.com/macbook.jpg")
                .build();
    }

    @Test
    void testAddProduct() {
        when(productRepository.save(sampleProduct)).thenReturn(sampleProduct);

        Product result = productService.addProduct(sampleProduct);

        assertNotNull(result);
        assertEquals("MacBook", result.getProductName());
        verify(productRepository, times(1)).save(sampleProduct);
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(sampleProduct));

        List<Product> result = productService.getAllProducts();

        assertEquals(1, result.size());
        assertEquals("MacBook", result.get(0).getProductName());
        verify(productRepository, times(1)).findAll();
    }
}