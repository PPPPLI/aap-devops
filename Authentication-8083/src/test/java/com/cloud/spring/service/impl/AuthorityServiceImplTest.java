package com.cloud.spring.service.impl;

import com.cloud.spring.entity.vo.Authority;
import com.cloud.spring.repository.AuthorityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class AuthorityServiceImplTest {

    @Mock
    private AuthorityRepository authorityRepository;

    @InjectMocks
    private AuthorityServiceImpl authorityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // 初始化 @Mock 和 @InjectMocks
    }

    @Test
    void testGetAuthority() {
        // Arrange
        String authorityName = "ROLE_USER";
        Authority expectedAuthority = new Authority();
        expectedAuthority.setAuthorityName(authorityName);

        when(authorityRepository.findByAuthorityName(authorityName)).thenReturn(expectedAuthority);

        // Act
        Authority result = authorityService.getAuthority(authorityName);

        // Assert
        assertNotNull(result);
        assertEquals(authorityName, result.getAuthorityName());
        verify(authorityRepository, times(1)).findByAuthorityName(authorityName);
    }
}