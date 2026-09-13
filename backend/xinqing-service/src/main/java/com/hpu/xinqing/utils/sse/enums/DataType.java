package com.hpu.xinqing.utils.sse.enums;

/**
 * SSE传输的数据类型枚举
 */
public enum DataType {
    // 连接状态
    CONNECT,
    // 文件信息
    FILE_INFO,
    // 文件数据（Base64编码）
    FILE_DATA,
    // 文件下载准备就绪
    DOWNLOAD_READY,
    // 错误信息
    ERROR,
    // 进度更新
    PROGRESS
}
