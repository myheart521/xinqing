package com.hpu.xinqing.TimeTask.warning;


/**
 * 此处仅说明 心理预警定时任务的使用方法
 *
 * 对外提供的Api 见 {@link com.hpu.xinqing.TimeTask.warning.api.PsyWarningApi}
 * 功能:
 * 1. 提交一个新用户的心理预警定时任务
 * 2. 取消一个用户的心理预警定时任务
 *
 *
 * 开发人员必读:
 * 1. 我该怎么配置心理预警服务的执行间隔时间呢? 比如我想每隔10分钟执行一次心理预警服务, 我该怎么配置呢?
 * 答: 你需要在application.yml中配置timeTask.warning.fixedDelay属性, 单位为ms. 例如10分钟为600000ms.
 *
 * 2. 我想要自定义心理预警服务的执行逻辑,比如我要让这个用户id的信息扔个AI让ai给我分析, 我该怎么配置呢?
 * 答: 2.1: 你需要实现{@link com.hpu.xinqing.TimeTask.warning.PsychologyWarningCoreTaskHandler}接口
 *     2.2并在application.yml中配置timeTask.warning.handler.task属性参照接口的support()方法
 * 3. 我想要自定义心理预警服务的异常处理逻辑,比如我要让这个用户调用PsychologyWarningCoreTaskHandler服务失败后直接打印日志, 然后不重试, 我该怎么配置呢?
 * 答: 3.1: 你需要实现{@link com.hpu.xinqing.TimeTask.warning.PsychologyWarningExceptionHandler}接口
 *     3.2并在application.yml中配置timeTask.warning.handler.exception属性参照接口的support()方法
 *     需要注意的是, boolean handleException(PsychologyWarningTask.InvokeRes invokeRes,Exception e)的返回值是boolean就是代表是否继续重试的意思
 * 4. 我想要自定义心理预警服务的通知逻辑,比如我要让这个用户调用PsychologyWarningCoreTaskHandler服务成功后, 我要给这个用户发送一条消息, 我该怎么配置呢?
 * 答: 4.1: 你需要实现{@link com.hpu.xinqing.TimeTask.warning.PsychologyWarningNotifyHandler}接口
 *     4.2并在application.yml中配置timeTask.warning.handler.notify属性参照接口的support()方法
 * 5. 那个JiangBlockingQueue是干什么的?
 * 答: 这个JiangBlockingQueue是JDK官方提供的阻塞队列的子类. JiangBlockingQueue继承自官方提供的ArrayBlockingQueue并重写了一部分方法,以方便更好配合心理预警服务使用
 *
 *
 *
 *
 */









