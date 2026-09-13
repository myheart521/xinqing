package com.hpu.xinqing.controller.ai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudentToolIntentResolverTest {

    @Test
    void directLookupOnlyForBasicStudentInfo() {
        assertTrue(StudentToolIntentResolver.shouldDirectLookupStudentInfo(
                "\u8bf7\u67e5\u8be2\u5b66\u53f7\u4e3a [100000000001] \u7684\u5b66\u751f\u4fe1\u606f"));
        assertTrue(StudentToolIntentResolver.shouldDirectLookupStudentInfo(
                "\u67e5\u8be2100000000001\u7684\u5b66\u751f\u57fa\u672c\u4fe1\u606f"));
        assertTrue(StudentToolIntentResolver.shouldDirectLookupStudentInfo(
                "\u67e5\u770b\u5b66\u53f7100000000001\u7684\u5b66\u751f\u8d44\u6599"));
    }

    @Test
    void functionCallToolsAreNotInterceptedAsBasicStudentInfo() {
        String[] prompts = {
                "\u8bf7\u67e5\u8be2\u5b66\u53f7\u4e3a [100000000001] \u7684\u5b66\u751f\u6700\u8fd1 2 \u6761\u6d3b\u52a8\u8bb0\u5f55",
                "\u8bf7\u67e5\u8be2\u5b66\u53f7\u4e3a [100000000001] \u7684\u5b66\u751f\u6700\u8fd1 2 \u6761\u535a\u5ba2\u52a8\u6001",
                "\u8bf7\u67e5\u8be2\u5b66\u53f7\u4e3a [100000000001] \u7684\u5b66\u751f\u6700\u8fd1 5 \u6761\u8bc4\u8bba",
                "\u8bf7\u5206\u6790\u5b66\u53f7\u4e3a [100000000001] \u7684\u5b66\u751f\u5fc3\u7406\u72b6\u6001",
                "\u8bf7\u67e5\u8be2\u5b66\u53f7\u4e3a [100000000001] \u7684\u5b66\u751f\u8fd0\u52a8\u8bb0\u5f55",
                "\u8bf7\u67e5\u8be2\u5b66\u53f7\u4e3a [100000000001] \u7684\u5b66\u751f\u5173\u6ce8\u7684\u5708\u5b50",
                "\u8bf7\u67e5\u8be2\u5b66\u53f7\u4e3a [100000000001] \u7684\u5b66\u751f\u5173\u6ce8\u7684\u7528\u6237",
                "\u8bf7\u67e5\u8be2\u5b66\u53f7\u4e3a [100000000001] \u7684\u5b66\u751f\u70b9\u8d5e\u7684\u6587\u7ae0",
                "\u8bf7\u5bfc\u51fa\u5b66\u53f7\u4e3a [100000000001] \u7684\u5b66\u751f\u6700\u8fd1 3 \u6761\u6d4b\u8bc4\u7ed3\u679c",
                "\u8bf7\u5bfc\u51fa\u6700\u8fd1 10 \u6761\u5b66\u751f\u5217\u8868",
                "\u8bf7\u6267\u884c\u6d4f\u89c8\u5668\u64cd\u4f5c",
                "\u8bf7\u53d1\u5e03\u4e00\u7bc7\u6587\u7ae0",
                "\u8bf7\u722c\u53d6\u7f51\u5740 https://example.com \u7684\u5185\u5bb9"
        };

        for (String prompt : prompts) {
            assertFalse(StudentToolIntentResolver.shouldDirectLookupStudentInfo(prompt), prompt);
        }
    }

    @Test
    void extractsStudentNumber() {
        assertEquals("100000000001", StudentToolIntentResolver.extractStudentNumber(
                "\u8bf7\u67e5\u8be2\u5b66\u53f7\u4e3a [100000000001] \u7684\u5b66\u751f\u4fe1\u606f"));
    }
}
