package com.cloud.spring.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @Test
    void testCreateAndVerifyToken() {
        String username = "alice";

        // 创建 Token
        String token = JwtUtil.createJwtToken(username);
        assertNotNull(token);

        // 验证 Token
        boolean isValid = JwtUtil.verifyToken(token);
        assertTrue(isValid);

        // 提取信息
        String extractedUser = JwtUtil.getUserInfoFromJWT(token);
        assertEquals(username, extractedUser);
    }

    @Test
    void testInvalidTokenVerification() {
        String invalidToken = "invalid.jwt.token";

        boolean isValid = JwtUtil.verifyToken(invalidToken);
        assertFalse(isValid);
    }

    @Test
    void testExpiredToken() {
        // 手动创建过期的 token
        String expiredToken = com.auth0.jwt.JWT.create()
                .withExpiresAt(java.time.Instant.now().minusSeconds(10))
                .withIssuer("LP")
                .withClaim("user", "bob")
                .sign(com.auth0.jwt.algorithms.Algorithm.HMAC256("bWljcm9zZXJ2aWNlcw"));

        boolean isValid = JwtUtil.verifyToken(expiredToken);
        assertFalse(isValid);
    }
}