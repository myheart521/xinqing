package com.hpu.xinqing.interceptor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebSocketHandshakeAuthTest {

    @Test
    void resolvesSenderIdFromSaTokenMappingFirst() {
        Long senderId = WebSocketHandshakeAuth.resolveSenderId(
                "token",
                ignored -> "14",
                ignored -> "15"
        );

        assertEquals(14L, senderId);
    }

    @Test
    void resolvesSenderIdFromJwtPayloadWhenTokenMappingIsMissing() {
        Long senderId = WebSocketHandshakeAuth.resolveSenderId(
                "jwt-token",
                ignored -> null,
                ignored -> 14
        );

        assertEquals(14L, senderId);
    }

    @Test
    void normalizesBearerPrefixBeforeResolving() {
        Long senderId = WebSocketHandshakeAuth.resolveSenderId(
                "Bearer jwt-token",
                ignored -> null,
                ignored -> 14
        );

        assertEquals(14L, senderId);
    }

    @Test
    void invalidTokenValuesReturnNullInsteadOfThrowing() {
        assertNull(WebSocketHandshakeAuth.resolveSenderId(null, ignored -> "14", ignored -> "14"));
        assertNull(WebSocketHandshakeAuth.resolveSenderId("token", ignored -> null, ignored -> null));
        assertNull(WebSocketHandshakeAuth.resolveSenderId("token", ignored -> "abc", ignored -> "def"));
    }

    @Test
    void validatesOptionalSenderIdParameterAgainstAuthenticatedUser() {
        assertTrue(WebSocketHandshakeAuth.isSenderIdConsistent(14L, null));
        assertTrue(WebSocketHandshakeAuth.isSenderIdConsistent(14L, ""));
        assertTrue(WebSocketHandshakeAuth.isSenderIdConsistent(14L, "14"));
        assertFalse(WebSocketHandshakeAuth.isSenderIdConsistent(14L, "24"));
        assertFalse(WebSocketHandshakeAuth.isSenderIdConsistent(14L, "abc"));
    }

    @Test
    void parsesReceiverIdWithoutThrowing() {
        assertEquals(24L, WebSocketHandshakeAuth.parseLongOrNull("24"));
        assertNull(WebSocketHandshakeAuth.parseLongOrNull(null));
        assertNull(WebSocketHandshakeAuth.parseLongOrNull(""));
        assertNull(WebSocketHandshakeAuth.parseLongOrNull("abc"));
    }

    @Test
    void choosesFirstNonBlankValueAndNormalizesBearerToken() {
        assertEquals("abc", WebSocketHandshakeAuth.firstText(null, " ", "abc", "def"));
        assertEquals("abc", WebSocketHandshakeAuth.normalizeToken("Bearer abc"));
        assertNull(WebSocketHandshakeAuth.normalizeToken("Bearer   "));
    }
}
