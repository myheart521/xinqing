package com.hpu.xinqing.service.ai;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/** Signs the fixed vendor endpoint; it never contacts the speech service. */
public final class XfAsrSigner {
    private static final String HOST = "iat-api.xfyun.cn";
    private XfAsrSigner() {}

    public record Session(String url, String appId, long expiresAt) {}

    public static Session sign(String appId, String apiKey, String apiSecret, Instant now) {
        if (appId == null || appId.isBlank() || apiKey == null || apiKey.isBlank()
                || apiSecret == null || apiSecret.isBlank()) {
            throw new IllegalArgumentException("Speech service credentials are not configured");
        }
        if (apiKey.contains("\"") || apiKey.contains("\r") || apiKey.contains("\n")) {
            throw new IllegalArgumentException("Invalid speech credential identifier");
        }
        try {
            String date = DateTimeFormatter.RFC_1123_DATE_TIME.format(now.atZone(ZoneId.of("GMT")));
            String canonical = "host: " + HOST + "\ndate: " + date + "\nGET /v2/iat HTTP/1.1";
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            String signature = Base64.getEncoder().encodeToString(mac.doFinal(canonical.getBytes(StandardCharsets.UTF_8)));
            String authorization = "api_key=\"" + apiKey + "\", algorithm=\"hmac-sha256\", headers=\"host date request-line\", signature=\"" + signature + "\"";
            String encoded = Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8));
            String url = "wss://" + HOST + "/v2/iat?authorization=" + encode(encoded)
                    + "&date=" + encode(date) + "&host=" + HOST;
            return new Session(url, appId, now.plusSeconds(300).getEpochSecond());
        } catch (java.security.GeneralSecurityException error) {
            throw new IllegalStateException("Speech authorization could not be generated", error);
        }
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
