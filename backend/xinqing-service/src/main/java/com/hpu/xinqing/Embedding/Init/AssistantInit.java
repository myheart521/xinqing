package com.hpu.xinqing.Embedding.Init;

import com.hpu.xinqing.manage.StudentAgent.tools.StudentTools;
import com.hpu.xinqing.manage.TeacherAgent.tools.JsoupCrawlerTools;
import com.hpu.xinqing.manage.TeacherAgent.tools.TeacherBasicTools;
import com.hpu.xinqing.service.ai.Assistant;
import com.hpu.xinqing.service.ai.AssistantTeacher;
import com.hpu.xinqing.service.ai.AssistantTeacherMCP;
import com.hpu.xinqing.service.ai.AssistantWeb;
import com.hpu.xinqing.service.ai.AssistantWaring;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.WebSearchTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
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

    @Resource(name = "mentalWarningChatModel")
    private OpenAiChatModel mentalWarningChatModel;

    @Value("${mcp.os.type:windows}")
    private String osType;

    @Value("${mcp.base.command.windows:cmd /c npx -y}")
    private String windowsBaseCommand;

    @Value("${mcp.base.command.linux:npx -y}")
    private String linuxBaseCommand;

    @Value("${mcp.fetch.command:mcp-server-fetch}")
    private String fetchCommand;

    @Value("${mcp.enabled:false}")
    private boolean mcpEnabled;

    @Value("${mcp.fetch.base.command:}")
    private String fetchBaseCommand;

    @Value("${baidu.map.api.key}")
    private String baiduMapApiKey;

    @Value("${mysql.host}")
    private String mysqlHost;

    @Value("${mysql.port:3306}")
    private String mysqlPort;

    @Value("${mysql.user:xinqing}")
    private String mysqlUser;

    @Value("${mysql.password}")
    private String mysqlPassword;

    @Value("${mysql.database:xinqing}")
    private String mysqlDatabase;

    public static final String QIANWEN_MODEL = "qianwen";
    public static final String OLLAMA_MODEL = "ollama";
    public static final List<String> supportAIList = List.of(QIANWEN_MODEL, OLLAMA_MODEL);

    @Bean
    public Assistant init(RedisEmbeddingStore embeddingStore) {
        return AiServices.builder(Assistant.class)
                .chatMemoryProvider(chatMemoryProvider)
                .streamingChatLanguageModel(chatModel)
                .contentRetriever(embeddingStoreContentRetriever)
                .tools(studentTools, new WebSearchTool(webSearchEngine))
                .build();
    }

    @Bean
    public AssistantWeb webInit(RedisEmbeddingStore embeddingStore) {
        return AiServices.builder(AssistantWeb.class)
                .chatMemoryProvider(chatMemoryProvider)
                .streamingChatLanguageModel(chatModel)
                .tools(new WebSearchTool(webSearchEngine))
                .build();
    }

    @Bean
    public AssistantTeacher assistantTeacher(RedisEmbeddingStore embeddingStore) {
        return AiServices.builder(AssistantTeacher.class)
                .chatMemoryProvider(chatMemoryProvider)
                .chatLanguageModel(qwenChatModel)
                .streamingChatLanguageModel(chatModel)
                .contentRetriever(embeddingStoreContentRetriever)
                .tools(teacherTools, new WebSearchTool(webSearchEngine))
                .build();
    }

    @Bean
    public AssistantTeacherMCP assistantMCP(RedisEmbeddingStore embeddingStore) {
        ChatLanguageModel myModel = QwenChatModel.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .modelName("qwen-max-2025-01-25")
                .build();

        if (!mcpEnabled) {
            log.warn("MCP tools are disabled. Set mcp.enabled=true only after MCP runtime commands are installed.");
            return AiServices.builder(AssistantTeacherMCP.class)
                    .chatMemoryProvider(chatMemoryProvider)
                    .chatLanguageModel(myModel)
                    .build();
        }

        try {
            McpTransport transportFetch = new StdioMcpTransport.Builder()
                    .command(getFetchCommand())
                    .logEvents(true)
                    .build();

            McpClient mcpClientFetch = new DefaultMcpClient.Builder()
                    .transport(transportFetch)
                    .build();

            McpTransport mcpClientThink = new StdioMcpTransport.Builder()
                    .command(getCommandForOS("@modelcontextprotocol/server-sequential-thinking"))
                    .build();
            McpClient mcpClientThinkServer = new DefaultMcpClient.Builder()
                    .transport(mcpClientThink)
                    .build();

            McpTransport transportBrowser = new StdioMcpTransport.Builder()
                    .command(getCommandForOS("@modelcontextprotocol/server-puppeteer"))
                    .build();
            McpClient mcpClientBrowser = new DefaultMcpClient.Builder()
                    .transport(transportBrowser)
                    .build();

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

            ToolProvider toolProvider = McpToolProvider.builder()
                    .mcpClients(List.of(mcpClientThinkServer, mcpClientMySQL, mcpClientBrowser, mcpClientFetch))
                    .build();

            return AiServices.builder(AssistantTeacherMCP.class)
                    .chatMemoryProvider(chatMemoryProvider)
                    .chatLanguageModel(myModel)
                    .toolProvider(toolProvider)
                    .build();
        } catch (Exception e) {
            log.warn("Failed to start MCP tools. AssistantTeacherMCP will run without MCP tools.", e);
            return AiServices.builder(AssistantTeacherMCP.class)
                    .chatMemoryProvider(chatMemoryProvider)
                    .chatLanguageModel(myModel)
                    .build();
        }
    }

    @Bean
    public AssistantWaring assistantWaring() {
        return AiServices.builder(AssistantWaring.class)
                .chatLanguageModel(mentalWarningChatModel)
                .build();
    }

    private List<String> getCommandForOS(String specificCommand) {
        String baseCommand = "windows".equalsIgnoreCase(osType) ? windowsBaseCommand : linuxBaseCommand;
        return buildCommand(baseCommand, specificCommand);
    }

    private List<String> getFetchCommand() {
        if (fetchBaseCommand != null && !fetchBaseCommand.isBlank()) {
            return buildCommand(fetchBaseCommand, fetchCommand);
        }
        return getCommandForOS(fetchCommand);
    }

    private List<String> buildCommand(String baseCommand, String specificCommand) {
        List<String> command = new ArrayList<>(Arrays.asList(baseCommand.trim().split("\\s+")));
        command.add(specificCommand);
        return command;
    }
}
