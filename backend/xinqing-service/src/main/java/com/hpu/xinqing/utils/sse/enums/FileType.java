package com.hpu.xinqing.utils.sse.enums;

import org.springframework.http.MediaType;

/**
 * 支持的文件类型枚举
 */
public enum FileType {
    TXT("txt", MediaType.TEXT_PLAIN_VALUE),
    MD("md", "text/markdown"),
    EXCEL("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    WORD("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    PDF("pdf", "application/pdf"),
    JSON("json", MediaType.APPLICATION_JSON_VALUE),
    XML("xml", MediaType.APPLICATION_XML_VALUE),
    CSV("csv", "text/csv");

    private final String extension;
    private final String mimeType;

    FileType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    /**
     * 根据文件名或扩展名获取文件类型
     */
    public static FileType fromFilename(String filename) {
        String ext = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        for (FileType type : values()) {
            if (type.extension.equalsIgnoreCase(ext)) {
                return type;
            }
        }
        return TXT; // 默认为文本文件
    }
}
