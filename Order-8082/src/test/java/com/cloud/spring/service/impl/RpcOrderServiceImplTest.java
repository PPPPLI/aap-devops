package com.cloud.spring.service.impl;


import net.devh.boot.grpc.examples.lib.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RpcOrderServiceImplTest {

    @InjectMocks
    private RpcOrderServiceImpl rpcOrderService;

    @Mock
    private OrderServiceGrpc.OrderServiceBlockingStub orderServiceBlockingStub;

    @Mock
    private ProductOrderServiceGrpc.ProductOrderServiceBlockingStub productOrderServiceBlockingStub;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testOrderToPayment() {
        String user = "alice";
        double price = 99.9;
        String orderId = "order123";

        PaymentResultReply mockReply = PaymentResultReply.newBuilder()
                .setPaymentId("pay001")
                .build();

        when(orderServiceBlockingStub.sendOrderToPayment(any())).thenReturn(mockReply);

        PaymentResultReply result = rpcOrderService.OrderToPayment(user, price, orderId);

        assertNotNull(result);
        assertEquals("pay001", result.getPaymentId());

        verify(orderServiceBlockingStub, times(1)).sendOrderToPayment(any());
    }

    @Test
    void testDeletePayment() {
        String paymentId = "pay001";
        PaymentResultReply mockReply = PaymentResultReply.newBuilder()
                .setPaymentId(paymentId)
                .build();

        when(orderServiceBlockingStub.deletePayment(any())).thenReturn(mockReply);

        PaymentResultReply result = rpcOrderService.deletePayment(paymentId);

        assertEquals(paymentId, result.getPaymentId());

        verify(orderServiceBlockingStub, times(1)).deletePayment(any());
    }

    @Test
    void testOrderToProduct() {
        // Arrange：构造 JPA 实体 Product 对象
        com.cloud.spring.sharedEntity.Product p1 = com.cloud.spring.sharedEntity.Product.builder()
                .productName("MacBook")
                .productQuantity(2)
                .build();

        com.cloud.spring.sharedEntity.Product p2 = com.cloud.spring.sharedEntity.Product.builder()
                .productName("iPhone")
                .productQuantity(5)
                .build();

        List<com.cloud.spring.sharedEntity.Product> products = List.of(p1, p2);

        ProductResultReply mockReply = ProductResultReply.newBuilder()
                .build();

        when(productOrderServiceBlockingStub.sendOrderToProduct(any())).thenReturn(mockReply);

        // Act
        ProductResultReply result = rpcOrderService.OrderToProduct(products);

        // Assert
        verify(productOrderServiceBlockingStub, times(1)).sendOrderToProduct(any());
    }
}