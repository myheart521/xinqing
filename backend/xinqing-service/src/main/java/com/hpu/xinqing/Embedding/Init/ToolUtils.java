package com.hpu.xinqing.Embedding.Init;

import dev.langchain4j.agent.tool.Tool;

import java.time.LocalDateTime;

public class ToolUtils{
    @Tool("查询当前时间")
    public String getDate(){
        return LocalDateTime.now().toString();
    }

    }