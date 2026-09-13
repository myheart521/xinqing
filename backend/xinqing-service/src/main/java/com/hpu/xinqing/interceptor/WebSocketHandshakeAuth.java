package com.hpu.xinqing.interceptor;

public final class WebSocketHandshakeAuth {

    private static final String BEARER_PREFIX = "Bearer ";

    private WebSocketHandshakeAuth() {
    }

    public static String firstText(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (hasText(value)) {
                return value.trim();
            }
        }
        return null;
    }

    public static String normalizeToken(String token) {
        if (!hasText(token)) {
            return null;
        }

        String normalized = token.trim();
        if ("Bearer".equalsIgnoreCase(normalized)) {
            return null;
        }
        if (normalized.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            normalized = normalized.substring(BEARER_PREFIX.length()).trim();
        }
        return hasText(normalized) ? normalized : null;
    }

    public static Long resolveSenderId(String token, LoginIdReader tokenMappingReader, LoginIdReader jwtReader) {
        String normalizedToken = normalizeToken(token);
        if (normalizedToken == null) {
            return null;
        }

        Long loginId = readLoginId(normalizedToken, tokenMappingReader);
        if (loginId != null) {
            return loginId;
        }

        return readLoginId(normalizedToken, jwtReader);
    }

    public static boolean isSenderIdConsistent(Long authenticatedSenderId, String senderIdParameter) {
        if (authenticatedSenderId == null) {
            return false;
        }
        if (!hasText(senderIdParameter)) {
            return true;
        }
        Long senderId = parseLongOrNull(senderIdParameter);
        return authenticatedSenderId.equals(senderId);
    }

    public static Long parseLongOrNull(Object value) {
        if (value == null) {
            return null;
        }
        String text = value.toString().trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static Long readLoginId(String token, LoginIdReader reader) {
        try {
            return parseLongOrNull(reader.read(token));
        } catch (Exception ignored) {
            return null;
        }
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    @FunctionalInterface
    public interface LoginIdReader {
        Object read(String token) throws Exception;
    }
}
