package com.hpu.xinqingcommon.utils;

import com.alibaba.dashscope.aigc.multimodalconversation.AudioParameters;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import org.springframework.stereotype.Component;
import org.stringtemplate.v4.ST;

public class AIUtil {
    static final String MODEL = "qwen-tts";
    static final String apiKey=System.getenv("DASHSCOPE_API_KEY");

    public static String qwenTTS(String text) {
        try {
            MultiModalConversation conv = new MultiModalConversation();
            MultiModalConversationParam param = MultiModalConversationParam.builder()
                    .model(MODEL)
                    .apiKey(apiKey)
                    .text(text)
                    .voice(AudioParameters.Voice.CHERRY)
                    .build();
            MultiModalConversationResult result = conv.call(param);
            String audioUrl = result.getOutput().getAudio().getUrl();
            System.out.println(audioUrl);
            return audioUrl;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
