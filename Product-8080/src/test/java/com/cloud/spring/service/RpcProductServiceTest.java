package com.cloud.spring.service;

import com.cloud.spring.repository.ProductRepository;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.examples.lib.OrderProductRequest;
import net.devh.boot.grpc.examples.lib.Product;
import net.devh.boot.grpc.examples.lib.ProductResultReply;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.Mockito.*;

class RpcProductServiceTest {

    @InjectMocks
    private RpcProductService rpcProductService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StreamObserver<ProductResultReply> responseObserver;

    private Product p1;
    private Product p2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        p1 = Product.newBuilder().setProductName("iPhone").setProductQuantity(2).build();
        p2 = Product.newBuilder().setProductName("iPad").setProductQuantity(1).build();
    }

    @Test
    void testSendOrderToProduct_Success() {
        OrderProductRequest request = OrderProductRequest.newBuilder()
                .addProducts(p1)
                .addProducts(p2)
                .build();

        // No exception thrown by repository mock
        doNothing().when(productRepository).updateProductByProduct_name(anyString(), anyInt());

        rpcProductService.sendOrderToProduct(request, responseObserver);

        // 验证调用
        verify(productRepository, times(1)).updateProductByProduct_name("iPhone", 2);
        verify(productRepository, times(1)).updateProductByProduct_name("iPad", 1);
        verify(responseObserver, times(1)).onNext(argThat(reply -> reply.getIsSucceed()));
        verify(responseObserver, times(1)).onCompleted();
    }

    @Test
    void testSendOrderToProduct_Failure() {
        OrderProductRequest request = OrderProductRequest.newBuilder()
                .addProducts(p1)
                .build();

        // 模拟抛异常
        doThrow(new RuntimeException("DB error")).when(productRepository)
                .updateProductByProduct_name("iPhone", 2);

        rpcProductService.sendOrderToProduct(request, responseObserver);

        verify(responseObserver, never()).onNext(any());
        verify(responseObserver, times(1)).onError(any(StatusRuntimeException.class));
    }
}
