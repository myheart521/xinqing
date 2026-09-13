package com.hpu.xinqing.TimeTask.warning;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultPsychologyWarningHandler implements PsychologyWarningExceptionHandler,
        PsychologyWarningCoreTaskHandler,PsychologyWarningNotifyHandler
{
    public static final String HANDLER_NAME="default";
    @Override
    public boolean handleException(PsychologyWarningTask.InvokeRes invokeRes, Exception e) {
        invokeRes.setRetryTimes(invokeRes.getRetryTimes()+1);
        if (invokeRes.getRetryTimes()==5){
            log.error("用户id为 "+invokeRes.getUserId()+" 的任务重试次数已达上限，不再重试, 已经从队列中丢弃");
            return false;
        }
        log.error("用户id为 "+invokeRes.getUserId()+" 的任务执行失败了, 现在重新放入队尾... 重试次数为 "+invokeRes.getRetryTimes()+" , 重试中...");
        return true;
    }

    @Override
    public PsychologyWarningTask.InvokeRes handleTask(Long userId) {
        log.info("模拟为用户id为 "+userId+" 的用户执行了一次心理计算");
        return null;
    }

    @Override
    public void notify(PsychologyWarningTask.InvokeRes invokeRes) {
        log.info("模拟为用户id为 "+invokeRes.getUserId()+" 的用户推送了一次心理预警");
    }

    @Override
    public boolean supports(String handlerName) {
        return HANDLER_NAME.equals(handlerName);
    }
}
