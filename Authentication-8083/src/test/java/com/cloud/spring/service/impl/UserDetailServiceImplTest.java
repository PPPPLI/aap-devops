package com.cloud.spring.service.impl;

import com.cloud.spring.entity.vo.Authority;
import com.cloud.spring.entity.vo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailServiceImplTest {

    @InjectMocks
    private UserDetailServiceImpl userDetailService;

    @Mock
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_UserExists() {
        // Arrange
        String username = "testuser";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPassword("hashedPassword");
        mockUser.setAuthorities(Set.of(Authority.builder().authorityName("ROLE_USER").build()));

        when(userService.getUser(username)).thenReturn(mockUser);

        // Act
        UserDetails result = userDetailService.loadUserByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("hashedPassword", result.getPassword());
        assertTrue(result.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));

        verify(userService, times(1)).getUser(username);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        String username = "unknown";

        when(userService.getUser(username)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailService.loadUserByUsername(username);
        });

        verify(userService, times(1)).getUser(username);
    }
}