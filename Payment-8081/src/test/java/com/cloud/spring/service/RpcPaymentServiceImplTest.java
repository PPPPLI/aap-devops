package com.cloud.spring.service;

import com.cloud.spring.repository.PaymentRepository;
import com.cloud.spring.sharedEntity.OrderForm;
import com.cloud.spring.vo.Payment;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.examples.lib.DeleteRequest;
import net.devh.boot.grpc.examples.lib.OrderPaymentRequest;
import net.devh.boot.grpc.examples.lib.PaymentResultReply;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RpcPaymentServiceImplTest {

    @InjectMocks
    private RpcPaymentServiceImpl rpcPaymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private StreamObserver<PaymentResultReply> responseObserver;

    private OrderPaymentRequest orderRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        orderRequest = OrderPaymentRequest.newBuilder()
                .setUserName("alice")
                .setOrderId(UUID.randomUUID().toString())
                .setOrderPrice(120.5)
                .build();
    }

    @Test
    void testSendOrderToPayment_Success() {
        // Arrange
        Payment savedPayment = Payment.builder()
                .paymentId(UUID.randomUUID())
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        // Act
        rpcPaymentService.sendOrderToPayment(orderRequest, responseObserver);

        // Assert
        verify(paymentRepository, times(1)).save(any());
        verify(responseObserver, times(1)).onNext(argThat(reply ->
                reply.getOrderPaid() && reply.getPaymentId().equals(savedPayment.getPaymentId().toString())));
        verify(responseObserver, times(1)).onCompleted();
    }

    @Test
    void testSendOrderToPayment_Failure() {
        when(paymentRepository.save(any())).thenThrow(new RuntimeException("DB Error"));

        rpcPaymentService.sendOrderToPayment(orderRequest, responseObserver);

        verify(responseObserver, times(1)).onNext(argThat(reply ->
                !reply.getOrderPaid()));
        verify(responseObserver, times(1)).onCompleted();
    }

    @Test
    void testDeletePayment() {
        String paymentId = UUID.randomUUID().toString();
        DeleteRequest deleteRequest = DeleteRequest.newBuilder()
                .setPaymentId(paymentId)
                .build();

        rpcPaymentService.deletePayment(deleteRequest, responseObserver);

        verify(paymentRepository, times(1)).deleteById(UUID.fromString(paymentId));
        verify(responseObserver, times(1)).onNext(argThat(reply -> reply.getOrderPaid()));
        verify(responseObserver, times(1)).onCompleted();
    }
}