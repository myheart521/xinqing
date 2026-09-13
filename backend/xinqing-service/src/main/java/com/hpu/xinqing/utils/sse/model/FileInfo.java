package com.hpu.xinqing.utils.sse.model;

import com.hpu.xinqing.utils.sse.enums.FileType;
import lombok.Getter;
import lombok.Setter;

/**
 * 文件信息模型
 */
@Getter
@Setter
public class FileInfo {
    private String fileName;
    private FileType fileType;
    private long fileSize;
    private String mimeType;
    private boolean isBase64;

    public FileInfo() {
    }

    public FileInfo(String fileName, FileType fileType, long fileSize) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.mimeType = fileType.getMimeType();
        this.isBase64 = true; // 默认使用Base64编码
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
        this.mimeType = fileType.getMimeType();
    }

    /**
     * 构建完整的文件名（包含扩展名）
     */
    public String getFullFileName() {
        if (fileName.contains(".")) {
            return fileName;
        }
        return fileName + "." + fileType.getExtension();
    }
}
