package com.hpu.xinqing.manage.ClaudeManager.service.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MentalAnalysisServiceImplTest {

    @Test
    void extractJsonPayloadShouldReturnPlainJsonUnchanged() {
        String rawJson = "{\"summary\":\"ok\"}";

        assertEquals(rawJson, MentalAnalysisServiceImpl.extractJsonPayload(rawJson));
    }

    @Test
    void extractJsonPayloadShouldStripJsonCodeFence() {
        String fencedJson = """
                ```json
                {"summary":"ok"}
                ```
                """;

        assertEquals("{\"summary\":\"ok\"}", MentalAnalysisServiceImpl.extractJsonPayload(fencedJson));
    }
}
