package com.hpu.xinqing.Embedding.Init;

import com.hpu.xinqing.manage.StudentAgent.tools.StudentTools;
import com.hpu.xinqing.manage.TeacherAgent.tools.JsoupCrawlerTools;
import com.hpu.xinqing.manage.TeacherAgent.tools.TeacherBasicTools;
import com.hpu.xinqing.manage.TeacherAgent.tools.WaringTools;
import com.hpu.xinqing.service.ai.*;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.WebSearchTool;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import javax.validation.constraints.Max;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

//import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;


@Configuration
@RequiredArgsConstructor
public class AssistantInit {
    private final QwenStreamingChatModel chatModel;
    private final QwenChatModel qwenChatModel;
    private final OllamaStreamingChatModel ollamaChatModel;
    private final ChatMemoryProvider chatMemoryProvider;
    private final WebSearchEngine webSearchEngine;
    private final EmbeddingStoreContentRetriever embeddingStoreContentRetriever;
    private final TeacherBasicTools teacherTools;
    private final StudentTools studentTools;
    private final JsoupCrawlerTools jsoupCrawlerTools;
    private final Environment environment;
    private final WaringTools waringTools;

    @Value("${mcp.os.type:windows}")
    private String osType;

    @Value("${mcp.base.command.windows:cmd /c npx -y}")
    private String windowsBaseCommand;

    @Value("${mcp.base.command.linux:npx -y}")
    private String linuxBaseCommand;

    @Value("${mcp.fetch.command:mcp-server-fetch}")
    private String fetchCommand;

    @Value("${baidu.map.api.key}")
    private String baiduMapApiKey;

    @Value("${mysql.host}")
    private String mysqlHost;

    @Value("${mysql.port:3306}")
    private String mysqlPort;

    @Value("${mysql.user}")
    private String mysqlUser;

    @Value("${mysql.password}")
    private String mysqlPassword;

    @Value("${mysql.database}")
    private String mysqlDatabase;

    public static final String QIANWEN_MODEL = "qianwen";
    public static final String OLLAMA_MODEL = "ollama";
    public static final List<String> supportAIList = List.of(QIANWEN_MODEL, OLLAMA_MODEL);


    @Bean
    public Assistant init(RedisEmbeddingStore embeddingStore) {
        return AiServices.builder(Assistant.class)
                .chatMemoryProvider(chatMemoryProvider)
                .streamingChatLanguageModel(chatModel)
                .contentRetriever(embeddingStoreContentRetriever)//使用向量数据库
                .tools(studentTools, new WebSearchTool(webSearchEngine))
                //new HighLevelCalculator()就直接放入自动把带有@Tool的方法放入大模型
                //new WebSearchTool(engine)集成联网搜查的能力
//              .tools(new HighLevelCalculator(), new WebSearchTool(engine))//自定义函数的调用,可放入一组的自定义函数
                .build();

    }

//    @Bean
//    public AssistantTeacher initTeacher(RedisEmbeddingStore embeddingStore) {
//        return AiServices.builder(AssistantTeacher.class)
//                .chatMemoryProvider(chatMemoryProvider)
//                .streamingChatLanguageModel(ollamaChatModel)
//
////               .contentRetriever(embeddingStoreContentRetriever)//使用向量数据库
//                //new HighLevelCalculator()就直接放入自动把带有@Tool的方法放入大模型
//                //new WebSearchTool(engine)集成联网搜查的能力

    /// /              .tools(new HighLevelCalculator(), new WebSearchTool(engine))//自定义函数的调用,可放入一组的自定义函数
//                .build();
//
//    }
    @Bean
    public AssistantWeb webInit(RedisEmbeddingStore embeddingStore) {
        return AiServices.builder(AssistantWeb.class)
                .chatMemoryProvider(chatMemoryProvider)
                .streamingChatLanguageModel(chatModel)
                .tools(new WebSearchTool(webSearchEngine))//加入web工具调用,
//            .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))//使用向量数据库
                //new HighLevelCalculator()就直接放入自动把带有@Tool的方法放入大模型
                //new WebSearchTool(engine)集成联网搜查的能力
//            .tools(new HighLevelCalculator(), new WebSearchTool(engine))//自定义函数的调用,可放入一组的自定义函数
                .build();

    }

    /**
     * 这个是老师端可以查数据库的AI
     *
     * @param embeddingStore
     * @return
     */
    @Bean
    public AssistantTeacher assistantTeacher(RedisEmbeddingStore embeddingStore) {
        return AiServices.builder(AssistantTeacher.class)
                .chatMemoryProvider(chatMemoryProvider)
                .streamingChatLanguageModel(chatModel)
                .contentRetriever(embeddingStoreContentRetriever)//使用向量数据库
                .tools(teacherTools, new WebSearchTool(webSearchEngine))
                .build();
    }

