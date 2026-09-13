package com.hpu.xinqing.TimeTask.warning;

public interface PsychologyWarningNotifyHandler {

    void notify(PsychologyWarningTask.InvokeRes invokeRes);

    boolean supports(String handlerName);
}
