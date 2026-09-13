package com.hpu.xinqingcommon.utils;

/**
 * JSON安全工具类
 * 用于处理可能导致JSON解析错误的特殊字符
 */
public class JsonSafeUtil {

    /**
     * 处理文本中的特殊字符，使其在JSON中安全
     * 适用于需要转义换行符、回车符和制表符的场景，如文件内容
     *
     * @param text 需要处理的文本
     * @return 处理后的安全文本
     */
    public static String escapeForJson(String text) {
        if (text == null) {
            return null;
        }
        return text.replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t")
                  .replace("\\", "\\\\")
                  .replace("\"", "\\\"");
    }
    
    /**
     * 处理文本中的换行符和回车符，将其替换为空格
     * 适用于需要保持单行文本的场景，如命令和查询
     *
     * @param text 需要处理的文本
     * @return 处理后的单行文本
     */
    public static String toSingleLine(String text) {
        if (text == null) {
            return null;
        }
        return text.replace("\n", " ")
                  .replace("\r", " ");
    }
    
    /**
     * 处理HTML内容，保留换行符但转义其他特殊字符
     * 适用于富文本内容的处理
     *
     * @param html HTML文本内容
     * @return 处理后的安全HTML文本
     */
    public static String escapeHtmlForJson(String html) {
        if (html == null) {
            return null;
        }
        return html.replace("\\", "\\\\")
                  .replace("\"", "\\\"");
    }
    
    /**
     * 处理可能包含控制字符的数据，便于数据传输
     * 适用于文件传输等场景
     *
     * @param data 需要处理的数据
     * @return 处理后的安全数据
     */
    public static String escapeForDataTransfer(String data) {
        if (data == null) {
            return null;
        }
        return data.replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\\", "\\\\")
                  .replace("\"", "\\\"");
    }
} 