    /**
     * 这个是使用mcp工具可以抓取网页内容并改写的AI
     * 使用mcp之前需要安装基础依赖，nodejs,python环境
     * pip install uv mcp  # 基础依赖
     */
    @Bean
    public AssistantTeacherMCP assistantMCP(RedisEmbeddingStore embeddingStore) {
        //        管理记忆，协调记忆的mcpServer
//        McpTransport transportMemory = new StdioMcpTransport.Builder()
//                .command(List.of("cmd",
//                        "/c",
//                        "npx",
//                        "-y",
//                        "@modelcontextprotocol/server-memory"
//                ))
//                .environment(Map.of("MEMORY_FILE_PATH", "D:/memory-bank-mcp.json"))
//                .build();
//        McpClient mcpClientMemory = new DefaultMcpClient.Builder()
//                .transport(transportMemory)
//                .build();

        //爬取网页的mcpServer
        //使用的uvx并且使用的是官方的调用方式
       McpTransport transportFetch = new StdioMcpTransport.Builder()
                .command(List.of("uvx", fetchCommand))
                .logEvents(true) // 开启日志（调试用）
                .build();

//        McpTransport transportFetch = new StdioMcpTransport.Builder()
//                .command(List.of("python3", "-m", "mcp-server-fetch"))
//                .logEvents(true) // 开启日志（调试用）
//                .build();

        McpClient mcpClientFetch = new DefaultMcpClient.Builder()
                .transport(transportFetch)
                .build();


        //深度思考的mcpServer、使大模型生成能力更强
        McpTransport mcpClientThink = new StdioMcpTransport.Builder()
                .command(getCommandForOS("@modelcontextprotocol/server-sequential-thinking"))
                .build();
        McpClient mcpClientThinkServer = new DefaultMcpClient.Builder()
                .transport(mcpClientThink)
                .build();
        //浏览器交互的mcpServer
        McpTransport transportBrowser = new StdioMcpTransport.Builder()
                .command(getCommandForOS("@modelcontextprotocol/server-puppeteer"))
                .build();
        McpClient mcpClientBrowser = new DefaultMcpClient.Builder()
                .transport(transportBrowser)
                .build();
        //mysql交互的mcpServer
        McpTransport transportMySQL = new StdioMcpTransport.Builder()
                .command(getCommandForOS("mysql-mcp-server"))
                .environment(Map.of(
                        "MYSQL_HOST", mysqlHost,
                        "MYSQL_PORT", mysqlPort,
                        "MYSQL_USER", mysqlUser,
                        "MYSQL_PASSWORD", mysqlPassword,
                        "MYSQL_DATABASE", mysqlDatabase))
                .build();
        McpClient mcpClientMySQL = new DefaultMcpClient.Builder()
                .transport(transportMySQL)
                .build();
//
//        百度API，测试
//        McpTransport transportBaidu = new StdioMcpTransport.Builder()
//                .command(getCommandForOS("@baidumap/mcp-server-baidu-map"))
//                .environment(Map.of("BAIDU_MAP_API_KEY", baiduMapApiKey))
//                .build();
//        McpClient mcpClientBaidu = new DefaultMcpClient.Builder()
//                .transport(transportBaidu)
//                .build();


        ToolProvider toolProvider = McpToolProvider.builder()
                .mcpClients(List.of(mcpClientThinkServer, mcpClientMySQL, mcpClientBrowser, mcpClientFetch))
                .build();

        ChatLanguageModel myModel = QwenChatModel.builder()
                .apiKey(System.getenv("LANGCHAIN4J_COMMUNITY_DASHSCOPE_STREAMING_CHAT_MODEL_API_KEY"))
                .modelName("qwen-max-2025-01-25")
                .build();
        return AiServices.builder(AssistantTeacherMCP.class)
                .chatMemoryProvider(chatMemoryProvider)
                .chatLanguageModel(myModel)
                .toolProvider(toolProvider)
                .build();
    }

    /**
     * 根据当前操作系统获取适用的命令
     *
     * @param specificCommand 具体的命令部分
     * @return 完整的命令列表
     */
    private List<String> getCommandForOS(String specificCommand) {
        if ("windows".equalsIgnoreCase(osType)) {
            return Arrays.asList(windowsBaseCommand.split("\\s+")[0],
                    windowsBaseCommand.split("\\s+")[1],
                    windowsBaseCommand.split("\\s+")[2],
                    specificCommand);
        } else {
            return Arrays.asList(linuxBaseCommand.split("\\s+")[0],
                    linuxBaseCommand.split("\\s+")[1],
                    specificCommand);
        }
    }


    @Bean
    public AssistantWaring assistantWaring(RedisEmbeddingStore embeddingStore) {
        ChatLanguageModel myModel = QwenChatModel.builder()
                .apiKey(System.getenv("LANGCHAIN4J_COMMUNITY_DASHSCOPE_STREAMING_CHAT_MODEL_API_KEY"))
//                .modelName("qwen-turbo-2025-04-28")//上下文长度为1M，约等于 100 万汉字
                .modelName("qwen-max-2025-01-25")
                .build();
        return AiServices.builder(AssistantWaring.class)
                .chatLanguageModel(myModel)
                .tools(waringTools, new WebSearchTool(webSearchEngine))
                .build();
    }
}
