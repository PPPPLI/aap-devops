package com.cloud.spring.service.impl;

import com.cloud.spring.repository.OrderRepository;
import com.cloud.spring.sharedEntity.OrderForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    private OrderForm sampleOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleOrder = new OrderForm();
        sampleOrder.setOrderId(UUID.randomUUID());
        sampleOrder.setOrderDate(LocalDateTime.of(2025, 7, 8, 10, 0));
        sampleOrder.setOrderOwnerName("alice");
    }

    @Test
    void testFindOrderByDateAndCustomer() {
        when(orderRepository.findOrderByOrderDateAndOrderOwnerName(any(), any()))
                .thenReturn(List.of(sampleOrder));

        List<OrderForm> result = orderService.findOrderByDateAndCustomer(sampleOrder.getOrderDate(), "alice");

        assertEquals(1, result.size());
        assertEquals("alice", result.get(0).getOrderOwnerName());
        verify(orderRepository).findOrderByOrderDateAndOrderOwnerName(sampleOrder.getOrderDate(), "alice");
    }

    @Test
    void testFindOrderByCustomer() {
        when(orderRepository.findOrderByOrderOwnerName("alice"))
                .thenReturn(List.of(sampleOrder));

        List<OrderForm> result = orderService.findOrderByCustomer("alice");

        assertEquals(1, result.size());
        assertEquals("alice", result.get(0).getOrderOwnerName());
        verify(orderRepository).findOrderByOrderOwnerName("alice");
    }

    @Test
    void testSaveOrder() {
        when(orderRepository.save(any())).thenReturn(sampleOrder);

        OrderForm result = orderService.saveOrder(sampleOrder);

        assertNotNull(result);
        assertEquals("alice", result.getOrderOwnerName());
        verify(orderRepository).save(sampleOrder);
    }

    @Test
    void testDeleteOrder() {
        UUID id = sampleOrder.getOrderId();
        when(orderRepository.deleteOrderByOrderId(id)).thenReturn(sampleOrder);

        OrderForm result = orderService.deleteOrder(id);

        assertNotNull(result);
        assertEquals(id, result.getOrderId());
        verify(orderRepository).deleteOrderByOrderId(id);
    }
}
