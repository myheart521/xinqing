package com.hpu.xinqing.TimeTask.warning;

public interface PsychologyWarningExceptionHandler {
    //这个Boolean代表是否继续重试
    boolean handleException(PsychologyWarningTask.InvokeRes invokeRes,Exception e);

    boolean supports(String handlerName);
}
