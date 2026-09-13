package com.hpu.xinqing.manage.TeacherAgent.tools;

import cn.hutool.core.util.StrUtil;
import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JsoupCrawlerTools {
    // TLS 配置增强
    private static final SSLContext sslContext;

    static {
        try {
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }}, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("SSL上下文初始化失败", e);
        }
    }

    // 优化线程池（支持TLS握手密集型任务）
    private static final ExecutorService executor =
            new ThreadPoolExecutor(
                    20, 50,
                    180L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(1000),
                    new ThreadFactory() {
                        private final AtomicInteger count = new AtomicInteger();

                        @Override
                        public Thread newThread(Runnable r) {
                            return new Thread(r, "Jsoup-Worker-" + count.incrementAndGet());
                        }
                    },
                    new ThreadPoolExecutor.AbortPolicy()
            );

    @Tool("高性能网页内容抓取工具，支持批量处理、智能重试及内容清洗")
    public String getWebContent(List<String> urls, Boolean isPublish, Boolean isExport) {
        // 防御性编程
        if (urls == null || urls.isEmpty()) return "ERROR: URL列表为空";
        if (urls.size() > 500) return "ERROR: 单次最大处理500个URL";

        ConcurrentHashMap<String, String> contentMap = new ConcurrentHashMap<>();
        ConcurrentLinkedQueue<String> errorQueue = new ConcurrentLinkedQueue<>();
        AtomicInteger successCount = new AtomicInteger();

        // 并行抓取（带短路保护）
        List<CompletableFuture<Void>> tasks = urls.stream().map(url ->
                CompletableFuture.runAsync(() ->
                                new SmartCrawler(url, 3).fetch(contentMap, successCount, errorQueue),
                        executor
                )
        ).collect(Collectors.toList());

        try {
            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]))
                    .get(3, TimeUnit.MINUTES);
        } catch (Exception e) {
            errorQueue.addAll(urls.stream()
                    .filter(u -> !contentMap.containsKey(u))
                    .collect(Collectors.toList()));
        }

        // 后处理流程
        processPipeline(contentMap, isPublish, isExport);

        return generateReport(urls.size(), successCount.get(), errorQueue);
    }


    @Tool("""
            该工具是一个地图的智能体，具有以下功能，提供全场景覆盖的地理信息服务，
            包括地理编码、逆地理编码、IP定位、天气查询、骑行路径规划、步行路径规划、驾车路径规划、
            公交路径规划、距离测量、关键词搜索、周边搜索、详情搜索等。当需要上述功能时直接传入问题""")
    public String getGaoDeMCP(@P("需求描述")String description){
        String response=null;
        try {
            response=getBaiLian(description);
        } catch (Exception e) {
            log.error("调用百炼MCP异常："+e);
        }
        if (StrUtil.isEmpty(response)){
            return "抱歉，我没有找到相关信息";
        }
        return response;
    }

    /**
     * 智能爬取引擎（内置SSL/TLS优化）
     */
    private class SmartCrawler {
        private final String url;
        private final int maxRetries;

        public SmartCrawler(String url, int maxRetries) {
            this.url = url;
            this.maxRetries = maxRetries;
        }

        public void fetch(ConcurrentHashMap<String, String> map,
                          AtomicInteger counter,
                          ConcurrentLinkedQueue<String> errors) {
            for (int attempt = 1; attempt <= maxRetries; attempt++) {
                try {
                    Document doc = Jsoup.connect(url)
                            .sslSocketFactory(sslContext.getSocketFactory()) // 强制TLSv1.2
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.1 Safari/537.36 Edg/127.0.0.1")
                            .timeout(20000)
                            .ignoreContentType(true)

                            .execute()
                            .parse();

                    // 智能内容清洗
                    String cleanContent = Jsoup.clean(
                            doc.body().html(),
                            Safelist.relaxed()
                                    .addTags("article", "section") // 保留语义标签
                                    .addAttributes("div", "class")
                    );
                    map.put(url, processContent(cleanContent));
                    counter.incrementAndGet();
                    return;
                } catch (IOException ex) {
                    if (attempt == maxRetries) {
                        errors.add(url + "【错误：" + ex.getMessage() + "】");
                    }
                    backoff(attempt);
                }
            }
        }

        private void backoff(int attempt) {
            try {
                Thread.sleep((long) Math.pow(2, attempt) * 1000 +
                        ThreadLocalRandom.current().nextInt(1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 生产级内容处理
     */
    private String processContent(String rawHtml) {
        // HTML到文本的精炼处理
        return Jsoup.parse(rawHtml).text()  // 去HTML标签
                .replaceAll("\\s{2,}", " ")    // 合并空格
                .replaceAll("[\\x00-\\x1F]", "") // 清除控制字符
                .trim();
    }

    /**
     * 异步管道处理
     */
    private void processPipeline(ConcurrentHashMap<String, String> data,
                                 Boolean publishFlag,
                                 Boolean exportFlag) {
        CompletableFuture.runAsync(() -> {
            if (Boolean.TRUE.equals(publishFlag)) {
                data.forEach((url, content) ->
                        System.out.println("发布[" + url + "]: " + content.substring(0, Math.min(50, content.length())) + "...")
                );
            }
        }, executor);

        CompletableFuture.runAsync(() -> {
            if (Boolean.TRUE.equals(exportFlag)) {
                data.forEach((url, content) ->
                        System.out.println("导出[" + url + "]到文件:" + url.hashCode() + ".txt")
                );
            }
        }, executor);
    }

    /**
     * 生成结构化报告
     */
    private String generateReport(int total, int success, ConcurrentLinkedQueue<String> errors) {
        String stats = String.format("""
                        ===== 抓取统计 =====
                        总数：%d
                        成功：%d
                        失败：%d
                        成功率：%.2f%%
                        """,
                total, success, total - success,
                (success * 100.0) / total
        );

        String detail = errors.isEmpty() ? "" : "\n===== 失败详情 =====\n" +
                errors.stream().limit(10).collect(Collectors.joining("\n")) +
                (errors.size() > 10 ? "\n...（更多失败记录省略）" : "");

        return stats + detail;
    }

    /**
     * 安全关闭钩子
     */
    public void shutdownGracefully() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(120, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public String getBaiLian(String question)
            throws ApiException, NoApiKeyException, InputRequiredException {
        ApplicationParam param = ApplicationParam.builder()
                // 若没有配置环境变量，可用百炼API Key将下行替换为：.apiKey(System.getenv("DASHSCOPE_API_KEY"))。但不建议在生产环境中直接将API Key硬编码到代码中，以减少API Key泄露风险。
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .appId(System.getenv("DASHSCOPE_APP_ID"))
                .prompt(question)
                .build();

        Application application = new Application();
        ApplicationResult result = application.call(param);

        return result.getOutput().getText();
    }
}
