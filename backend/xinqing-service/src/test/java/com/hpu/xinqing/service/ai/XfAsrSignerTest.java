package com.hpu.xinqing.service.ai;

import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;

class XfAsrSignerTest {
    @Test
    void producesFixedEndpointAuthorizationWithoutReturningTheSecret() {
        Instant time = Instant.parse("2026-01-02T03:04:05Z");
        var session = XfAsrSigner.sign("test-app", "test-key", "test-secret", time);
        URI uri = URI.create(session.url());
        assertEquals("wss", uri.getScheme());
        assertEquals("iat-api.xfyun.cn", uri.getHost());
        assertEquals("/v2/iat", uri.getPath());
        String query = URLDecoder.decode(uri.getRawQuery(), StandardCharsets.UTF_8);
        String encoded = query.substring("authorization=".length(), query.indexOf("&date="));
        String authorization = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        assertTrue(authorization.contains("api_key=\"test-key\""));
        assertTrue(authorization.contains("headers=\"host date request-line\""));
        assertFalse(authorization.contains("test-secret"));
        assertEquals(time.plusSeconds(300).getEpochSecond(), session.expiresAt());
        assertEquals("test-app", session.appId());
        assertEquals(session, XfAsrSigner.sign("test-app", "test-key", "test-secret", time));
    }

    @Test
    void rejectsMissingAndHeaderInjectionCredentials() {
        assertThrows(IllegalArgumentException.class, () -> XfAsrSigner.sign("", "", "", Instant.EPOCH));
        assertThrows(IllegalArgumentException.class, () -> XfAsrSigner.sign("app", "bad\r\nkey", "secret", Instant.EPOCH));
    }
}
