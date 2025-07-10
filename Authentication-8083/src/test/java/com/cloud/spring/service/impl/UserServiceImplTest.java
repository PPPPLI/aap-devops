package com.cloud.spring.service.impl;

import com.cloud.spring.entity.vo.Authority;
import com.cloud.spring.entity.vo.User;
import com.cloud.spring.repository.AuthorityRepository;
import com.cloud.spring.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthorityRepository authorityRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUser() {
        String username = "alice";
        User mockUser = new User();
        mockUser.setUsername(username);

        when(userRepository.findUserByUsername(username)).thenReturn(mockUser);

        User result = userService.getUser(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userRepository, times(1)).findUserByUsername(username);
    }

    @Test
    void testAddUser() {
        User newUser = new User();
        newUser.setUsername("bob");
        newUser.setPassword("securePassword");

        Authority authority = new Authority();
        authority.setAuthorityName("user");

        when(authorityRepository.findByAuthorityName("user")).thenReturn(authority);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.addUser(newUser);

        assertNotNull(result);
        assertEquals("bob", result.getUsername());
        assertEquals("securePassword", result.getPassword());
        assertEquals(1, result.getAuthorities().size());
        assertTrue(result.getAuthorities().stream().anyMatch(a -> a.getAuthorityName().equals("user")));

        verify(authorityRepository, times(1)).findByAuthorityName("user");
        verify(userRepository, times(1)).save(newUser);
    }
}