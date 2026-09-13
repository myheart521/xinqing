package com.hpu.xinqing.utils.sse;

import com.hpu.xinqing.utils.sse.enums.DataType;
import com.hpu.xinqing.utils.sse.enums.FileType;
import com.hpu.xinqing.utils.sse.model.FileInfo;
import com.hpu.xinqing.utils.sse.model.SseMessage;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 文件传输服务
 * 负责生成和传输各种类型的文件到前端
 */
@Service
public class FileTransferService {
    private static final Logger log = LoggerFactory.getLogger(FileTransferService.class);

    private final SseManager sseManager;

    //指定使用的线程池
    @Resource(name = "fileTaskExecutor")
    private TaskExecutor executorService; // 修改为 TaskExecutor 类型

    @Autowired
    public FileTransferService(SseManager sseManager) {
        this.sseManager = sseManager;
    }

    /**
     * 异步生成并发送文件
     *
     * @param userId   用户ID
     * @param content  文件内容
     * @param fileType 文件类型
     * @param fileName 文件名 (可选)
     * @return 是否成功启动传输流程
     */
    public boolean transferFile(Long userId, String content, FileType fileType, String fileName) {
        return transferFileWithEncoding(userId, content, fileType, fileName, false);
    }
    
    /**
     * 异步发送二进制文件（例如Excel）
     * 
     * @param userId   用户ID
     * @param base64Content 已经Base64编码的二进制内容
     * @param fileType 文件类型
     * @param fileName 文件名 (可选)
     * @return 是否成功启动传输流程
     */
    public boolean transferBinaryFile(Long userId, String base64Content, FileType fileType, String fileName) {
        return transferFileWithEncoding(userId, base64Content, fileType, fileName, true);
    }
    
    /**
     * 内部方法：异步生成并发送文件，支持不同的编码方式
     *
     * @param userId   用户ID
     * @param content  文件内容
     * @param fileType 文件类型
     * @param fileName 文件名 (可选)
     * @param isPreEncoded 内容是否已经Base64编码
     * @return 是否成功启动传输流程
     */
    private boolean transferFileWithEncoding(Long userId, String content, FileType fileType, String fileName, boolean isPreEncoded) {
        if (!sseManager.hasActiveConnection(userId)) {
            log.warn("用户 {} 没有活跃的SSE连接，无法传输文件", userId);
            return false;
        }

        // 生成文件名
        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "output_" + System.currentTimeMillis();
        }

        // 确保文件名具有正确的扩展名
        if (!fileName.contains(".")) {
            fileName = fileName + "." + fileType.getExtension();
        }

        // 文件源数据和Base64编码
        byte[] fileData;
        String base64Data;
        
        if (isPreEncoded) {
            // 内容已经是Base64编码，直接使用
            base64Data = content;
            // 为了计算正确的文件大小，需要解码
            fileData = Base64.getDecoder().decode(content);
        } else {
            // 文本内容，需要转换为二进制并编码
            fileData = content.getBytes(StandardCharsets.UTF_8);
            base64Data = Base64.getEncoder().encodeToString(fileData);
        }

        // 计算文件大小
        long fileSize = fileData.length;

        // 创建文件信息对象
        FileInfo fileInfo = new FileInfo(fileName, fileType, fileSize);

        // 启动异步传输任务
        final String finalFileName = fileName;
        final String finalBase64Data = base64Data;
        CompletableFuture.runAsync(() -> {
            try {
                // 发送文件信息事件
                SseMessage fileInfoMsg = new SseMessage(DataType.FILE_INFO, fileInfo);
                boolean infoSent = sseManager.pushEvent(userId, fileInfoMsg);

                if (!infoSent) {
                    log.error("向用户 {} 发送文件信息失败，中止传输", userId);
                    return;
                }

                log.info("向用户 {} 发送文件信息: {}, 大小: {}", userId, finalFileName, fileSize);

                // 发送文件数据事件
                Map<String, Object> fileDataMap = new HashMap<>();
                fileDataMap.put("fileName", finalFileName);
                fileDataMap.put("data", finalBase64Data);
                fileDataMap.put("mimeType", fileType.getMimeType());

                SseMessage fileDataMsg = new SseMessage(DataType.FILE_DATA, fileDataMap);
                boolean dataSent = sseManager.pushEvent(userId, fileDataMsg);

                if (!dataSent) {
                    log.error("向用户 {} 发送文件数据失败，中止传输", userId);
                    return;
                }

                log.info("向用户 {} 发送文件数据完成: {}", userId, finalFileName);

                // 发送下载就绪事件
                Map<String, Object> readyMap = new HashMap<>();
                readyMap.put("fileName", finalFileName);
                readyMap.put("fileType", fileType.name());
                readyMap.put("downloadId", UUID.randomUUID().toString());

                SseMessage readyMsg = new SseMessage(DataType.DOWNLOAD_READY, readyMap);
                boolean readySent = sseManager.pushEvent(userId, readyMsg);

                if (!readySent) {
                    log.error("向用户 {} 发送下载就绪消息失败", userId);
                    return;
                }

                log.info("用户 {} 的文件传输已完成: {}", userId, finalFileName);

            } catch (Exception e) {
                log.error("向用户 {} 传输文件 {} 时发生错误: {}", userId, finalFileName, e.getMessage(), e);

                // 发送错误事件
                Map<String, Object> errorMap = new HashMap<>();
                errorMap.put("message", "文件传输失败: " + e.getMessage());
                errorMap.put("fileName", finalFileName);

                SseMessage errorMsg = new SseMessage(DataType.ERROR, errorMap);
                sseManager.pushEvent(userId, errorMsg);
            }
        }, executorService);

        return true;
    }

    /**
     * 发送文本文件
     */
    public boolean transferTextFile(Long userId, String content, String fileName) {
        return transferFile(userId, content, FileType.TXT, fileName);
    }

    /**
     * 发送Markdown文件
     */
    public boolean transferMarkdownFile(Long userId, String content, String fileName) {
        return transferFile(userId, content, FileType.MD, fileName);
    }

    /**
     * 发送JSON文件
     */
    public boolean transferJsonFile(Long userId, String content, String fileName) {
        return transferFile(userId, content, FileType.JSON, fileName);
    }

    /**
     * 发送XML文件
     */
    public boolean transferXmlFile(Long userId, String content, String fileName) {
        return transferFile(userId, content, FileType.XML, fileName);
    }

    /**
     * 发送CSV文件
     */
    public boolean transferCsvFile(Long userId, String content, String fileName) {
        return transferFile(userId, content, FileType.CSV, fileName);
    }
}
