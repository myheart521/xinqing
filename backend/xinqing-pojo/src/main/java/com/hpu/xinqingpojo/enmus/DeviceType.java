package com.hpu.xinqingpojo.enmus;

import lombok.Getter;

@Getter
public enum DeviceType {
    Running("running"),
    Body("body"),
    smartWatch("smartWatch");

    private String type;

    DeviceType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
