package com.hpu.xinqing.TimeTask.warning;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hpu.xinqing.config.ThreadPoolConfig;
import com.hpu.xinqing.mapper.UserMapper;
import com.hpu.xinqingpojo.entity.User;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

/**
 * 心理预警定时任务
 */
//@Component
@Slf4j
public class PsychologyWarningTask {
    //默认的间隔时间为1个小时
    @Value("${timeTask.warning.fixedDelay}")
    private long fixedDelay = 3600000;
    //异常处理器名字
    @Value("${timeTask.warning.handler.exception}")
    private String exceptionHandlerName = DefaultPsychologyWarningHandler.HANDLER_NAME;
    @Value("${timeTask.warning.handler.task}")
    private String coreTaskHandlerName = DefaultPsychologyWarningHandler.HANDLER_NAME;
    @Value("${timeTask.warning.handler.notify}")
    private String notifyHandlerName = DefaultPsychologyWarningHandler.HANDLER_NAME;

    private HashMap<Long, ScheduledFuture<?>> scheduleExecutorMap = new HashMap<>();

    @Resource
    UserMapper userMapper;
    @Resource(name = ThreadPoolConfig.IO_POOL)
    TaskExecutor notifyHandlerTaskExecutor;

    @Autowired
    DefaultPsychologyWarningHandler defaultHandler;

    @Autowired
    List<PsychologyWarningExceptionHandler> exceptionHandlers;
    @Autowired
    List<PsychologyWarningCoreTaskHandler> coreTaskHandlers;
    @Autowired
    List<PsychologyWarningNotifyHandler> notifyHandlers;

    private PsychologyWarningExceptionHandler exceptionHandler;
    private PsychologyWarningCoreTaskHandler coreTaskHandler;
    private PsychologyWarningNotifyHandler notifyHandler;


    private static final ScheduledExecutorService scheduledExecutor = new ScheduledThreadPoolExecutor(10);

    /**
     * 阻塞队列，用于存放待处理的定时任务的结果
     * - 需要做可靠性保证
     */
    private final JiangBlockingQueue<InvokeRes> blockingQueue = new JiangBlockingQueue<>(200);


    public void submit(UserTaskDomain domain) {
        long firstStartTaskTime = computeDelay(domain.getCreateTime());
        ScheduledFuture<?> task = scheduledExecutor.scheduleWithFixedDelay(coreTask(domain.getId()), firstStartTaskTime, fixedDelay, TimeUnit.MILLISECONDS);
        scheduleExecutorMap.put(domain.getId(), task);
    }

    public boolean cancel(Long userId) {
        ScheduledFuture<?> task = scheduleExecutorMap.get(userId);
        if (task != null) {
            return task.cancel(false);
        }
        return false;
    }

    @PostConstruct
    public void init() throws InterruptedException {
        log.info("心理预警服务启动中......STARTING");
        //这里做一个初始化
        //1. 初始化handler
        this.coreTaskHandler = selectCoreTaskHandler(coreTaskHandlers);
        this.exceptionHandler = selectExceptionHandler(exceptionHandlers);
        this.notifyHandler = selectNotifyHandler(notifyHandlers);

        //2. 初始化定时任务
        for (UserTaskDomain domain : getUserTaskDomains()) {
            submit(domain);
        }
        //3. 处理结果通知
        new Thread(() -> {
            try {
                disposeInvokeRes();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        log.info("心理预警服务启动完成......SUCCESS");
    }

    //这里做一个初始化
    @PreDestroy
    public void destroy() {
        log.info("心理预警服务关闭中......STOPPING");
        for (Long userId : scheduleExecutorMap.keySet()) {
            scheduleExecutorMap.get(userId).cancel(false);
        }
        scheduledExecutor.shutdown();
        log.info("心理预警服务关闭完成......STOPPED");
    }


    //这里结合阻塞队列, 封装一个可靠性保证
    private void disposeInvokeRes() throws InterruptedException {
        while (true) {
            InvokeRes res = blockingQueue.take();
            notifyHandlerTaskExecutor.execute(() -> notifyHandler.notify(res));
        }
    }

    //封装一个核心任务
    private Runnable coreTask(Long userId) {
        return () -> {
            InvokeRes res = null;
            try {
                res = coreTaskHandler.handleTask(userId);
                blockingQueue.put(res);
            } catch (Exception e) {
                if (exceptionHandler.handleException(res, e)) {
                    try {
                        blockingQueue.put(res);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        };
    }

    @Data
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InvokeRes {
        private boolean success;
        private Long userId;
        private Object res;//自定义返回结果
        private int retryTimes = 0;

        public InvokeRes(Boolean success, Long userId, Object res) {
            this.userId = userId;
            this.success = success;
            this.res = res;
        }

        public static InvokeRes failure(Long userId) {
            return new InvokeRes(false, userId, null);

        }

        public static InvokeRes success(Long userId, Object res) {
            return new InvokeRes(true, userId, res);
        }
    }

    @Data
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TeskResult {
        private Long userId;
        private String school;
        private Integer score;
        private String studentNumber;
        //now + "学号为：" + user.getStudentNumber() + "，姓名：" + user.getUserName() + "同学用AI聊天消极情绪比例为：" + score + "，请及时处理";
        private String content;
        private LocalDate now;
    }

    @Data
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public class UserTaskDomain {

        private Long id;
        private LocalDateTime createTime;
    }

    //计算第一次执行的时间
    private long computeDelay(LocalDateTime createTime) {
        long registerTimeMils = createTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        return registerTimeMils % fixedDelay;
    }

    //获取所有的用户任务
    private List<UserTaskDomain> getUserTaskDomains() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.select("id", "create_time");
        return userMapper.selectList(wrapper).stream()
                .map(user -> new UserTaskDomain(user.getId(), user.getCreateTime())).toList();
    }

    //选择handler
    private PsychologyWarningCoreTaskHandler selectCoreTaskHandler(List<PsychologyWarningCoreTaskHandler> list) {
        for (PsychologyWarningCoreTaskHandler handler : list) {
            if (handler.supports(coreTaskHandlerName)) {
                return handler;
            }
        }
        return defaultHandler;
    }

    private PsychologyWarningExceptionHandler selectExceptionHandler(List<PsychologyWarningExceptionHandler> list) {
        for (PsychologyWarningExceptionHandler handler : list) {
            if (handler.supports(exceptionHandlerName)) {
                return handler;
            }
        }
        return defaultHandler;
    }

    private PsychologyWarningNotifyHandler selectNotifyHandler(List<PsychologyWarningNotifyHandler> list) {
        for (PsychologyWarningNotifyHandler handler : list) {
            if (handler.supports(notifyHandlerName)) {
                return handler;
            }
        }
        return defaultHandler;
    }
}
