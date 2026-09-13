package com.hpu.xinqing.controller.ai;

import cn.hutool.core.util.ObjectUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class StudentToolIntentResolver {

    private static final Pattern STUDENT_NUMBER_PATTERN = Pattern.compile("(\\d{12})");

    private static final String[] BASIC_INFO_KEYWORDS = {
            "\u5b66\u751f\u4fe1\u606f",
            "\u5b66\u751f\u8d44\u6599",
            "\u57fa\u672c\u4fe1\u606f",
            "\u8be6\u7ec6\u4fe1\u606f",
            "\u4e2a\u4eba\u4fe1\u606f"
    };

    private static final String[] FUNCTION_CALL_TOOL_KEYWORDS = {
            "\u6d3b\u52a8",
            "\u535a\u5ba2",
            "\u52a8\u6001",
            "\u8bc4\u8bba",
            "\u5fc3\u7406",
            "\u72b6\u6001",
            "\u8fd0\u52a8",
            "\u5708\u5b50",
            "\u5173\u6ce8",
            "\u70b9\u8d5e",
            "\u6587\u7ae0",
            "\u6587\u4ef6",
            "\u5bfc\u51fa",
            "\u6d4b\u8bc4",
            "\u6d4f\u89c8\u5668",
            "\u53d1\u5e03",
            "\u722c\u53d6",
            "\u7f51\u9875",
            "\u7f51\u5740",
            "\u5730\u56fe"
    };

    private StudentToolIntentResolver() {
    }

    static boolean shouldDirectLookupStudentInfo(String prompt) {
        if (ObjectUtil.isEmpty(prompt) || extractStudentNumber(prompt) == null) {
            return false;
        }
        if (containsAny(prompt, FUNCTION_CALL_TOOL_KEYWORDS)) {
            return false;
        }
        return containsAny(prompt, BASIC_INFO_KEYWORDS);
    }

    static String extractStudentNumber(String prompt) {
        if (ObjectUtil.isEmpty(prompt)) {
            return null;
        }
        Matcher matcher = STUDENT_NUMBER_PATTERN.matcher(prompt);
        return matcher.find() ? matcher.group(1) : null;
    }

    private static boolean containsAny(String text, String[] keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
