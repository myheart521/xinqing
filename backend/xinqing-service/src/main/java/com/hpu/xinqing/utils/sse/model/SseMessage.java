package com.hpu.xinqing.utils.sse.model;

import com.hpu.xinqing.utils.sse.enums.DataType;

import java.util.Map;
import java.util.UUID;

/**
 * SSE消息模型
 */
public class SseMessage {
    private String id;
    private DataType eventType;
    private Object data;
    private Map<String, Object> metadata;

    public SseMessage() {
        this.id = UUID.randomUUID().toString();
    }

    public SseMessage(DataType eventType, Object data) {
        this.id = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.data = data;
    }

    public SseMessage(DataType eventType, Object data, Map<String, Object> metadata) {
        this.id = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.data = data;
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DataType getEventType() {
        return eventType;
    }

    public void setEventType(DataType eventType) {
        this.eventType = eventType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
