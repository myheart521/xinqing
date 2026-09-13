package com.hpu.xinqing.TimeTask.warning;

public interface PsychologyWarningCoreTaskHandler {

    PsychologyWarningTask.InvokeRes handleTask(Long userId);

    boolean supports(String handlerName);
}
