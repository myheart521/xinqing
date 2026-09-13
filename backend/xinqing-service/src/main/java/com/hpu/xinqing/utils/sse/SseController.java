package com.hpu.xinqing.utils.sse;

import com.hpu.xinqing.utils.sse.enums.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * SSE文件传输控制器
 */
@RestController
@RequestMapping("/api/sse1")
public class SseController {
    private static final Logger log = LoggerFactory.getLogger(SseController.class);

    private final SseManager sseManager;
    private final FileTransferService fileTransferService;

    @Autowired
    public SseController(SseManager sseManager, FileTransferService fileTransferService) {
        this.sseManager = sseManager;
        this.fileTransferService = fileTransferService;
    }

    /**
     * 建立WebFlux风格的SSE连接
     *
     * @param userId 用户ID
     * @return SSE事件流
     */
    @GetMapping(path = "/connect/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Object>> connect(@PathVariable Long userId) {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        lock.unlock();
        log.info("接收到用户 {} 的SSE连接请求", userId);
        return sseManager.createConnection(userId);
    }

    /**
     * 建立MVC风格的SSE连接
     *
     * @param userId 用户ID
     * @return SseEmitter实例
     */
    @GetMapping("/connect-mvc/{userId}")
    public SseEmitter connectMvc(@PathVariable Long userId) {
        log.info("接收到用户 {} 的MVC风格SSE连接请求", userId);
        return sseManager.createMvcConnection(userId);
    }

    /**
     * 关闭SSE连接
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/disconnect/{userId}")
    public ResponseEntity<Map<String, Object>> disconnect(@PathVariable Long userId) {
        log.info("接收到关闭用户 {} 的SSE连接请求", userId);

        boolean hasConnection = sseManager.hasActiveConnection(userId);
        if (!hasConnection) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "success", false,
                            "message", "未找到用户的活跃连接",
                            "userId", userId
                    ));
        }

        sseManager.closeConnection(userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "连接已关闭",
                "userId", userId
        ));
    }

    /**
     * 生成并传输文件测试的接口，可以在其他地方仿照此接口调用文件生成api传递给前端
     *
     * @param userId   用户ID
     * @param content  文件内容
     * @param fileType 文件类型
     * @param fileName 文件名（可选）
     * @return 操作结果
     */
    @PostMapping("/transfer")
    public ResponseEntity<Map<String, Object>> transferFile(
            @RequestParam Long userId,
            @RequestParam String content,
            @RequestParam FileType fileType,
            @RequestParam(required = false) String fileName
    ) {
        log.info("接收到文件传输请求: userId={}, fileType={}, fileName={}, contentLength={}",
                userId, fileType, fileName, content.length());

        // 检查连接状态
        if (!sseManager.hasActiveConnection(userId)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "未找到活跃的SSE连接，请先连接",
                    "userId", userId
            ));
        }

        // 启动文件传输
        boolean started = fileTransferService.transferFile(userId, content, fileType, fileName);

        //这是文件上传失败的返回
        if (!started) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "启动文件传输失败",
                    "userId", userId
            ));
        }

        // 构建响应内容，这是上传成功的返回
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "文件传输已开始");
        response.put("userId", userId);
        response.put("fileType", fileType.name());
        if (fileName != null && !fileName.isEmpty()) {
            response.put("fileName", fileName);
        }

        return ResponseEntity.accepted().body(response);
    }

    /**
     * 测试连接状态
     *
     * @param userId 用户ID
     * @return 连接状态
     */
    @GetMapping("/status/{userId}")
    public ResponseEntity<Map<String, Object>> checkStatus(@PathVariable Long userId) {
        boolean hasConnection = sseManager.hasActiveConnection(userId);

        return ResponseEntity.ok(Map.of(
                "connected", hasConnection,
                "userId", userId
        ));
    }
}
